package dev.lld.practice.taskscheduler;

import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler implements SchedulerApi {

    private AtomicBoolean isShutdown = new AtomicBoolean(false);
    private AtomicInteger size = new AtomicInteger(0);
    private Object pqLock = new Object();

    private SchedulerThread schedulerThread = new SchedulerThread();

    public boolean shutdown() {
        this.isShutdown.compareAndSet(false, true);
        try {
            schedulerThread.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void start() {
        schedulerThread.start();
    }

    /*public void printEnqueuedTasks() {
        synchronized (pqLock) {
            Iterator<Task> taskIterator = priorityQueue.iterator();
            while (taskIterator.hasNext()) {
                System.out.println(taskIterator.next().taskName);
            }
        }
    }

    public void retrieveAndPrintEnqueuedTasks() {
        synchronized (pqLock) {
            while (!priorityQueue.isEmpty()) {
                System.out.println(priorityQueue.poll().taskName);
                size.decrementAndGet();
            }
        }
    }*/

    private final class SchedulerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (isShutdown.get() && size.get() == 0) {
                    break;
                }

                Task task = null;
                synchronized (pqLock) {
                    task = priorityQueue.poll();
                }

                if (task == null) {
                    // rerun the loop: maybe queue is empty.
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } else {
//                    System.out.println("Task at top of PQ was " + task.taskName + " with launchTime = " + task.launchTime);

                    try {
                        long currentTimeMillis = System.currentTimeMillis();
                        // if current >= task.launchTime: run it.
                        if (currentTimeMillis >= task.launchTime) {
                            // using a new thread ???
//                            new Thread(task.runnable).start();
                            size.decrementAndGet();
                            System.out.println("Running task " + task.getTaskName());
                            task.runnable.run();
                        } else {
                            // put the task back
                            synchronized (pqLock) {
                                priorityQueue.add(task);
                            }
                            Thread.sleep(task.launchTime - currentTimeMillis);
                        }
                    } catch (InterruptedException e) {
                        // interrupted before sleep was over.
                        // maybe another task was added which could have a launchTime earlier.
                        // may simply loop again?
                    }
                }
            }
        }
    }

    @Override
    public void schedule(Runnable runnable, String taskName, long delayInMs) {
        if (this.isShutdown.get()) {
            System.out.println("Scheduler has shut down, cannot schedule any more");
        } else {
            // our priorityQueue is unbounded.
            // to implemented a bounded memory PQ, will need
            System.out.println(String.format("Scheduling %s with delayInMs = %d", taskName, delayInMs));
            long currentTimeMillis = System.currentTimeMillis();
            priorityQueue.add(new Task(runnable, taskName, currentTimeMillis + delayInMs));
            size.incrementAndGet();
            schedulerThread.interrupt();
        }
    }



    public static final class Task implements Comparable<Task> {

        public final Runnable runnable;
        private final String taskName;
        public final long launchTime;

        public Task(Runnable runnable, String taskName, long launchTime) {
            this.runnable = runnable;
            this.taskName = taskName;
            this.launchTime = launchTime;
        }

        @Override
        public int compareTo(Task o) {
            return Long.valueOf(this.launchTime).compareTo(Long.valueOf(o.launchTime));
        }

        public String getTaskName() {
            return taskName;
        }
    }
    private final PriorityQueue<Task> priorityQueue;
    public Scheduler() {
        priorityQueue = new PriorityQueue<>();
    }
}
