package dev.evoraces.block;

import dev.evoraces.EvoRaces;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import dev.evoraces.block.custom.SteamBoilerBlock;

public class ModBlocks {

    // 1. Criar o bloco da Caldeira a Vapor
    // Usamos as propriedades do Bloco de Ferro (som de metal, força 5.0, precisa de picareta)
    public static final Block STEAM_BOILER = registerBlock("steam_boiler",
            new SteamBoilerBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).requiresTool().strength(5.0f)));

    // Função auxiliar para registar o Bloco e o Item do Bloco em simultâneo
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(EvoRaces.MOD_ID, name), block);
    }

    // Função que cria a versão "Item" do bloco para o podermos ter no inventário
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(EvoRaces.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    // Carregar os blocos e colocá-los na aba correta do Modo Criativo
    public static void registerModBlocks() {
        EvoRaces.LOGGER.info("A registar Blocos do EvoRaces...");

        // Coloca a Caldeira na aba de blocos Funcionais (junto com fornalhas e bancadas)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(STEAM_BOILER);
        });
    }
}