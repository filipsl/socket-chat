package server.thread.receive;

import server.data.ClientData;

import java.io.IOException;


public class ReceiveTcpThread implements Runnable {
    private final ClientData clientData;
    //todo this thread will be blocked on tcp in buffer of each client - if readLine returns null, connection closed

    public ReceiveTcpThread(ClientData clientData) {
        this.clientData = clientData;
    }

    @Override
    public void run() {
        try {
            String msg = clientData.getTcpIn().readLine();
            System.out.println("received msg: " + msg);
            clientData.getTcpOut().println("Pong Java Tcp");
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
