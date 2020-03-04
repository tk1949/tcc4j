package network.message;

import io.netty.channel.Channel;

import java.io.Serializable;

public abstract class BaseMessage implements Serializable
{
    public abstract void consume(Channel channel);

    public void transmit(Channel channel)
    {
        channel.writeAndFlush(this);
    }
}