package dev.evoraces;

import dev.evoraces.client.FloatingNumberRegistry;
import dev.evoraces.client.StatusTextRegistry;
import dev.evoraces.network.ClientPacketHandler;
import dev.evoraces.network.ModMessages;
import dev.evoraces.screen.ModScreenHandlers;
import dev.evoraces.screen.SteamBoilerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvoRacesClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("evoraces/client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("EvoRaces Client inicializando...");

        ClientPacketHandler.register();
        ModMessages.registerS2CPackets();
        registerScreens();
        registerTickEvents();

        LOGGER.info("EvoRaces Client inicializado.");
    }

    private void registerScreens() {
        HandledScreens.register(ModScreenHandlers.STEAM_BOILER_SCREEN_HANDLER, SteamBoilerScreen::new);
    }

    private void registerTickEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FloatingNumberRegistry.tick();
            StatusTextRegistry.tick();
        });
    }
}