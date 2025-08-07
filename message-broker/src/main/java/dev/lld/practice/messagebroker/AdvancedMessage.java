package dev.lld.practice.messagebroker;
import java.io.*;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class AdvancedMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    // Thread-safe ID generation with better initialization
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private final long id;
    private final Object payload;
    private final Instant timestamp;
    private final String topic;
    private final MessageHeaders headers;

    // Primary constructor
    public AdvancedMessage(Object payload) {
        this(null, payload, MessageHeaders.empty());
    }

    public AdvancedMessage(String topic, Object payload) {
        this(topic, payload, MessageHeaders.empty());
    }

    public AdvancedMessage(String topic, Object payload, MessageHeaders headers) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.payload = Objects.requireNonNull(payload, "Payload cannot be null");
        this.timestamp = Instant.now();
        this.topic = topic;
        this.headers = Objects.requireNonNull(headers, "Headers cannot be null");
    }

    // Constructor for deserialization/reconstruction with known ID
    private AdvancedMessage(long id, Object payload, Instant timestamp, String topic, MessageHeaders headers) {
        this.id = id;
        this.payload = payload;
        this.timestamp = timestamp;
        this.topic = topic;
        this.headers = headers;
    }

    // Getters
    public long getId() {
        return id;
    }

    public Object getPayload() {
        return payload;
    }

    @SuppressWarnings("unchecked")
    public <T> T getPayload(Class<T> type) {
        if (type.isInstance(payload)) {
            return type.cast(payload);
        }
        throw new ClassCastException("Payload is not of type " + type.getSimpleName());
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public MessageHeaders getHeaders() {
        return headers;
    }

    // Serialization methods for persistence
    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {

            oos.writeLong(id);
            oos.writeLong(timestamp.toEpochMilli());
            oos.writeUTF(topic != null ? topic : "");
            oos.writeObject(headers);
            oos.writeObject(payload);

            return bos.toByteArray();
        }
    }

    public static AdvancedMessage deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            long id = ois.readLong();
            long timestampMillis = ois.readLong();
            String topic = ois.readUTF();
            MessageHeaders headers = (MessageHeaders) ois.readObject();
            Object payload = ois.readObject();

            return new AdvancedMessage(
                    id,
                    payload,
                    Instant.ofEpochMilli(timestampMillis),
                    topic.isEmpty() ? null : topic,
                    headers
            );
        }
    }

    // Builder pattern for complex message construction
    public static class Builder {
        private String topic;
        private Object payload;
        private MessageHeaders headers = MessageHeaders.empty();

        public Builder payload(Object payload) {
            this.payload = payload;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder headers(MessageHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder header(String key, String value) {
            this.headers = this.headers.with(key, value);
            return this;
        }

        public AdvancedMessage build() {
            return new AdvancedMessage(topic, payload, headers);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Factory methods for common message types
    public static AdvancedMessage of(Object payload) {
        return new AdvancedMessage(payload);
    }

    public static AdvancedMessage of(String topic, Object payload) {
        return new AdvancedMessage(topic, payload);
    }

    public static AdvancedMessage textMessage(String text) {
        return new AdvancedMessage(text);
    }

    public static AdvancedMessage jsonMessage(String topic, String json) {
        return AdvancedMessage.builder()
                .topic(topic)
                .payload(json)
                .header("Content-Type", "application/json")
                .build();
    }

    // Create a copy with modified fields
    public AdvancedMessage withTopic(String newTopic) {
        return new AdvancedMessage(getId(), payload, timestamp, newTopic, headers);
    }

    public AdvancedMessage withHeaders(MessageHeaders newHeaders) {
        return new AdvancedMessage(getId(), payload, timestamp, topic, newHeaders);
    }

    public AdvancedMessage withHeader(String key, String value) {
        return withHeaders(headers.with(key, value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AdvancedMessage message = (AdvancedMessage) obj;
        return id == message.id &&
                Objects.equals(payload, message.payload) &&
                Objects.equals(timestamp, message.timestamp) &&
                Objects.equals(topic, message.topic) &&
                Objects.equals(headers, message.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, payload, timestamp, topic, headers);
    }

    @Override
    public String toString() {
        return String.format("Message{id=%d, topic='%s', timestamp=%s, payload=%s, headers=%s}",
                id, topic, timestamp, payload, headers);
    }

    // Static method to get next ID without creating message (useful for testing)
    public static long peekNextId() {
        return ID_GENERATOR.get() + 1;
    }

    // Reset ID generator (use with caution, mainly for testing)
    static void resetIdGenerator() {
        ID_GENERATOR.set(0);
    }
}

// Supporting MessageHeaders class
final class MessageHeaders implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final MessageHeaders EMPTY = new MessageHeaders(java.util.Collections.emptyMap());

    private final java.util.Map<String, String> headers;

    private MessageHeaders(java.util.Map<String, String> headers) {
        this.headers = new java.util.HashMap<>(headers);
    }

    public static MessageHeaders empty() {
        return EMPTY;
    }

    public static MessageHeaders of(String key, String value) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put(key, value);
        return new MessageHeaders(map);
    }

    public MessageHeaders with(String key, String value) {
        java.util.Map<String, String> newHeaders = new java.util.HashMap<>(this.headers);
        newHeaders.put(key, value);
        return new MessageHeaders(newHeaders);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public String getOrDefault(String key, String defaultValue) {
        return headers.getOrDefault(key, defaultValue);
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public java.util.Set<String> keySet() {
        return java.util.Collections.unmodifiableSet(headers.keySet());
    }

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MessageHeaders that = (MessageHeaders) obj;
        return Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headers);
    }

    @Override
    public String toString() {
        return headers.toString();
    }
}
