package dev.evoraces.attribute;

import dev.evoraces.EvoRaces;
import dev.evoraces.player.PlayerData;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceEvents;
import dev.evoraces.race.RaceRegistry;
import dev.evoraces.race.effect.RacialEffect;
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
        EvoRaces.LOGGER.info("AttributeSystem registrado (Suporte a Efeitos Genéricos)");
    }

    public static void onPlayerDisconnect(UUID playerId) {
        // Reservado para limpeza futura de estado por jogador
    }

    public static void forceUpdatePlayerAttributes(ServerPlayerEntity player) {
        applyRaceAttributes(player, PlayerData.getRaceId(player));
    }

    public static void checkDynamicEnvironment(ServerPlayerEntity player) {
        String raceId = PlayerData.getRaceId(player);
        if (raceId == null) return;

        RaceRegistry registry = RaceRegistry.getInstance();
        if (!registry.hasRace(raceId)) return;

        Race race = registry.getRace(raceId);
        for (RacialEffect effect : race.getRacialEffects()) {
            effect.tick(player);
        }
    }

    private static void registerRaceChangeEvent() {
        RaceEvents.ON_RACE_CHANGE.register((player, oldRaceId, newRaceId) -> {
            removeOldRaceEffects(player, oldRaceId);
            applyRaceAttributes(player, newRaceId);
            applyNewRaceEffects(player, newRaceId);
        });
    }

    private static void registerTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++tickCounter % TICKS_PER_SECOND != 0) return;
            server.getPlayerManager().getPlayerList()
                    .forEach(AttributeSystem::checkDynamicEnvironment);
        });
    }

    private static void removeOldRaceEffects(ServerPlayerEntity player, String oldRaceId) {
        if (!isValidRace(oldRaceId)) return;
        for (RacialEffect effect : RaceRegistry.getInstance().getRace(oldRaceId).getRacialEffects()) {
            effect.remove(player);
        }
    }

    private static void applyNewRaceEffects(ServerPlayerEntity player, String newRaceId) {
        if (!isValidRace(newRaceId)) return;
        for (RacialEffect effect : RaceRegistry.getInstance().getRace(newRaceId).getRacialEffects()) {
            effect.apply(player);
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
        applySpeedAttribute(player, race);
        applyDamageAttribute(player, race);
        applyArmorAttribute(player, race);
    }

    private static void applyHealthAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attr == null) return;

        double delta = (race.getAttributes().getVitality() - 100) * 0.2;
        attr.removeModifier(AttributeModifiers.RACE_HEALTH_MODIFIER_ID);
        if (delta != 0) attr.addPersistentModifier(AttributeModifiers.createHealthModifier(delta));

        float newMax = (float) attr.getValue();
        if (player.getHealth() > newMax) player.setHealth(newMax);
    }

    private static void applySpeedAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (attr == null) return;

        double delta = (race.getAttributes().getAgility() - 100) * 0.001;
        attr.removeModifier(AttributeModifiers.RACE_SPEED_MODIFIER_ID);
        if (delta != 0) attr.addPersistentModifier(AttributeModifiers.createSpeedModifier(delta));
    }

    private static void applyDamageAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attr == null) return;

        double delta = (race.getAttributes().getStrength() - 100) * 0.01;
        attr.removeModifier(AttributeModifiers.RACE_DAMAGE_MODIFIER_ID);
        if (delta != 0) attr.addPersistentModifier(AttributeModifiers.createDamageModifier(delta));
    }

    private static void applyArmorAttribute(ServerPlayerEntity player, Race race) {
        EntityAttributeInstance attr = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (attr == null) return;

        double delta = (race.getAttributes().getResistance() - 100) * 0.25;
        attr.removeModifier(AttributeModifiers.RACE_ARMOR_MODIFIER_ID);
        if (delta != 0) attr.addPersistentModifier(AttributeModifiers.createArmorModifier(delta));
    }

    private static void clearRaceAttributes(ServerPlayerEntity player) {
        removeAttributeModifier(player, EntityAttributes.GENERIC_MAX_HEALTH,      AttributeModifiers.RACE_HEALTH_MODIFIER_ID);
        removeAttributeModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED,  AttributeModifiers.RACE_SPEED_MODIFIER_ID);
        removeAttributeModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE,   AttributeModifiers.RACE_DAMAGE_MODIFIER_ID);
        removeAttributeModifier(player, EntityAttributes.GENERIC_ARMOR,           AttributeModifiers.RACE_ARMOR_MODIFIER_ID);

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