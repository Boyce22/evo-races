package dev.evoraces.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;

public class StatusTextRegistry {

    private static final Int2ObjectMap<List<StatusTextPopup>> activeTexts = new Int2ObjectOpenHashMap<>();
    private static final int MAX_TEXTS_PER_ENTITY = 3;

    public static void add(int entityId, String message, int color, boolean isCritical) {
        List<StatusTextPopup> list = activeTexts.computeIfAbsent(entityId, k -> new ArrayList<>());

        if (list.size() >= MAX_TEXTS_PER_ENTITY) {
            list.remove(0);
        }

        // Críticos são adicionados no topo
        if (isCritical) {
            list.add(0, new StatusTextPopup(entityId, message, color, true));
        } else {
            list.add(new StatusTextPopup(entityId, message, color, false));
        }
    }

    public static List<StatusTextPopup> getActiveFor(int entityId) {
        return activeTexts.getOrDefault(entityId, List.of());
    }

    public static void tick() {
        activeTexts.values().removeIf(list -> {
            list.removeIf(popup -> {
                popup.tick();
                return popup.isExpired();
            });
            return list.isEmpty();
        });
    }
}