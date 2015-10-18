package boris.Glimmer.dbInfo;

import boris.mo.BlockArrayList;
import boris.mo.CollectionInformation;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.*;
import static boris.Glimmer.dbInfo.usersInfromation.hasUser;


/**
 * messages集合的构建信息，包含四个键：from，to，message,addTime
 * addTime is Date
 * //todo:加入state键
 * Created by Boris on 2015/9/26.
 */
public class messagesInformation extends CollectionInformation {
    public static final String mdbName = "chatServer";
    public static final String mcollectionName = "messages";
    public static final String keyFrom = "from";
    public static final String keyTo = "to";
    public static final String keyMessage = "message";
    public static final String keyAddTime = "addtime";
    public static final String indexFt = "ft";
    //每次查询返回的文档数量限制
    public static final int batchSize = 50;

    public messagesInformation() {
        this.dbName = mdbName;
        this.collectionName = mcollectionName;
        this.indexesList = new HashMap<String, Document>();
        this.indexesList.put(indexFt,
                new Document(keyFrom, 1).append(keyTo, 1).append(keyAddTime, -1)
        );
    }

    /**
     * message文档模型
     */
    public static class message {
        public final String from;
        public final String to;
        public final String message;
        public final Date addTime;

        /**
         * 初始化
         * @param f 发信人
         * @param t 收信人
         * @param m 信息内容
         * @param a 发信时间
         */
        public message(String f, String  t, String m, Date a) {
            from = f;
            to = t;
            message = m;
            addTime = a;
        }
    }

    /**
     * TODO:如何处理群发？
     * TODO:加入过期索引，currentTimeMillis与Mongo的过期索引的兼容性
     * 向messages集合中增加一条文档
     * @param mc MongoClient引用
     * @param f 发信人
     * @param t 收信人
     * @param message 信息内容
     * @return 当收信人与发信人校验成功时返回true
     */
    public static boolean addMessage(MongoClient mc, String f, String t, String message) {
        if (hasUser(mc, f) & hasUser(mc, t)) {
            mc.getDatabase(mdbName)
                    .getCollection(mcollectionName)
                    .insertOne(
                            new Document(keyFrom, f)
                            .append(keyTo, t)
                            .append(keyMessage, message)
                            .append(keyAddTime, new Date(System.currentTimeMillis()))
                    );
            return true;
        }
        return false;
    }

    /**
     * 查询通用类
     * @param mc MongoClient引用
     * @param bs 查询规则生成的Bson
     * @return 返回查询结果组成的ArrayList<message>
     */
    private static ArrayList<message> findMessage(MongoClient mc, Bson bs) {
        FindIterable<Document> fit = mc.getDatabase(mdbName)
                .getCollection(mcollectionName)
                .find(bs)
                .sort(descending(keyAddTime))
                .batchSize(batchSize);

        ArrayList<message> al = new ArrayList<message>();

        fit.forEach(new BlockArrayList<Document, message>(al) {
            @Override
            public void apply(Document document) {
                al.add(
                        new message(
                                document.getString(keyFrom),
                                document.getString(keyTo),
                                document.getString(keyMessage),
                                document.getDate(keyAddTime)
                        )
                );
            }
        });
        return al;
    }

    /**
     * 查询信息
     * @param mc MongoClient引用
     * @param from 发信人
     * @param to 收信人
     * @return 返回查询结果组成的ArrayList
     */
    public static ArrayList<message> findSend(MongoClient mc, String from, String to) {
        if (!hasUser(mc, from, to)) {
            return null;
        }
        return findMessage(mc, and(eq(keyFrom, from), eq(keyTo, to)));
    }

    /**
     * 查询信息
     * @param mc MongoClient引用
     * @param from 发信人
     * @return 返回查询结果组成的ArrayList
     */
    public static ArrayList<message> findSend(MongoClient mc, String from) {
        if (!hasUser(mc, from)) {
            return null;
        }
        return findMessage(mc, eq(keyFrom, from));
    }
}
