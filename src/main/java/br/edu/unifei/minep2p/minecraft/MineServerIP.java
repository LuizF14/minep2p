package br.edu.unifei.minep2p.minecraft;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerNetworkIo;
import net.minecraft.client.MinecraftClient;

public class MineServerIP {
    public static InetAddress getServerIP() throws UnknownHostException {
        MinecraftServer server = MinecraftClient.getInstance().getServer();
        InetAddress ipAddress = InetAddress.getByName(server.getServerIp());
        return ipAddress;
    }
}
