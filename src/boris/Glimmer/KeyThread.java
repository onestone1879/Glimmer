package boris.Glimmer;

import com.mongodb.MongoClient;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.*;

/**
 * Created by Boris on 2015/9/17.
 * ���ڶ�key���ж��̲߳����ĳ����࣬ͬʱ������������Ҫ��ͨ�ú���д�ڸ�����
 *
 */
public abstract class KeyThread extends Thread {
    protected final SelectionKey key;
    protected final MongoClient mc;
    protected StringBuilder sb;
    protected Map<String, SelectionKey> clients;
    protected List<SelectionKey> keylists;

    /**
     * ��key���в�������ע�ᣬ��ȡ��
     */
    protected abstract void operate () throws IOException;

    /**
     * ��operate�쳣���д���ĺ���
     * @param e
     */
    protected abstract void catchEx (IOException e);

    /**
     * ���߳�ִ��
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
        System.out.println("�������:" + key.toString());
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
