package tools;

import java.util.Random;

public final class IdWorker
{
    public static final IdWorker build = new IdWorker();

    private static final long WORKER_ID_BITS       = 5L;
    private static final long DATACENTER_ID_BITS   = 5L;
    private static final long MAX_WORKER_ID        = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID    = ~(-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_BITS        = 12L;
    private static final long WORKER_ID_SHIFT      = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT  = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private static final long SEQUENCE_MASK        = ~(-1L << SEQUENCE_BITS);

    private static final Random r = new Random();

    private final long workerId;
    private final long datacenterId;
    private final long idepoch;

    private long lastTimestamp = -1L;
    private long sequence;

    public IdWorker()
    {
        this(1344322705519L);
    }

    public IdWorker(long idepoch)
    {
        this(r.nextInt((int) MAX_WORKER_ID), r.nextInt((int) MAX_DATACENTER_ID), 0, idepoch);
    }

    public IdWorker(long workerId, long datacenterId, long sequence)
    {
        this(workerId, datacenterId, sequence, 1344322705519L);
    }

    public IdWorker(long workerId, long datacenterId, long sequence, long idepoch)
    {
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        this.sequence = sequence;
        this.idepoch = idepoch;
        if (workerId < 0 || workerId > MAX_WORKER_ID)
        {
            throw new IllegalArgumentException("workerId is illegal: " + workerId);
        }
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID)
        {
            throw new IllegalArgumentException("datacenterId is illegal: " + workerId);
        }
        if (idepoch >= System.currentTimeMillis())
        {
            throw new IllegalArgumentException("idepoch is illegal: " + idepoch);
        }
    }

    private synchronized long nextId()
    {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp)
        {
            throw new IllegalStateException("Clock moved backwards.");
        }
        if (lastTimestamp == timestamp)
        {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0)
            {
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        else
        {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - idepoch) << TIMESTAMP_LEFT_SHIFT)
               |(datacenterId         << DATACENTER_ID_SHIFT )
               |(workerId             << WORKER_ID_SHIFT     )
               | sequence;
    }


    public long getIdTimestamp(long id)
    {
        return idepoch + (id >> TIMESTAMP_LEFT_SHIFT);
    }

    private long tilNextMillis(long lastTimestamp)
    {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp)
        {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen()
    {
        return System.currentTimeMillis();
    }
}
