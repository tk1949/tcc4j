package network.message.lock;

import core.Lock;
import core.LockPool;
import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorrowLockMessage extends BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(BorrowLockMessage.class);

    private String lockId;
    private long   waitingTime;

    public BorrowLockMessage(String lockId, long waitingTime)
    {
        this.lockId      = lockId;
        this.waitingTime = waitingTime;
    }

    @Override
    public void consume(Channel channel)
    {
        Lock lock = LockPool.find(lockId);
        boolean result = lock != null && lock.lock();

        BorrowResultMessage message = new BorrowResultMessage(lockId, result);
        message.transmit(channel);

        logger.info(channel.remoteAddress() + " -> 分布式锁获取 -> " + lockId);
    }
}