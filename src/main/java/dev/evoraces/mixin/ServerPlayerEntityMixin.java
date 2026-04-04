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

    private static final String NBT_RACE_KEY = "evoraces_race_id";

    @Unique 
    private String evoraces$raceId = null;

    @Override
    public String evoraces$getRaceId() { return evoraces$raceId; }

    @Override
    public void evoraces$setRaceId(String raceId) { this.evoraces$raceId = raceId; }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void evoraces$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        if (evoraces$raceId != null) nbt.putString(NBT_RACE_KEY, evoraces$raceId);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void evoraces$readNbt(NbtCompound nbt, CallbackInfo ci) {
        evoraces$raceId = nbt.contains(NBT_RACE_KEY) ? nbt.getString(NBT_RACE_KEY) : null;
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void evoraces$onSpawn(CallbackInfo ci) {
        forceUpdateAttributes();
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void evoraces$onCopyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        this.evoraces$raceId = ((PlayerDataHolder) oldPlayer).evoraces$getRaceId();
        forceUpdateAttributes();
    }

    @Unique
    private void forceUpdateAttributes() {
        AttributeSystem.forceUpdatePlayerAttributes((ServerPlayerEntity) (Object) this);
    }
}