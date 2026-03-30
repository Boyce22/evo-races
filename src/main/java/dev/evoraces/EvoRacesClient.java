package dev.evoraces;

import dev.evoraces.client.DamageIndicatorRegistry;
import dev.evoraces.network.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvoRacesClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("evoraces/client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[EvoRaces] Client inicializando...");

        ClientPacketHandler.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> DamageIndicatorRegistry.tick());

        LOGGER.info("[EvoRaces] Client inicializado.");
    }
}