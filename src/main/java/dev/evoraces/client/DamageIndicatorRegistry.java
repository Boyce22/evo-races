package dev.evoraces.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageIndicatorRegistry {

    private static final Map<Integer, List<DamageIndicator>> indicators = new HashMap<>();
    private static final int MAX_PER_ENTITY = 5;

    public static void add(int entityId, int damage, int color) {
        add(entityId, damage, color, 1.0f, null, false);
    }

    public static void add(int entityId, int damage, int color, float glowIntensity, String effectText, boolean isCritical) {
        List<DamageIndicator> list = indicators.computeIfAbsent(entityId, k -> new ArrayList<>());
        if (list.size() < MAX_PER_ENTITY) {
            list.add(new DamageIndicator(entityId, damage, color, glowIntensity, effectText, isCritical));
        }
    }

    public static List<DamageIndicator> getActiveFor(int entityId) {
        return indicators.getOrDefault(entityId, List.of());
    }

    public static List<DamageIndicator> getActive() {
        return indicators.values().stream().flatMap(List::stream).toList();
    }

    public static void tick() {
        indicators.values().removeIf(list -> {
            for (DamageIndicator indicator : list) {
                indicator.tick();
            }
            list.removeIf(DamageIndicator::isExpired);
            return list.isEmpty();
        });
    }
}