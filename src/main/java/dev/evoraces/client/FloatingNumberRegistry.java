package dev.evoraces.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FloatingNumberRegistry {
    private static final List<FloatingNumber> ACTIVE_NUMBERS = new ArrayList<>();

    public static void add(int entityId, float amount, boolean isCritical, boolean isHeal) {
        // CURA não acumula para não confundir o jogador
        if (isHeal) {
            ACTIVE_NUMBERS.add(new FloatingNumber(entityId, amount, isCritical, true));
            return;
        }

        // Tenta encontrar um número de dano já existente para este bicho
        FloatingNumber existing = null;
        for (FloatingNumber fn : ACTIVE_NUMBERS) {
            if (fn.entityId == entityId && !fn.isExpired()) {
                existing = fn;
                break;
            }
        }

        if (existing != null) {
            // Se achou, apenas atualiza o valor!
            existing.addAmount(amount, isCritical);
        } else {
            // Se não achou, cria um novo
            ACTIVE_NUMBERS.add(new FloatingNumber(entityId, amount, isCritical, false));
        }
    }

    public static List<FloatingNumber> getActiveFor(int entityId) {
        return ACTIVE_NUMBERS.stream()
                .filter(n -> n.entityId == entityId)
                .collect(Collectors.toList());
    }

    public static void tick() {
        Iterator<FloatingNumber> it = ACTIVE_NUMBERS.iterator();
        while (it.hasNext()) {
            FloatingNumber n = it.next();
            n.tick();
            if (n.isExpired()) it.remove();
        }
    }
}