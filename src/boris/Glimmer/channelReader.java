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

    //用于判断字符串功能的正则表达式
    //在LoginOrRegist中规则如下：
    //登录用规则："username@pwd"
    //注册用规则："@username@pwd"
    //------------------------------
    //在channelReader中规则如下：
    //发送消息用规则："username@message"
    //fixme:接受消息长度不应该有6~16的限制
    public static final String regLoginOrMsg = "(^[a-zA-Z_]{1}\\w{4,12})@(\\w{6,16})$";
    public static final String regRegist = "^@([a-zA-Z_]{1}\\w{4,12})@(\\w{6,16})$";
    public static final Pattern pLoginOrMsg = Pattern.compile(regLoginOrMsg);
    public static final Pattern pRegist = Pattern.compile(regRegist);

    protected SocketChannel sc;
    private final String username;

    /**
     * 初始化传入SelectionKey
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
     * 读取客户端发来的信息
     * 多个并发线程可安全地使用套接字通道。尽管在任意给定时刻最多只能
     * 有一个线程进行读取和写入操作，但数据报通道支持并发的读写。
     * connect 和 finishConnect 方法是相互同步的，如果正在调用其中某个
     * 方法的同时试图发起读取或写入操作，则在该调用完成之前该操作被阻塞。
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
     * 读取客户端发来的信息
     * 多个并发线程可安全地使用套接字通道。尽管在任意给定时刻最多只能
     * 有一个线程进行读取和写入操作，但数据报通道支持并发的读写。
     * connect 和 finishConnect 方法是相互同步的，如果正在调用其中某个
     * 方法的同时试图发起读取或写入操作，则在该调用完成之前该操作被阻塞。
     * --------------------------------------------------------------------
     * fixme:客户端发送消息过快可能导致信息丢失，原因是用“/”来作为
     * fixme:消息终止符号时，后续连续发来的消息会被丢弃
     * fixme:改变delete顺序即可
     * --------------------------------------------------------------------
     * fixme:当客户端强制关闭时多次抛出异常，原因未知，这是改为内
     * fixme:置StringBuilder后新增的bug
     * --------------------------------------------------------------------
     * fixme:当使用类内部的ByteBuffer时，读取会出错
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
     * 对operate执行时抛出的异常进行处理
     * @param e
     */
    @Override
    protected void catchEx(IOException e) {
        System.out.println("错误代码"+ this.getClass().toString() + ":" + e.getMessage());
        //todo:只是需要考虑一下这个下线功能是不是太简单了
        if (clients.containsKey(username)) {
            clients.remove(username);
            updateOnline(mc, username, false);
        }
        key.cancel();
        if (key.channel() != null) {
            //真SB
            try {
                key.channel().close();
            }
            catch (IOException ee) {
                System.out.println("错误代码"+ this.getClass().toString() + ":" + ee.getMessage());
            }
        }
    }
}
