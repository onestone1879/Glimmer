package boris.Glimmer.dbInfo;

import boris.mo.CollectionInformation;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.HashMap;


/**
 * Created by Boris on 2015/9/26.
 * users���ϵĹ�����Ϣ�������ĸ�����username��pwd��lastIP,��ΪString��onlineΪboolean
 * ����Ϊ��̬��
 */
public class usersInfromation extends CollectionInformation {
    public static final String udbName = "chatServer";
    public static final String ucollectionName = "users";
    public static final String keyUsname = "username";
    public static final String keyPwd = "pwd";
    public static final String keyOnline = "online";
    public static final String keyLastIP = "lastIP";
    public static final String indexUsname = "un@u";

    public usersInfromation() {
        this.dbName = udbName;
        this.collectionName = ucollectionName;
        this.indexesList = new HashMap<String, Document>();
        this.indexesList.put(indexUsname,
                new Document(keyUsname, 1)
        );
    }

    /**
     * user�ĵ�����ģ��
     */
    public static class user {
        public final String username;
        public final String pwd;
        public final boolean online;
        public final String lastIP;

        /**
         * ��ʼ��
         * @param u �û���
         * @param p �û���������
         * @param o �Ƿ�����
         * @param l �ϴ�/��ǰIP
         */
        public user(String u, String p, boolean o, String l) {
            username = u;
            pwd = p;
            online = o;
            lastIP = l;
        }

        public user(String u, String p, String l) {
            username = u;
            pwd = p;
            online = false;
            lastIP = l;
        }
    }

    /**
     * ͨ���û��������û���Ϣ
     * @param mc MongoClient����
     * @param username Ҫ��ѯ���û���
     * @return ���ز�ѯ�õ���user����ģ�ͣ���ѯʧ�ܷ���null
     */
    public static user findUser(MongoClient mc, String username) {
        FindIterable<Document> fit = mc.getDatabase(udbName)
                .getCollection(ucollectionName)
                .find(
                        new Document(keyUsname, username)
                );
        Document d = fit.first();
        if (null == d) {
            return null;
        } else {
            try {
                return new user(
                        d.getString(usersInfromation.keyUsname),
                        d.getString(usersInfromation.keyPwd),
                        d.getBoolean(usersInfromation.keyOnline),
                        d.getString(usersInfromation.keyLastIP)
                );
            } catch (Exception e) {
                return new user(
                        d.getString(usersInfromation.keyUsname),
                        d.getString(usersInfromation.keyPwd),
                        d.getString(usersInfromation.keyLastIP)
                );
            }
        }
    }

    /**
     * �жϵ�ǰusers�������Ƿ�������û���
     * @param mc MongoClient����
     * @param username Ҫ��ѯ���û���
     * @return ���ڸ��û��򷵻�true
     */
    private static boolean hasOneUser(MongoClient mc, String username) {
        if (null == findUser(mc, username)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * �жϵ�ǰusers�������Ƿ������Щ�û���
     * @param mc MongoClient����
     * @param usersname Ҫ��ѯ���û�����
     * @return ������Щ�û��򷵻�true
     */
    public static boolean hasUser(MongoClient mc, String... usersname) {
        for (String username : usersname) {
            if (!hasOneUser(mc, username)) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO:��������洢
     * ��users�������������û���Ϣ
     * @param mc MongoClient����
     * @param username Ҫ��ӵ��û���
     * @param pwd �����û�����
     * @return �ɹ�����򷵻�true���������ͬ���û��ȵ������ʧ�ܣ��򷵻�false
     */
    public static boolean addUser(MongoClient mc, String username, String pwd) {
        if (! hasUser(mc, username)) {
            mc.getDatabase(udbName)
                    .getCollection(ucollectionName)
                    .insertOne(
                            new Document(keyUsname, username)
                            .append(keyPwd, pwd)
                    );
            return true;
        } else {
            return false;
        }
    }

    /**
     * ����У��
     * @param mc MongoClient����
     * @param username �û���
     * @param pwd �û�����
     * @return �����Ƿ�ƥ��
     */
    public static boolean checkPwd(MongoClient mc, String username, String pwd) {
        user u = findUser(mc, username);
        if (null == u) {
            System.out.println("userInformation:δ�ҵ�" + username);
            return false;
        }
        return (u.pwd.equals(pwd));
    }

    /**
     * ��������
     * @param mc MongoClient����
     * @param username �û���
     * @param d ������
     * @return ����û�ʧ���򷵻�false
     */
    private static boolean updataByName(MongoClient mc, String username, Document d) {
        if (!hasUser(mc, username)) {
            return false;
        }
        mc.getDatabase(udbName)
                .getCollection(ucollectionName)
                .updateOne(
                        new Document(keyUsname, username),
                        new Document("$set", d)
                );
        return true;
    }

    /**
     * �������߱�ʶ
     * @param mc MongoClient����
     * @param username �û���
     * @param online ���߱�ʶ
     * @return ����û�ʧ���򷵻�false
     */
    public static boolean updateOnline(MongoClient mc, String username, boolean online) {
        return updataByName(mc, username,
                new Document(keyOnline, online)
        );
    }

    /**
     * �������߱�ʶ
     * @param mc MongoClient����
     * @param username �û���
     * @param online ���߱�ʶ
     * @param ip LastIP
     * @return ����û�ʧ���򷵻�false
     */
    public static boolean updateOnline(MongoClient mc, String username, boolean online, String ip) {
        return updataByName(mc, username,
                new Document(keyOnline, online)
                .append(keyLastIP, ip)
        );
    }

    //TODO:��ѯ����״̬(###)��ʱ�ò���
    //TODO:��������
}