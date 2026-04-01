package dev.evoraces.mixin;

import dev.evoraces.client.FloatingNumber;
import dev.evoraces.client.FloatingNumberRegistry;
import dev.evoraces.client.StatusTextPopup;
import dev.evoraces.client.StatusTextRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Unique private static final int FULL_BRIGHT = 15728880;
    @Unique private static final int OUTLINE_COLOR = 0xFF000000;
    @Unique private static final float GRAVITY = 0.03f; // Força de puxo para baixo

    @Inject(method = "render*", at = @At("TAIL"))
    private void renderRpgPopups(T entity, float entityYaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {

        List<StatusTextPopup> popups = StatusTextRegistry.getActiveFor(entity.getId());
        List<FloatingNumber> numbers = FloatingNumberRegistry.getActiveFor(entity.getId());

        if (popups.isEmpty() && numbers.isEmpty()) return;

        var client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Quaternionf cameraRotation = client.gameRenderer.getCamera().getRotation();

        // --- 1. RENDER STATUS TEXTS ---
        for (StatusTextPopup popup : popups) {
            float progress = popup.progress(tickDelta);
            float exactAge = popup.age + tickDelta; // Tempo exato em ticks (0 a 25)

            // Movimento Horizontal contínuo
            float currentX = popup.startX + (popup.vx * exactAge);
            float currentZ = popup.startZ + (popup.vz * exactAge);
            float currentY;

            if (popup.isCritical) {
                // Parábola de impacto (Sobe e cai com a gravidade)
                currentY = popup.startY + (popup.vy * exactAge) - (0.5f * GRAVITY * exactAge * exactAge);
            } else {
                // Flutuação de fumaça + Sway senoidal
                currentY = popup.startY + (popup.vy * exactAge);
                currentX += (float) Math.sin(exactAge * 0.2f) * 0.1f;
            }

            matrices.push();
            matrices.translate(currentX, entity.getHeight() + 0.3f + currentY, currentZ);
            matrices.multiply(cameraRotation);

            float scaleMultiplier = 1.0f;
            if (progress < 0.1f) scaleMultiplier = MathHelper.lerp(progress / 0.1f, 0.5f, 1.4f);
            else if (progress < 0.2f) scaleMultiplier = MathHelper.lerp((progress - 0.1f) / 0.1f, 1.4f, 1.0f);

            float fade = 1.0f, shrink = 1.0f;
            if (progress > 0.5f) {
                float fadeProgress = (progress - 0.5f) / 0.5f;
                fade = 1.0f - fadeProgress;
                shrink = MathHelper.lerp(fadeProgress, 1.0f, 0.5f);
            }

            float baseScale = popup.isCritical ? -0.015f : -0.010f;
            float finalScale = baseScale * scaleMultiplier * MathHelper.clamp(fade, 0f, 1f) * shrink;
            matrices.scale(finalScale, finalScale, finalScale);

            int alpha = (int) (MathHelper.clamp(fade, 0f, 1f) * 255);
            int finalColor = (popup.color & 0xFFFFFF) | (alpha << 24);

            textRenderer.drawWithOutline(popup.text, -popup.textWidth / 2f, 0, finalColor, OUTLINE_COLOR, matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT);
            matrices.pop();
        }

        // --- 2. RENDER FLOATING NUMBERS ---
        for (FloatingNumber number : numbers) {
            float progress = number.progress(tickDelta);
            float exactAge = number.age + tickDelta; // Tempo exato em ticks (0 a 20)

            // Posição Física em Parábola
            float currentX = number.startX + (number.vx * exactAge);
            float currentZ = number.startZ + (number.vz * exactAge);
            // Fórmula da Cinemática: y = y0 + v0*t - (g*t^2)/2
            float currentY = number.startY + (number.vy * exactAge) - (0.5f * GRAVITY * exactAge * exactAge);

            float jitterX = 0, jitterY = 0;
            if (number.isCritical && progress < 0.2f) {
                jitterX = (float) (Math.random() - 0.5f) * 0.06f;
                jitterY = (float) (Math.random() - 0.5f) * 0.06f;
            }

            matrices.push();
            matrices.translate(currentX + jitterX, entity.getHeight() + 0.3f + currentY + jitterY, currentZ);
            matrices.multiply(cameraRotation);

            float scaleMultiplier = 1.0f;
            if (progress < 0.1f) scaleMultiplier = MathHelper.lerp(progress / 0.1f, 0.5f, 1.4f);
            else if (progress < 0.2f) scaleMultiplier = MathHelper.lerp((progress - 0.1f) / 0.1f, 1.4f, 1.0f);

            float fade = 1.0f, shrink = 1.0f;
            if (progress > 0.5f) {
                float fadeProgress = (progress - 0.5f) / 0.5f;
                fade = 1.0f - fadeProgress;
                shrink = MathHelper.lerp(fadeProgress, 1.0f, 0.4f);
            }

            float baseScale = number.isCritical ? -0.030f : -0.018f;
            float finalScale = baseScale * scaleMultiplier * MathHelper.clamp(fade, 0f, 1f) * shrink;
            matrices.scale(finalScale, finalScale, finalScale);

            textRenderer.drawWithOutline(number.text, -number.textWidth / 2f, 0, number.color, OUTLINE_COLOR, matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT);
            matrices.pop();
        }
    }
}