package server.thread.receive;

import server.Server;
import server.data.ClientData;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EstablishTcpThread implements Runnable{

    private final Server server;
    private final int portNumber;
    private final int backlog;
    private final InetAddress inetAddress;

    public EstablishTcpThread(Server server, int portNumber, int backlog, InetAddress inetAddress){
        this.server = server;
        this.portNumber = portNumber;
        this.backlog = backlog;
        this.inetAddress = inetAddress;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            // create socket
            serverSocket = new ServerSocket(this.portNumber, this.backlog, this.inetAddress);

            while(true){

                // accept client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: id: " + server.getClientIdCounter());

                ClientData clientData = new ClientData(server.getClientIdCounter(), clientSocket);
                server.addClient(clientData);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
