package tools;

import scheduler.TaskScheduler;

import java.util.concurrent.TimeUnit;

public final class Timestamp
{
    private static volatile long now;

    static
    {
        now = System.currentTimeMillis();
        TaskScheduler.scheduleAtFixedRate(
                () -> now = System.currentTimeMillis(),
                0L, 1L, TimeUnit.MILLISECONDS
        );
    }

    public static long cacheTimeMillis()
    {
        return now;
    }

    public static long currentTimeMillis()
    {
        return System.currentTimeMillis();
    }
}