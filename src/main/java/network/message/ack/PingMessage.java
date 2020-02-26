package network.message.ack;

import io.netty.channel.Channel;
import kryo.KryoFactory;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingMessage implements BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(PingMessage.class);

    public static final byte[] ping = KryoFactory.writeToByteArray(new PingMessage());

    @Override
    public void consume(Channel channel)
    {
        logger.debug("PingMessage -> ack form {}", channel.remoteAddress());
    }

    @Override
    public void transmit(Channel channel)
    {
    }
}