package br.edu.unifei.minep2p.service.handlers;

import java.io.*;
import java.net.*;
import java.nio.file.Path;

import br.edu.unifei.minep2p.service.messages.ConToMeMessage;
import br.edu.unifei.minep2p.service.messages.ProtocolMessage;
import br.edu.unifei.minep2p.service.messages.WorldMessage;
import br.edu.unifei.minep2p.infrastructure.DirectoryZipper;
import br.edu.unifei.minep2p.infrastructure.FileReceiver;
import br.edu.unifei.minep2p.infrastructure.NetworkServer;
import br.edu.unifei.minep2p.infrastructure.NetworkState;
import br.edu.unifei.minep2p.minecraft.WorldFiles;
import br.edu.unifei.minep2p.minecraft.WorldSwitcher;

public class WorldHandler extends ConnectionHandler {
    private WorldMessage msg;

    public WorldHandler(Socket socket, ProtocolMessage msg, NetworkServer server) throws IOException {
        super(socket, server);
        this.msg = new WorldMessage(msg);
    }

    @Override
    public void handle() throws IOException {
        Path zipPath = FileReceiver.receiveFile(this.msg.getBodyLength(), this.msg.getBodyStream());

        System.out.println("[MineP2P] Mundo recebido e salvo em: " + zipPath.toAbsolutePath());

        Path saveDir = WorldFiles.getSavesDir().resolve(this.msg.getWorldName());
        System.out.println("o caminho: " + saveDir.toString());
        DirectoryZipper.unzipDirectory(zipPath, saveDir);

        WorldSwitcher.disconnect();

        final NetworkState serverState = this.server.state;

        WorldSwitcher.loadWorld(this.msg.getWorldName(), () -> {
            try {
                System.out.println("Carga do mundo completa. Tentando abrir LAN...");
                WorldSwitcher.openToLan("Survival", port -> {
                    try {
                        InetAddress localHost = InetAddress.getLocalHost();
                        ConToMeMessage conmsg = ConToMeMessage.creatMessage(localHost, port, this.msg.getWorldName());
                        serverState.setLastConToMeMessage(conmsg);
                        
                        System.out.println("LAN aberto com sucesso na porta: " + port);
                        
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao criar a mensagem de conexão.", e);
                    }
                });

            } catch (Exception e) {
                throw new RuntimeException("Deu algum erro na hora de tentar abrir a LAN", e);
            }
        });
    }
}
