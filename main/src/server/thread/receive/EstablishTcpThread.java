package server.thread.receive;

import server.Server;
import server.data.ClientData;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class EstablishTcpThread implements Runnable {

    private final Server server;
    private final int portNumber;
    private final int backlog;
    private final InetAddress inetAddress;

    public EstablishTcpThread(Server server, int portNumber, int backlog, InetAddress inetAddress) {
        this.server = server;
        this.portNumber = portNumber;
        this.backlog = backlog;
        this.inetAddress = inetAddress;
    }

    @Override
    public void run() {
        try {
            server.setServerSocket(new ServerSocket(this.portNumber, this.backlog, this.inetAddress));

            while (server.isRunning()) {
                Socket clientSocket = server.getServerSocket().accept();
                server.printSynchronized("New client accepted: id: " + server.getClientIdCounter());
                ClientData clientData = new ClientData(server.getClientIdCounter(), clientSocket);
                server.addClient(clientData);
            }

        } catch (SocketException e) {
            if(server.getServerSocket().isClosed())
                server.printSynchronized("Socket establishing TCP connections closed.");
            else
                server.printSynchronized("Some error with socket establishing connections occurred.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
