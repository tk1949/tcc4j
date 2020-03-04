package core;

import lombok.Getter;
import tools.Timestamp;

@Getter
public final class Lock
{
    private String lockId;
    private long   ttl;
    private int    amount;
    private int    remainingAmount;

    public Lock(String lockId, long ttl, int amount)
    {
        this.lockId = lockId;
        this.ttl    = ttl == 0 ? Long.MAX_VALUE : Timestamp.cacheTimeMillis() + ttl;
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
}