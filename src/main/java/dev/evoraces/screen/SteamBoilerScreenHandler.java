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


// === SLOTS DA MÁQUINA ===

        // Slot 0: Água (Quadrado superior esquerdo)
        // Reduzimos o X para 35 (para a esquerda) e aumentamos o Y para 21 (para baixo)
        this.addSlot(new Slot(inventory, 0, 35, 21));

        // Slot 1: Combustível (Quadrado inferior esquerdo)
        // Mesmo X (35), e descemos o Y para 57
        this.addSlot(new Slot(inventory, 1, 35, 57));

        // Slot 2: Frasco Vazio (Quadrado superior do lado direito)
        // Voltamos o X para 116 (que estava centralizado numa foto antiga) e descemos o Y para 21
        this.addSlot(new Slot(inventory, 2, 116, 21));

        // Slot 3: Saída do Vapor (Embaixo do frasco vazio)
        this.addSlot(new SteamBoilerOutputSlot(inventory, 3, 116, 57));


        // === INVENTÁRIO DO JOGADOR ===
        int m;
        int l;

        // Subimos o Y de 99 para 96 para os itens não "caírem" da grade
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 96 + m * 18));
            }
        }

        // Subimos o Y da Hotbar de 157 para 154
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 154));
        }
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