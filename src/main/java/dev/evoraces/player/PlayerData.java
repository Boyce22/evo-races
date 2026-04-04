package dev.evoraces.player;

import dev.evoraces.network.ModMessages;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * API de alto nível para dados de jogador do EvoRaces.
 * <p>
 * É o único ponto de entrada para leitura e escrita de dados de raça.
 * Sistemas, comandos e handlers de rede devem usar exclusivamente esta classe —
 * nunca acessar {@link PlayerDataHolder} diretamente.
 * <p>
 * Futuramente: cooldowns, buffs temporários, progressão, inventário de raça, etc.
 */
public final class PlayerData {

    private PlayerData() {}

    /**
     * Retorna o ‘ID’ da raça atual do jogador, ou {@code null} se não tiver raça.
     */
    public static String getRaceId(ServerPlayerEntity player) {
        return holder(player).evoraces$getRaceId();
    }

    /**
     * Retorna {@code true} se o jogador possui uma raça atribuída.
     */
    public static boolean hasRace(ServerPlayerEntity player) {
        String id = getRaceId(player);
        return id != null && !id.isEmpty();
    }

    /**
     * Define a raça do jogador, recalcula dimensões e sincroniza com o cliente.
     * <p>
     * O evento {@code ON_RACE_CHANGE} é disparado dentro do Mixin
     * em {@code evoraces$setRaceId} — não precisa ser chamado aqui.
     *
     * @param raceId ‘ID’ da raça, ou {@code null} para remover a raça.
     */
    public static void setRace(ServerPlayerEntity player, String raceId) {
        holder(player).evoraces$setRaceId(raceId);
        player.calculateDimensions();
        ModMessages.sendRaceSync(player, raceId);
    }

    /**
     * Remove a raça do jogador.
     */
    public static void clearRace(ServerPlayerEntity player) {
        setRace(player, null);
    }

    private static PlayerDataHolder holder(ServerPlayerEntity player) {
        return (PlayerDataHolder) player;
    }
}