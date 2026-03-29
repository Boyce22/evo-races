package dev.evoraces;

import net.fabricmc.api.ClientModInitializer;

public class EvoRacesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // TODO: Inicializar sistemas do cliente
        EvoRaces.LOGGER.info("EvoRaces Client inicializando...");
    }
}