package boris.Glimmer;

import com.mongodb.MongoClient;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

/**
 * Created by Boris on 2015/9/17.
 * 接受新的客户端连接，并验证身份
 */
public final class channelRegister extends KeyThread {
    private Selector s;

    public channelRegister (SelectionKey key, Selector s, MongoClient mc,
                            Map<String, SelectionKey> cli, List<SelectionKey> kl) {
        super(key, mc, cli, kl);
        this.s = s;
    }

    /**
     * 处理客户端连接事件
     * 思考：这个是否需要多线程，以及多线程涉及的selector同步问题
     * s选择器自身可由多个并发线程安全使用，但是其键集并非如此。
     * 键集只在主线程中处理，不涉及并发问题
     * @throws IOException
     */
    @Override
    protected void operate() throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel sc = ssc.accept();
        System.out.println("channelRegister:已获取客户端SocketChannel");
        sc.configureBlocking(false);
        System.out.println("channelRegister:已将SocketChannel设为非阻塞");
        SelectionKey newKey = sc.register(s, SelectionKey.OP_READ);
        System.out.println("channelRegister:已向selector注册SocketChannel");
        //将新连接的客户端交给LoginOrRegist处理
        //将处理key的类挂载到key的附件中，因为每个channelReader实例都存储对应key的当前读取值，因此其不为静态
        newKey.attach(new LoginOrRegist(newKey, mc, clients, keylists));
        System.out.println(sc.getRemoteAddress().toString()+"已连接服务器，传递给lor处理");
    }

    @Override
    protected void catchEx(IOException e) {
        //先这样写
        System.out.println("错误代码"+ this.getClass().toString() + ":" + e.getMessage());
    }
}
