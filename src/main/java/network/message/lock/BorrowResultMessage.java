package network.message.lock;

import io.netty.channel.Channel;
import network.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorrowResultMessage extends BaseMessage
{
    private static final Logger logger = LoggerFactory.getLogger(BorrowResultMessage.class);

    private String  lockId;
    private boolean result;

    public BorrowResultMessage(String lockId, boolean result)
    {
        this.lockId = lockId;
        this.result = result;
    }

    @Override
    public void consume(Channel channel)
    {
        logger.info(channel.remoteAddress() + " -> 分布式锁获取 -> " + lockId + " ,结果 -> " + result);
    }
}