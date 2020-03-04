package core;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class LockPool
{
    private static final Map<String, Lock> lockMaps = new ConcurrentSkipListMap<>();

    public static boolean add(Lock lock)
    {
        return ! lock.equals(lockMaps.putIfAbsent(lock.getLockId(), lock));
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

    }
}