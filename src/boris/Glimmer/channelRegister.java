package boris.Glimmer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by Boris on 2015/9/17.
 * �����µĿͻ������ӣ�����֤���
 */
public final class channelRegister extends KeyThread {
    private Selector s;

    public channelRegister (SelectionKey key, Selector s) {
        super(key);
        this.s = s;
    }

    /**
     * ����ͻ��������¼�
     * ˼��������Ƿ���Ҫ���̣߳��Լ����߳��漰��selectorͬ������
     * sѡ����������ɶ�������̰߳�ȫʹ�ã����������������ˡ�
     * ����ֻ�����߳��д������漰��������
     * TODO:ע���û���Ϣ�������ݿ�ͨ��
     * @throws IOException
     */
    @Override
    protected void operate() throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        SelectionKey newKey = sc.register(s, SelectionKey.OP_READ);
        newKey.attach(new channelReader(newKey));
        System.out.println(sc.getRemoteAddress().toString()+"�����ӷ�����");
    }

    @Override
    protected void catchEx(IOException e) {
        //������д
        System.out.println("�������"+ this.getClass().toString() + ":" + e.getMessage());
    }
}
