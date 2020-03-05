package server.thread.receive;

import server.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;

public class ReceiveMulticastThread implements Runnable {

    private Server server;

    public ReceiveMulticastThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];
        while (server.isRunning()) {
            Arrays.fill(receiveBuffer, (byte) '\0');
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                server.getMulticastSocket().receive(receivePacket);
                String msg = new String(receivePacket.getData()).trim();
                System.out.println("received msg multicast: \n" + msg);
            } catch (SocketException e){
                if(server.getMulticastSocket().isClosed())
                    server.printSynchronized("Multicast socket closed.");
                else
                    server.printSynchronized("Some error occurred with multicast socket.");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
