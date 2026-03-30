package dev.evoraces;

import dev.evoraces.client.FloatingNumberRegistry;
import dev.evoraces.client.StatusTextRegistry; // <-- IMPORTANTE
import dev.evoraces.network.ClientPacketHandler;
import dev.evoraces.network.ModMessages;
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

        // Atualizamos os dois sistemas a cada tick do jogo
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FloatingNumberRegistry.tick();
            StatusTextRegistry.tick(); // <-- NOVO: Faz os textos sumirem após 2 segundos
        });

        LOGGER.info("[EvoRaces] Client inicializado.");

        // Liga o rádio do Cliente para escutar as mensagens do Servidor
        ModMessages.registerS2CPackets();

        EvoRaces.LOGGER.info("EvoRaces Cliente inicializado e escutando a rede!");
    }
}