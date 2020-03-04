package server;

import server.data.ClientData;
import server.thread.receive.EstablishTcpThread;
import server.thread.receive.ReceiveTcpThread;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private final int portNumber = 12346;
    private final InetAddress tcpAddr;
    private final int backlog = 50;
    private final int clientLimit = 30;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(clientLimit + 5);
    private int clientIdCounter = 0;
    private List<ClientData> clientDataList = new CopyOnWriteArrayList<>();


    public Server() throws IOException {
        this.tcpAddr = InetAddress.getByName("127.0.0.1");
    }

    public void run() {
        this.printSynchronized("Server is starting...");
        EstablishTcpThread establishTcpThread = new EstablishTcpThread(
                this, this.portNumber, this.backlog, this.tcpAddr);
        executor.execute(establishTcpThread);

        Scanner scanner = new Scanner(System.in);
    }

    public void addClient(ClientData clientData) {
        this.clientDataList.add(clientData);
        this.incClientIdCounter();
        this.executor.execute(new ReceiveTcpThread(this, clientData));
    }

    public synchronized void sendToOthersTcp(ClientData clientData, String message) {
        message = clientData.getId() + "#" + clientData.getNick() +": " + message;
        for (ClientData cd : this.clientDataList) {
            if(!cd.equals(clientData)){
                cd.getTcpOut().println(message);
            }
        }
    }

    public void removeClient(ClientData clientData) {
        this.clientDataList.remove(clientData);
    }

    public int getClientIdCounter() {
        return this.clientIdCounter;
    }

    private void incClientIdCounter() {
        this.clientIdCounter += 1;
    }

    public synchronized void printSynchronized(String s) {
        System.out.println(s);
    }

    public int getClientLimit() {
        return clientLimit;
    }

    public List<ClientData> getClientDataList() {
        return clientDataList;
    }
}
