package dev.lld.practice.logger;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LoggerTest {

    @Test
    public void testGetInstance_DefaultConfiguration_ReturnsSameInstance() {
        // Test singleton behavior for default configuration
        MultiThreadedLogger logger1 = Logger.getInstance();
        MultiThreadedLogger logger2 = Logger.getInstance();

        assertSame("getInstance() should return the same instance", logger1, logger2);
        assertNotNull("Logger instance should not be null", logger1);
    }

    @Test
    public void testGetInstance_CustomConfiguration_ReturnsSameInstance() {
        // Test singleton behavior for custom configuration
        // Note: This test assumes no previous custom instance was created
        MultiThreadedLogger logger1 = Logger.getInstance("test.log", LogLevel.INFO, true, 1000);
        MultiThreadedLogger logger2 = Logger.getInstance("different.log", LogLevel.DEBUG, false, 500);

        assertSame("Custom getInstance() should return the same instance (first config wins)", logger1, logger2);
    }

    @Test
    public void testGetInstance_DefaultAndCustom_ReturnDifferentInstances() {
        MultiThreadedLogger defaultLogger = Logger.getInstance();
        MultiThreadedLogger customLogger = Logger.getInstance("test.log", LogLevel.INFO, true, 1000);

        // They should be different instances
        assertNotSame("Default and custom instances should be different", defaultLogger, customLogger);
    }

    @Test(timeout = 5000) // 5 second timeout
    public void testThreadSafety_DefaultInstance() throws InterruptedException {
        final int threadCount = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final AtomicReference<MultiThreadedLogger> firstInstance = new AtomicReference<>();
        final AtomicInteger successCount = new AtomicInteger(0);

        // Create multiple threads that all try to get instance simultaneously
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await(); // Wait for all threads to be ready
                        MultiThreadedLogger instance = Logger.getInstance();

                        // First thread sets the reference, others compare
                        if (firstInstance.compareAndSet(null, instance) ||
                                firstInstance.get() == instance) {
                            successCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                }
            }).start();
        }

        startLatch.countDown(); // Start all threads
        assertTrue("All threads should complete", doneLatch.await(3, TimeUnit.SECONDS));
        assertEquals("All threads should get the same instance", threadCount, successCount.get());
    }

    @Test(timeout = 10000) // 10 second timeout for this more complex test
    public void testThreadSafety_CustomInstance_NewJVM() throws InterruptedException {
        // This test works best in a fresh JVM where no custom instance exists yet
        final int threadCount = 50;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final AtomicReference<MultiThreadedLogger> firstInstance = new AtomicReference<>();
        final AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i; // For unique filenames
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        // Use same parameters so they should get same instance
                        MultiThreadedLogger instance = Logger.getInstance("concurrent-test.log", LogLevel.INFO, true, 1000);

                        if (firstInstance.compareAndSet(null, instance) ||
                                firstInstance.get() == instance) {
                            successCount.incrementAndGet();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneLatch.countDown();
                    }
                }
            }).start();
        }

        startLatch.countDown();
        assertTrue("All threads should complete", doneLatch.await(5, TimeUnit.SECONDS));
        assertEquals("All threads should get the same custom instance", threadCount, successCount.get());
    }

    @Test
    public void testRepeatedInstantiation_DefaultInstance() {
        // Test multiple instantiations return same instance
        MultiThreadedLogger logger1 = Logger.getInstance();
        MultiThreadedLogger logger2 = Logger.getInstance();
        MultiThreadedLogger logger3 = Logger.getInstance();

        assertSame(logger1, logger2);
        assertSame(logger2, logger3);
        assertSame(logger1, logger3);
    }

    @Test
    public void testStaticConvenienceMethods_DoNotThrowExceptions() {
        // Test that static convenience methods don't throw exceptions
        try {
            Logger.debug("Debug message");
            Logger.info("Info message");
            Logger.warning("Warning message");
            Logger.error("Error message");
            Logger.critical("Critical message");
        } catch (Exception e) {
            fail("Static convenience methods should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    public void testMemoryLeaks_MultipleInstanceCreations() {
        // Test that we're not creating multiple instances unintentionally
        MultiThreadedLogger logger1 = Logger.getInstance();

        // Force garbage collection
        System.gc();

        MultiThreadedLogger logger2 = Logger.getInstance();
        assertSame("Should return same instance even after GC", logger1, logger2);
    }

    @Test(timeout = 5000)
    public void testConcurrentAccessToDifferentInstanceTypes() throws InterruptedException {
        final int threadCount = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final AtomicInteger completedThreads = new AtomicInteger(0);

        // Half threads get default, half get custom
        for (int i = 0; i < threadCount; i++) {
            final boolean getDefault = i % 2 == 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.await();
                        if (getDefault) {
                            MultiThreadedLogger logger = Logger.getInstance();
                            assertNotNull("Default logger should not be null", logger);
                        } else {
                            MultiThreadedLogger logger = Logger.getInstance("concurrent.log", LogLevel.INFO, true, 1000);
                            assertNotNull("Custom logger should not be null", logger);
                        }
                        completedThreads.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        fail("Thread failed with exception: " + e.getMessage());
                    } finally {
                        doneLatch.countDown();
                    }
                }
            }).start();
        }

        startLatch.countDown();
        assertTrue("All threads should complete", doneLatch.await(3, TimeUnit.SECONDS));
        assertEquals("All threads should complete successfully", threadCount, completedThreads.get());
    }

    @Test
    public void testInstanceInitialization_DoesNotBlock() {
        // Test that getting instance doesn't cause deadlocks
        long startTime = System.currentTimeMillis();

        MultiThreadedLogger logger = Logger.getInstance();

        long endTime = System.currentTimeMillis();
        assertNotNull(logger);
        assertTrue("Instance creation should be fast (< 1 second)", endTime - startTime < 1000);
    }

    @Test
    public void testCustomConfiguration_ParametersProvided() {
        // Test that we can call the parameterized version without exceptions
        try {
            MultiThreadedLogger logger1 = Logger.getInstance("config-test.log", LogLevel.DEBUG, true, 100);
            assertNotNull("Custom configured logger should not be null", logger1);

            // Second call with different params should return same instance
            MultiThreadedLogger logger2 = Logger.getInstance("different.log", LogLevel.ERROR, false, 200);
            assertSame("Should return same instance regardless of new parameters", logger1, logger2);

        } catch (Exception e) {
            fail("Custom configuration should not throw exceptions: " + e.getMessage());
        }
    }

    @Test
    public void testBothInstanceTypes_SeparateInstances() {
        // Get default instance first
        MultiThreadedLogger defaultLogger = Logger.getInstance();

        // Get custom instance
        MultiThreadedLogger customLogger = Logger.getInstance("separate-test.log", LogLevel.WARNING, false, 500);

        // They should be different
        assertNotSame("Default and custom loggers should be separate instances", defaultLogger, customLogger);

        // Verify they're both functional
        assertNotNull("Default logger should not be null", defaultLogger);
        assertNotNull("Custom logger should not be null", customLogger);
    }
}