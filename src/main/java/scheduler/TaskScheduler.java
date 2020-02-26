package scheduler;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskScheduler
{
    private static final ScheduledThreadPoolExecutor executor;

    static
    {
        executor = new ScheduledThreadPoolExecutor(2/*, r ->
        {
            Thread thread = new Thread("TaskScheduler");
            thread.setDaemon(true);
            thread.setPriority(Thread.MAX_PRIORITY);
            return thread;
        }*/);
    }

    public static void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
    {
        executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
}