package dev.evoraces.utils;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKey;

import java.util.HashMap;
import java.util.Map;

public class DamageTypeUtils {

    private static final Map<RegistryKey<DamageType>, DamageVisuals> DAMAGE_VISUALS_MAP = new HashMap<>();

    static {
        DamageVisuals fireVisuals = new DamageVisuals("BURNING", 0xFFFF4400);
        DAMAGE_VISUALS_MAP.put(DamageTypes.IN_FIRE, fireVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.ON_FIRE, fireVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.HOT_FLOOR, fireVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.LAVA, new DamageVisuals("MELTED", 0xFFFF3300));
        DAMAGE_VISUALS_MAP.put(DamageTypes.FIREBALL, fireVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.UNATTRIBUTED_FIREBALL, fireVisuals);

        DamageVisuals magicVisuals = new DamageVisuals("MAGIC", 0xFFFF55FF);
        DAMAGE_VISUALS_MAP.put(DamageTypes.MAGIC, magicVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.INDIRECT_MAGIC, magicVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.WITHER, new DamageVisuals("WITHERED", 0xFF550055));
        DAMAGE_VISUALS_MAP.put(DamageTypes.WITHER_SKULL, new DamageVisuals("WITHERED", 0xFF550055));
        DAMAGE_VISUALS_MAP.put(DamageTypes.DRAGON_BREATH, new DamageVisuals("DRAGON BREATH", 0xFFFF00FF));

        DAMAGE_VISUALS_MAP.put(DamageTypes.FREEZE, new DamageVisuals("FROZEN", 0xFF55FFFF));
        DAMAGE_VISUALS_MAP.put(DamageTypes.DROWN, new DamageVisuals("DROWNING", 0xFF00AAFF));
        DAMAGE_VISUALS_MAP.put(DamageTypes.STARVE, new DamageVisuals("STARVING", 0xFFAAAA00));
        DAMAGE_VISUALS_MAP.put(DamageTypes.LIGHTNING_BOLT, new DamageVisuals("ZAP!", 0xFFFFFF00));
        DAMAGE_VISUALS_MAP.put(DamageTypes.SONIC_BOOM, new DamageVisuals("SONIC BOOM", 0xFF0055FF));

        DamageVisuals fallVisuals = new DamageVisuals("FALL", 0xFFAAAAAA);
        DAMAGE_VISUALS_MAP.put(DamageTypes.FALL, fallVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.FALLING_BLOCK, fallVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.FALLING_ANVIL, new DamageVisuals("CRUSHED", 0xFFAAAAAA));
        DAMAGE_VISUALS_MAP.put(DamageTypes.FALLING_STALACTITE, fallVisuals);

        DamageVisuals prickVisuals = new DamageVisuals("PRICKED", 0xFF00AA00);
        DAMAGE_VISUALS_MAP.put(DamageTypes.CACTUS, prickVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.SWEET_BERRY_BUSH, prickVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.STALAGMITE, new DamageVisuals("IMPALED", 0xFFAAAAAA));
        DAMAGE_VISUALS_MAP.put(DamageTypes.STING, new DamageVisuals("STUNG", 0xFFFFFF00));

        DamageVisuals explosionVisuals = new DamageVisuals("BOOM", 0xFFFF5500);
        DAMAGE_VISUALS_MAP.put(DamageTypes.EXPLOSION, explosionVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.PLAYER_EXPLOSION, explosionVisuals);
        DAMAGE_VISUALS_MAP.put(DamageTypes.BAD_RESPAWN_POINT, explosionVisuals);

        DAMAGE_VISUALS_MAP.put(DamageTypes.OUT_OF_WORLD, new DamageVisuals("VOID", 0xFF000000));
        DAMAGE_VISUALS_MAP.put(DamageTypes.CRAMMING, new DamageVisuals("CRAMMING", 0xFF555555));
        DAMAGE_VISUALS_MAP.put(DamageTypes.IN_WALL, new DamageVisuals("SUFFOCATING", 0xFF555555));
        DAMAGE_VISUALS_MAP.put(DamageTypes.THORNS, new DamageVisuals("THORNS", 0xFF00AA00));
    }

    public static DamageVisuals getVisuals(DamageSource source) {
        for (Map.Entry<RegistryKey<DamageType>, DamageVisuals> entry : DAMAGE_VISUALS_MAP.entrySet()) {
            if (source.isOf(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}