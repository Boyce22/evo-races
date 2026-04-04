package dev.evoraces.race.buffs;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class CaveSpeedBuff implements RacialBuff {

    private static final UUID CAVE_SPEED_MODIFIER_UUID = UUID.fromString("c0ffee00-0000-0000-0000-000000000000");
    private static final String MODIFIER_NAME = "Racial Cave Bonus";
    private static final int HASTE_DURATION_TICKS = 45;

    private final double speedBonus;
    private final int thresholdY;
    private final int hasteLevel;

    public CaveSpeedBuff(double speedBonus, int thresholdY, int hasteLevel) {
        this.speedBonus = speedBonus;
        this.thresholdY = thresholdY;
        this.hasteLevel = hasteLevel;
    }

    @Override
    public void apply(ServerPlayerEntity player) {
        // Buff aplicado no tick
    }

    @Override
    public void remove(ServerPlayerEntity player) {
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) speedAttr.removeModifier(CAVE_SPEED_MODIFIER_UUID);
    }

    @Override
    public void tick(ServerPlayerEntity player) {
        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr == null) return;

        if (isInsideCave(player)) {
            applyCaveBuffs(player, speedAttr);
        } else {
            removeCaveSpeedBuff(speedAttr);
        }
    }

    private boolean isInsideCave(ServerPlayerEntity player) {
        return player.getY() < thresholdY;
    }

    private void applyCaveBuffs(ServerPlayerEntity player, EntityAttributeInstance speedAttr) {
        applySpeedBuff(speedAttr);
        applyHasteBuff(player);
    }

    private void applySpeedBuff(EntityAttributeInstance speedAttr) {
        boolean alreadyApplied = speedAttr.getModifier(CAVE_SPEED_MODIFIER_UUID) != null;
        if (alreadyApplied) return;

        speedAttr.addPersistentModifier(new EntityAttributeModifier(
                CAVE_SPEED_MODIFIER_UUID,
                MODIFIER_NAME,
                speedBonus,
                EntityAttributeModifier.Operation.ADDITION
        ));
    }

    private void applyHasteBuff(ServerPlayerEntity player) {
        if (hasteLevel <= 0) return;

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HASTE,
                HASTE_DURATION_TICKS,
                hasteLevel - 1,
                false, false, false
        ));
    }

    private void removeCaveSpeedBuff(EntityAttributeInstance speedAttr) {
        boolean hasBuff = speedAttr.getModifier(CAVE_SPEED_MODIFIER_UUID) != null;
        if (hasBuff) speedAttr.removeModifier(CAVE_SPEED_MODIFIER_UUID);
    }
}