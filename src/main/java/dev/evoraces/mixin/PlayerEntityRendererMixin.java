package dev.evoraces.mixin;

import dev.evoraces.player.PlayerDataHolder;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    // Injeta o código na hora em que o jogo escala o modelo do jogador
    @Inject(method = "scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("HEAD"))
    private void evoraces$scaleDwarf(AbstractClientPlayerEntity player, MatrixStack matrices, float amount, CallbackInfo ci) {
        if (player instanceof PlayerDataHolder) {
            var raceId = ((PlayerDataHolder) player).evoraces$getRaceId();
            if ("dwarf".equals(raceId)) {
                // Diminui os eixos X, Y e Z para 60%
                matrices.scale(0.75f, 0.75f, 0.75f);
            } else if ("fairy".equals(raceId)) {
                matrices.scale(0.55f, 0.55f, 0.55f);
            }
        }
    }
}