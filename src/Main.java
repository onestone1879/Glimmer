import boris.Glimmer.chatServer;
import boris.Glimmer.dbInfo.messagesInformation;
import boris.Glimmer.dbInfo.usersInfromation;
import boris.mo.DBuilder;
import com.mongodb.MongoClient;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static boris.Glimmer.dbInfo.messagesInformation.message;

public class Main {

    /**
     * fixme:如何处理客户端断开连接时触发的ex
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception{
        System.out.println("Hello World!");

        chatServer();
//        buildDB();
    }


    /**
     * 服务器主函数
     */
    public static void chatServer() throws Exception{
        MongoClient mc = new MongoClient();
        buildDB(mc);

        chatServer server = new chatServer(8963, mc);
        server.listen();
    }

    /**
     * 测试构建数据库
     */
    public static void buildDB(MongoClient mc) {
        //TODO:针对MongoClient的连接配置，需要建立数据库信息配置类
//        MongoClient mc = new MongoClient();
        DBuilder.build(mc, new usersInfromation());
        DBuilder.build(mc, new messagesInformation());

//        usersInfromation.user d = usersInfromation.findUser(mc, "Sonton");
//        if (null == d) {
//            System.out.println("null");
//        } else {
//            System.out.println(d.username + ":" + d.pwd + ":" + d.online);
//        }
//
//        String f = "sonton";
//        String t = "boris";
//        usersInfromation.addUser(mc, f, "123");
//        usersInfromation.addUser(mc, t, "123");
//        messagesInformation.addMessage(mc, f, t, "hello!");
//        messagesInformation.addMessage(mc, t, f, "hello!!!");
//        ArrayList<message> al = messagesInformation.findSend(mc, f, t);
//        for (message m : al) {
//            System.out.printf("from:%s;to:%s;message:%s;Date:%s\n", m.from, m.to, m.message, m.addTime.toString());
//        }
    }
}
