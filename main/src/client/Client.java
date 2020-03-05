package client;

import client.thread.receive.ReceiveMulticastClientThread;
import client.thread.receive.ReceiveTcpClientThread;
import client.thread.receive.ReceiveUdpClientThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client {

    private final int portNumber;
    private final InetAddress address;
    private final InetAddress groupAddress;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
    private int id;
    private String nick;
    private Socket tcpSocket;
    private PrintWriter out;
    private BufferedReader in;
    private DatagramSocket udpSocket;
    private MulticastSocket multicastSocket;
    private boolean isRunning = true;


    public Client(InetAddress clientIp, int portNumber, InetAddress multicastIp) {
        this.address = clientIp;
        this.portNumber = portNumber;
        this.groupAddress = multicastIp;
    }

    public Socket getTcpSocket() {
        return tcpSocket;
    }

    public BufferedReader getIn() {
        return in;
    }


    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }

    public void run() throws IOException {
        System.out.println("CHAT CLIENT\n**************");
        System.out.print("Enter your nick: ");
        Scanner scanner = new Scanner(System.in);
        this.nick = scanner.next();

        System.out.println(getHelp() + "\n");

        if (this.connectTcp()) {
            this.setupUdp();
            this.executor.execute(new ReceiveUdpClientThread(this));
        }
        this.setupMulticast();
        this.executor.execute(new ReceiveMulticastClientThread(this));

        this.handleInput();
    }

    public boolean connectTcp() {
        try {
            this.tcpSocket = new Socket(this.address, this.portNumber);

            // in & out streams
            this.out = new PrintWriter(this.tcpSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.tcpSocket.getInputStream()));
            this.out.println(nick);
            String response = in.readLine();

            if (response == null) {
                System.out.println("Server unreachable. TCP connection failed.");
            } else {
                try {
                    int id = Integer.parseInt(response);
                    if (id == -1) {
                        System.out.println("Server cannot accept more connections.");
                    } else {
                        this.id = id;
                        System.out.println("TCP Connection with server established.");
                        this.executor.execute(new ReceiveTcpClientThread(this));
                        return true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Unrecognized response from server: " + response);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setupUdp() throws IOException {
        this.udpSocket = new DatagramSocket();
        byte[] sendBuffer = getClientPrefix().getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
        udpSocket.send(sendPacket);
    }

    private void setupMulticast() throws IOException {
        this.multicastSocket = new MulticastSocket(portNumber);
        this.multicastSocket.joinGroup(groupAddress);
    }

    public String getClientPrefix() {
        return this.id + "#";
    }

    public void handleInput() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            String msg = scanner.nextLine();
            if (msg.startsWith("T")) {
                this.sendTcp(msg.substring(1).strip());
            } else if (msg.startsWith("U")) {
                this.sendUdp();
            } else if (msg.startsWith("M")) {
                this.sendMulticast();
            } else if (msg.startsWith("exit")) {
                this.handleExit();
            }
        }
    }

    public void handleExit() {
        this.isRunning = false;
        try {
            tcpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        udpSocket.close();
        multicastSocket.close();
        executor.shutdownNow();
    }

    public void sendTcp(String msg) {
        this.out.println(msg);
    }

    public void sendUdp() {
        byte[] sendBuffer = (getClientPrefix() +
                ".       ___,-----.___\n" +
                "    ,--'             `--.\n" +
                "   /                     \\\n" +
                "  /                       \\\n" +
                " |                         |\n" +
                "|                           |\n" +
                "|        |~~~~~~~~~|        |\n" +
                "|        \\         /        |\n" +
                " |        \\       /        |\n" +
                "  \\        \\     /        /\n" +
                "   \\        |   |        /\n" +
                "    \\       |   |       /\n" +
                "     \\      |   |      /\n" +
                "      \\     |   |     /\n" +
                "       \\____|___| ___/\n" +
                "       )___,-----'___(\n" +
                "       )___,-----'___(\n" +
                "       )___,-----'___(\n" +
                "       )___,-----'___(\n" +
                "       \\_____________/\n" +
                "            \\___/").getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
        try {
            udpSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMulticast() {
        byte[] sendBuffer = (getClientPrefix() +
                ".                __..-----')\n" +
                "       ,.--._ .-'_..--...-'\n" +
                "      '-\"'. _/_ /  ..--''\"\"'-.\n" +
                "      _.--\"\"...:._:(_ ..:\"::. \\\n" +
                "   .-' ..::--\"\"_(##)#)\"':. \\ \\)    \\ _|_ /\n" +
                "  /_:-:'/  :__(##)##)    ): )   '-./'   '\\.-'\n" +
                "  \"  / |  :' :/\"\"\\///)  /:.'    --(       )--\n" +
                "    / :( :( :(   (#//)  \"       .-'\\.___./'-.\n" +
                "   / :/|\\ :\\_:\\   \\#//\\            /  |  \\\n" +
                "   |:/ | \"\"--':\\   (#//)              '\n" +
                "   \\/  \\ :|  \\ :\\  (#//)\n" +
                "        \\:\\   '.':. \\#//\\\n" +
                "         ':|    \"--'(#///)\n" +
                "                    (#///)\n" +
                "                    (#///)\n" +
                "                     \\#///\\\n" +
                "                     (##///)\n" +
                "                     (##///)\n" +
                "                     (##///)\n" +
                "                     (##///)\n" +
                "                      \\##///\\\n" +
                "                      (###///)\n" +
                "                      (sjw////)__...-----....__\n" +
                "                      (#/::'''                 \"\"--.._\n" +
                "                 __..-'''                             \"-._\n" +
                "         __..--\"\"                                         '._\n" +
                "___..--\"\"                                                    \"-..____\n" +
                "  (_ \"\"---....___                                     __...--\"\" _)\n" +
                "    \"\"\"--...  ___\"\"\"\"\"-----......._______......----\"\"\"     --\"\"\"\n" +
                "                  \"\"\"\"       ---.....   ___....----").getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, groupAddress, portNumber);
        try {
            multicastSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void printSynchronized(String msg) {
        System.out.println(msg);
    }

    public boolean isRunning() {
        return isRunning;
    }

    private String getHelp() {
        return " Usage:\n"
                + "T message - send message with TCP\n"
                + "U - send ASCII art with UDP\n"
                + "M - send ASCII art with multicast\n"
                + "exit - close application";
    }
}
