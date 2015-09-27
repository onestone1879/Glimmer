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
     * ��ʼ������SelectionKey
     * @param key
     */
    public channelReader (SelectionKey key) {
        //this.key = key;
        super(key);
    }

    /**
     * ��ȡ�ͻ��˷�������Ϣ
     * ��������߳̿ɰ�ȫ��ʹ���׽���ͨ�����������������ʱ�����ֻ��
     * ��һ���߳̽��ж�ȡ��д������������ݱ�ͨ��֧�ֲ����Ķ�д��
     * connect �� finishConnect �������໥ͬ���ģ�������ڵ�������ĳ��
     * ������ͬʱ��ͼ�����ȡ��д����������ڸõ������֮ǰ�ò�����������
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
     * ��operateִ��ʱ�׳����쳣���д���
     * @param e
     */
    @Override
    protected void catchEx(IOException e) {
        System.out.println(e.getMessage());
        key.cancel();
        if (key.channel() != null) {
            //��SB
            try {
                key.channel().close();
            }
            catch (IOException ee) {
                System.out.println(ee.getMessage());
            }
        }
    }
}
