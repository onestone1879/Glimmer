package boris.mo;

import com.mongodb.Block;

import java.util.ArrayList;

/**
 * Created by Boris on 2015/9/26.
 * ���ڷ��ز�ѯ�Ȳ����Ľ���ĳ����࣬���Iteraterʹ��
 */
public abstract class BlockArrayList<K, V> implements Block<K> {
    // ����ArrayList�����ã����ڷ���String
    ArrayList<V> al;

    /**
     * ���ArrayList����
     * @param al
     */
    public BlockArrayList (ArrayList<V> al) {
        this.al = al;
    }
}
