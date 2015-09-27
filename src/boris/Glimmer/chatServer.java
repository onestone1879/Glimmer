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
    //ͨ��������
    private Selector selector;
    private int sPort;
    //����������ʹclients�Ĵ洢��ʽ������
    //TODO:��δʵ�ֵ�¼�û�������,δ���ô˱���
    private Map<SelectionKey, StringBuilder> clients;

    /**
     * ���һ��serverSocketͨ��������ͨ������ʼ��
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
     * ��ѵ��ʽ�������������¼������ⲿ����
     * @throws IOException
     */
    public void listen() throws IOException {
        System.out.println("����������ɹ����˿ڣ�" + sPort);
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
