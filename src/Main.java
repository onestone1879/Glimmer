import boris.Glimmer.chatServer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {

    /**
     * TODO:如何处理客户端断开连接时触发的ex
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        System.out.println("Hello World!");

        chatServer();
    }


    /**
     * 服务器主函数
     */
    public static void chatServer() throws IOException{
        //TODO: 1. 添加身份验证
        // TODO:2. 使用数据库
        Map<SelectionKey, StringBuilder> clients =
                Collections.synchronizedMap(new HashMap<SelectionKey, StringBuilder>());

        chatServer server = new chatServer(8963, clients);
        server.listen();
    }
}
