package server;


import server.data.ClientData;
import server.thread.receive.EstablishTcpThread;
import server.thread.receive.ReceiveMulticastThread;
import server.thread.receive.ReceiveTcpThread;
import server.thread.receive.ReceiveUdpThread;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {

    private final int portNumber;
    private final InetAddress tcpAddr;
    private InetAddress multicastAddr;
    private final int backlog = 50;
    private final int clientLimit = 10;
    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(clientLimit + 3);
    private int clientIdCounter = 0;
    private List<ClientData> clientDataList = new CopyOnWriteArrayList<>();
    private Set<Integer> idSetUdp = Collections.synchronizedSet(new HashSet<>());
    private DatagramSocket datagramSocket;
    private ServerSocket serverSocket;
    private MulticastSocket multicastSocket;
    private boolean isRunning = true;


    public Server(InetAddress tcpAddr, int portNumber) throws SocketException {
        this.tcpAddr = tcpAddr;
        this.portNumber = portNumber;
        this.datagramSocket = new DatagramSocket(portNumber, tcpAddr);
    }

    public Server(InetAddress tcpAddr, int portNumber, InetAddress multicastAddress) throws IOException {
        this.tcpAddr = tcpAddr;
        this.portNumber = portNumber;
        this.datagramSocket = new DatagramSocket(portNumber, tcpAddr);
        this.multicastAddr = multicastAddress;
        this.multicastSocket = new MulticastSocket(portNumber);
    }

    public void run() {
        System.out.println("CHAT SERVER\n**************");
        System.out.println(getHelp() + "\n");
        EstablishTcpThread establishTcpThread = new EstablishTcpThread(
                this, this.portNumber, this.backlog, this.tcpAddr);
        executor.execute(establishTcpThread);
        executor.execute(new ReceiveUdpThread(this));

        if (this.multicastAddr != null) {
            executor.execute(new ReceiveMulticastThread(this));
        }

        this.handleInput();
    }

    private void handleInput() {
        Scanner scanner = new Scanner(System.in);
        while (this.isRunning) {
            String s = scanner.next();
            if (s.equals("exit")) {
                handleExit();
            }
        }
    }

    private void handleExit() {
        this.isRunning = false;
        for (ClientData clientData : clientDataList) {
            try {
                clientData.getTcpSocket().shutdownOutput();
                clientData.getTcpSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        datagramSocket.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (multicastSocket != null) {
            try {
                multicastSocket.leaveGroup(multicastAddr);
                multicastSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.executor.shutdownNow();
    }

    public void addClient(ClientData clientData) {
        this.clientDataList.add(clientData);
        this.incClientIdCounter();
        this.executor.execute(new ReceiveTcpThread(this, clientData));
    }

    public synchronized void sendToOthersTcp(ClientData clientData, String message) {
        message = clientData.getId() + "#" + clientData.getNick() + ":\n" + message;
        for (ClientData cd : this.clientDataList) {
            if (!cd.equals(clientData)) {
                cd.getTcpOut().println(message);
            }
        }
    }

    public synchronized void sendToOthersUdp(int clientId, String message) {
        for (ClientData sender : clientDataList) {
            if (sender.getId() == clientId) {
                message = sender.getId() + "#" + sender.getNick() + ":\n" + message;
                for (ClientData cd1 : this.clientDataList) {
                    if (!cd1.equals(sender)) {
                        try {
                            byte[] sendBuffer = (message).getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(
                                    sendBuffer, sendBuffer.length, cd1.getInetAddress(), cd1.getUdpPort());
                            datagramSocket.send(sendPacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
        }
    }


    public void removeClient(ClientData clientData) {
        this.idSetUdp.remove(clientData.getId());
        this.clientDataList.remove(clientData);
    }

    public void setClientUdp(Integer clientId) {
        this.idSetUdp.add(clientId);
    }

    public boolean isClientUdpSet(Integer clientId) {
        return this.idSetUdp.contains(clientId);
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

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public MulticastSocket getMulticastSocket() {
        return multicastSocket;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private String getHelp() {
        return " Usage:\n"
                + "exit - close application";
    }
}
