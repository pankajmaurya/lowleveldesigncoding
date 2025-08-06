package dev.lld.practice.blockingqueue;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for various blocking queues here
 */
public class BlockingQueueTest
{

    @Test
    public void testBoundedBlockingQUsingWaitNotify() throws Exception {
        BoundedBlockingQ<String> q = new BoundedBlockingQUsingWaitNotify<>(1);
        doGenericTest(q);
    }

    @Test
    public void testBoundedBlockingQUsingCondVar() throws Exception {
        BoundedBlockingQ<String> q = new BoundedBlockingQUsingCondVar<>(1);
        doGenericTest(q);
    }

    @Test
    public void testSemaphoreBoundedQueue() throws Exception {
        BoundedBlockingQ<String> q = new SemaphoreBoundedQueue<>(1);
        doGenericTest(q);
    }

    private void doGenericTest(BoundedBlockingQ<String> q) throws Exception {
        AtomicInteger produced = new AtomicInteger(0);
        // This map checks our correctness
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>(100);

        // 5 producers
        for (int c = 1; c <= 5; c++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 1; i <= 10; i++) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            //
                        }
                        try {
                            // String uuid = UUID.randomUUID().toString();
                            String uuid = String.valueOf(produced.incrementAndGet());
                            q.add(uuid);
                            map.computeIfPresent(uuid, (k, v) -> (v + 1));
                            map.putIfAbsent(uuid, 1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }

        // 1 consumer
        Thread singleConsumer = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 50; i++) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        //
                    }
                    try {
                        String uuid = q.remove();
                        map.computeIfPresent(uuid, (k, v) -> v - 1);
                        map.putIfAbsent(uuid, -1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        singleConsumer.start();

        singleConsumer.join();

        map.forEachValue(20, v -> {assertTrue(v == 0);});
    }
}
