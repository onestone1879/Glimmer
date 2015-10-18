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
    //ͨ��������
    private Selector selector;
    private int sPort;
    private MongoClient mc;
    //���ڽ���Ϣ���ݸ�ָ�����û�
    private Map<String, SelectionKey> clients;

    //����key�ظ�������⣬�����ǰ���߳����ڴ������key���¼��������ñ��Ϊfalse
    //��run�����������⵽markΪfalse�򲻽��в���
    //�Ҳ�֪��д������ò���
    private static List<SelectionKey> keyLists = Collections.synchronizedList(
            new ArrayList<SelectionKey>()
    );

    /**
     * ���һ��serverSocketͨ��������ͨ������ʼ��
     */
    public chatServer(int port, MongoClient mc) throws IOException {
        this.sPort = port;
        //ֻ�����ڲ�����
        this.clients = Collections.synchronizedMap(new HashMap<String, SelectionKey>());;
        this.mc = mc;

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        this.selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * ��ѵ��ʽ�������������¼������ⲿ����
     * TODO:��Ϊʹ���̳߳�
     * @throws IOException
     */
    public void listen() throws Exception {
        System.out.println("����������ɹ����˿ڣ�" + sPort);
        while (true) {
            int num = selector.select();

            Set selectedKeys = selector.selectedKeys();
            Iterator it = selectedKeys.iterator();

            while (it.hasNext()) {
                SelectionKey key = (SelectionKey)it.next();
                it.remove();

                //���鵱ǰ�Ƿ����ڴ����key
                if (keyLists.contains(key)) {
                    System.out.println("�Ѱ���:" + key.toString());
                    //fixme:ĳ��������������selector��keyLists֮һ��������ʹ�߳̽������ܽ�key����keyLists��ɾ��
                    Thread.sleep(1);
                    continue;
                } else {
                    keyLists.add(key);
                    System.out.println("���ڴ���:" + key.toString());
                }

                if (key.isAcceptable()) {
                    //����������ʱ��ʹ��channelRegister��keyע�ᵽselector��
                    //ͬʱ��channelReader���ص�key�У��Դ���key��readable��Ϣ
                    //��MongoClient���ô��ݸ�channelReader
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
