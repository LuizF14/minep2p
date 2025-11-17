package br.edu.unifei.minep2p.service.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import br.edu.unifei.minep2p.infrastructure.NetworkSender;
import br.edu.unifei.minep2p.minecraft.PlayersManager;
import br.edu.unifei.minep2p.minecraft.WorldFiles;
import br.edu.unifei.minep2p.service.messages.ListPlayersMessage;
import br.edu.unifei.minep2p.service.messages.WorldMessage;
import br.edu.unifei.minep2p.infrastructure.DirectoryZipper;

public class TransferHostController extends BaseController<InetAddress> {
    @Override
    public String execute(InetAddress ipTarget) throws IOException, InterruptedException {
        CompletableFuture.runAsync(() -> {
            try {
                WorldFiles.saveWorld();
                Thread.sleep(200);
        
                Path worldPath = WorldFiles.getWorldPath();
                Path zipPath = DirectoryZipper.zipDirectory(worldPath);
                System.out.println("o mundo aqui oh: " + zipPath.toString());
                WorldMessage worldMsg = WorldMessage.creatMessage(zipPath, WorldFiles.getCurrentWorldName());
                ListPlayersMessage listMsg = ListPlayersMessage.createMessage(PlayersManager.getAllConnectedPlayersIP());

                NetworkSender.send(worldMsg, ipTarget);
                NetworkSender.send(listMsg, ipTarget);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return "";
    }
}
