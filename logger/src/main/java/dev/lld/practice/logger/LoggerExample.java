package dev.lld.practice.logger;

import java.util.concurrent.*;

// Example usage and test class
class LoggerExample {
    public static void main(String[] args) throws InterruptedException {
        // Initialize logger with file output
        MultiThreadedLogger logger = new MultiThreadedLogger(
                "application-run-flushevery100-run2.log",
                LogLevel.DEBUG,
                false,
                1000
        );

        long start = System.currentTimeMillis();

        // Test single-threaded logging
        logger.info("Application starting");
        logger.debug("Debug message");
        logger.warning("This is a warning");
        logger.error("This is an error");
        logger.critical("Critical error occurred");

        // Test formatted logging
        logger.info("User %s logged in from IP %s", "john.doe", "192.168.1.100");
        logger.error("Failed to process %d records out of %d", 5, 100);

        // Test multi-threaded logging
        System.out.println("Testing multi-threaded logging...");

        // Create multiple threads that log concurrently
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                for (int j = 0; j < 1000; j++) {
                    logger.info("Thread %d - Message %d", threadNum, j);
                    logger.debug("Thread %d - Debug %d", threadNum, j);

                    try {
                        Thread.sleep(10); // Small delay to mix up the logging
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Monitor queue size
        System.out.println("Current queue size: " + logger.getQueueSize());

        // Test singleton logger
        Logger.info("Using singleton logger");
        Logger.error("Singleton error message");

        // Wait a bit for all logs to be processed
        Thread.sleep(1000);

        System.out.println("Final queue size: " + logger.getQueueSize());
        System.out.println("Logging test completed. Check 'application.log' file.");

        // Shutdown logger
        logger.shutdown();

        Logger.info("After shutdown: Using singleton logger");
        Logger.error("After shutdown: Singleton error message");

        long end = System.currentTimeMillis();

        System.out.println("Total time = " + (end - start));
    }
}