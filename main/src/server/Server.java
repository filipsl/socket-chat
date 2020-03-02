package server;

import server.data.ClientData;
import server.thread.receive.EstablishTcpThread;
import server.thread.receive.ReceiveTcpThread;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private final int portNumber = 12346;
    private final InetAddress tcpAddr;
    private final int backlog = 50;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private int clientIdCounter = 0;
    private List<ClientData> clientDataList = Collections.synchronizedList(new LinkedList<>());


    public Server() throws IOException {
        this.tcpAddr = InetAddress.getByName("127.0.0.1");
    }

    public void start() {
        this.print("Server is starting...");
        EstablishTcpThread establishTcpThread = new EstablishTcpThread(
                this, this.portNumber, this.backlog, this.tcpAddr);
        executor.execute(establishTcpThread);
    }

    public void addClient(ClientData clientData){
        this.clientDataList.add(clientData);
        this.incClientIdCounter();
        this.executor.execute(new ReceiveTcpThread(clientData));
    }

    public void removeClient(ClientData clientData){
        this.clientDataList.remove(clientData);
    }

    public int getClientIdCounter() {
        return this.clientIdCounter;
    }

    private void incClientIdCounter() {
        this.clientIdCounter += 1;
    }

    public synchronized void print(String s) {
        System.out.println(s);
    }
}
