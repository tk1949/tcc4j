package network.message;

import io.netty.channel.Channel;

import java.io.Serializable;

public interface BaseMessage extends Serializable
{
    void consume(Channel channel);

    void transmit(Channel channel);
}