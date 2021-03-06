package network.message.tx;

import core.Transaction;
import core.TransactionPool;
import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * step 1
 */
public class TryMessage extends BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(TryMessage.class);

    private String txId;
    private long   ttl;
    private int    size;

    public TryMessage(String txId, long ttl, int size)
    {
        this.txId = txId;
        this.ttl  = ttl;
        this.size = size;
    }

    @Override
    public void consume(Channel channel)
    {
        TransactionPool.add(new Transaction(txId, ttl, size, channel));
        logger.info(channel.remoteAddress() + " -> 事务创建 -> " + txId);
    }
}