package network.message.lock;

import core.Lock;
import core.LockPool;
import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateLockMessage implements BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(CreateLockMessage.class);

    private String lockId;
    private long   ttl;
    private int    amount;

    public CreateLockMessage(String lockId, long ttl, int amount)
    {
        this.lockId = lockId;
        this.ttl    = ttl;
        this.amount = amount;
    }

    @Override
    public void consume(Channel channel)
    {
        Lock lock = new Lock(lockId, ttl, amount);
        boolean add = LockPool.add(lock);

        CreateResultMessage message = new CreateResultMessage(lockId, add);
        message.transmit(channel);

        logger.info(channel.remoteAddress() + " -> 分布式锁创建 -> " + lockId + " ,数量 -> " + amount);
    }

    @Override
    public void transmit(Channel channel)
    {
        channel.writeAndFlush(this);
    }
}