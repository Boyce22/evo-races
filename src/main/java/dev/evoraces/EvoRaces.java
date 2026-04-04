package dev.evoraces;

import dev.evoraces.attribute.AttributeSystem;
import dev.evoraces.block.ModBlocks;
import dev.evoraces.block.entity.ModBlockEntities;
import dev.evoraces.command.RaceCommand;
import dev.evoraces.data.DataLoader;
import dev.evoraces.item.ModItems;
import dev.evoraces.network.ModMessages;
import dev.evoraces.player.PlayerData;
import dev.evoraces.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvoRaces implements ModInitializer {

    public static final String MOD_ID = "evoraces";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("EvoRaces inicializando...");

        registerRegistries();
        registerSystems();
        registerEvents();

        LOGGER.info("EvoRaces inicializado com sucesso!");
    }

    private void registerRegistries() {
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();
    }

    private void registerSystems() {
        DataLoader.register();
        LOGGER.info("DataLoader registrado");

        AttributeSystem.register();
        LOGGER.info("AttributeSystem registrado");
    }

    private void registerEvents() {
        registerDisconnectEvent();
        registerCommandEvent();
        registerEntityLoadEvent();
    }

    private void registerDisconnectEvent() {
        ServerPlayConnectionEvents.DISCONNECT
                .register((handler, server) -> AttributeSystem.onPlayerDisconnect(handler.player.getUuid()));
        LOGGER.info("Evento de desconexão registrado");
    }

    private void registerCommandEvent() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            RaceCommand.register(dispatcher, registryAccess, environment);
            LOGGER.info("Comando /race registrado");
        });
    }

    private void registerEntityLoadEvent() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                onPlayerLoad(player);
            }
        });
    }

    private void onPlayerLoad(ServerPlayerEntity player) {
        String raceId = PlayerData.getRaceId(player);
        player.calculateDimensions();
        ModMessages.sendRaceSync(player, raceId);
    }
}