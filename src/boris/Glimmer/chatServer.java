package boris.Glimmer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Boris on 2015/9/12.
 */
public class chatServer {
    //通道管理器
    private Selector selector;
    private int sPort;
    //这样做可以使clients的存储方式多样化
    //TODO:还未实现登录用户管理功能,未启用此变量
    private Map<SelectionKey, StringBuilder> clients;

    /**
     * 获得一个serverSocket通道，并对通道做初始化
     */
    public chatServer(int port, Map<SelectionKey, StringBuilder> cli) throws IOException {
        this.sPort = port;
        this.clients = cli;

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        this.selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 轮训方式监听网络连接事件，由外部调用
     * @throws IOException
     */
    public void listen() throws IOException {
        System.out.println("服务端启动成功，端口：" + sPort);
        while (true) {
            int num = selector.select();

            Set selectedKeys = selector.selectedKeys();
            Iterator it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey)it.next();
                it.remove();
                if (key.isAcceptable()) {
                    //addClient(key);
                    new channelRegister(key, selector).run();
                }
                else if (key.isReadable()) {
                    new channelReader(key).run();
                }
            }
        }
    }
}
