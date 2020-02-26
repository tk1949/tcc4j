package network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import kryo.KryoFactory;

import java.util.List;

/**
 * Message decoder
 */
public class Decoder extends ByteToMessageDecoder
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
    {
        int length = in.readableBytes();
        byte[] code = new byte[length];
        in.readBytes(code);
        out.add(KryoFactory.readFromByteArray(code));
    }
}