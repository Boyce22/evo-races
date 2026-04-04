package dev.evoraces.mixin;

import dev.evoraces.player.PlayerDataHolder;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Unique 
    private static final float SCALE_DWARF = 0.75f;

    @Unique 
    private static final float SCALE_SMALL = 0.55f; 

    @Inject(
        method = "scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V",
        at = @At("HEAD")
    )
    private void evoraces$applyRaceScale(AbstractClientPlayerEntity player,
                                          MatrixStack matrices, float amount, CallbackInfo ci) {
        if (!(player instanceof PlayerDataHolder holder)) return;

        float scale = resolveRaceScale(holder.evoraces$getRaceId());
        if (scale != 1.0f) matrices.scale(scale, scale, scale);
    }

    @Unique
    private float resolveRaceScale(String raceId) {
        if (raceId == null) return 1.0f;
        return switch (raceId) {
            case "dwarf"        -> SCALE_DWARF;
            case "fairy","gnome"-> SCALE_SMALL;
            default             -> 1.0f;
        };
    }
}