package core;

import scheduler.TaskScheduler;
import tools.Timestamp;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

public class LockPool
{
    private static final Map<String, Lock> lockMaps = new ConcurrentSkipListMap<>();

    static
    {
        TaskScheduler.scheduleAtFixedRate(LockPool::shuffle, 1000L, 1000L, TimeUnit.MILLISECONDS);
    }
    
    public static boolean add(Lock lock)
    {
        return !lock.equals(
                lockMaps.putIfAbsent(lock.getLockId(), lock)
        );
    }

    public static Lock find(String lockId)
    {
        return lockMaps.get(lockId);
    }

    public static boolean remove(String lockId)
    {
        return lockMaps.remove(lockId) != null;
    }

    private static void shuffle()
    {
        long now = Timestamp.cacheTimeMillis();
        lockMaps.values()
                .parallelStream()
                .filter(lock -> lock.getTtl() != 0 && lock.getTtl() < now)
                .forEach(Lock::close);
    }
}