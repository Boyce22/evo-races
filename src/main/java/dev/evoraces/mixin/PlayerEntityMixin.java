package dev.evoraces.mixin;

import dev.evoraces.player.PlayerDataHolder;
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

    @Unique
    private static final TrackedData<String> EVORACES_RACE_ID =
            DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.STRING);

    @Unique
    private String evoraces$lastKnownRace = "";

    // --- DIMENSÕES DO ANÃO ---
    @Unique
    private static final EntityDimensions DWARF_NORMAL = EntityDimensions.fixed(0.5f, 1.1f);
    @Unique
    private static final EntityDimensions DWARF_CROUCHING = EntityDimensions.changing(0.5f, 0.9f);

    // --- DIMENSÕES DA FADA ---
    @Unique
    private static final EntityDimensions FAIRY_NORMAL = EntityDimensions.fixed(0.33f, 0.99f);

    @Unique
    private static final EntityDimensions FAIRY_CROUCHING = EntityDimensions.changing(0.33f, 0.82f);

    @Unique
    private PlayerEntity evoraces$self() {
        return (PlayerEntity)(Object)this;
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void evoraces$initDataTracker(CallbackInfo ci) {
        // Mude para "fairy" aqui se quiser testar a fada direto no spawn
        evoraces$self().getDataTracker().startTracking(EVORACES_RACE_ID, "dwarf");
    }

    @Override
    public String evoraces$getRaceId() {
        return evoraces$self().getDataTracker().get(EVORACES_RACE_ID);
    }

    @Override
    public void evoraces$setRaceId(String raceId) {
        evoraces$self().getDataTracker().set(EVORACES_RACE_ID, raceId == null ? "" : raceId);
        evoraces$self().calculateDimensions();
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void evoraces$getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        String race = evoraces$getRaceId();

        if ("dwarf".equals(race)) {
            cir.setReturnValue(pose == EntityPose.CROUCHING ? DWARF_CROUCHING : DWARF_NORMAL);
        } else if ("fairy".equals(race)) {
            cir.setReturnValue(pose == EntityPose.CROUCHING ? FAIRY_CROUCHING : FAIRY_NORMAL);
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void evoraces$getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        String race = evoraces$getRaceId();

        if ("dwarf".equals(race)) {
            cir.setReturnValue(pose == EntityPose.CROUCHING ? 0.80f : 0.98f);
        } else if ("fairy".equals(race)) {
            cir.setReturnValue(pose == EntityPose.CROUCHING ? 0.74f : 0.9f);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void evoraces$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        String id = evoraces$getRaceId();
        if (id != null && !id.isEmpty()) nbt.putString("evoraces_race_id", id);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void evoraces$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("evoraces_race_id")) {
            evoraces$setRaceId(nbt.getString("evoraces_race_id"));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void evoraces$tickMovement(CallbackInfo ci) {
        PlayerEntity player = evoraces$self();
        String currentRace = evoraces$getRaceId();
        if (currentRace == null) currentRace = "";

        // Sincroniza dimensões quando a raça muda
        if (!currentRace.equals(evoraces$lastKnownRace)) {
            evoraces$lastKnownRace = currentRace;
            player.calculateDimensions();

            // Step height (altura do degrau que sobe sem pular)
            if ("dwarf".equals(currentRace)) {
                player.setStepHeight(0.5f);
            } else if ("fairy".equals(currentRace)) {
                player.setStepHeight(0.4f);
            } else {
                player.setStepHeight(0.6f);
            }
        }

        // --- FÍSICA ESPECÍFICA ---

        // 1. Anão na água (Lógica original mantida)
        if ("dwarf".equals(currentRace) && player.isTouchingWater()) {
            player.setVelocity(player.getVelocity().multiply(0.6, 0.8, 0.6));
            player.addVelocity(0, -0.01, 0);
        }

        // 2. Fada no ar (Queda lenta/Voo leve)
        if ("fairy".equals(currentRace) && !player.isOnGround() && !player.isSubmergedInWater()) {
            if (player.getVelocity().y < 0 && !player.isSneaking()) {
                player.setVelocity(player.getVelocity().multiply(1.0, 0.6, 1.0));
            }
        }
    }
}