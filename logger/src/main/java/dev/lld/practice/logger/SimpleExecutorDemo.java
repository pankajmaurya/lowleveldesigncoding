package dev.lld.practice.logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleExecutorDemo {

    static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        submitNewRunnable(executorService, "label1");

        submitNewRunnable(executorService, "label2");

        submitNewRunnable(executorService, "label3");
        submitNewRunnable(executorService, "label4");
        submitNewRunnable(executorService, "label5");

        if (!executorService.awaitTermination(10009, TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow(); // Force shutdown
        }
    }

    private static void submitNewRunnable(ExecutorService executorService, String label) {
        executorService.submit(new MyRunnable(label));
    }

    private static class MyRunnable implements Runnable {
        private final String label;

        public MyRunnable(String label) {
            this.label = label;
        }
        @Override
        public void run() {
            int count = 0;

            while (true) {
                try {
                    count++;
                    Thread.sleep(1000);
                    System.out.println(this.label + " | Printing by " + Thread.currentThread().getName() + " -> " + counter.incrementAndGet() + " : " + System.currentTimeMillis());
                    if (count >= 5) {
                        return;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }
}
