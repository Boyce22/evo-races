package dev.evoraces.race.effect;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class CaveSpeedEffect implements RacialEffect {

    private static final UUID CAVE_SPEED_MODIFIER_UUID = UUID.fromString("c0ffee00-0000-0000-0000-000000000000");
    private static final String MODIFIER_NAME = "Racial Cave Bonus";
    private static final int HASTE_DURATION_TICKS = 45;

    private final double speedBonus;
    private final int thresholdY;
    private final int hasteLevel;

    public CaveSpeedEffect(double speedBonus, int thresholdY, int hasteLevel) {
        this.speedBonus = speedBonus;
        this.thresholdY = thresholdY;
        this.hasteLevel = hasteLevel;
    }

    @Override
    public void apply(ServerPlayerEntity player) {
        // Efeito aplicado reativamente no tick; nenhuma ação necessária na inicialização
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
            applyCaveEffects(player, speedAttr);
        } else {
            removeCaveSpeedBonus(speedAttr);
        }
    }

    private boolean isInsideCave(ServerPlayerEntity player) {
        return player.getY() < thresholdY;
    }

    private void applyCaveEffects(ServerPlayerEntity player, EntityAttributeInstance speedAttr) {
        applySpeedBonus(speedAttr);
        applyHaste(player);
    }

    private void applySpeedBonus(EntityAttributeInstance speedAttr) {
        boolean alreadyApplied = speedAttr.getModifier(CAVE_SPEED_MODIFIER_UUID) != null;
        if (alreadyApplied) return;

        speedAttr.addPersistentModifier(new EntityAttributeModifier(
                CAVE_SPEED_MODIFIER_UUID,
                MODIFIER_NAME,
                speedBonus,
                EntityAttributeModifier.Operation.ADDITION
        ));
    }

    private void applyHaste(ServerPlayerEntity player) {
        if (hasteLevel <= 0) return;

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HASTE,
                HASTE_DURATION_TICKS,
                hasteLevel - 1,
                false, false, false
        ));
    }

    private void removeCaveSpeedBonus(EntityAttributeInstance speedAttr) {
        boolean hasBonus = speedAttr.getModifier(CAVE_SPEED_MODIFIER_UUID) != null;
        if (hasBonus) speedAttr.removeModifier(CAVE_SPEED_MODIFIER_UUID);
    }
}