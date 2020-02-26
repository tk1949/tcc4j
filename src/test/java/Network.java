import io.netty.channel.nio.NioEventLoopGroup;
import network.Spoiler;
import network.message.ConfirmMessage;
import network.message.TryMessage;

public class Network
{
    public static void main(String[] args) throws InterruptedException
    {
        Spoiler spoiler = new Spoiler(new NioEventLoopGroup(), "127.0.0.1", 10000);
        spoiler.start();
        Spoiler spoiler1 = new Spoiler(new NioEventLoopGroup(), "127.0.0.1", 10000);
        spoiler1.start();
        Spoiler spoiler2 = new Spoiler(new NioEventLoopGroup(), "127.0.0.1", 10000);
        spoiler2.start();

        spoiler.submission(new TryMessage("001", 2000, 2));
        spoiler.submission(new TryMessage("002", 2000, 3));

        Thread.sleep(1000);

        spoiler1.submission(new ConfirmMessage("001"));
        spoiler1.submission(new ConfirmMessage("002"));

        spoiler2.submission(new ConfirmMessage("002"));
        spoiler2.submission(new ConfirmMessage("001"));
    }
}