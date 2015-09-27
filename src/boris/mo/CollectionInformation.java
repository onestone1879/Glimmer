package boris.mo;

import org.bson.Document;

import java.util.Map;

/**
 * Created by Boris on 2015/9/26.
 */
public abstract class CollectionInformation {
    public String dbName = null;
    public String collectionName = null;
    public Map<String, Document> indexesList = null;
}
