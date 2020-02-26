package network.message.ack;

import io.netty.channel.Channel;
import kryo.KryoFactory;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PongMessage implements BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(PongMessage.class);

    public static final byte[] pong = KryoFactory.writeToByteArray(new PongMessage());

    @Override
    public void consume(Channel channel)
    {
        logger.debug("PongMessage -> ack form {}", channel.remoteAddress());
    }

    @Override
    public void transmit(Channel channel)
    {
    }
}