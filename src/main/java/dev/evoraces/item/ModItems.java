package dev.evoraces.item;

import dev.evoraces.EvoRaces;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // 1. Criar o Item "Vapor Condensado"
    // Deixei o limite de 'pack' em 16, para imitar o comportamento de itens líquidos/garrafas
    public static final Item CONDENSED_STEAM = registerItem("condensed_steam",
            new Item(new FabricItemSettings().maxCount(16)));

    // Função auxiliar para registar itens no Fabric
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(EvoRaces.MOD_ID, name), item);
    }

    // Função para carregar os itens e colocá-los nas abas do Criativo
    public static void registerModItems() {
        EvoRaces.LOGGER.info("A registar Itens do EvoRaces...");

        // Adiciona o Vapor na aba de "Ingredientes" do Modo Criativo
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(CONDENSED_STEAM);
        });
    }
}