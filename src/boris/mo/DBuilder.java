package boris.mo;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Created by Boris on 2015/9/22.
 * ���ڴ���database��collection����Ⲣ��������
 * users������ÿ��collection����username��pwd��online������username��������
 * messages�����а���from��to��message������fromto��to��������
 */
public final class DBuilder {

    public static void build(CollectionInformation ci) {
        //��ȡchatServer���ݿ�
        MongoClient mc = new MongoClient();
        MongoDatabase cs = mc.getDatabase(ci.dbName);

        //���users�Ƿ񴴽�,δ�����򴴽�
        FindIterable<Document> it = cs.getCollection(ci.collectionName).find();
        if ( null == it.first()) {
            //δ�������ݿ�
            System.out.printf("���ݿ�%sδ����%s���ϣ�������\n", ci.dbName, ci.collectionName);
            cs.getCollection(ci.collectionName).insertOne(
                    new Document("x", 1)
            );
        }

        //��鲢��������
        ListIndexesIterable<Document> lit = cs.getCollection(ci.collectionName).listIndexes();
        ArrayList<String> al = new ArrayList<String>();
        lit.forEach(
                new BlockArrayList<Document>(al) {
                    @Override
                    public void apply(Document document) {
                        al.add(document.getString("name"));
                    }
                }
        );
        for (String s : ci.indexesList.keySet()) {
            boolean hasUnIndex = false;
            for (String ss : al) {
                System.out.printf("s: %s, ss: %s,\n", s, ss);
                if (s.equals(ss)) {
                    hasUnIndex = true;
                    break;
                }
            }
            if (!hasUnIndex) {
                System.out.printf("����%s������%s������������������\n", ci.dbName, s);
                cs.getCollection(ci.collectionName).createIndex(
                        ci.indexesList.get(s), new IndexOptions().name(s)
                );
            }
        }
        mc.close();
    }
}
