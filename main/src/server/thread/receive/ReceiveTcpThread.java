package server.thread.receive;

import server.Server;
import server.data.ClientData;

import java.io.IOException;


public class ReceiveTcpThread implements Runnable {
    private final ClientData clientData;
    private final Server server;
    //todo this thread will be blocked on tcp in buffer of each client - if readLine returns null, connection closed

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

                while (true) {
                    String msg = clientData.getTcpIn().readLine();
                    System.out.println("Received message: " + clientData.getId() + "#" + clientData.getNick() + "\n" + msg);
                    server.sendToOthersTcp(this.clientData, msg);
                }
            } else {
                // Cannot accept new clients
                clientData.getTcpOut().println("-1");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientData.getTcpSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
