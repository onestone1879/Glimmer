package boris.Glimmer;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by Boris on 2015/9/17.
 * ���ڶ�key���ж��̲߳����ĳ�����
 */
public abstract class KeyThread extends Thread {
    protected final SelectionKey key;

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
        try {
            this.operate();
        }
        catch (IOException e) {
            this.catchEx(e);
        }
    }
    
    public KeyThread(SelectionKey key) {
        this.key = key;
    }
}
