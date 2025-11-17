package br.edu.unifei.minep2p.service.messages;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtocolMessage {
    protected String header;
    protected List<String> args = new ArrayList<String>();
    protected InputStream body;
    protected PrintWriter writer;

    public ProtocolMessage() {}

    // Construtor de cópia
    public ProtocolMessage(ProtocolMessage other) {
        this.header = other.header;
        this.args = new ArrayList<>(other.args);
        this.body = other.body;
    }

    public static ProtocolMessage readMessage(InputStream in) throws IOException {
        ProtocolMessage msg = new ProtocolMessage();
        ByteArrayOutputStream headerBytes = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') break;
            headerBytes.write(b);
        }

        if (headerBytes.size() == 0) {
            throw new IOException("Cabeçalho vazio ou conexão interrompida.");
        }

        String[] headerLine = headerBytes.toString(StandardCharsets.UTF_8).trim().split("\\s+");

        msg.header = headerLine[0];
        msg.args = new ArrayList<>();
        if (headerLine.length > 1) {
            msg.args.addAll(Arrays.asList(headerLine).subList(1, headerLine.length));
        }

        msg.body = in;
        return msg;
    }

    public void serializeAndCopyTo(OutputStream out) throws IOException {
        this.writer = new PrintWriter(new OutputStreamWriter(out), true);
        
        StringBuilder headerLine = new StringBuilder();
        headerLine.append(this.header);
        
        for (String arg : this.args) {
            headerLine.append(" ").append(arg);
        }
        headerLine.append("\n"); 

        writer.write(headerLine.toString());
        writer.flush();
    }

    public String getHeader() {
        return header;
    }

    public List<String> getArgs() {
        return args;
    }
}
