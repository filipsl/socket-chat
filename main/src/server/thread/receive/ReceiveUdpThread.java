package server.thread.receive;

import server.Server;
import server.data.ClientData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Arrays;

public class ReceiveUdpThread implements Runnable {

    private final Server server;

    public ReceiveUdpThread(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];
        while (server.isRunning()) {
            Arrays.fill(receiveBuffer, (byte) '\0');
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                server.getDatagramSocket().receive(receivePacket);
                String msg = new String(receivePacket.getData()).trim();
                int hashIndex = msg.indexOf("#");
                String clientIdString = msg.substring(0, hashIndex);
                try {
                    int clientId = Integer.parseInt(clientIdString);

                    if (server.isClientUdpSet(clientId)) {
                        msg = msg.substring(hashIndex + 1);
                        server.printSynchronized("received msg UDP " + clientIdString + ":\n" + msg);
                        server.sendToOthersUdp(clientId, msg);
                    } else {
                        for (ClientData clientData : server.getClientDataList()) {
                            if (clientData.getId() == clientId) {
                                clientData.setUdpPort(receivePacket.getPort());
                                clientData.setInetAddress(receivePacket.getAddress());
                                server.setClientUdp(clientId);
                            }
                        }
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } catch (SocketException e){
                if(server.getDatagramSocket().isClosed())
                    server.printSynchronized("UDP socket closed.");
                else
                    server.printSynchronized("Some error with UDP socket occurred.");
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
