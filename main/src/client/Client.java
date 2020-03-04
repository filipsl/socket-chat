package client;

import client.thread.receive.ReceiveTcpClientThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client {

    private final int portNumber = 12346;
    private final InetAddress tcpAddr;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private int id;
    private String nick;
    private Socket tcpSocket;
    private PrintWriter out;
    private BufferedReader in;


    public Client() throws IOException {
        this.tcpAddr = InetAddress.getByName("127.0.0.1");
    }

    public void run() throws IOException {
        System.out.println("CHAT CLIENT\n**************");
        System.out.print("Enter your nick: ");
        Scanner scanner = new Scanner(System.in);
        this.nick = scanner.next();

        this.connectTcp();
        this.handleInput();
    }

    public void connectTcp(){
        try {
            this.tcpSocket = new Socket(this.tcpAddr, this.portNumber);

            // in & out streams
            this.out = new PrintWriter(this.tcpSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.tcpSocket.getInputStream()));
            this.out.println(nick);
            String response = in.readLine();

            if(response == null){
                System.out.println("Server unreachable. TCP connection failed.");
            }else{
                try{
                    int id = Integer.parseInt(response);
                    if(id == -1 ){
                        System.out.println("Server cannot accept more connections.");
                    }else{
                        this.id = id;
                        // TODO established tcp connection, read TCP, UDP, multicast
                        this.executor.execute(new ReceiveTcpClientThread(this, this.in));
                    }
                } catch (NumberFormatException e){
                    System.out.println("Unrecognized response from server: " + response);
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void handleInput(){
        Scanner scanner = new Scanner(System.in);
        while(true){
            String msg = scanner.nextLine();
            if(msg.startsWith("T")){
                this.sendTcp(msg.substring(1).strip());
            }
        }
    }

    public void sendTcp(String msg){
        this.out.println(msg);
    }

    public synchronized void printSynchronized(String msg) {
        System.out.println(msg);
    }
}
