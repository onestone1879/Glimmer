package boris.Glimmer;

import com.mongodb.MongoClient;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.*;

/**
 * Created by Boris on 2015/9/17.
 * 用于对key进行多线程操作的抽象类，同时，各子类中需要的通用函数写在该类下
 *
 */
public abstract class KeyThread extends Thread {
    protected final SelectionKey key;
    protected final MongoClient mc;
    protected StringBuilder sb;
    protected Map<String, SelectionKey> clients;
    protected List<SelectionKey> keylists;

    /**
     * 对key进行操作，如注册，读取等
     */
    protected abstract void operate () throws IOException;

    /**
     * 对operate异常进行处理的函数
     * @param e
     */
    protected abstract void catchEx (IOException e);

    /**
     * 多线程执行
     */
    @Override
    public void run() {
//        if (this.mark) {
        try {
            this.operate();
        }
        catch (IOException e) {
            this.catchEx(e);
        }
        keylists.remove(key);
        System.out.println("处理完成:" + key.toString());
//        }
    }
    
    public KeyThread(
            SelectionKey key,
            MongoClient mc,
            Map<String, SelectionKey> cli,
            List<SelectionKey> kl ) {
        this.key = key;
        this.mc = mc;
        this.sb = new StringBuilder();
        this.clients = cli;
        this.keylists = kl;
    }
}
