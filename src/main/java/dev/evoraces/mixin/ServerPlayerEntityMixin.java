package dev.evoraces.mixin;

import dev.evoraces.attribute.AttributeSystem;
import dev.evoraces.player.PlayerDataHolder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements PlayerDataHolder {

    @Unique private String evoraces$raceId = null;

    private static final String NBT_KEY      = "EvoRacesData";
    private static final String RACE_ID_KEY  = "race_id";

    @Override
    public String evoraces$getRaceId() { return evoraces$raceId; }

    @Override
    public void evoraces$setRaceId(String raceId) { this.evoraces$raceId = raceId; }

    // Salva no NBT do jogador — chamado automaticamente pelo Minecraft ao salvar
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void evoraces$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound data = new NbtCompound();
        if (evoraces$raceId != null) data.putString(RACE_ID_KEY, evoraces$raceId);
        nbt.put(NBT_KEY, data);
    }

    // Carrega do NBT — chamado automaticamente pelo Minecraft ao carregar
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void evoraces$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(NBT_KEY)) {
            NbtCompound data = nbt.getCompound(NBT_KEY);
            evoraces$raceId = data.contains(RACE_ID_KEY) ? data.getString(RACE_ID_KEY) : null;
        }
    }

    // Aplica atributos ao spawnar (primeiro login ou respawn)
    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void evoraces$onPlayerSpawn(CallbackInfo ci) {
        AttributeSystem.forceUpdatePlayerAttributes((ServerPlayerEntity) (Object) this);
    }

    // Copia a raça do jogador anterior após morte ou troca de dimensão
    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void evoraces$onPlayerCopy(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.evoraces$raceId = ((PlayerDataHolder) oldPlayer).evoraces$getRaceId();
        AttributeSystem.forceUpdatePlayerAttributes((ServerPlayerEntity) (Object) this);
    }
}