package br.edu.unifei.minep2p;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.unifei.minep2p.infrastructure.NetworkSender;
import br.edu.unifei.minep2p.infrastructure.NetworkServer;
import br.edu.unifei.minep2p.minecraft.TickScheduler;
import br.edu.unifei.minep2p.minecraft.TransferHostCommand;
import br.edu.unifei.minep2p.minecraft.WorldSwitcher;
import br.edu.unifei.minep2p.service.controllers.TransferHostController;

public class MineP2P implements ModInitializer {
	public static final String MOD_ID = "minep2p";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		new TransferHostCommand(new TransferHostController()).register();
		WorldSwitcher.initializeEvents();
		TickScheduler.init();

		try {
			NetworkServer netServer;
			if (System.getProperty("fabric.testclient.isclient") == null) {
				netServer = new NetworkServer(3031);
				NetworkSender.PORT = 3030;
			} else {
				netServer = new NetworkServer(3030);
				NetworkSender.PORT = 3031;
			}

			netServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}