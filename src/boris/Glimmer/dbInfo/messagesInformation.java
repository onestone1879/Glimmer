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
 * messages���ϵĹ�����Ϣ�������ĸ�����from��to��message,addTime
 * addTime is Date
 * //todo:����state��
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
    //ÿ�β�ѯ���ص��ĵ���������
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
     * message�ĵ�ģ��
     */
    public static class message {
        public final String from;
        public final String to;
        public final String message;
        public final Date addTime;

        /**
         * ��ʼ��
         * @param f ������
         * @param t ������
         * @param m ��Ϣ����
         * @param a ����ʱ��
         */
        public message(String f, String  t, String m, Date a) {
            from = f;
            to = t;
            message = m;
            addTime = a;
        }
    }

    /**
     * TODO:��δ���Ⱥ����
     * TODO:�������������currentTimeMillis��Mongo�Ĺ��������ļ�����
     * ��messages����������һ���ĵ�
     * @param mc MongoClient����
     * @param f ������
     * @param t ������
     * @param message ��Ϣ����
     * @return ���������뷢����У��ɹ�ʱ����true
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
     * ��ѯͨ����
     * @param mc MongoClient����
     * @param bs ��ѯ�������ɵ�Bson
     * @return ���ز�ѯ�����ɵ�ArrayList<message>
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
     * ��ѯ��Ϣ
     * @param mc MongoClient����
     * @param from ������
     * @param to ������
     * @return ���ز�ѯ�����ɵ�ArrayList
     */
    public static ArrayList<message> findSend(MongoClient mc, String from, String to) {
        if (!hasUser(mc, from, to)) {
            return null;
        }
        return findMessage(mc, and(eq(keyFrom, from), eq(keyTo, to)));
    }

    /**
     * ��ѯ��Ϣ
     * @param mc MongoClient����
     * @param from ������
     * @return ���ز�ѯ�����ɵ�ArrayList
     */
    public static ArrayList<message> findSend(MongoClient mc, String from) {
        if (!hasUser(mc, from)) {
            return null;
        }
        return findMessage(mc, eq(keyFrom, from));
    }
}
