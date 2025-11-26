package br.edu.unifei.minep2p.service.handlers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import br.edu.unifei.minep2p.infrastructure.NetworkSender;
import br.edu.unifei.minep2p.infrastructure.NetworkServer;
import br.edu.unifei.minep2p.service.messages.ConToMeMessage;
import br.edu.unifei.minep2p.service.messages.ListPlayersMessage;
import br.edu.unifei.minep2p.service.messages.ProtocolMessage;
public class ListPlayersHandler extends ConnectionHandler {
    private ListPlayersMessage msg;

    public ListPlayersHandler(Socket socket, ProtocolMessage msg, NetworkServer server) throws IOException {
        super(socket, server);
        this.msg = new ListPlayersMessage(msg);
    }

    @Override
    public void handle() throws IOException {
        ConToMeMessage conmsg = this.server.state.getLastConToMeMessage();

        if (conmsg == null) {
            throw new RuntimeException("A espera pela mensagem foi interrompida.");
        }

        for (InetAddress player : this.msg.players) {
            try {
                NetworkSender.send(conmsg, player);
            } catch (Exception e) {
                throw new RuntimeException("Não consegui avisar o player: " + player.toString() + ", " + e.getMessage());
            }
        }
    }
}
