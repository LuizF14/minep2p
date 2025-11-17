package br.edu.unifei.minep2p.minecraft;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayersManager {
    public static List<ServerPlayerEntity> getAllConnectedPlayers() {
        MinecraftClient client = MinecraftClient.getInstance();
        MinecraftServer server = client.getServer();
        PlayerManager playerManager = server.getPlayerManager();
        return playerManager.getPlayerList();
    }

    public static List<InetAddress> getAllConnectedPlayersIP() {
        List<InetAddress> ipList = new ArrayList<>();
        for (ServerPlayerEntity player : getAllConnectedPlayers()) {
            try {
                SocketAddress socketAddress = player.networkHandler.getConnectionAddress();
                
                if (socketAddress instanceof InetSocketAddress inet) {
                    InetAddress ip = inet.getAddress();
                    ipList.add(ip);
                }
            } catch (Exception e) {
                System.err.println("Erro ao obter IP do jogador " + player.getName().getString() + ": " + e);
            }
        }
        return ipList;
    }

    public static List<String> getAllConnectedPlayerNames() {
        return getAllConnectedPlayers().stream()
            .map(player -> player.getName().getString())
            .toList();
    }
}
