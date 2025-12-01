package br.edu.unifei.minep2p.minecraft;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TickScheduler {
    private static int ticksRemaining = -1;
    private static Runnable task = null;

    public static void schedule(int ticks, Runnable r) {
        ticksRemaining = ticks;
        task = r;
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ticksRemaining > 0) {
                ticksRemaining--;
                System.out.println("tick");
            } else if (ticksRemaining == 0) {
                ticksRemaining = -1;
                if (task != null) {
                    task.run();
                    task = null;
                }
            }
        });
    }
}
