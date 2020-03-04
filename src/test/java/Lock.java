import io.netty.channel.nio.NioEventLoopGroup;
import network.Spoiler;
import network.message.lock.*;
import network.message.tx.ConfirmMessage;
import network.message.tx.TryMessage;

public class Lock
{
    public static void main(String[] args) throws InterruptedException
    {
        Spoiler spoiler = new Spoiler(new NioEventLoopGroup(), "127.0.0.1", 10000);
        spoiler.start();

        spoiler.submission(new CreateLockMessage("001", 1000, 1));

        Thread.sleep(1000);

        spoiler.submission(new BorrowLockMessage("001", 10));
        spoiler.submission(new BorrowLockMessage("001", 10));

        spoiler.submission(new ReturnLockMessage("001"));
        spoiler.submission(new ReturnLockMessage("001"));


        spoiler.submission(new BorrowLockMessage("001", 10));

        spoiler.submission(new ReturnLockMessage("001"));

        Thread.sleep(1000);

        spoiler.submission(new DestroyLockMessage("001"));
    }
}