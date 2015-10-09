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
 * 用于创建database和collection，检测并创建索引
 * users集合中每条collection包含username，pwd，online键，对username建立索引
 * messages集合中包含from，to，message键，对fromto，to建立索引
 */
public final class DBuilder {

    public static void build(MongoClient mc,  CollectionInformation ci) {
        //获取数据表引用
        MongoDatabase cs = mc.getDatabase(ci.dbName);
        FindIterable<Document> it = cs.getCollection(ci.collectionName).find();
        if ( null == it.first()) {
            //未创建数据库
            System.out.printf("数据库%s未创建%s集合，将创建\n", ci.dbName, ci.collectionName);
            cs.getCollection(ci.collectionName).insertOne(
                    new Document("x", 1)
            );
        }

        //检查并创建索引
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
            //s中应包含索引的option信息
            String opt = "";
            String name = "";
            int index = s.indexOf("@");
            if (index > -1) {
                opt = s.substring(index+1);
                name = s.substring(0, index);
            } else {
                name = s;
            }

            //构建indexOptions，参数唯一，且随用随加
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
                System.out.printf("集合%s不包含%s索引，将创建该索引\n", ci.collectionName, name);
                cs.getCollection(ci.collectionName).createIndex(
                        ci.indexesList.get(s), io
                );
            }
        }
    }
}
