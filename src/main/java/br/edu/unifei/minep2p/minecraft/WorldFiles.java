package br.edu.unifei.minep2p.minecraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;

import java.nio.file.*;

public class WorldFiles {
    public static Path getSavesDir() {
        MinecraftClient client = MinecraftClient.getInstance();
        LevelStorage storage = client.getLevelStorage();

        return storage.getSavesDirectory();
    }

    public static Path getWorldPath() {
        MinecraftClient client = MinecraftClient.getInstance();
        MinecraftServer server = client.getServer();

        if (server == null) {
            throw new RuntimeException("Você não é o host do server atual!");
        }

        return server.getSavePath(WorldSavePath.ROOT);
    }

    public static void saveWorld() throws RuntimeException {
        MinecraftClient client = MinecraftClient.getInstance();
        MinecraftServer server = client.getServer();
        server.saveAll(false, true, true);
    }

    public static String getCurrentWorldName() throws RuntimeException {
        MinecraftClient client = MinecraftClient.getInstance();
        MinecraftServer server = client.getServer();
        return server.getSaveProperties().getLevelName();
    }
}
