package br.edu.unifei.minep2p.infrastructure;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.unifei.minep2p.service.handlers.ConToMeHandler;
import br.edu.unifei.minep2p.service.handlers.ConnectionHandler;
import br.edu.unifei.minep2p.service.handlers.ListPlayersHandler;
import br.edu.unifei.minep2p.service.handlers.WorldHandler;
import br.edu.unifei.minep2p.service.messages.ProtocolMessage;

public class NetworkServer {
    public int PORT;

    private final ExecutorService acceptExecutor;
    private final ExecutorService handlerExecutor;
    private boolean running = false; 

    public NetworkState state;

    public NetworkServer(int port) {
        this.PORT = port;
        this.state = new NetworkState();
        this.acceptExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "MineP2P-AcceptLoop");
            t.setDaemon(true);
            return t;
        });
        this.handlerExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "MineP2P-Handler");
            t.setDaemon(true);
            return t;
        });
    }

    public CompletableFuture<Void> start() throws IOException {
        running = true;
        return CompletableFuture.runAsync(() -> {
            try {
                listenLoop();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }, acceptExecutor);
    }

    private void listenLoop() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[MineP2P] Servidor P2P ouvindo na porta " + PORT);
            
            while (running && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("[MineP2P] Conexão recebida de " + socket.getInetAddress());

                ConnectionHandler handler = this.create(socket);
                CompletableFuture.runAsync(() -> {
                    try {
                        handler.handle();
                    } catch (IOException ex) {
                        throw new CompletionException(ex);
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException ignored) { }
                    }
                }, handlerExecutor).exceptionally(t -> {
                    System.err.println("[MineP2P] Erro no handler para " + socket.getInetAddress() + ": " + t.getMessage());
                    t.printStackTrace();
                    return null;
                });
            }
        } catch (IOException e) {
            throw e;
        }
    }

    public ConnectionHandler create(Socket socket) throws IOException {
        ProtocolMessage msg = ProtocolMessage.readMessage(socket.getInputStream());
        return switch (msg.getHeader()) {
            case "WORLD" -> new WorldHandler(socket, msg, this);
            case "LISTPLAYERS" -> new ListPlayersHandler(socket, msg, this);
            case "CONTOME" -> new ConToMeHandler(socket, msg, this);
            default -> throw new IOException("Mensagem desconhecida no protocolo", null);
        };
    }

    public void stop() {
        running = false;
        acceptExecutor.shutdownNow();
    }
}
