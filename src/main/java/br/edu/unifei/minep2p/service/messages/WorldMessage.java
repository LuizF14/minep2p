package br.edu.unifei.minep2p.service.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class WorldMessage extends ProtocolMessage {
    private long bodyLength;
    private Path bodyPath;
    private String worldName;
    
    public InputStream getBodyStream() {
        return this.body;
    }
    
    public long getBodyLength() {
        return bodyLength;
    }

    public String getWorldName() {
        return worldName;
    }

    public WorldMessage() {}

    public WorldMessage(ProtocolMessage baseMsg) throws IOException {
        super(baseMsg);

        if (this.args.size() < 1) {
            throw new IOException("Está faltando o argumento de body_length.");
        }

        try {
            this.bodyLength = Long.parseLong(this.args.get(0)); 
        } catch (NumberFormatException e) {
            throw new IOException("O argumento de body_length não é um número válido.");
        }

        if (this.bodyLength <= 0) {
            throw new IOException("Nenhum mundo foi enviado");
        }

        this.worldName = this.args.get(1);
    }

    public static WorldMessage creatMessage(Path bodyPath, String worldName) throws IOException {
        WorldMessage msg = new WorldMessage();
        msg.header = "WORLD";
        msg.bodyPath = bodyPath;
        msg.worldName = worldName;

        try {
            msg.bodyLength = Files.size(bodyPath);
        } catch (IOException e) {
            msg.bodyLength = -1; 
        }

        if (msg.bodyLength <= 0) {
            throw new IOException("Arquivo vazio ou inválido!");
        }

        return msg;
    }

    @Override
    public void serializeAndCopyTo(OutputStream out) throws IOException {
        this.args.add(String.valueOf(this.bodyLength));
        this.args.add(worldName);

        super.serializeAndCopyTo(out);
        
        try (InputStream fileIn = Files.newInputStream(this.bodyPath)) {
            long bytesCopied = fileIn.transferTo(out); 
            
            if (bytesCopied != this.bodyLength) {
                throw new IOException("Bytes enviados (" + bytesCopied + ") diferente do esperado (" + this.bodyLength + ")");
            }
        }
    }
}
