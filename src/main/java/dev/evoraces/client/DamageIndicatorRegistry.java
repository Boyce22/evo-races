package dev.evoraces.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;

public class DamageIndicatorRegistry {

    // Usa int primitivo como chave, sem Autoboxing
    private static final Int2ObjectMap<List<DamageIndicator>> indicators = new Int2ObjectOpenHashMap<>();
    private static final int MAX_PER_ENTITY = 5;

    public static void add(int entityId, float damage, boolean isCritical) {
        List<DamageIndicator> list = indicators.computeIfAbsent(entityId, k -> new ArrayList<>());
        if (list.size() < MAX_PER_ENTITY) {
            list.add(new DamageIndicator(entityId, damage, isCritical));
        }
    }

    public static List<DamageIndicator> getActiveFor(int entityId) {
        return indicators.getOrDefault(entityId, List.of());
    }

    public static void tick() {
        indicators.values().removeIf(list -> {
            list.removeIf(indicator -> {
                indicator.tick();
                return indicator.isExpired();
            });
            return list.isEmpty();
        });
    }
}