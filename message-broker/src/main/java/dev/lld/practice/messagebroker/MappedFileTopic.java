package dev.lld.practice.messagebroker;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class MappedFileTopic {
    private static final int MESSAGE_HEADER_SIZE = 4; // 4 bytes length
    private static final long DEFAULT_FILE_SIZE = 1024 * 1024 * 100; // 100MB

    private final RandomAccessFile file;
    private final MappedByteBuffer buffer;
    private final AtomicLong writeOffset = new AtomicLong(0);
    private final AtomicLong messageCount = new AtomicLong(0);

    private final ConcurrentHashMap<Consumer, Long> consumerToOffsetMap = new ConcurrentHashMap<>();
    private final ReadWriteLock bufferLock = new ReentrantReadWriteLock();

    // In-memory index for quick offset-to-position mapping
    private final ConcurrentHashMap<Long, Long> messageIndexToPosition = new ConcurrentHashMap<>();

    public MappedFileTopic(String filename) throws IOException {
        this(filename, DEFAULT_FILE_SIZE);
    }

    public MappedFileTopic(String filename, long size) throws IOException {
        this.file = new RandomAccessFile(filename, "rw");
        this.file.setLength(size);
        this.buffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);

        // Recovery: scan existing file to rebuild index
        recoverFromFile();
    }

    public void publishMessage(AdvancedMessage message) throws IOException {
        byte[] messageBytes = serializeMessage(message);
        int totalSize = MESSAGE_HEADER_SIZE + messageBytes.length;

        bufferLock.writeLock().lock();
        try {
            long currentOffset = writeOffset.get();

            // Check if we have enough space
            if (currentOffset + totalSize > buffer.capacity()) {
                throw new IOException("Buffer overflow - consider implementing log rotation");
            }

            // Write message length
            buffer.putInt((int) currentOffset, messageBytes.length);
            // Write message data
            buffer.position((int) currentOffset + MESSAGE_HEADER_SIZE);
            buffer.put(messageBytes);

            // Update index
            long messageIndex = messageCount.getAndIncrement();
            messageIndexToPosition.put(messageIndex, currentOffset);

            // Update write offset
            writeOffset.addAndGet(totalSize);

            // Force to disk for durability (optional - impacts performance)
            buffer.force();

        } finally {
            bufferLock.writeLock().unlock();
        }
    }

    public void subscribe(Consumer consumer) {
        subscribe(consumer, 0L); // Start from beginning by default
    }

    public void subscribe(Consumer consumer, long startOffset) {
        consumerToOffsetMap.put(consumer, startOffset);
    }

    public AdvancedMessage readForConsumer(Consumer consumer) throws IOException, ClassNotFoundException {
        Long messageIndex = consumerToOffsetMap.get(consumer);
        if (messageIndex == null) {
            throw new IllegalStateException("Consumer not subscribed");
        }

        if (messageIndex >= messageCount.get()) {
            return null; // No more messages
        }

        Long position = messageIndexToPosition.get(messageIndex);
        if (position == null) {
            return null; // Message not found
        }

        bufferLock.readLock().lock();
        try {
            // Read message
            AdvancedMessage message = deserializeMessage(position);

            // Advance consumer offset
            consumerToOffsetMap.put(consumer, messageIndex + 1);

            return message;
        } finally {
            bufferLock.readLock().unlock();
        }
    }

    private AdvancedMessage deserializeMessage(long position) throws IOException, ClassNotFoundException {
        // Read message length
        int messageLength = buffer.getInt((int) position);

        // Read message data
        byte[] messageBytes = new byte[messageLength];
        buffer.position((int) position + MESSAGE_HEADER_SIZE);
        buffer.get(messageBytes);

        return AdvancedMessage.deserialize(messageBytes);
    }

    private byte[] serializeMessage(AdvancedMessage message) {
        try {
            return message.serialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void recoverFromFile() throws IOException {
        long position = 0;
        long messageIndex = 0;

        while (position < buffer.capacity() - MESSAGE_HEADER_SIZE) {
            // Try to read message length
            int messageLength = buffer.getInt((int) position);

            // Check for uninitialized area (assuming 0 means empty)
            if (messageLength == 0) {
                break;
            }

            // Validate message length
            if (messageLength < 0 || position + MESSAGE_HEADER_SIZE + messageLength > buffer.capacity()) {
                break;
            }

            // Record this message in our index
            messageIndexToPosition.put(messageIndex++, position);

            // Move to next message
            position += MESSAGE_HEADER_SIZE + messageLength;
        }

        writeOffset.set(position);
        messageCount.set(messageIndex);
    }

    public void close() throws IOException {
        if (buffer != null) {
            buffer.force(); // Ensure all data is written
        }
        if (file != null) {
            file.close();
        }
    }

    // Additional utility methods
    public long getMessageCount() {
        return messageCount.get();
    }

    public long getConsumerOffset(Consumer consumer) {
        return consumerToOffsetMap.getOrDefault(consumer, 0L);
    }

    public void resetConsumerOffset(Consumer consumer, long offset) {
        consumerToOffsetMap.put(consumer, offset);
    }
}
