package network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Distributed transaction middleman
 */
public class Mediator
{
    private static final Logger logger = LoggerFactory.getLogger(Mediator.class);

    private EventLoopGroup boos;
    private EventLoopGroup worker;
    private int            port;
    private ChannelGroup   channels;

    public Mediator(EventLoopGroup boss, EventLoopGroup worker, int port)
    {
        this.boos     = boss;
        this.worker   = worker;
        this.port     = port;
        this.channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    public void start() throws InterruptedException
    {
        ServerBootstrap b = new ServerBootstrap();
        b.group(boos, worker)
         .channel(NioServerSocketChannel.class)
         .option(ChannelOption.SO_BACKLOG, 1024 * 2)
         .childHandler(new ChannelInitializer<SocketChannel>()
         {
             @Override
             protected void initChannel(SocketChannel ch)
             {
                 ch.pipeline().addLast(
                         new ReadTimeoutHandler(60),
                         new WriteTimeoutHandler(60),
                         new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 8, 0, 8),
                         new LengthFieldPrepender(8),
                         new Decoder(),
                         new Encoder(),
                         new FrameHandler()
                 );
             }
         }).bind(port).sync().channel();
    }

    public void stop()
    {
        boos.shutdownGracefully();
        worker.shutdownGracefully();
    }

    public void push(byte[] msg)
    {
        channels.writeAndFlush(Unpooled.wrappedBuffer(msg));
    }

    private class FrameHandler extends SimpleChannelInboundHandler<BaseMessage>
    {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx)
        {
            channels.add(ctx.channel());
            ctx.fireChannelRegistered();
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, BaseMessage msg)
        {
            try
            {
                msg.consume(ctx.channel());
            }
            catch (Exception e)
            {
                logger.error("Mediator -> channelRead0 {}", e.getMessage(), e);
            }
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx)
        {
            channels.remove(ctx.channel());
            ctx.fireChannelUnregistered();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        {
            ctx.close();
            logger.error("Mediator -> exceptionCaught {}", cause.getMessage(), cause);
        }
    }
}