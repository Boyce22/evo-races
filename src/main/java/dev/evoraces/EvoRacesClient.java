package dev.evoraces;


import dev.evoraces.client.FloatingNumberRegistry;
import dev.evoraces.client.StatusTextRegistry;
import dev.evoraces.client.race.RaceSelectionManager;
import dev.evoraces.client.race.RaceSelectionScreen;
import dev.evoraces.network.ClientPacketHandler;
import dev.evoraces.network.ModMessages;
import dev.evoraces.screen.ModScreenHandlers;
import dev.evoraces.screen.SteamBoilerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.server.MinecraftServer;

import java.io.File;

public class EvoRacesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        EvoRaces.LOGGER.info("[EvoRaces] Client inicializando...");

        ClientPacketHandler.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FloatingNumberRegistry.tick();
            StatusTextRegistry.tick();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null && client.getServer() != null && !RaceSelectionManager.getInstance().hasReceivedServerData()) {
                MinecraftServer server = client.getServer();
                File saveDir = server.getRunDirectory();
                RaceSelectionManager.getInstance().onWorldJoin(saveDir);
                
                if (RaceSelectionManager.getInstance().shouldShowSelection()) {
                    client.setScreen(new RaceSelectionScreen(client.currentScreen));
                }
                
                RaceSelectionManager.getInstance().setReceivedServerData(true);
            }
        });

        ModMessages.registerS2CPackets();

        EvoRaces.LOGGER.info("EvoRaces Cliente inicializado e escutando a rede!");

        HandledScreens.register(ModScreenHandlers.STEAM_BOILER_SCREEN_HANDLER, SteamBoilerScreen::new);
    }
}
