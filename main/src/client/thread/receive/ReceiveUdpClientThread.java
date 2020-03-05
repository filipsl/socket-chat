package client.thread.receive;

import client.Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;

public class ReceiveUdpClientThread implements Runnable {

    private Client client;

    public ReceiveUdpClientThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {

        byte[] receiveBuffer = new byte[1024];

        while (client.isRunning()) {
            Arrays.fill(receiveBuffer, (byte) '\0');
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                client.getUdpSocket().receive(receivePacket);
                String msg = new String(receivePacket.getData()).trim();
                System.out.println("received msg UDP: \n" + msg);
            } catch (SocketException e){
                client.printSynchronized("UDP socket closed.");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
