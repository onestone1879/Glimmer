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
     * ��ʼ������SelectionKey
     * @param key
     */
    public channelReader (SelectionKey key) {
        super(key);
        this.sc = (SocketChannel) key.channel();
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
        String s = readchannel();
        if (null != s) {
            System.out.println("[" + sc.getRemoteAddress().toString() + "]" + s + "[end]");
        }
    }

    /**
     * ��ȡ�ͻ��˷�������Ϣ
     * ��������߳̿ɰ�ȫ��ʹ���׽���ͨ�����������������ʱ�����ֻ��
     * ��һ���߳̽��ж�ȡ��д������������ݱ�ͨ��֧�ֲ����Ķ�д��
     * connect �� finishConnect �������໥ͬ���ģ�������ڵ�������ĳ��
     * ������ͬʱ��ͼ�����ȡ��д����������ڸõ������֮ǰ�ò�����������
     * --------------------------------------------------------------------
     * TODO:bugfix:�ͻ��˷�����Ϣ������ܵ�����Ϣ��ʧ��ԭ�����á�/������Ϊ
     * TODO:bugfix:��Ϣ��ֹ����ʱ������������������Ϣ�ᱻ����
     * --------------------------------------------------------------------
     * TODO:bugfix:���ͻ���ǿ�ƹر�ʱ����׳��쳣��ԭ��δ֪�����Ǹ�Ϊ��
     * TODO:bugfix:��StringBuilder��������bug
     * --------------------------------------------------------------------
     * TODO:bugfix:��ʹ�����ڲ���ByteBufferʱ����ȡ�����
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
     * ��operateִ��ʱ�׳����쳣���д���
     * @param e
     */
    @Override
    protected void catchEx(IOException e) {
        System.out.println("�������"+ this.getClass().toString() + ":" + e.getMessage());
        key.cancel();
        if (key.channel() != null) {
            //��SB
            try {
                key.channel().close();
            }
            catch (IOException ee) {
                System.out.println("�������"+ this.getClass().toString() + ":" + ee.getMessage());
            }
        }
    }
}
