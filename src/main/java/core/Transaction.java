package core;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import network.message.BaseMessage;
import network.message.tx.CancelMessage;
import network.message.tx.CommitMessage;
import tools.Timestamp;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@Getter
public final class Transaction implements Closeable
{
    private String       txId;
    private long         ttl;
    private int          targetSize;
    private int          replySize;
    private int          status;
    private List<String> nodes;

    private transient ChannelGroup partner;

    public Transaction(String txId, long ttl, int targetSize, Channel sponsor)
    {
        this.txId       = txId;
        this.ttl        = Timestamp.cacheTimeMillis() + ttl + 1000L;
        this.targetSize = targetSize;
        this.replySize  = 0;

        this.nodes      = new LinkedList<>();
        this.partner    = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        this.nodes.add(sponsor.remoteAddress().toString());
        this.partner.add(sponsor);
    }

    public synchronized void addChannel(Channel channel)
    {
        nodes.add(channel.remoteAddress().toString());
        partner.add(channel);
        if (targetSize == ++replySize)
        {
            commit();
        }
    }

    private void commit()
    {
        CommitMessage message = new CommitMessage(txId);
        transmit(message);
        status = 1;
        close();
    }

    public void cancel()
    {
        CancelMessage message = new CancelMessage(txId);
        transmit(message);
        status = 0;
        close();
    }

    private void transmit(BaseMessage message)
    {
        partner.writeAndFlush(message);
    }

    @Override
    public void close()
    {
        partner.clear();
        TransactionPool.remove(txId);
    }

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }
}