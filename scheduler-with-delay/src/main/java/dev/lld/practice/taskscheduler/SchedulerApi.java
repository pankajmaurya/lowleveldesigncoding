package dev.lld.practice.taskscheduler;

public interface SchedulerApi {

    void schedule(Runnable runnable, String taskName, long delayInMs);
}
