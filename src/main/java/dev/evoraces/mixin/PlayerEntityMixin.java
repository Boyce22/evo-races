package dev.evoraces.mixin; // ← confira se este é seu pacote real

import dev.evoraces.player.PlayerDataHolder; // ← ajuste se PlayerDataHolder estiver em outro pacote
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerDataHolder {

    // ── TrackedData para sync automático ──────────────────────────────────────
    @Unique
    private static final TrackedData<String> EVORACES_RACE_ID =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    /**
     * Helper @Unique: evita o problema de @Shadow em métodos herdados.
     * O cast é seguro porque este Mixin só existe em PlayerEntity.
     */
    @Unique
    private PlayerEntity evoraces$self() {
        return (PlayerEntity)(Object)this;
    }

    // ── Inicializa o tracker ──────────────────────────────────────────────────
    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void evoraces$initDataTracker(CallbackInfo ci) {
        evoraces$self().getDataTracker().startTracking(EVORACES_RACE_ID, "");
    }

    // ── Interface PlayerDataHolder ────────────────────────────────────────────
    @Override
    public String evoraces$getRaceId() {
        return evoraces$self().getDataTracker().get(EVORACES_RACE_ID);
    }

    @Override
    public void evoraces$setRaceId(String raceId) {
        evoraces$self().getDataTracker().set(EVORACES_RACE_ID, raceId == null ? "" : raceId);
        evoraces$self().calculateDimensions(); // servidor recalcula imediatamente
    }

    // ── Dimensões da hitbox ───────────────────────────────────────────────────
    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void evoraces$getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (!"dwarf".equals(evoraces$getRaceId())) return;
        cir.setReturnValue(pose == EntityPose.CROUCHING
                ? EntityDimensions.changing(0.5f, 0.9f)
                : EntityDimensions.fixed(0.5f, 1.1f));
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void evoraces$getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions,
                                             CallbackInfoReturnable<Float> cir) {
        if (!"dwarf".equals(evoraces$getRaceId())) return;
        cir.setReturnValue(pose == EntityPose.CROUCHING ? 0.80f : 0.98f);
    }

    // ── Step Height ───────────────────────────────────────────────────────────
    @Inject(method = "tick", at = @At("HEAD"))
    private void evoraces$tick(CallbackInfo ci) {
        if ("dwarf".equals(evoraces$getRaceId())) {
            evoraces$self().setStepHeight(0.5f);
        }
    }

    // ── Persistência NBT ──────────────────────────────────────────────────────
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void evoraces$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        String id = evoraces$getRaceId();
        if (!id.isEmpty()) nbt.putString("evoraces_race_id", id);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void evoraces$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("evoraces_race_id")) {
            evoraces$setRaceId(nbt.getString("evoraces_race_id"));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void evoraces$dwarfPhysicsTick(CallbackInfo ci) {
        // Usamos o cast para a interface que criamos, assim o Java reconhece o método
        String currentRace = ((PlayerDataHolder) this).evoraces$getRaceId();

        if ("dwarf".equals(currentRace)) {
            PlayerEntity player = (PlayerEntity)(Object)this;

            // 1. Ajuste de degrau (estabilidade)
            player.setStepHeight(0.5f);

            // 2. PENALIDADE DE NADO (ANÃO DE CHUMBO)
            if (player.isTouchingWater()) {
                // Reduz a velocidade horizontal (X e Z) e vertical (Y)
                // O anão agora se move como se estivesse pesado
                player.setVelocity(player.getVelocity().multiply(0.6, 0.8, 0.6));
                player.addVelocity(0, -0.01, 0); // Puxa ele levemente para baixo constantemente
            }
        }
    }
}