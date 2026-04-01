package dev.evoraces.block.entity;

import dev.evoraces.EvoRaces;
import dev.evoraces.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<SteamBoilerBlockEntity> STEAM_BOILER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(EvoRaces.MOD_ID, "steam_boiler_be"),
                    FabricBlockEntityTypeBuilder.create(SteamBoilerBlockEntity::new,
                            ModBlocks.STEAM_BOILER).build());

    public static void registerBlockEntities() {
        EvoRaces.LOGGER.info("Registrando Block Entities do EvoRaces...");
    }
}