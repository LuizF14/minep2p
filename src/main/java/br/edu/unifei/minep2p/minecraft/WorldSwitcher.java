package br.edu.unifei.minep2p.minecraft;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.level.storage.LevelStorage;

import java.util.function.Consumer;

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

    public static void openToLan(String gameMode, Consumer<Integer> portConsumer) {
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
                portConsumer.accept(port); 
            } catch (Exception e) {
                System.err.println("Erro ao abrir para LAN: " + e.getMessage());
            }
        });
    }
}
