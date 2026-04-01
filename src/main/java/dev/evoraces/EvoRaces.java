package dev.evoraces;

import dev.evoraces.attribute.AttributeSystem;
import dev.evoraces.item.ModItems;
import dev.evoraces.block.ModBlocks;
import dev.evoraces.command.RaceCommand;
import dev.evoraces.data.DataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import dev.evoraces.block.entity.ModBlockEntities;
import dev.evoraces.screen.ModScreenHandlers;

public class EvoRaces implements ModInitializer {
    public static final String MOD_ID = "evoraces";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("EvoRaces inicializando...");
        initSystems();
        LOGGER.info("EvoRaces inicializado com sucesso!");
        // Registar itens e blocos primeiro
        ModItems.registerModItems();
        ModBlocks.registerModBlocks(); // <-- Chama a função aqui!
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        // (O teu código existente da física e eventos continua aqui para baixo)

        LOGGER.info("EvoRaces inicializado!");
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

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                // O Minecraft 1.20.1 às vezes pede que você use o player diretamente
                String raceId = dev.evoraces.player.PlayerData.getPlayerRaceId(player);

                // Se a linha abaixo ainda der erro, apenas delete ela.
                // O Mixin acima já vai cuidar da física assim que o jogador se mexer.
                player.calculateDimensions();

                dev.evoraces.network.ModMessages.sendRaceSync(player, raceId);
            }
        });
    }
}