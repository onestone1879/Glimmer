package boris.mo;

import com.mongodb.Block;

import java.util.ArrayList;

/**
 * Created by Boris on 2015/9/26.
 * ���ڷ��ز�ѯ�Ȳ����Ľ���ĳ����࣬���Iteraterʹ��
 */
public abstract class BlockArrayList<Document> implements Block<Document> {
    // ����ArrayList�����ã����ڷ���String
    ArrayList<String> al;

    /**
     * ���ArrayList����
     * @param al
     */
    public BlockArrayList (ArrayList<String> al) {
        this.al = al;
    }
}
