package boris.Glimmer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Boris on 2015/9/14.
 */
public class channelReader extends KeyThread {
    /**
     * 初始化传入SelectionKey
     * @param key
     */
    public channelReader (SelectionKey key) {
        //this.key = key;
        super(key);
    }

    /**
     * 读取客户端发来的信息
     * 多个并发线程可安全地使用套接字通道。尽管在任意给定时刻最多只能
     * 有一个线程进行读取和写入操作，但数据报通道支持并发的读写。
     * connect 和 finishConnect 方法是相互同步的，如果正在调用其中某个
     * 方法的同时试图发起读取或写入操作，则在该调用完成之前该操作被阻塞。
     */
    @Override
    protected void operate() throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        sc.read(buffer);
        byte[] data = buffer.array();
        StringBuilder sb = (StringBuilder) key.attachment();
        sb.append(new String(data).trim());
        if (sb.indexOf("/") > -1) {
            sb.delete(sb.indexOf("/"), sb.length());
            System.out.println(sc.getRemoteAddress().toString() + sb.toString() + "-end");
            sb.delete(0, sb.length());
        }
    }

    /**
     * 对operate执行时抛出的异常进行处理
     * @param e
     */
    @Override
    protected void catchEx(IOException e) {
        System.out.println(e.getMessage());
        key.cancel();
        if (key.channel() != null) {
            //真SB
            try {
                key.channel().close();
            }
            catch (IOException ee) {
                System.out.println(ee.getMessage());
            }
        }
    }
}
