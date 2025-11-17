package br.edu.unifei.minep2p.infrastructure;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import br.edu.unifei.minep2p.service.messages.ProtocolMessage;

public class NetworkSender {
    public static int PORT;

    public static void send(ProtocolMessage msg, InetAddress ipTarget) throws IOException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                try (Socket socket = new Socket(ipTarget, PORT);
                     OutputStream out = socket.getOutputStream();) {
                    msg.serializeAndCopyTo(out);
                    out.flush();
                }
            } catch (Exception e) {
                throw new RuntimeException("Erro durante o envio das mensagens: " + e.getMessage());
            }
        });

        try {
            future.join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
            if (e.getCause() instanceof InterruptedException) throw (InterruptedException) e.getCause();
            throw e; 
        }
    }
}
