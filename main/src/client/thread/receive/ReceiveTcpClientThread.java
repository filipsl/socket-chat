package client.thread.receive;

import client.Client;

import java.io.IOException;
import java.net.SocketException;

public class ReceiveTcpClientThread implements Runnable {
    private final Client client;

    public ReceiveTcpClientThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        boolean serverActive = true;
        while (serverActive && client.isRunning()) {
            String msg;
            try {
                msg = client.getIn().readLine();
                if (msg == null) {
                    client.printSynchronized("TCP connection closed");
                    try {
                        client.getTcpSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    serverActive = false;
                } else {
                    client.printSynchronized(msg);
                }
            } catch (SocketException e) {
                client.printSynchronized("TCP socket closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
