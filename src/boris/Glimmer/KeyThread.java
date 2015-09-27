package boris.Glimmer;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by Boris on 2015/9/17.
 * 用于对key进行多线程操作的抽象类
 */
public abstract class KeyThread extends Thread {
    protected final SelectionKey key;

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
