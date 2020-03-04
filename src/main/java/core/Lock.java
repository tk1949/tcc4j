package core;

import lombok.Getter;
import tools.Timestamp;

import java.io.Closeable;

@Getter
public final class Lock implements Closeable
{
    private String lockId;
    private long   ttl;
    private int    amount;
    private int    remainingAmount;

    public Lock(String lockId, long ttl, int amount)
    {
        this.lockId = lockId;
        this.ttl    = ttl == 0L ? 0L : Timestamp.cacheTimeMillis() + ttl;
        this.amount = this.remainingAmount = amount;
    }

    public synchronized boolean lock()
    {
        if (remainingAmount > 0)
        {
            remainingAmount--;
            return true;
        }
        else
        {
            return false;
        }
    }

    public synchronized boolean unlock()
    {
        if (remainingAmount < amount)
        {
            remainingAmount++;
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return this == obj || obj instanceof Lock && this.lockId.equals(((Lock) obj).lockId);
    }

    @Override
    public void close()
    {
        LockPool.remove(lockId);
    }
}