package dev.evoraces;

import dev.evoraces.network.ModMessages;
import net.fabricmc.api.ClientModInitializer;

public class EvoRacesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Liga o rádio do Cliente para escutar as mensagens do Servidor
        ModMessages.registerS2CPackets();

        EvoRaces.LOGGER.info("EvoRaces Cliente inicializado e escutando a rede!");
    }
}