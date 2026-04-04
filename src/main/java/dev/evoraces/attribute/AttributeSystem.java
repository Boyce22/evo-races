package dev.evoraces.attribute;

import dev.evoraces.EvoRaces;
import dev.evoraces.player.PlayerDataHolder;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceEvents;
import dev.evoraces.race.RaceRegistry;
import dev.evoraces.race.buffs.RacialBuff;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class AttributeSystem {

    private static final int TICKS_PER_SECOND = 20;
    private static int tickCounter = 0;

    public static void register() {
        registerRaceChangeEvent();
        registerTickEvent();
        EvoRaces.LOGGER.info("AttributeSystem registrado (Agilidade não afeta movimento)");
    }

    public static void removeAllRaceAttributes(ServerPlayerEntity player) {
        if (player == null) return;
        clearRaceAttributes(player);
    }

    public static void forceUpdatePlayerAttributes(ServerPlayerEntity player) {
        if (player == null) return;
        String raceId = ((PlayerDataHolder) player).evoraces$getRaceId();
        applyRaceAttributes(player, raceId);
    }

    public static void checkDynamicEnvironment(ServerPlayerEntity player) {
        String raceId = ((PlayerDataHolder) player).evoraces$getRaceId();
        if (raceId == null) return;

        RaceRegistry registry = RaceRegistry.getInstance();
        if (!registry.hasRace(raceId)) return;

        Race race = registry.getRace(raceId);
        for (RacialBuff buff : race.getRacialBuffs()) {
            buff.tick(player);
        }
    }

    private static void registerRaceChangeEvent() {
        RaceEvents.ON_RACE_CHANGE.register((player, oldRaceId, newRaceId) -> {
            removeOldRaceBuffs(player, oldRaceId);
            applyRaceAttributes(player, newRaceId);
            applyNewRaceBuffs(player, newRaceId);
        });
    }

    private static void registerTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++tickCounter % TICKS_PER_SECOND != 0) return;
            server.getPlayerManager().getPlayerList()
                    .forEach(AttributeSystem::checkDynamicEnvironment);
        });
    }

    private static void removeOldRaceBuffs(ServerPlayerEntity player, String oldRaceId) {
        if (!isValidRace(oldRaceId)) return;
        for (RacialBuff buff : RaceRegistry.getInstance().getRace(oldRaceId).getRacialBuffs()) {
            buff.remove(player);
        }
    }

    private static void applyNewRaceBuffs(ServerPlayerEntity player, String newRaceId) {
        if (!isValidRace(newRaceId)) return;
        for (RacialBuff buff : RaceRegistry.getInstance().getRace(newRaceId).getRacialBuffs()) {
            buff.apply(player);
        }
    }

    private static void applyRaceAttributes(ServerPlayerEntity player, String raceId) {
        if (raceId == null) {
            clearRaceAttributes(player);
            EvoRaces.LOGGER.debug("Atributos de raça removidos do jogador: {}", player.getName().getString());
            return;
        }

        RaceRegistry registry = RaceRegistry.getInstance();
        if (!registry.hasRace(raceId)) {
            EvoRaces.LOGGER.warn("Raça não encontrada ao aplicar atributos: {}", raceId);
            return;
        }

        applyAttributesFromRace(player, registry.getRace(raceId));
        EvoRaces.LOGGER.debug("Atributos da raça {} aplicados ao jogador: {}", raceId, player.getName().getString());
    }

    private static void applyAttributesFromRace(ServerPlayerEntity player, Race race) {
        applyHealthAttribute(player, race);
        // applySpeedAttribute REMOVIDO – Agilidade NÃO afeta movimento
        applyDamageAttribute(player, race);
        applyArmorAttribute(player, race);
        applyAttackSpeedAttribute(player, race);
    }

    private static void applyHealthAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attr == null) return;

        double bonus = AttributeModifiers.calculateHealthBonus(race.getAttributes().getVitality());
        attr.removeModifier(AttributeModifiers.RACE_HEALTH_MODIFIER_ID);
        if (bonus != 0) {
            attr.addPersistentModifier(AttributeModifiers.createHealthModifier(bonus));
        }

        float newMax = (float) attr.getValue();
        if (player.getHealth() > newMax) player.setHealth(newMax);
    }

    // Método applySpeedAttribute foi removido completamente

    private static void applyDamageAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attr == null) return;

        // Força: 1,5% de dano por ponto acima de 100
        double delta = (race.getAttributes().getStrength() - 100) * 0.015;
        attr.removeModifier(AttributeModifiers.RACE_DAMAGE_MODIFIER_ID);
        if (delta != 0) {
            attr.addPersistentModifier(AttributeModifiers.createDamageModifier(delta));
        }
    }

    private static void applyArmorAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (attr == null) return;

        double delta = (race.getAttributes().getResistance() - 100) * 0.25;
        attr.removeModifier(AttributeModifiers.RACE_ARMOR_MODIFIER_ID);
        if (delta != 0) {
            attr.addPersistentModifier(AttributeModifiers.createArmorModifier(delta));
        }
    }

    private static void applyAttackSpeedAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED);
        if (attr == null) return;

        // Destreza: 0,2% de velocidade de ataque por ponto acima de 100
        double bonus = AttributeModifiers.calculateAttackSpeedBonus(race.getAttributes().getDexterity());
        attr.removeModifier(AttributeModifiers.RACE_ATTACK_SPEED_MODIFIER_ID);
        if (bonus != 0) {
            attr.addPersistentModifier(AttributeModifiers.createAttackSpeedModifier(bonus));
        }
    }

    private static void clearRaceAttributes(ServerPlayerEntity player) {
        removeAttributeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH,      AttributeModifiers.RACE_HEALTH_MODIFIER_ID);
        removeAttributeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED,  AttributeModifiers.RACE_SPEED_MODIFIER_ID);
        removeAttributeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE,   AttributeModifiers.RACE_DAMAGE_MODIFIER_ID);
        removeAttributeModifier(player, EntityAttributes.GENERIC_ARMOR,           AttributeModifiers.RACE_ARMOR_MODIFIER_ID);
        removeAttributeModifier(player, EntityAttributes.GENERIC_ATTACK_SPEED,    AttributeModifiers.RACE_ATTACK_SPEED_MODIFIER_ID);

        if (player.getHealth() > 20.0f) player.setHealth(20.0f);
    }

    private static void removeAttributeModifier(ServerPlayerEntity player,
                                                net.minecraft.entity.attribute.EntityAttribute attribute,
                                                UUID modifierId) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance != null) instance.removeModifier(modifierId);
    }

    private static boolean isValidRace(String raceId) {
        return raceId != null && RaceRegistry.getInstance().hasRace(raceId);
    }
}