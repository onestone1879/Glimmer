package boris.Glimmer.dbInfo;

import boris.mo.CollectionInformation;
import org.bson.Document;

import java.util.HashMap;

/**
 * Created by Boris on 2015/9/26.
 */
public class messagesInformation extends CollectionInformation {
    public messagesInformation() {
        this.dbName = "chatServer";
        this.collectionName = "messages";
        this.indexesList = new HashMap<String, Document>();
        this.indexesList.put("ft",
                new Document("from", 1).append("to", 1)
        );
        this.indexesList.put("to",
                new Document("to", 1)
        );
    }
}
