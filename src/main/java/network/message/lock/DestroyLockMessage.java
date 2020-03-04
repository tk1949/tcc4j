package network.message.lock;

import core.LockPool;
import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DestroyLockMessage implements BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(DestroyLockMessage.class);

    private String lockId;

    public DestroyLockMessage(String lockId)
    {
        this.lockId = lockId;
    }

    @Override
    public void consume(Channel channel)
    {
        boolean remove = LockPool.remove(lockId);

        DestroyResultMessage message = new DestroyResultMessage(lockId, remove);
        message.transmit(channel);

        logger.info(channel.remoteAddress() + " -> 分布式锁销毁 -> " + lockId);
    }

    @Override
    public void transmit(Channel channel)
    {
        channel.writeAndFlush(this);
    }
}