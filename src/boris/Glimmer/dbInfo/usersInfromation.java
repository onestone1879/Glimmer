package boris.Glimmer.dbInfo;

import boris.mo.CollectionInformation;
import org.bson.Document;

import java.util.HashMap;

/**
 * Created by Boris on 2015/9/26.
 * ����users���ϵĹ�����Ϣ
 */
public class usersInfromation extends CollectionInformation {
    public usersInfromation() {
        this.dbName = "chatServer";
        this.collectionName = "users";
        this.indexesList = new HashMap<String, Document>();
        this.indexesList.put("un@u",
                new Document("username", 1)
        );
    }
}
