import org.junit.Test;

import javax.crypto.spec.PSource;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class TestPipe {
    @Test
    public void test1() throws Exception{
        Pipe pipe = Pipe.open();
        ByteBuffer buf = ByteBuffer.allocate(1024);

        Pipe.SinkChannel sinkChannel = pipe.sink();
        buf.put("通过单向管道发送数据".getBytes());
        buf.flip();
        sinkChannel.write(buf);

        Pipe.SourceChannel sourceChannel = pipe.source();
        buf.flip();
        int len = sourceChannel.read(buf);
        System.out.println(new String(buf.array(), 0, len));

        sourceChannel.close();
        sinkChannel.close();
    }
}
