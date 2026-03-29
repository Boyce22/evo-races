package dev.evoraces.attribute;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.UUID;

public class AttributeModifiers {

    public static final UUID RACE_HEALTH_MODIFIER_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    public static final UUID RACE_SPEED_MODIFIER_ID  = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f23456789012");
    public static final UUID RACE_DAMAGE_MODIFIER_ID = UUID.fromString("c3d4e5f6-a7b8-9012-cdef-345678901234");
    public static final UUID RACE_ARMOR_MODIFIER_ID  = UUID.fromString("d4e5f6a7-b8c9-0123-def0-456789012345");

    public static EntityAttributeModifier createHealthModifier(double amount) {
        return new EntityAttributeModifier(
                RACE_HEALTH_MODIFIER_ID, "evoraces.health",
                amount, EntityAttributeModifier.Operation.ADDITION
        );
    }

    public static EntityAttributeModifier createSpeedModifier(double amount) {
        return new EntityAttributeModifier(
                RACE_SPEED_MODIFIER_ID, "evoraces.speed",
                amount, EntityAttributeModifier.Operation.ADDITION
        );
    }

    // Trocado de MULTIPLY_TOTAL para ADDITION para evitar conflito com outros mods
    public static EntityAttributeModifier createDamageModifier(double amount) {
        return new EntityAttributeModifier(
                RACE_DAMAGE_MODIFIER_ID, "evoraces.damage",
                amount, EntityAttributeModifier.Operation.ADDITION
        );
    }

    public static EntityAttributeModifier createArmorModifier(double amount) {
        return new EntityAttributeModifier(
                RACE_ARMOR_MODIFIER_ID, "evoraces.armor",
                amount, EntityAttributeModifier.Operation.ADDITION
        );
    }
}