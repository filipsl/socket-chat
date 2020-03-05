package server.thread.receive;

import server.Server;
import server.data.ClientData;

import java.io.IOException;
import java.net.SocketException;


public class ReceiveTcpThread implements Runnable {
    private final ClientData clientData;
    private final Server server;

    public ReceiveTcpThread(Server server, ClientData clientData) {
        this.clientData = clientData;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            String nick = clientData.getTcpIn().readLine();
            clientData.setNick(nick);
            if (server.getClientLimit() >= server.getClientDataList().size()) {
                clientData.getTcpOut().println(clientData.getId());
                boolean clientAvailable = true;
                while (clientAvailable) {
                    String msg = clientData.getTcpIn().readLine();
                    if (msg != null) {
                        server.printSynchronized("Received msg TCP: " + clientData + ":\n" + msg);
                        server.sendToOthersTcp(this.clientData, msg);
                    } else {
                        server.removeClient(clientData);
                        clientAvailable = false;
                        server.printSynchronized("Client " + clientData + " disconnected.");
                    }
                }
            } else {
                // Cannot accept new clients
                clientData.getTcpOut().println("-1");
            }
        } catch (SocketException e) {
            if (clientData.getTcpSocket().isClosed())
                server.printSynchronized(clientData + " TCP socket closed.");
            else
                server.printSynchronized("Some error with " + clientData + " TCP socket occurred.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
