package server.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

public class ClientData {

    private final int id;
    private final Socket tcpSocket;
    private int udpPort;
    private InetAddress inetAddress;
    private String nick;

    private PrintWriter tcpOut;
    private BufferedReader tcpIn;

    public ClientData(int id, Socket tcpSocket) throws IOException {
        this.id = id;
        this.tcpSocket = tcpSocket;
        this.tcpOut = new PrintWriter(this.tcpSocket.getOutputStream(), true);
        this.tcpIn = new BufferedReader(new InputStreamReader(this.tcpSocket.getInputStream()));
    }


    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getId() {
        return id;
    }

    public PrintWriter getTcpOut() {
        return tcpOut;
    }

    public BufferedReader getTcpIn() {
        return tcpIn;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Socket getTcpSocket() {
        return tcpSocket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientData that = (ClientData) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return this.getId() + "#" + this.getNick();
    }
}
