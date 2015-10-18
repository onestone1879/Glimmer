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
 * �ȴ��ͻ��˷�������Ϣ
 * ����Ϣ���ض��ĸ�ʽ����ƥ������
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
     * ��֤�û��������룬���е�¼����
     * @param username �û���
     * @param pwd �û�����
     * @return ��¼�ɹ��򷵻�true
     * @throws IOException getRemoteAddressʱ�׳��Ĵ���
     */
    private boolean login (String username, String pwd) throws IOException  {
        if (checkPwd(mc, username, pwd)) {
            System.out.println(username + "������ȷ");
            if (updateOnline(mc, username, true,
                    ((SocketChannel)(key.channel())).getRemoteAddress().toString())) {
                key.attach(new channelReader(key, mc, clients, keylists, username));
                clients.put(username, key);
                System.out.println(username + ":�ɹ���¼");
                return true;
            }
        }
        System.out.println(username + ":��¼ʧ��");
        return false;
    }

    /**
     * ע�����û�
     * @param username �û���
     * @param pwd �û�����
     * @return �Ƿ�ɹ�
     */
    private boolean regist (String username, String pwd) throws IOException {
        //todo:������������û�������У��
        if (!addUser(mc, username, pwd)) {
            return false;
        }
        return login(username, pwd);
    }

    /**
     * ��ȡ��Ϣ��ע����¼
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
    // todo:4��������Ҫ������μ��ܴ��䡢�洢���룡
}
