package network.message.lock;

import core.Lock;
import core.LockPool;
import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnLockMessage extends BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(ReturnLockMessage.class);

    private String lockId;

    public ReturnLockMessage(String lockId)
    {
        this.lockId = lockId;
    }

    @Override
    public void consume(Channel channel)
    {
        Lock lock = LockPool.find(lockId);
        boolean result = lock != null && lock.unlock();

        ReturnResultMessage message = new ReturnResultMessage(lockId, result);
        message.transmit(channel);

        logger.info(channel.remoteAddress() + " -> 分布式锁归还 -> " + lockId);
    }
}