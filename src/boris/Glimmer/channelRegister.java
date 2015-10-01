package boris.Glimmer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by Boris on 2015/9/17.
 * 接受新的客户端连接，并验证身份
 */
public final class channelRegister extends KeyThread {
    private Selector s;

    public channelRegister (SelectionKey key, Selector s) {
        super(key);
        this.s = s;
    }

    /**
     * 处理客户端连接事件
     * 思考：这个是否需要多线程，以及多线程涉及的selector同步问题
     * s选择器自身可由多个并发线程安全使用，但是其键集并非如此。
     * 键集只在主线程中处理，不涉及并发问题
     * TODO:注册用户信息，与数据库通信
     * @throws IOException
     */
    @Override
    protected void operate() throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        SelectionKey newKey = sc.register(s, SelectionKey.OP_READ);
        newKey.attach(new channelReader(newKey));
        System.out.println(sc.getRemoteAddress().toString()+"已连接服务器");
    }

    @Override
    protected void catchEx(IOException e) {
        //先这样写
        System.out.println("错误代码"+ this.getClass().toString() + ":" + e.getMessage());
    }
}
