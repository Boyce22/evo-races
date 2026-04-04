package dev.evoraces.mixin;

import dev.evoraces.player.PlayerDataHolder;
import dev.evoraces.race.RaceEvents;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerDataHolder {

    private static final String NBT_RACE_KEY = "evoraces_race_id";

    @Unique
    private static final EntityDimensions DWARF_NORMAL = EntityDimensions.fixed(0.5f, 1.10f);
    @Unique
    private static final EntityDimensions DWARF_CROUCHING = EntityDimensions.changing(0.5f, 0.90f);
    @Unique
    private static final EntityDimensions FAIRY_NORMAL = EntityDimensions.fixed(0.33f, 0.99f);
    @Unique
    private static final EntityDimensions FAIRY_CROUCHING = EntityDimensions.changing(0.33f, 0.82f);
    @Unique
    private static final EntityDimensions GNOME_NORMAL = EntityDimensions.fixed(0.33f, 0.99f);
    @Unique
    private static final EntityDimensions GNOME_CROUCHING = EntityDimensions.changing(0.33f, 0.82f);

    @Unique
    private static final TrackedData<String> EVORACES_RACE = DataTracker.registerData(PlayerEntity.class,
            TrackedDataHandlerRegistry.STRING);

    @Unique
    private String evoraces$pendingRace = null;

    @Unique
    private boolean evoraces$raceApplied = false;

    @Unique
    private boolean evoraces$isDwarf = false;
    
    @Unique
    private boolean evoraces$isFairy = false;

    @Unique
    private PlayerEntity evoraces$self() {
        return (PlayerEntity) (Object) this;
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void evoraces$initDataTracker(CallbackInfo ci) {
        evoraces$self().getDataTracker().startTracking(EVORACES_RACE, "");
    }

    @Override
    public String evoraces$getRaceId() {
        String race = evoraces$self().getDataTracker().get(EVORACES_RACE);
        return race.isEmpty() ? null : race;
    }

    @Override
    public void evoraces$setRaceId(String raceId) {
        String current = evoraces$self().getDataTracker().get(EVORACES_RACE);
        String oldRaceId = current.isEmpty() ? null : current;
        String normalized = (raceId == null || raceId.isEmpty()) ? "" : raceId;

        if (normalized.equals(current))
            return;

        String nullable = normalized.isEmpty() ? null : normalized;

        evoraces$self().getDataTracker().set(EVORACES_RACE, normalized);
        evoraces$rebuildPhysicsFlags(nullable);

        PlayerEntity self = evoraces$self();
        if (self.getWorld() != null && !self.getWorld().isClient) {
            self.calculateDimensions();
            RaceEvents.ON_RACE_CHANGE.invoker().onRaceChange(
                    (ServerPlayerEntity) self,
                    oldRaceId,
                    nullable);
        }
    }

    @Unique
    private void evoraces$rebuildPhysicsFlags(String race) {
        evoraces$isDwarf = "dwarf".equals(race);
        evoraces$isFairy = "fairy".equals(race);
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void evoraces$getDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        String race = evoraces$getRaceId();
        if (race == null)
            return;

        EntityDimensions dims = switch (race) {
            case "dwarf" -> pose == EntityPose.CROUCHING ? DWARF_CROUCHING : DWARF_NORMAL;
            case "fairy" -> pose == EntityPose.CROUCHING ? FAIRY_CROUCHING : FAIRY_NORMAL;
            case "gnome" -> pose == EntityPose.CROUCHING ? GNOME_CROUCHING : GNOME_NORMAL;
            default -> null;
        };

        if (dims != null) {
            cir.setReturnValue(dims);
            cir.cancel();
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void evoraces$getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions,
            CallbackInfoReturnable<Float> cir) {
        if (evoraces$getRaceId() == null)
            return;
        cir.setReturnValue(evoraces$calculateEyeHeight(pose, dimensions));
        cir.cancel();
    }

    @Unique
    private float evoraces$calculateEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        float offset = (pose == EntityPose.CROUCHING)
                ? dimensions.height * 0.15f
                : dimensions.height * 0.10f;
        return dimensions.height - offset;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void evoraces$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        String race = evoraces$getRaceId();
        if (race != null)
            nbt.putString(NBT_RACE_KEY, race);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void evoraces$readNbt(NbtCompound nbt, CallbackInfo ci) {
        evoraces$pendingRace = nbt.contains(NBT_RACE_KEY) ? nbt.getString(NBT_RACE_KEY) : null;
        evoraces$raceApplied = false;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void evoraces$tick(CallbackInfo ci) {
        evoraces$applyPendingRace();

        PlayerEntity self = evoraces$self();
        if (self.getWorld() != null && !self.getWorld().isClient) {
            evoraces$applyRacePhysics();
        }
    }

    @Unique
    private void evoraces$applyPendingRace() {
        if (evoraces$raceApplied)
            return;

        evoraces$raceApplied = true;

        if (evoraces$pendingRace == null)
            return;

        evoraces$setRaceId(evoraces$pendingRace.isEmpty() ? null : evoraces$pendingRace);
        evoraces$pendingRace = null;
    }

    @Unique
    private void evoraces$applyRacePhysics() {
        if (evoraces$isDwarf)
            evoraces$applyDwarfWaterPenalty();
        else if (evoraces$isFairy)
            evoraces$applyFairyGlide();
    }

    @Unique
    private void evoraces$applyDwarfWaterPenalty() {
        PlayerEntity player = evoraces$self();
        if (!player.isTouchingWater())
            return;

        Vec3d vel = player.getVelocity();
        player.setVelocity(vel.x * 0.6, vel.y * 0.8 - 0.01, vel.z * 0.6);
    }

    @Unique
    private void evoraces$applyFairyGlide() {
        PlayerEntity player = evoraces$self();

        if (player.isOnGround()
                || player.isSubmergedInWater()
                || player.isSneaking())
            return;

        Vec3d vel = player.getVelocity();
        if (vel.y >= 0)
            return;

        player.setVelocity(vel.x, vel.y * 0.6, vel.z);
    }
}