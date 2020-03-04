package network.message.ack;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import kryo.KryoFactory;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingMessage extends BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(PingMessage.class);

    public static final PingMessage ping = new PingMessage();

    private byte[] code;

    private PingMessage()
    {
        this.code = KryoFactory.writeToByteArray(this);
    }

    @Override
    public void consume(Channel channel)
    {
        logger.debug("PingMessage -> ack form {}", channel.remoteAddress());
    }

    @Override
    public void transmit(Channel channel)
    {
        channel.writeAndFlush(Unpooled.wrappedBuffer(code));
    }
}