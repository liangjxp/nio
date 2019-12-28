import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class BlockIOTest {
    @Test
    public void client() throws Exception {
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        FileChannel inChannel = FileChannel.open(Paths.get("resources/1.jpg"), StandardOpenOption.READ);
        // 分配缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        // 读取本地文件，并发送到服务端
        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }
        inChannel.close();
        sChannel.close();
    }

    @Test
    public void server() throws Exception {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        FileChannel outChannel = FileChannel.open(Paths.get("resources/2.jpg"), StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);
        // 绑定连接
        ssChannel.bind(new InetSocketAddress(9898));
        // 获取客户端连接通道
        SocketChannel sChannel = ssChannel.accept();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }
        sChannel.close();
        ssChannel.close();
        outChannel.close();
    }
}
