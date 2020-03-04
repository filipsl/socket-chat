package client.thread.receive;

import client.Client;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceiveTcpClientThread implements Runnable {
    private final Client client;
    private final BufferedReader in;

    public ReceiveTcpClientThread(Client client, BufferedReader in) {
        this.client = client;
        this.in = in;
    }

    @Override
    public void run() {
        while (true) {
            String msg = null;
            try {
                msg = this.in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (msg == null) {
                client.printSynchronized("TCP Connection closed");
            } else {
                client.printSynchronized(msg);
            }
        }
    }
}
