package boris.Glimmer;

import com.mongodb.MongoClient;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static boris.Glimmer.dbInfo.usersInfromation.addUser;
import static boris.Glimmer.dbInfo.usersInfromation.checkPwd;
import static boris.Glimmer.dbInfo.usersInfromation.updateOnline;


/**
 * Created by Boris on 2015/9/30.
 *
 * 等待客户端发来的消息
 * 该消息有特定的格式，不匹配则丢弃
 */
public class LoginOrRegist extends channelReader {

    /**
     * @throws IOException
     */
    @Override
    public void operate () throws IOException {
        String s = readchannel();
        if (null != s) {
            System.out.println("lor received : " + s);
            LoR(s);
        }
    }

    public LoginOrRegist(SelectionKey key, MongoClient mc,
                         Map<String, SelectionKey> cli, List<SelectionKey> kl) {
        super(key, mc, cli, kl);
    }

    /**
     * 验证用户名及密码，进行登录操作
     * @param username 用户名
     * @param pwd 用户密码
     * @return 登录成功则返回true
     * @throws IOException getRemoteAddress时抛出的错误
     */
    private boolean login (String username, String pwd) throws IOException  {
        if (checkPwd(mc, username, pwd)) {
            System.out.println(username + "密码正确");
            if (updateOnline(mc, username, true,
                    ((SocketChannel)(key.channel())).getRemoteAddress().toString())) {
                key.attach(new channelReader(key, mc, clients, keylists, username));
                clients.put(username, key);
                System.out.println(username + ":成功登录");
                return true;
            }
        }
        System.out.println(username + ":登录失败");
        return false;
    }

    /**
     * 注册新用户
     * @param username 用户名
     * @param pwd 用户密码
     * @return 是否成功
     */
    private boolean regist (String username, String pwd) throws IOException {
        //todo:添加密码规则和用户名规则校验
        if (!addUser(mc, username, pwd)) {
            return false;
        }
        return login(username, pwd);
    }

    /**
     * 读取消息，注册或登录
     * @throws IOException
     */
    private void LoR(String s) throws IOException {
        Matcher mLogin = pLoginOrMsg.matcher(s);
        Matcher mRegist = pRegist.matcher(s);
        if (mLogin.matches()) {
            String username = mLogin.group(1);
            String pwd = mLogin.group(2);
            System.out.println(username + " : " + pwd);
            login(username, pwd);
        }
        else if (mRegist.matches()) {
            String username = mRegist.group(1);
            String pwd = mRegist.group(2);
            regist(username, pwd);
        }
    }
    // todo:4、后续需要考虑如何加密传输、存储密码！
}
