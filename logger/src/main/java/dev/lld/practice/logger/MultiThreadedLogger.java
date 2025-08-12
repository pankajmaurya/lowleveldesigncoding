package dev.lld.practice.logger;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// Main logger class
public class MultiThreadedLogger {
    private final BlockingQueue<LogEntry> logQueue;
    private final AtomicBoolean running;
    private final ExecutorService executor;
    private final PrintWriter fileWriter;
    private final Object writerLock;

    private volatile LogLevel minLevel;
    private volatile boolean consoleOutput;
    private final int maxQueueSize;

    private AtomicInteger unflushedLogs = new AtomicInteger(0);
    private static final Integer MAX_UNFLUSHED_LOGS = 100;

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public MultiThreadedLogger() {
        this("", LogLevel.INFO, true, 10000);
    }

    public MultiThreadedLogger(String filename, LogLevel minLevel,
                               boolean consoleOutput, int maxQueueSize) {
        this.logQueue = new LinkedBlockingQueue<>(maxQueueSize);
        this.running = new AtomicBoolean(true);
        this.minLevel = minLevel;
        this.consoleOutput = consoleOutput;
        this.maxQueueSize = maxQueueSize;
        this.writerLock = new Object();

        // Initialize file writer
        PrintWriter writer = null;
        if (filename != null && !filename.isEmpty()) {
            try {
                writer = new PrintWriter(new FileWriter(filename, true));
            } catch (IOException e) {
                System.err.println("Failed to open log file: " + filename);
                System.err.println("Error: " + e.getMessage());
            }
        }
        this.fileWriter = writer;


        this.executor = Executors.newFixedThreadPool(2);
        this.executor.submit(this::workerLoop);
        this.executor.submit(this::workerLoop);

//        // Start worker thread
//        this.executor = Executors.newSingleThreadExecutor(r -> {
//            Thread t = new Thread(r, "LoggerWorker");
//            t.setDaemon(true);
//            return t;
//        });
//
//        this.executor.submit(this::workerLoop);

        // Add shutdown hook to ensure graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    // Worker thread loop
    private void workerLoop() {
        while (running.get() || !logQueue.isEmpty()) {
            try {
                LogEntry entry = logQueue.poll(100, TimeUnit.MILLISECONDS);
                if (entry != null) {
                    writeLog(entry, unflushedLogs.incrementAndGet() >= MAX_UNFLUSHED_LOGS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Write log entry to file and/or console
    private void writeLog(LogEntry entry, boolean flush) {
        if (entry.getLevel().getValue() < minLevel.getValue()) {
            return;
        }

        String formattedMessage = formatMessage(entry);

        synchronized (writerLock) {
            // Write to file
            if (fileWriter != null) {
                fileWriter.println(formattedMessage);
                if (flush) {
                    fileWriter.flush();
                    unflushedLogs.set(0);
                }
            }

            // Write to console
            if (consoleOutput) {
                if (entry.getLevel().getValue() >= LogLevel.ERROR.getValue()) {
                    System.err.println(formattedMessage);
                } else {
                    System.out.println(formattedMessage);
                }
            }
        }
    }

    // Main logging method
    public void log(LogLevel level, String message) {
        if (!running.get()) {
            return;
        }

        LogEntry entry = new LogEntry(level, message);
        logQueue.offer(entry);
    }

    // Shutdown the logger gracefully
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // Close file writer
            synchronized (writerLock) {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            }
        }
    }

    // Convenience methods for different log levels
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public void critical(String message) {
        log(LogLevel.CRITICAL, message);
    }

    // Formatted logging methods
    public void debug(String format, Object... args) {
        log(LogLevel.DEBUG, String.format(format, args));
    }

    public void info(String format, Object... args) {
        log(LogLevel.INFO, String.format(format, args));
    }

    public void warning(String format, Object... args) {
        log(LogLevel.WARNING, String.format(format, args));
    }

    public void error(String format, Object... args) {
        log(LogLevel.ERROR, String.format(format, args));
    }

    public void critical(String format, Object... args) {
        log(LogLevel.CRITICAL, String.format(format, args));
    }

    // Configuration methods
    public void setMinLevel(LogLevel level) {
        this.minLevel = level;
    }

    public void setConsoleOutput(boolean enabled) {
        this.consoleOutput = enabled;
    }

    public LogLevel getMinLevel() {
        return minLevel;
    }

    public boolean isConsoleOutputEnabled() {
        return consoleOutput;
    }

    // Get current queue size for monitoring
    public int getQueueSize() {
        return logQueue.size();
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    // Check if logger is running
    public boolean isRunning() {
        return running.get();
    }

    // Format log message with timestamp and metadata
    private String formatMessage(LogEntry entry) {
        StringBuilder sb = new StringBuilder();

        // Format timestamp
        sb.append("[").append(entry.getTimestamp().format(TIMESTAMP_FORMAT)).append("]");

        // Add log level
        sb.append(" [").append(entry.getLevel().name()).append("]");

        // Add thread name
        sb.append(" [").append(entry.getThreadName()).append("]");

        // Add message
        sb.append(" ").append(entry.getMessage());

        return sb.toString();
    }
}