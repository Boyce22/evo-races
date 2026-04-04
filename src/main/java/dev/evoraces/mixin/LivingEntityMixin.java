package dev.evoraces.mixin;

import dev.evoraces.network.DamagePayload;
import dev.evoraces.network.EffectPayload;
import dev.evoraces.network.HealPayload;
import dev.evoraces.utils.DamageTypeUtils;
import dev.evoraces.utils.DamageVisuals;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private static final int CRITICAL_COLOR = 0xFFFFD700;

    @Unique
    private static final String CRITICAL_LABEL = "CRITICAL";

    @Unique
    private static final long UNSET_TICK = -1L;

    @Unique
    private static final long MIN_TICK_INTERVAL = 1L;

    @Unique
    private long lastDamageTick = UNSET_TICK;
    
    @Unique
    private long lastHealTick = UNSET_TICK;

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = self();
        if (!isValidDamageEvent(entity, amount))
            return;

        boolean isCrit = isCriticalHit(source);
        DamageVisuals visuals = resolveVisuals(source, isCrit);
        DamagePayload damagePayload = new DamagePayload(entity.getId(), amount, isCrit);
        EffectPayload effectPayload = buildEffectPayload(entity.getId(), visuals, isCrit);

        dispatchDamageToTrackers(entity, damagePayload, effectPayload);
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void onHeal(float amount, CallbackInfo ci) {
        LivingEntity entity = self();
        if (!isValidHealEvent(entity, amount))
            return;

        dispatchHealToTrackers(entity, new HealPayload(entity.getId(), amount));
    }

    @Unique
    private boolean isValidDamageEvent(LivingEntity entity, float amount) {
        if (entity.getWorld().isClient() || amount <= 0)
            return false;
        long currentTick = entity.getWorld().getTime();
        if (lastDamageTick != UNSET_TICK && currentTick - lastDamageTick < MIN_TICK_INTERVAL)
            return false;
        lastDamageTick = currentTick;
        return true;
    }

    @Unique
    private boolean isValidHealEvent(LivingEntity entity, float amount) {
        if (entity.getWorld().isClient())
            return false;
        if (amount <= 0 || entity.getHealth() >= entity.getMaxHealth())
            return false;
        long currentTick = entity.getWorld().getTime();
        if (lastHealTick != UNSET_TICK && currentTick - lastHealTick < MIN_TICK_INTERVAL)
            return false;
        lastHealTick = currentTick;
        return true;
    }

    @Unique
    private boolean isCriticalHit(DamageSource source) {
        if (!(source.getAttacker() instanceof PlayerEntity player))
            return false;
        return player.fallDistance > 0.0F
                && !player.isOnGround()
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !player.hasVehicle();
    }

    @Unique
    private DamageVisuals resolveVisuals(DamageSource source, boolean isCrit) {
        return isCrit
                ? new DamageVisuals(CRITICAL_LABEL, CRITICAL_COLOR)
                : DamageTypeUtils.getVisuals(source);
    }

    @Unique
    private EffectPayload buildEffectPayload(int entityId, DamageVisuals visuals, boolean isCrit) {
        if (visuals == null)
            return null;
        return new EffectPayload(entityId, visuals.text(), visuals.color(), isCrit);
    }

    @Unique
    private void dispatchDamageToTrackers(LivingEntity entity, DamagePayload damagePayload,
            EffectPayload effectPayload) {
        for (ServerPlayerEntity tracker : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(tracker, damagePayload);
            if (effectPayload != null)
                ServerPlayNetworking.send(tracker, effectPayload);
        }
        if (entity instanceof ServerPlayerEntity self) {
            ServerPlayNetworking.send(self, damagePayload);
            if (effectPayload != null)
                ServerPlayNetworking.send(self, effectPayload);
        }
    }

    @Unique
    private void dispatchHealToTrackers(LivingEntity entity, HealPayload payload) {
        for (ServerPlayerEntity tracker : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(tracker, payload);
        }
        if (entity instanceof ServerPlayerEntity self) {
            ServerPlayNetworking.send(self, payload);
        }
    }

    @Unique
    private LivingEntity self() {
        return (LivingEntity) (Object) this;
    }
}