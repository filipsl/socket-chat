package client.thread.receive;

import client.Client;

public class ReceiveMulticastClientThread implements Runnable {

    private Client client;

    public ReceiveMulticastClientThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {

    }
}
