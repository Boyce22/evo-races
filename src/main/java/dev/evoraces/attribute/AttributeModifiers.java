package dev.evoraces.attribute;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;

public class AttributeModifiers {

    // UUIDs existentes (mantenha)
    public static final UUID RACE_HEALTH_MODIFIER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    public static final UUID RACE_SPEED_MODIFIER_ID  = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f23456789012");
    public static final UUID RACE_DAMAGE_MODIFIER_ID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-345678901234");
    public static final UUID RACE_ARMOR_MODIFIER_ID  = UUID.fromString("d4e5f6a7-b8c9-0123-def0-456789012345");

    // NOVOS UUIDs
    public static final UUID RACE_ATTACK_SPEED_MODIFIER_ID = UUID.fromString("e5f6a7b8-c9d0-1234-ef01-567890123456");

    // Métodos factory existentes (mantenha)
    public static EntityAttributeModifier createHealthModifier(double amount) {
        return new EntityAttributeModifier(RACE_HEALTH_MODIFIER_ID, "evoraces.health", amount, EntityAttributeModifier.Operation.ADDITION);
    }

    public static EntityAttributeModifier createSpeedModifier(double amount) {
        return new EntityAttributeModifier(RACE_SPEED_MODIFIER_ID, "evoraces.speed", amount, EntityAttributeModifier.Operation.ADDITION);
    }

    public static EntityAttributeModifier createDamageModifier(double amount) {
        return new EntityAttributeModifier(RACE_DAMAGE_MODIFIER_ID, "evoraces.damage", amount, EntityAttributeModifier.Operation.ADDITION);
    }

    public static EntityAttributeModifier createArmorModifier(double amount) {
        return new EntityAttributeModifier(RACE_ARMOR_MODIFIER_ID, "evoraces.armor", amount, EntityAttributeModifier.Operation.ADDITION);
    }

    public static EntityAttributeModifier createAttackSpeedModifier(double amount) {
        return new EntityAttributeModifier(RACE_ATTACK_SPEED_MODIFIER_ID, "evoraces.attack_speed", amount, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    }

    // === CÁLCULOS BASE 100 (delta = valor - 100) ===

    /** Força: +1.5% dano corpo a corpo por ponto acima de 100 */
    public static double calculateMeleeMultiplier(int strength) {
        return 1.0 + ((strength - 100) * 0.015);
    }

    /** Agilidade: +0.2% velocidade ataque por ponto acima de 100 */
    public static double calculateAttackSpeedBonus(int agility) {
        return (agility - 100) * 0.002;
    }

    /** Agilidade: 0.05% chance esquiva por ponto acima de 100 (máx 75%) */
    public static double calculateDodgeChance(int agility) {
        return Math.min(0.75, Math.max(0, (agility - 100) * 0.0005));
    }

    /** Vitalidade: +0.2 HP por ponto acima de 100 */
    public static double calculateHealthBonus(int vitality) {
        return (vitality - 100) * 0.2;
    }

    /** Vitalidade: Intervalo de regen em ticks. 100 = sem regen */
    public static int calculateRegenInterval(int vitality) {
        if (vitality <= 100) return Integer.MAX_VALUE;
        return Math.max(20, 100 - ((vitality - 100) / 2));
    }

    /** Destreza: +1.2% dano à distância por ponto acima de 100 */
    public static double calculateRangedMultiplier(int dexterity) {
        return 1.0 + ((dexterity - 100) * 0.012);
    }

    /** Inteligência: +1.8% dano mágico por ponto acima de 100 */
    public static double calculateMagicMultiplier(int intellect) {
        return 1.0 + ((intellect - 100) * 0.018);
    }

    /** Sorte: 0.1% chance crítico por ponto acima de 100 (máx 100%) */
    public static double calculateCritChance(int luck) {
        return Math.min(1.0, Math.max(0, (luck - 100) * 0.001));
    }
}