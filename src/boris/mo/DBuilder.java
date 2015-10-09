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

    public static void build(MongoClient mc,  CollectionInformation ci) {
        //��ȡ���ݱ�����
        MongoDatabase cs = mc.getDatabase(ci.dbName);
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
                new BlockArrayList<Document, String>(al) {
                    @Override
                    public void apply(Document document) {
                        al.add(document.getString("name"));
                    }
                }
        );
        for (String s : ci.indexesList.keySet()) {
            //s��Ӧ����������option��Ϣ
            String opt = "";
            String name = "";
            int index = s.indexOf("@");
            if (index > -1) {
                opt = s.substring(index+1);
                name = s.substring(0, index);
            } else {
                name = s;
            }

            //����indexOptions������Ψһ�����������
            IndexOptions io = new IndexOptions().name(name);
            if (opt.contains("u")) {    //  isUnique
                io.unique(true);
            }

            boolean hasUnIndex = false;
            for (String ss : al) {
                System.out.printf("name: %s, ss: %s,\n", name, ss);
                if (name.equals(ss)) {
                    hasUnIndex = true;
                    break;
                }
            }
            if (!hasUnIndex) {
                System.out.printf("����%s������%s������������������\n", ci.collectionName, name);
                cs.getCollection(ci.collectionName).createIndex(
                        ci.indexesList.get(s), io
                );
            }
        }
    }
}
