package dev.evoraces.race;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public final class RaceEvents {

    private RaceEvents() {}

    /**
     * Disparado quando a raça de um jogador é alterada.
     */
    public static final Event<OnRaceChange> ON_RACE_CHANGE = EventFactory.createArrayBacked(
            OnRaceChange.class,
            listeners -> (player, oldRaceId, newRaceId) -> {
                for (OnRaceChange listener : listeners) {
                    listener.onRaceChange(player, oldRaceId, newRaceId);
                }
            }
    );

    @FunctionalInterface
    public interface OnRaceChange {
        /**
         * @param player     O jogador cuja raça mudou.
         * @param oldRaceId  O ‘ID’ da raça anterior (pode ser null).
         * @param newRaceId  O ‘ID’ da nova raça (pode ser null).
         */
        void onRaceChange(ServerPlayerEntity player, String oldRaceId, String newRaceId);
    }
}