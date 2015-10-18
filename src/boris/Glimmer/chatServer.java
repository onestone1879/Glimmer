package boris.Glimmer;

import com.mongodb.MongoClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.*;

/**
 * Created by Boris on 2015/9/12.
 */
public final class chatServer {
    //通道管理器
    private Selector selector;
    private int sPort;
    private MongoClient mc;
    //用于将消息传递给指定的用户
    private Map<String, SelectionKey> clients;

    //用于key重复触发检测，如果当前有线程正在处理这个key的事件，则设置标记为false
    //在run函数中如果检测到mark为false则不进行操作
    //我不知道写在这里好不好
    private static List<SelectionKey> keyLists = Collections.synchronizedList(
            new ArrayList<SelectionKey>()
    );

    /**
     * 获得一个serverSocket通道，并对通道做初始化
     */
    public chatServer(int port, MongoClient mc) throws IOException {
        this.sPort = port;
        //只能在内部访问
        this.clients = Collections.synchronizedMap(new HashMap<String, SelectionKey>());;
        this.mc = mc;

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        this.selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 轮训方式监听网络连接事件，由外部调用
     * TODO:改为使用线程池
     * @throws IOException
     */
    public void listen() throws Exception {
        System.out.println("服务端启动成功，端口：" + sPort);
        while (true) {
            int num = selector.select();

            Set selectedKeys = selector.selectedKeys();
            Iterator it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey)it.next();
                it.remove();

                //检验当前是否正在处理该key
                if (keyLists.contains(key)) {
                    System.out.println("已包含:" + key.toString());
                    //fixme:某个变量（可能是selector、keyLists之一）锁死，使线程结束后不能将key键从keyLists中删除
                    Thread.sleep(1);
                    continue;
                } else {
                    keyLists.add(key);
                    System.out.println("正在处理:" + key.toString());
                }

                if (key.isAcceptable()) {
                    //当有新连接时，使用channelRegister将key注册到selector中
                    //同时将channelReader挂载到key中，以处理key的readable消息
                    //将MongoClient引用传递给channelReader
                    new Thread(new channelRegister(key, selector, mc, clients, keyLists)).start();
                }
                else if (key.isReadable()) {
                    Thread t = new Thread((Runnable)key.attachment());
                    t.start();
                }
            }
        }
    }
}
