import boris.Glimmer.chatServer;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {

    /**
     * TODO:��δ���ͻ��˶Ͽ�����ʱ������ex
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        System.out.println("Hello World!");

        chatServer();
    }


    /**
     * ������������
     */
    public static void chatServer() throws IOException{
        //TODO: 1. ��������֤
        // TODO:2. ʹ�����ݿ�
        Map<SelectionKey, StringBuilder> clients =
                Collections.synchronizedMap(new HashMap<SelectionKey, StringBuilder>());

        chatServer server = new chatServer(8963, clients);
        server.listen();
    }
}
