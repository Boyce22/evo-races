package dev.evoraces.mixin;

import dev.evoraces.network.DamagePayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private long lastDamageTick = -1L;

    @Inject(method = "damage", at = @At("HEAD"))
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld().isClient()) return;
        if (amount <= 0) return;

        long currentTick = entity.getWorld().getTime();
        if (lastDamageTick != -1 && currentTick - lastDamageTick < 1) return;
        lastDamageTick = currentTick;

        DamagePayload payload = new DamagePayload(entity.getId(), amount);

        PlayerLookup.tracking(entity).forEach(player -> ServerPlayNetworking.send(player, payload));
        if (entity instanceof ServerPlayerEntity self) {
            ServerPlayNetworking.send(self, payload);
        }
    }
}