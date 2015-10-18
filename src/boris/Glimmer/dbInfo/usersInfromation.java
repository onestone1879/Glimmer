package boris.Glimmer.dbInfo;

import boris.mo.CollectionInformation;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.HashMap;


/**
 * Created by Boris on 2015/9/26.
 * users集合的构造信息，包含四个键：username，pwd，lastIP,均为String，online为boolean
 * 该类为静态类
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
     * user文档数据模型
     */
    public static class user {
        public final String username;
        public final String pwd;
        public final boolean online;
        public final String lastIP;

        /**
         * 初始化
         * @param u 用户名
         * @param p 用户明文密码
         * @param o 是否在线
         * @param l 上次/当前IP
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
     * 通过用户名查找用户信息
     * @param mc MongoClient引用
     * @param username 要查询的用户名
     * @return 返回查询得到的user数据模型，查询失败返回null
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
     * 判断当前users集合中是否包含该用户名
     * @param mc MongoClient引用
     * @param username 要查询的用户名
     * @return 存在该用户则返回true
     */
    private static boolean hasOneUser(MongoClient mc, String username) {
        if (null == findUser(mc, username)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断当前users集合中是否包含这些用户名
     * @param mc MongoClient引用
     * @param usersname 要查询的用户名们
     * @return 存在这些用户则返回true
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
     * TODO:加密密码存储
     * 向users集合中新增新用户信息
     * @param mc MongoClient引用
     * @param username 要添加的用户名
     * @param pwd 明文用户密码
     * @return 成功添加则返回true，若因存在同名用户等导致添加失败，则返回false
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
     * 密码校验
     * @param mc MongoClient引用
     * @param username 用户名
     * @param pwd 用户密码
     * @return 密码是否匹配
     */
    public static boolean checkPwd(MongoClient mc, String username, String pwd) {
        user u = findUser(mc, username);
        if (null == u) {
            System.out.println("userInformation:未找到" + username);
            return false;
        }
        return (u.pwd.equals(pwd));
    }

    /**
     * 更新数据
     * @param mc MongoClient引用
     * @param username 用户名
     * @param d 新内容
     * @return 检测用户失败则返回false
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
     * 更新在线标识
     * @param mc MongoClient引用
     * @param username 用户名
     * @param online 在线标识
     * @return 检测用户失败则返回false
     */
    public static boolean updateOnline(MongoClient mc, String username, boolean online) {
        return updataByName(mc, username,
                new Document(keyOnline, online)
        );
    }

    /**
     * 更新在线标识
     * @param mc MongoClient引用
     * @param username 用户名
     * @param online 在线标识
     * @param ip LastIP
     * @return 检测用户失败则返回false
     */
    public static boolean updateOnline(MongoClient mc, String username, boolean online, String ip) {
        return updataByName(mc, username,
                new Document(keyOnline, online)
                .append(keyLastIP, ip)
        );
    }

    //TODO:查询在线状态(###)暂时用不上
    //TODO:更改密码
}