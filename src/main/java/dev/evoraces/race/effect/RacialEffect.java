package dev.evoraces.race.effect;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Interface base para efeitos de raça que podem ser aplicados, removidos ou processados a cada tick.
 */
public interface RacialEffect {
    /**
     * Chamado quando a raça é aplicada ao jogador.
     */
    void apply(ServerPlayerEntity player);

    /**
     * Chamado quando a raça é removida do jogador.
     */
    void remove(ServerPlayerEntity player);

    /**
     * Chamado periodicamente (ex: a cada 20 ticks) para efeitos dinâmicos.
     */
    void tick(ServerPlayerEntity player);
}
