import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ChannelTest {

    @Test
    public void test5() throws Exception {
        Charset cs = Charset.forName("GBK");
        CharsetEncoder ce = cs.newEncoder();
        CharsetDecoder cd = cs.newDecoder();

        CharBuffer cbuf = CharBuffer.allocate(1024);
        cbuf.put("中国");
        cbuf.flip();

        // 编码
        ByteBuffer bBuf = ce.encode(cbuf);
        for (int i = 0; i < 4; i++) {
            System.out.println(bBuf.get());
        }
        // 解码
        bBuf.flip();
        CharBuffer dBuf = cd.decode(bBuf);
        System.out.println(dBuf.toString());
    }


    @Test
    public void test4() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("1.txt", "rw");
        FileChannel channel = raf.getChannel();

        ByteBuffer buf1 = ByteBuffer.allocate(512);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);

        //分散读取
        ByteBuffer[] bufs = {buf1, buf2};
        channel.read(bufs);

        for(ByteBuffer byteBuffer: bufs){
            byteBuffer.flip();
        }

        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        // 聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
        FileChannel channel1 = raf2.getChannel();

        channel1.write(bufs);
    }

    @Test
    public void test() throws Exception {
        FileInputStream fis = new FileInputStream("resources/1.jpg");
        FileOutputStream fos = new FileOutputStream("resources/2.jpg");

        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (inChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }
        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();
    }

    @Test  // 使用直接缓冲区完成文件的复制，只有ByteBuffer支持
    public void test2() throws Exception {
        FileChannel inChannel = FileChannel.open(Paths.get("resources/1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("resources/2.jpg"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);
        // 内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());
        // 直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();
    }

    @Test // 通道之间数据传输(直接缓冲区方式)
    public void test3() throws Exception {
        FileChannel inChannel = FileChannel.open(Paths.get("resources/1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("resources/2.jpg"), StandardOpenOption.WRITE,
                StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

        inChannel.transferTo(0, inChannel.size(), outChannel);
//        outChannel.transferFrom(inChannel, 0, inChannel.size());

        inChannel.close();
        outChannel.close();
    }
}
