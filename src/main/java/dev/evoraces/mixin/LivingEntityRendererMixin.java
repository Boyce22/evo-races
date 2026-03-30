package dev.evoraces.mixin;

import dev.evoraces.client.DamageIndicator;
import dev.evoraces.client.DamageIndicatorRegistry;
import dev.evoraces.client.DamageIndicatorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderDamageIndicators(T entity, float entityYaw, float tickDelta,
                                        MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                        int light, CallbackInfo ci) {

        List<DamageIndicator> indicators = DamageIndicatorRegistry.getActiveFor(entity.getId());
        if (indicators.isEmpty()) return;

        for (DamageIndicator indicator : indicators) {
            float progress = indicator.progress(tickDelta);

            matrices.push();

            float baseHeight = entity.getHeight() + 0.5f;
            float floatOffset = progress * 1.5f;
            matrices.translate(0.0, baseHeight + floatOffset, 0.0);

            matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());

            float fade;
            if (progress < 0.2f) {
                fade = progress / 0.2f;
            } else {
                fade = 1.0f - ((progress - 0.2f) / 0.8f);
            }
            float scale = -0.045f * Math.max(fade, 0f);
            matrices.scale(scale, scale, scale);

            DamageIndicatorRenderer.renderSingle(matrices,
                    MinecraftClient.getInstance(),
                    vertexConsumers,
                    indicator,
                    tickDelta);

            matrices.pop();
        }
    }
}