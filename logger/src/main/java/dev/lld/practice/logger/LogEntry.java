package dev.lld.practice.logger;

import java.time.LocalDateTime;

// Log levels enum
enum LogLevel {
    DEBUG(0), INFO(1), WARNING(2), ERROR(3), CRITICAL(4);

    private final int value;

    LogLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

// Log entry class
public class LogEntry {
    private final LogLevel level;
    private final String message;
    private final LocalDateTime timestamp;
    private final String threadName;

    public LogEntry(LogLevel level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.threadName = Thread.currentThread().getName();
    }

    // Getters
    public LogLevel getLevel() { return level; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getThreadName() { return threadName; }
}
