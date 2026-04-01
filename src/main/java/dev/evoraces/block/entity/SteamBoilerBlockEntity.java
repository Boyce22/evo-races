package dev.evoraces.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import dev.evoraces.screen.SteamBoilerScreenHandler;

public class SteamBoilerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {

    // Cria a "Memória" da máquina: Uma lista de 4 espaços (slots), todos começando vazios
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

    // Variáveis para controlar o progresso da máquina
    protected int progress = 0;
    protected int maxProgress = 72; // Tempo que demora para fazer 1 vapor
    protected int fuelTime = 0;
    protected int maxFuelTime = 0;

    public SteamBoilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STEAM_BOILER_BE, pos, state);
    }

    // Método que SALVA os dados quando você sai do mundo (para os itens não sumirem)
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("steam_boiler.progress", progress);
        nbt.putInt("steam_boiler.fuelTime", fuelTime);
        nbt.putInt("steam_boiler.maxFuelTime", maxFuelTime);
    }

    // Método que LÊ os dados quando você entra no mundo
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("steam_boiler.progress");
        fuelTime = nbt.getInt("steam_boiler.fuelTime");
        maxFuelTime = nbt.getInt("steam_boiler.maxFuelTime");
    }

    // Um metodo simples para outras classes conseguirem ver o inventário
    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    // --- MÉTODOS DA TELA (NamedScreenHandlerFactory) ---

    // Este é o nome que vai aparecer no topo da interface (Ex: "Caldeira a Vapor")
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.evoraces.steam_boiler");
    }

    // Aqui o Cérebro chama o Garçom passando os itens do jogador e da máquina
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SteamBoilerScreenHandler(syncId, playerInventory);
    }
}