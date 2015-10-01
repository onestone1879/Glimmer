package boris.Glimmer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Boris on 2015/9/14.
 */
public class channelReader extends KeyThread {

    protected SocketChannel sc;

    /**
     * 初始化传入SelectionKey
     * @param key
     */
    public channelReader (SelectionKey key) {
        super(key);
        this.sc = (SocketChannel) key.channel();
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
        String s = readchannel();
        if (null != s) {
            System.out.println("[" + sc.getRemoteAddress().toString() + "]" + s + "[end]");
        }
    }

    /**
     * 读取客户端发来的信息
     * 多个并发线程可安全地使用套接字通道。尽管在任意给定时刻最多只能
     * 有一个线程进行读取和写入操作，但数据报通道支持并发的读写。
     * connect 和 finishConnect 方法是相互同步的，如果正在调用其中某个
     * 方法的同时试图发起读取或写入操作，则在该调用完成之前该操作被阻塞。
     * --------------------------------------------------------------------
     * TODO:bugfix:客户端发送消息过快可能导致信息丢失，原因是用“/”来作为
     * TODO:bugfix:消息终止符号时，后续连续发来的消息会被丢弃
     * --------------------------------------------------------------------
     * TODO:bugfix:当客户端强制关闭时多次抛出异常，原因未知，这是改为内
     * TODO:bugfix:置StringBuilder后新增的bug
     * --------------------------------------------------------------------
     * TODO:bugfix:当使用类内部的ByteBuffer时，读取会出错
     * --------------------------------------------------------------------
     * @throws IOException
     */
    protected String readchannel() throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(10);
        sc.read(buffer);
        byte[] data = buffer.array();
        sb.append(new String(data).trim());
        if (sb.indexOf("/") > -1) {
            sb.delete(sb.indexOf("/"), sb.length());
            String s = sb.toString();
            sb.delete(0, sb.length());
            return s;
        }
        return null;
    }

    /**
     * 对operate执行时抛出的异常进行处理
     * @param e
     */
    @Override
    protected void catchEx(IOException e) {
        System.out.println("错误代码"+ this.getClass().toString() + ":" + e.getMessage());
        key.cancel();
        if (key.channel() != null) {
            //真SB
            try {
                key.channel().close();
            }
            catch (IOException ee) {
                System.out.println("错误代码"+ this.getClass().toString() + ":" + ee.getMessage());
            }
        }
    }
}
