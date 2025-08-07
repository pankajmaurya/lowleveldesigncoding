package dev.lld.practice.taskscheduler;

public class Demo {
    public static void main(String[] args) throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        scheduler.start();

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("This is runnable 1, delayInMs = 5000");
            }
        }, "Task1", 5000);

        System.out.println("Main thread sleeping for 1000 ms");
        Thread.sleep(1000L);
        System.out.println("Main thread woke up after 1000 ms");

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("This is runnable 2, delayInMs = 1000");
            }
        }, "Task2", 1000);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("This is runnable 3, delayInMs = 2000");
            }
        }, "Task3", 2000);

        System.out.println("Main thread sleeping for 1500 ms");
        Thread.sleep(1500L);
        System.out.println("Main thread woke up after 1500 ms");

        // runnable 3 should get executed at 1700 Ms, so should appear before runnable 2
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("This is runnable 4, delayInMs = 200");
            }
        }, "Task4", 200);

        scheduler.shutdown();

//        scheduler.printEnqueuedTasks();
//        scheduler.retrieveAndPrintEnqueuedTasks();
    }
}
