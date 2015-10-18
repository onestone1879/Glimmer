package boris.Glimmer;

import com.mongodb.MongoClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static boris.Glimmer.dbInfo.usersInfromation.hasUser;
import static boris.Glimmer.dbInfo.usersInfromation.updateOnline;
import static boris.Glimmer.dbInfo.messagesInformation.addMessage;

/**
 * Created by Boris on 2015/9/14.
 */
public class channelReader extends KeyThread {

    //�����ж��ַ������ܵ�������ʽ
    //��LoginOrRegist�й������£�
    //��¼�ù���"username@pwd"
    //ע���ù���"@username@pwd"
    //------------------------------
    //��channelReader�й������£�
    //������Ϣ�ù���"username@message"
    //fixme:������Ϣ���Ȳ�Ӧ����6~16������
    public static final String regLoginOrMsg = "(^[a-zA-Z_]{1}\\w{4,12})@(\\w{6,16})$";
    public static final String regRegist = "^@([a-zA-Z_]{1}\\w{4,12})@(\\w{6,16})$";
    public static final Pattern pLoginOrMsg = Pattern.compile(regLoginOrMsg);
    public static final Pattern pRegist = Pattern.compile(regRegist);

    protected SocketChannel sc;
    private final String username;

    /**
     * ��ʼ������SelectionKey
     * @param key
     */
    public channelReader (SelectionKey key, MongoClient mc,
                          Map<String, SelectionKey> cli, List<SelectionKey> kl) {
        super(key, mc, cli, kl);
        this.sc = (SocketChannel) key.channel();
        this.username = "";
    }

    public channelReader (SelectionKey key, MongoClient mc,
                          Map<String, SelectionKey> cli, List<SelectionKey> kl, String usnm) {
        super(key, mc, cli, kl);
        this.sc = (SocketChannel) key.channel();
        this.username = usnm;
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
            System.out.println("cR received : " + s);
//            System.out.println(
//                    "[" + sc.getRemoteAddress().toString() + "]" + s + "[end]"
//            );
            Matcher mMsg = pLoginOrMsg.matcher(s);
            if (mMsg.matches()) {
                String to = mMsg.group(1);
                String msg = mMsg.group(2);
                if (hasUser(mc, to) & clients.containsKey(to)) {
                    ((SocketChannel)(clients.get(to).channel()))
                            .write(
                                    ByteBuffer.wrap((s + "\r\n").getBytes())
                            );
                    addMessage(mc, username, to, msg);
                }
            }
        }
    }

    /**
     * ��ȡ�ͻ��˷�������Ϣ
     * ��������߳̿ɰ�ȫ��ʹ���׽���ͨ�����������������ʱ�����ֻ��
     * ��һ���߳̽��ж�ȡ��д������������ݱ�ͨ��֧�ֲ����Ķ�д��
     * connect �� finishConnect �������໥ͬ���ģ�������ڵ�������ĳ��
     * ������ͬʱ��ͼ�����ȡ��д����������ڸõ������֮ǰ�ò�����������
     * --------------------------------------------------------------------
     * fixme:�ͻ��˷�����Ϣ������ܵ�����Ϣ��ʧ��ԭ�����á�/������Ϊ
     * fixme:��Ϣ��ֹ����ʱ������������������Ϣ�ᱻ����
     * fixme:�ı�delete˳�򼴿�
     * --------------------------------------------------------------------
     * fixme:���ͻ���ǿ�ƹر�ʱ����׳��쳣��ԭ��δ֪�����Ǹ�Ϊ��
     * fixme:��StringBuilder��������bug
     * --------------------------------------------------------------------
     * fixme:��ʹ�����ڲ���ByteBufferʱ����ȡ�����
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
        //todo:ֻ����Ҫ����һ��������߹����ǲ���̫����
        if (clients.containsKey(username)) {
            clients.remove(username);
            updateOnline(mc, username, false);
        }
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
