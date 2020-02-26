package network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import kryo.KryoFactory;
import network.message.BaseMessage;

/**
 * Message encoder
 */
public class Encoder extends MessageToByteEncoder<BaseMessage>
{
    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMessage in, ByteBuf out)
    {
        byte[] bytes = KryoFactory.writeToByteArray(in);
        out.writeBytes(bytes);
    }
}