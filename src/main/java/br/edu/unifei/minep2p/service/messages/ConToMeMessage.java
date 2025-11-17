package br.edu.unifei.minep2p.service.messages;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

public class ConToMeMessage extends ProtocolMessage {
    private InetAddress newHostAddress;
    private int port;
    private String worldName;

    public ConToMeMessage() {}

    public ConToMeMessage(ProtocolMessage baseMsg) throws IOException {
        super(baseMsg);

        this.newHostAddress = InetAddress.getByName(this.args.get(0));
        this.port = Integer.parseInt(this.args.get(1));
        this.worldName = this.args.get(2);
    }

    public static ConToMeMessage creatMessage(InetAddress newHostAddress, int port, String worldName) throws IOException {
        ConToMeMessage msg = new ConToMeMessage();
        msg.header = "CONTOME";
        msg.newHostAddress = newHostAddress;
        msg.port = port;
        msg.worldName = worldName;

        return msg;
    }

    public void serializeAndCopyTo(OutputStream out) throws IOException {
        this.args.add(this.newHostAddress.toString());
        this.args.add(String.valueOf(this.port));
        this.args.add(this.worldName);

        System.out.println(this.args.toString());

        super.serializeAndCopyTo(out);
    }
}
