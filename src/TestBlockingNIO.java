import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

public class TestBlockingNIO {
    @Test
    public void client() throws Exception {
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        // 切换非阻塞模式
        sChannel.configureBlocking(false);
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(new Date().toString().getBytes());
        buf.flip();
        sChannel.write(buf);
        buf.clear();

        sChannel.close();
    }
    @Test
    public void server() throws Exception {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false);
        ssChannel.bind(new InetSocketAddress(9898));
        //获取连接器
        Selector selector = Selector.open();
        // 将通道注册到选择器上，并指定监听接收事件
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 轮询式获取选择器上已经"准备就绪"的时间
        while(selector.select() > 0){
            // 获取当前选择器中所有注册的选择键
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while(it.hasNext()){
                // 获取准备就绪的事件
                SelectionKey sk = it.next();
                if(sk.isAcceptable()){
                    // 获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();
                    sChannel.configureBlocking(false);
                    // 将该通道注册到选择器上
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if(sk.isReadable()){
                    SocketChannel sChannel = (SocketChannel) sk.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int len = 0;
                    while((len = sChannel.read(buf)) > 0){
                        buf.flip();
                        System.out.println(new String(buf.array(), 0, len));
                        buf.clear();
                    }
                }
                // 取消选择键
                it.remove();
            }
        }

    }
}
