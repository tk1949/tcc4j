package network.message.tx;

import core.Transaction;
import core.TransactionPool;
import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * step 2
 */
public class ConfirmMessage extends BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(ConfirmMessage.class);

    private String txId;

    public ConfirmMessage(String txId)
    {
        this.txId = txId;
    }

    @Override
    public void consume(Channel channel)
    {
        Transaction tx = TransactionPool.find(txId);
        if (tx == null)
        {
            logger.error("ConfirmMessage -> Lookup transaction does not exist, txId : {}", txId);
            return;
        }
        tx.addChannel(channel);
        logger.info(channel.remoteAddress() + " -> 事务一致 -> " + txId);
    }
}