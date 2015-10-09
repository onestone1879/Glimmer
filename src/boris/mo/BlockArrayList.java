package boris.mo;

import com.mongodb.Block;

import java.util.ArrayList;

/**
 * Created by Boris on 2015/9/26.
 * 用于返回查询等操作的结果的抽象类，配合Iterater使用
 */
public abstract class BlockArrayList<K, V> implements Block<K> {
    // 传入ArrayList的引用，用于返回String
    ArrayList<V> al;

    /**
     * 获得ArrayList引用
     * @param al
     */
    public BlockArrayList (ArrayList<V> al) {
        this.al = al;
    }
}
