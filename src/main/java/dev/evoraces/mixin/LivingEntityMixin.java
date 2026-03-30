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
    private long lastDamageTick = -1L;

    @Unique
    private long lastHealTick = -1L;

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (!isValidDamageEvent(entity, amount)) return;

        boolean isCrit = isCriticalHit(source);
        DamageVisuals visuals = resolveVisuals(source, isCrit);

        DamagePayload damagePayload = new DamagePayload(entity.getId(), amount, isCrit);
        EffectPayload effectPayload = createEffectPayload(entity.getId(), visuals, isCrit);

        dispatchPayloads(entity, damagePayload, effectPayload);
    }

    @Inject(method = "heal", at = @At("HEAD"))
    private void onHeal(float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.getWorld().isClient() || amount <= 0 || entity.getHealth() >= entity.getMaxHealth()) return;

        long currentTick = entity.getWorld().getTime();
        if (lastHealTick != -1 && currentTick - lastHealTick < 1) return;
        lastHealTick = currentTick;

        HealPayload payload = new HealPayload(entity.getId(), amount);

        for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(trackingPlayer, payload);
        }

        if (entity instanceof ServerPlayerEntity self) {
            ServerPlayNetworking.send(self, payload);
        }
    }

    /**
     * Valida se o dano deve ser processado (ignora lado cliente, dano nulo ou ‘spam’ de ticks).
     */
    @Unique
    private boolean isValidDamageEvent(LivingEntity entity, float amount) {
        if (entity.getWorld().isClient() || amount <= 0) return false;

        long currentTick = entity.getWorld().getTime();
        if (lastDamageTick != -1 && currentTick - lastDamageTick < 1) return false;

        lastDamageTick = currentTick;
        return true;
    }

    /**
     * Isola as regras complexas de acerto crítico físico do Vanilla.
     */
    @Unique
    private boolean isCriticalHit(DamageSource source) {
        if (!(source.getAttacker() instanceof PlayerEntity player)) return false;

        return player.fallDistance > 0.0F
                && !player.isOnGround()
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !player.hasVehicle();
    }

    /**
     * Resolve o que será exibido (seja o crítico nativo ou um dano mapeado).
     */
    @Unique
    private DamageVisuals resolveVisuals(DamageSource source, boolean isCrit) {
        if (isCrit) {
            return new DamageVisuals("CRITICAL", 0xFFFFD700);
        }
        return DamageTypeUtils.getVisuals(source);
    }

    /**
     * Fabrica o payload de efeito apenas se houver visuais para exibir.
     */
    @Unique
    private EffectPayload createEffectPayload(int entityId, DamageVisuals visuals, boolean isCrit) {
        if (visuals == null) return null;
        return new EffectPayload(entityId, visuals.text(), visuals.color(), isCrit);
    }

    /**
     * Centraliza a lógica de envio de pacotes na rede (rastreados e autorrastreio).
     */
    @Unique
    private void dispatchPayloads(LivingEntity entity, DamagePayload damagePayload, EffectPayload effectPayload) {
        for (ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(trackingPlayer, damagePayload);
            if (effectPayload != null) {
                ServerPlayNetworking.send(trackingPlayer, effectPayload);
            }
        }

        if (entity instanceof ServerPlayerEntity self) {
            ServerPlayNetworking.send(self, damagePayload);
            if (effectPayload != null) {
                ServerPlayNetworking.send(self, effectPayload);
            }
        }
    }
}