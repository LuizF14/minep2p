package br.edu.unifei.minep2p.service.handlers;

import java.io.IOException;
import java.net.*;

import br.edu.unifei.minep2p.infrastructure.NetworkServer;

public abstract class ConnectionHandler {
    protected Socket socket;
    protected NetworkServer server;

    public ConnectionHandler(Socket socket, NetworkServer server) {
        this.socket = socket;
        this.server = server;
    }

    public abstract void handle() throws IOException;
}
