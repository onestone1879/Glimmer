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
 * �����µĿͻ������ӣ�����֤���
 */
public final class channelRegister extends KeyThread {
    private Selector s;

    public channelRegister (SelectionKey key, Selector s, MongoClient mc,
                            Map<String, SelectionKey> cli, List<SelectionKey> kl) {
        super(key, mc, cli, kl);
        this.s = s;
    }

    /**
     * ����ͻ��������¼�
     * ˼��������Ƿ���Ҫ���̣߳��Լ����߳��漰��selectorͬ������
     * sѡ����������ɶ�������̰߳�ȫʹ�ã����������������ˡ�
     * ����ֻ�����߳��д������漰��������
     * @throws IOException
     */
    @Override
    protected void operate() throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel sc = ssc.accept();
        System.out.println("channelRegister:�ѻ�ȡ�ͻ���SocketChannel");
        sc.configureBlocking(false);
        System.out.println("channelRegister:�ѽ�SocketChannel��Ϊ������");
        SelectionKey newKey = sc.register(s, SelectionKey.OP_READ);
        System.out.println("channelRegister:����selectorע��SocketChannel");
        //�������ӵĿͻ��˽���LoginOrRegist����
        //������key������ص�key�ĸ����У���Ϊÿ��channelReaderʵ�����洢��Ӧkey�ĵ�ǰ��ȡֵ������䲻Ϊ��̬
        newKey.attach(new LoginOrRegist(newKey, mc, clients, keylists));
        System.out.println(sc.getRemoteAddress().toString()+"�����ӷ����������ݸ�lor����");
    }

    @Override
    protected void catchEx(IOException e) {
        //������д
        System.out.println("�������"+ this.getClass().toString() + ":" + e.getMessage());
    }
}
