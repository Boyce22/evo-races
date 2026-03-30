package dev.evoraces.mixin;

import dev.evoraces.client.FloatingNumber;
import dev.evoraces.client.FloatingNumberRegistry;
import dev.evoraces.client.StatusTextPopup;
import dev.evoraces.client.StatusTextRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Unique
    private static final int FULL_BRIGHT = 15728880;
    @Unique
    private static final int OUTLINE_COLOR = 0xFF000000;

    @Inject(method = "render*", at = @At("TAIL"))
    private void renderRpgPopups(T entity, float entityYaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        var client = MinecraftClient.getInstance();
        var textRenderer = client.textRenderer;
        var rotation = client.gameRenderer.getCamera().getRotation();

        List<StatusTextPopup> popups = StatusTextRegistry.getActiveFor(entity.getId());
        for (StatusTextPopup popup : popups) {
            float progress = popup.progress(tickDelta);

            float p = 1.0f - progress;
            float easeOut = 1.0f - (p * p * p);

            matrices.push();
            matrices.translate(popup.offsetX, entity.getHeight() + 0.6f + (easeOut * 0.8f), popup.offsetZ);
            matrices.multiply(rotation);

            float scale = popup.isCritical ? -0.030f : -0.020f;
            matrices.scale(scale, scale, scale);

            float fade = progress > 0.7f ? 1.0f - ((progress - 0.7f) / 0.3f) : 1.0f;
            int alpha = (int) (Math.max(fade, 0f) * 255);
            int finalColor = (popup.color & 0xFFFFFF) | (alpha << 24);

            float x = -textRenderer.getWidth(popup.text) / 2f;

            textRenderer.drawWithOutline(popup.text, x, 0, finalColor, OUTLINE_COLOR, matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT);

            matrices.pop();
        }

        List<FloatingNumber> numbers = FloatingNumberRegistry.getActiveFor(entity.getId());
        for (FloatingNumber number : numbers) {
            float progress = number.progress(tickDelta);

            float p = 1.0f - progress;
            float easeOut = 1.0f - (p * p * p);

            matrices.push();
            matrices.translate(number.offsetX, entity.getHeight() + 0.3f + (easeOut), number.offsetZ);
            matrices.multiply(rotation);

            float fade = 1.0f;
            if (progress < 0.1f) fade = progress / 0.1f;
            else if (progress > 0.7f) fade = 1.0f - ((progress - 0.7f) / 0.3f);

            float baseScale = number.isCritical ? -0.035f : -0.025f;
            float scale = baseScale * Math.max(fade, 0f);
            matrices.scale(scale, scale, scale);

            float x = -textRenderer.getWidth(number.text) / 2f;
            textRenderer.drawWithOutline(number.text, x, 0, number.color, OUTLINE_COLOR, matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT);

            matrices.pop();
        }
    }
}