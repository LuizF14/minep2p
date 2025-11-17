package br.edu.unifei.minep2p.service.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class ListPlayersMessage extends ProtocolMessage {
    public List<InetAddress> players;

    public ListPlayersMessage() {}

    public ListPlayersMessage(ProtocolMessage baseMsg) throws IOException {
        super(baseMsg);

        try (DataInputStream dis = new DataInputStream(this.body)) {
            
            this.players = new ArrayList<>();
            int playerCount = dis.readInt();
            
            for (int i = 0; i < playerCount; i++) {
                int addressLength = dis.readInt();
                byte[] addressBytes = new byte[addressLength];
                dis.readFully(addressBytes); 

                InetAddress address = InetAddress.getByAddress(addressBytes);
                
                this.players.add(address);
            }
        }
    }

    public static ListPlayersMessage createMessage(List<InetAddress> players) throws IOException {
        ListPlayersMessage msg = new ListPlayersMessage();
        msg.header = "LISTPLAYERS";
        msg.players = players;

        return msg;
    }

    @Override
    public void serializeAndCopyTo(OutputStream out) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            dos.writeInt(players.size());

            for (InetAddress player : players) {
                byte[] addressBytes = player.getAddress();
                
                dos.writeInt(addressBytes.length);
                dos.write(addressBytes);
            }
            
            dos.flush();
            this.body = new ByteArrayInputStream(baos.toByteArray());
        }

        super.serializeAndCopyTo(out);
        this.body.transferTo(out);
    }
}
