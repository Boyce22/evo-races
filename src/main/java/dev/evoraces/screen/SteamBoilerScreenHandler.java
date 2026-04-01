package dev.evoraces.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class SteamBoilerScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    // Construtor do Cliente (quando você abre a tela no seu jogo)
    public SteamBoilerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(4));
    }

    // Construtor do Servidor (onde os itens realmente estão salvos)
    public SteamBoilerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ModScreenHandlers.STEAM_BOILER_SCREEN_HANDLER, syncId);
        checkSize(inventory, 4);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // NOSSOS SLOTS DA MÁQUINA (Coordenadas X e Y da tela)
        this.addSlot(new Slot(inventory, 0, 80, 11)); // Slot 0: Água (Em cima)
        this.addSlot(new Slot(inventory, 1, 80, 59)); // Slot 1: Carvão (Embaixo)
        this.addSlot(new Slot(inventory, 2, 50, 34)); // Slot 2: Frasco Vazio (Esquerda)
        this.addSlot(new Slot(inventory, 3, 110, 34)); // Slot 3: Frasco de Vapor (Direita)

        // SLOTS DO JOGADOR (Sua mochila e hotbar)
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // Deixando vazio por enquanto para não bugar o Shift+Click
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    // --- Funções matemáticas padrão para desenhar a mochila do jogador ---
    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}