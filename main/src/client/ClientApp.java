package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        if (args.length == 3) {
            InetAddress serverIp = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            InetAddress multicastIp = InetAddress.getByName(args[2]);
            Client client = new Client(serverIp, port, multicastIp);
            client.run();
        } else {
            System.out.println("arguments: serverIP port multicastIP");
        }
    }
}
