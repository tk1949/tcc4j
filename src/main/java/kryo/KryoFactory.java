package kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Serialization tool
 */
public class KryoFactory
{
    /**
     *
     */
    private static final ThreadLocal<Kryo> kryoLocal ;

    static
    {
        kryoLocal = ThreadLocal.withInitial(() ->
        {
            Kryo kryo = new Kryo();
            kryo.setReferences(true);
            kryo.setRegistrationRequired(false);
            Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        });
    }

    public static <T> byte[] writeToByteArray(T obj)
    {
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)
        ) {
            Kryo kryo = kryoLocal.get();
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readFromByteArray(byte[] byteArray)
    {
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
            Input input = new Input(byteArrayInputStream)
        ) {
            Kryo kryo = kryoLocal.get();
            return (T) kryo.readClassAndObject(input);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}