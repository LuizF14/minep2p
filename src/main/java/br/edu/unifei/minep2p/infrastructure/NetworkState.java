package br.edu.unifei.minep2p.infrastructure;

import br.edu.unifei.minep2p.service.messages.ConToMeMessage;

public class NetworkState {
    private ConToMeMessage lastConToMeMessage = null;

    public synchronized ConToMeMessage getLastConToMeMessage() {
        while (lastConToMeMessage == null) {
            try {
                wait(); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                return null;
            }
        }

        ConToMeMessage tmp = lastConToMeMessage;
        lastConToMeMessage = null;
        return tmp;
    }

    public synchronized void setLastConToMeMessage(ConToMeMessage msg) {
        this.lastConToMeMessage = msg;
        notifyAll();
    }
}
