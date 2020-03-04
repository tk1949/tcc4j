package network.message.ack;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import kryo.KryoFactory;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PongMessage extends BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(PongMessage.class);

    public static final PongMessage pong = new PongMessage();

    private byte[] code;

    private PongMessage()
    {
        this.code = KryoFactory.writeToByteArray(this);
    }

    @Override
    public void consume(Channel channel)
    {
        logger.debug("PongMessage -> ack form {}", channel.remoteAddress());
    }

    @Override
    public void transmit(Channel channel)
    {
        channel.writeAndFlush(Unpooled.wrappedBuffer(code));
    }
}