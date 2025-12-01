package br.edu.unifei.minep2p.infrastructure;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkIP {
    public static InetAddress getMyIP() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 80);
            return socket.getLocalAddress();
        } catch (Exception e) {
            return InetAddress.getLoopbackAddress();
        }
    }
}
