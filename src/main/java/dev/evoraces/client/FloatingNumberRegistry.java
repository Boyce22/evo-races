package dev.evoraces.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;

public class FloatingNumberRegistry {

    private static final Int2ObjectMap<List<FloatingNumber>> numbers = new Int2ObjectOpenHashMap<>();
    private static final int MAX_PER_ENTITY = 5;

    public static void add(int entityId, float amount, boolean isCritical, boolean isHeal) {
        List<FloatingNumber> list = numbers.computeIfAbsent(entityId, k -> new ArrayList<>());
        if (list.size() < MAX_PER_ENTITY) {
            list.add(new FloatingNumber(entityId, amount, isCritical, isHeal));
        }
    }

    public static List<FloatingNumber> getActiveFor(int entityId) {
        return numbers.getOrDefault(entityId, List.of());
    }

    public static void tick() {
        numbers.values().removeIf(list -> {
            list.removeIf(number -> {
                number.tick();
                return number.isExpired();
            });
            return list.isEmpty();
        });
    }
}