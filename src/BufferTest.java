import org.junit.Test;

import java.nio.ByteBuffer;

public class BufferTest {
    String str = "abcde";
    @Test
    public void test1(){
        // 分配一个指定大小的缓冲池
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        // 存入数据到缓冲区中
        buffer.put(str.getBytes());
        // 切换成读取数据模式
        buffer.flip();
        // get()读取缓冲区数据
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst);
        System.out.println(new String(dst, 0, dst.length));
        // rewind() 可重复读
        buffer.rewind();
        // clear() 清空缓冲区，三个指针复原，但数据还是存在
        buffer.clear();
        // 判断缓冲区是否还有剩余数据，输出还可以操作的数量
        if(buffer.hasRemaining()){
            System.out.println(buffer.remaining());
        }
        buffer.isDirect();



    }

}
