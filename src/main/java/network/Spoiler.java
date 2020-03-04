package network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import network.message.BaseMessage;
import network.message.ack.PingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Distributed transaction caller
 */
public class Spoiler
{
    private static final Logger logger = LoggerFactory.getLogger(Spoiler.class);

    private EventLoopGroup boos;
    private String         ip;
    private int            port;
    private Bootstrap      boot;
    private int            reconnection;
    private Channel        channel;

    public Spoiler(EventLoopGroup boss, String ip, int port)
    {
        this.boos = boss;
        this.ip   = ip;
        this.port = port;
        this.boot = new Bootstrap();
    }

    public void start()
    {
        try
        {
            channel = boot.group(boos)
                          .remoteAddress(ip, port)
                          .channel(NioSocketChannel.class)
                          .option(ChannelOption.TCP_NODELAY, true)
                          .handler(new ChannelInitializer<SocketChannel>()
                          {
                              @Override
                              protected void initChannel(SocketChannel ch)
                              {
                                  ch.pipeline().addLast(
                                          new IdleStateHandler(0L, 0L, 5000L, TimeUnit.MILLISECONDS),
                                          new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 8, 0, 8),
                                          new LengthFieldPrepender(8),
                                          new Decoder(),
                                          new Encoder(),
                                          new FrameHandler()
                                  );
                              }
                          }).connect().sync().channel();
        }
        catch (Exception e)
        {
            logger.error("Spoiler -> start {}", e.getMessage(), e);
            reconnection = 8;
        }
    }

    public void stop()
    {
        channel.close();
        boos.shutdownGracefully();
    }

    public void submission(byte[] msg)
    {
        if (reconnection == 0)
        {
            channel.writeAndFlush(Unpooled.wrappedBuffer(msg));
        }
        else
        {
            logger.error("Spoiler -> submission error");
        }
    }

    public void submission(BaseMessage msg)
    {
        if (reconnection == 0)
        {
            msg.transmit(channel);
        }
        else
        {
            logger.error("Spoiler -> submission error");
        }
    }

    public boolean isActive()
    {
        return reconnection == 0;
    }

    private class FrameHandler extends SimpleChannelInboundHandler<BaseMessage>
    {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, BaseMessage msg)
        {
            try
            {
                msg.consume(ctx.channel());
            }
            catch (Exception e)
            {
                logger.error("Spoiler -> channelRead0 {}", e.getMessage(), e);
            }
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx)
        {
            if (reconnection++ < 10)
            {
                ctx.channel().eventLoop().schedule(() ->
                {
                    try
                    {
                        channel = boot.connect().addListener((ChannelFutureListener) future ->
                        {
                            if (future.cause() == null)
                            {
                                reconnection = 0;
                            }
                        }).sync().channel();
                    }
                    catch (InterruptedException e)
                    {
                        logger.error("Spoiler -> channelUnregistered {}", e.getMessage(), e);
                    }
                }, 5000L, TimeUnit.MILLISECONDS);
            }
            else
            {
                stop();
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
        {
            if (evt instanceof IdleStateEvent)
            {
                IdleStateEvent e = (IdleStateEvent) evt;
                if (e.state() == IdleState.ALL_IDLE)
                {
                    PingMessage.ping.transmit(ctx.channel());
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
        {
            ctx.close();
            logger.error("Spoiler -> exceptionCaught {}", cause.getMessage(), cause);
        }
    }
}