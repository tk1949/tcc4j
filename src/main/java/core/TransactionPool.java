package core;

import scheduler.TaskScheduler;
import tools.Timestamp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class TransactionPool
{
    private static final Map<String, Transaction> txMaps = new ConcurrentSkipListMap<>();

    static
    {
        TaskScheduler.scheduleAtFixedRate(TransactionPool::shuffle, 1000L, 1000L, TimeUnit.MILLISECONDS);
    }

    public static void add(Transaction tx)
    {
        txMaps.put(tx.getTxId(), tx);
    }

    public static Transaction find(String txId)
    {
        return txMaps.get(txId);
    }

    public static Transaction remove(String txId)
    {
        return txMaps.remove(txId);
    }

    public static void shuffle()
    {
        long now = Timestamp.cacheTimeMillis();
        txMaps.values()
              .parallelStream()
              .filter(tx -> tx.getTtl() < now)
              .forEach(Transaction::cancel);
    }
}