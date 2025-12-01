package br.edu.unifei.minep2p.service.handlers;

import java.io.IOException;
import java.net.Socket;

import br.edu.unifei.minep2p.infrastructure.NetworkServer;
import br.edu.unifei.minep2p.minecraft.WorldSwitcher;
import br.edu.unifei.minep2p.service.messages.ConToMeMessage;
import br.edu.unifei.minep2p.service.messages.ProtocolMessage;

public class ConToMeHandler extends ConnectionHandler {
    private ConToMeMessage msg;

    public ConToMeHandler(Socket socket, ProtocolMessage msg, NetworkServer server) throws IOException {
        super(socket, server);
        this.msg = new ConToMeMessage(msg);
    }

    @Override
    public void handle() throws IOException {
        WorldSwitcher.switchToServer(msg.getNewHostAddress(), msg.getPort());
        // WorldSwitcher.disconnect();
        // WorldSwitcher.connectTo(msg.getNewHostAddress(), msg.getPort());
    }
}
