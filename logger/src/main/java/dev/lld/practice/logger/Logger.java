package dev.lld.practice.logger;

// Singleton logger for global access
class Logger {
    private Logger() {}

    // Holder class - loaded only when referenced
    private static class LoggerHolder {
        // Default instance with default configuration
        private static final MultiThreadedLogger INSTANCE = new MultiThreadedLogger();
    }

    // Simple getInstance() for default configuration
    public static MultiThreadedLogger getInstance() {
        return LoggerHolder.INSTANCE;
    }

    // For custom configuration - uses double-checked locking since we can't predict parameters at class loading time
    private static volatile MultiThreadedLogger customInstance;
    private static final Object lock = new Object();

    public static MultiThreadedLogger getInstance(String filename, LogLevel minLevel,
                                                  boolean consoleOutput, int maxQueueSize) {
        if (customInstance == null) {
            synchronized (lock) {
                if (customInstance == null) {
                    customInstance = new MultiThreadedLogger(filename, minLevel, consoleOutput, maxQueueSize);
                }
            }
        }
        return customInstance;
    }

    // Convenience static methods
    public static void debug(String message) {
        getInstance().debug(message);
    }

    public static void info(String message) {
        getInstance().info(message);
    }

    public static void warning(String message) {
        getInstance().warning(message);
    }

    public static void error(String message) {
        getInstance().error(message);
    }

    public static void critical(String message) {
        getInstance().critical(message);
    }
}