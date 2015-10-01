package boris.Glimmer;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by Boris on 2015/9/30.
 *
 * 等待客户端发来的消息
 * 该消息有特定的格式，不匹配则丢弃
 */
public class LoginOrRegist extends channelReader {

    /**
     * TODO: finish it
     * @throws IOException
     */
    @Override
    public void operate () throws IOException {

    }

    public LoginOrRegist(SelectionKey key) {
        super(key);
    }
}
