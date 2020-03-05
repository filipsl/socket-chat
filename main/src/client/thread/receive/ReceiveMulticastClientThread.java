package client.thread.receive;

import client.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;

public class ReceiveMulticastClientThread implements Runnable {

    private Client client;

    public ReceiveMulticastClientThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];
        while (client.isRunning()) {
            Arrays.fill(receiveBuffer, (byte) '\0');
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                client.getMulticastSocket().receive(receivePacket);
                String msg = new String(receivePacket.getData()).trim();
                System.out.println("received msg multicast: \n" + msg);
            } catch (SocketException e){
                if(client.getMulticastSocket().isClosed())
                    client.printSynchronized("Multicast socket closed.");
                else
                    client.printSynchronized("Some error occurred with multicast socket.");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
