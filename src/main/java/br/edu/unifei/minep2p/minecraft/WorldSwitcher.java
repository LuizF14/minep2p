package br.edu.unifei.minep2p.minecraft;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.storage.LevelStorage;

import java.net.InetAddress;
import java.util.function.BiConsumer;

public class WorldSwitcher {
    private static volatile Runnable PENDING_ACTION = null;

    public static void initializeEvents() {
        ClientPlayConnectionEvents.JOIN.register(WorldSwitcher::onClientJoin);
    }

    private static void onClientJoin(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client) {
        if (client.isIntegratedServerRunning() && PENDING_ACTION != null) {
            if (client.getNetworkHandler() != null) {
                System.out.println("Tentando rodar PENDING_ACTION após JOIN do cliente.");
                PENDING_ACTION.run();
                PENDING_ACTION = null;
            }
        }
    }

    public static void disconnect() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.getNetworkHandler() == null) {
            throw new RuntimeException("Você não está conectado a um mundo. ");
        }

        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        handler.getConnection().disconnect(Text.literal("Desconectado automaticamente"));
    }

    public static void loadWorld(String worldName, Runnable onLoadedCallback) {
        MinecraftClient client = MinecraftClient.getInstance();
        PENDING_ACTION = onLoadedCallback;

        client.execute(() -> {
            try {
                LevelStorage storage = client.getLevelStorage();
                IntegratedServerLoader loader = new IntegratedServerLoader(client, storage);

                loader.start(worldName, () -> {
                    throw new RuntimeException("Operação cancelada.");
                });

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Não consegui carregar o mundo!", e);
            }
        });
    }

    public static void openToLan(String gameMode, BiConsumer<InetAddress, Integer> callback) {
        MinecraftClient client = MinecraftClient.getInstance();

        GameMode gmMode;
        if (gameMode.equals("Creative")) {
            gmMode = GameMode.CREATIVE;
        } else {
            gmMode = GameMode.SURVIVAL;
        }

        client.execute(() -> {
            try {
                IntegratedServer server = client.getServer();
                if (server == null) {
                    throw new RuntimeException("O mundo ainda não foi iniciado!");
                }

                boolean lanIsOn = server.openToLan(gmMode, true, 5050);

                if (!lanIsOn) {
                    throw new RuntimeException("Falha ao abrir para LAN.");
                }

                int port = server.getServerPort();
                InetAddress ip = InetAddress.getByName(server.getServerIp());
                callback.accept(ip, port);
            } catch (Exception e) {
                System.err.println("Erro ao abrir para LAN: " + e.getMessage());
            }
        });
    }


    public static void switchToServer(InetAddress ipTarget, int port) {
        MinecraftClient client = MinecraftClient.getInstance();

        System.out.println("Client world: " + client.world);
        System.out.println("Client network handler" + client.getNetworkHandler());
        System.out.println("Client server: " + client.getServer());
    
        client.execute(() -> {
            MultiplayerScreen multiplayerScreen = new MultiplayerScreen(null);
            if (client.world != null) {
                client.world.disconnect(Text.literal("Desconectado automaticamente"));
                client.disconnectWithSavingScreen();
                client.setScreen(multiplayerScreen);
            }

            TickScheduler.schedule(30, () -> {
                String ipString = ipTarget.getHostAddress();
                String addressString = ipString + ":" + port;
    
                multiplayerScreen.connect();
                ServerInfo info = new ServerInfo(
                    "LAN World",
                    addressString,
                    ServerInfo.ServerType.LAN
                );
                
                info.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.PROMPT);
                ServerAddress serverAddress = ServerAddress.parse(addressString);
                Screen prev = new MultiplayerScreen(null);  
                
                try {
                    System.out.println("Conectando a " + addressString);
                    ConnectScreen.connect(prev, client, serverAddress, info, false, null);
                } catch (RuntimeException e) {
                    System.out.println("Erro ao conectar: " + e.getMessage() + "\n " + e.getStackTrace());
                }
            });

        });
    }
}
