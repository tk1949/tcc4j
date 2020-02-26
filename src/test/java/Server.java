import io.netty.channel.nio.NioEventLoopGroup;
import network.Mediator;

public class Server
{
    public static void main(String[] args) throws InterruptedException
    {
        Mediator mediator = new Mediator(new NioEventLoopGroup(), new NioEventLoopGroup(), 10000);
        mediator.start();
    }
}