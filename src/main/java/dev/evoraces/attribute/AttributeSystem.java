package dev.evoraces.attribute;

import dev.evoraces.EvoRaces;
import dev.evoraces.player.PlayerData;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttributeSystem {

    private static final Map<UUID, String> playerLastRace = new HashMap<>();
    private static int tickCounter = 0;

    public static void register() {
        // Verifica atributos apenas 1x/segundo (a cada 20 ticks) em vez de todo tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++tickCounter % 20 != 0) return;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updatePlayerAttributes(player);
            }
        });

        EvoRaces.LOGGER.info("AttributeSystem registrado");
    }

    /**
     * Chamado pelo evento de desconexão para limpar entradas mortas do Map.
     */
    public static void onPlayerDisconnect(UUID playerId) {
        playerLastRace.remove(playerId);
    }

    private static void updatePlayerAttributes(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        String currentRaceId = PlayerData.getPlayerRaceId(player);
        String lastRaceId = playerLastRace.get(playerId);

        if (currentRaceId == null && lastRaceId == null) return;

        if (!stringsEqual(currentRaceId, lastRaceId)) {
            applyRaceAttributes(player, currentRaceId);
            playerLastRace.put(playerId, currentRaceId);
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
        EntityAttributeInstance maxHealthAttr    = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance movementSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        EntityAttributeInstance attackDamageAttr  = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        EntityAttributeInstance armorAttr         = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);

        if (maxHealthAttr != null) {
            // Fórmula: 100 = padrão (sem bônus), cada ponto acima/abaixo de 100 vale +/- 0.2 de vida
            double healthDelta = (race.getAttributes().getVitality() - 100) * 0.2;
            maxHealthAttr.removeModifier(AttributeModifiers.RACE_HEALTH_MODIFIER_ID);
            if (healthDelta != 0) {
                maxHealthAttr.addPersistentModifier(AttributeModifiers.createHealthModifier(healthDelta));
            }
            float newMax = (float) maxHealthAttr.getValue();
            if (player.getHealth() > newMax) {
                player.setHealth(newMax);
            }
        }

        if (movementSpeedAttr != null) {
            // Fórmula: 100 = padrão, cada ponto vale +/- 0.001 de velocidade
            double speedDelta = (race.getAttributes().getAgility() - 100) * 0.001;
            movementSpeedAttr.removeModifier(AttributeModifiers.RACE_SPEED_MODIFIER_ID);
            if (speedDelta != 0) {
                movementSpeedAttr.addPersistentModifier(AttributeModifiers.createSpeedModifier(speedDelta));
            }
        }

        if (attackDamageAttr != null) {
            // Fórmula: 100 = padrão, cada ponto vale +/- 0.01 de dano (ADDITION para evitar conflito com outros mods)
            double damageDelta = (race.getAttributes().getStrength() - 100) * 0.01;
            attackDamageAttr.removeModifier(AttributeModifiers.RACE_DAMAGE_MODIFIER_ID);
            if (damageDelta != 0) {
                attackDamageAttr.addPersistentModifier(AttributeModifiers.createDamageModifier(damageDelta));
            }
        }

        if (armorAttr != null) {
            // Fórmula: 100 = padrão (sem armadura extra), cada ponto acima vale 0.25
            double armorDelta = (race.getAttributes().getResistance() - 100) * 0.25;
            armorAttr.removeModifier(AttributeModifiers.RACE_ARMOR_MODIFIER_ID);
            if (armorDelta != 0) {
                armorAttr.addPersistentModifier(AttributeModifiers.createArmorModifier(armorDelta));
            }
        }
    }

    private static void clearRaceAttributes(ServerPlayerEntity player) {
        EntityAttributeInstance maxHealthAttr    = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance movementSpeedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        EntityAttributeInstance attackDamageAttr  = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        EntityAttributeInstance armorAttr         = player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);

        if (maxHealthAttr != null)     maxHealthAttr.removeModifier(AttributeModifiers.RACE_HEALTH_MODIFIER_ID);
        if (movementSpeedAttr != null) movementSpeedAttr.removeModifier(AttributeModifiers.RACE_SPEED_MODIFIER_ID);
        if (attackDamageAttr != null)  attackDamageAttr.removeModifier(AttributeModifiers.RACE_DAMAGE_MODIFIER_ID);
        if (armorAttr != null)         armorAttr.removeModifier(AttributeModifiers.RACE_ARMOR_MODIFIER_ID);

        if (player.getHealth() > 20.0f) {
            player.setHealth(20.0f);
        }
    }

    public static void forceUpdatePlayerAttributes(ServerPlayerEntity player) {
        playerLastRace.remove(player.getUuid());
        updatePlayerAttributes(player);
    }

    private static boolean stringsEqual(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}