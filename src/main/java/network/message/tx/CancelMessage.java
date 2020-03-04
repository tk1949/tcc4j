package network.message.tx;

import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * step 3.2
 */
public class CancelMessage implements BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(CancelMessage.class);

    private String txId;

    public CancelMessage(String txId)
    {
        this.txId = txId;
    }

    @Override
    public void consume(Channel channel)
    {
        logger.info(channel.remoteAddress() + " -> 事务取消 -> " + txId);
    }

    @Override
    public void transmit(Channel channel)
    {
        channel.writeAndFlush(this);
    }
}