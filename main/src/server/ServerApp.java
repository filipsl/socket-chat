package server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class ServerApp {
    public static void main(String[] args) throws IOException {

        System.out.println(Arrays.toString(args));

        if(args.length == 2){
            InetAddress serverIp = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            Server server = new Server(serverIp, port);
            server.run();
        }else if(args.length == 3){
            InetAddress serverIp = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            InetAddress multicastIp = InetAddress.getByName(args[2]);
            Server server = new Server(serverIp, port, multicastIp);
            server.run();
        }else {
            System.out.println("arguments: serverIP port [multicastIP]");
        }
    }
}
