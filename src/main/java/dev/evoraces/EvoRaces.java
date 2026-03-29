package dev.evoraces;

import dev.evoraces.attribute.AttributeSystem;
import dev.evoraces.command.RaceCommand;
import dev.evoraces.data.DataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvoRaces implements ModInitializer {
    public static final String MOD_ID = "evoraces";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("EvoRaces inicializando...");
        initSystems();
        LOGGER.info("EvoRaces inicializado com sucesso!");
    }

    private void initSystems() {
        DataLoader.register();
        LOGGER.info("DataLoader registrado");

        AttributeSystem.register();
        LOGGER.info("AttributeSystem registrado");

        // Limpa entradas mortas do Map ao desconectar
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                AttributeSystem.onPlayerDisconnect(handler.player.getUuid())
        );
        LOGGER.info("Evento de desconexão registrado");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RaceCommand.register(dispatcher, registryAccess, environment);
            LOGGER.info("Comando /race registrado");
        });
    }
}