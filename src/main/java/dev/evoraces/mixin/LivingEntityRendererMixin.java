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

    @Unique private static final int   FULL_BRIGHT           = 15728880;
    @Unique private static final int   OUTLINE_COLOR         = 0xFF000000;
    @Unique private static final float GRAVITY               = 0.03f;

    @Unique private static final float POPUP_BASE_SCALE      = -0.010f;
    @Unique private static final float POPUP_CRITICAL_SCALE  = -0.015f;
    @Unique private static final float NUMBER_BASE_SCALE     = -0.018f;
    @Unique private static final float NUMBER_CRITICAL_SCALE = -0.030f;
    @Unique private static final float NUMBER_HEAL_SCALE     = -0.020f;

    @Inject(method = "render*", at = @At("TAIL"))
    private void renderRpgPopups(T entity, float entityYaw, float tickDelta,
                                 MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                 int light, CallbackInfo ci) {
        List<StatusTextPopup> popups = StatusTextRegistry.getActiveFor(entity.getId());
        List<FloatingNumber>  numbers = FloatingNumberRegistry.getActiveFor(entity.getId());

        if (popups.isEmpty() && numbers.isEmpty()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer    textRenderer    = client.textRenderer;
        Quaternionf     cameraRotation  = client.gameRenderer.getCamera().getRotation();
        float           entityHeight    = entity.getHeight();

        for (StatusTextPopup popup : popups) {
            renderStatusTextPopup(popup, tickDelta, matrices, vertexConsumers, textRenderer, cameraRotation, entityHeight);
        }

        for (FloatingNumber number : numbers) {
            renderFloatingNumber(number, tickDelta, matrices, vertexConsumers, textRenderer, cameraRotation, entityHeight);
        }
    }

    @Unique
    private void renderStatusTextPopup(StatusTextPopup popup, float tickDelta,
                                       MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                       TextRenderer textRenderer, Quaternionf cameraRotation,
                                       float entityHeight) {
        float exactAge = popup.age + tickDelta;
        float progress = popup.progress(tickDelta);

        float[] pos   = computePopupPosition(popup, exactAge);
        float   scale = computePopupScale(popup, progress);
        int     color = computePopupColor(popup, progress);

        matrices.push();
        matrices.translate(pos[0], entityHeight + 0.3f + pos[1], pos[2]);
        matrices.multiply(cameraRotation);
        matrices.scale(scale, scale, scale);

        textRenderer.drawWithOutline(
                popup.text, -popup.textWidth / 2f, 0,
                color, OUTLINE_COLOR,
                matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT
        );
        matrices.pop();
    }

    @Unique
    private void renderFloatingNumber(FloatingNumber number, float tickDelta,
                                      MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                      TextRenderer textRenderer, Quaternionf cameraRotation,
                                      float entityHeight) {
        float exactAge = number.age + tickDelta;
        float progress = number.progress(tickDelta);

        float[] pos    = computeNumberPosition(number, exactAge);
        float[] jitter = number.isHeal ? new float[]{0f, 0f} : computeCriticalJitter(number, progress);
        float   scale  = computeNumberScale(number, progress);

        matrices.push();
        matrices.translate(
                pos[0] + jitter[0],
                entityHeight + 0.3f + pos[1] + jitter[1],
                pos[2]
        );
        matrices.multiply(cameraRotation);
        matrices.scale(scale, scale, scale);

        textRenderer.drawWithOutline(
                number.text, -number.textWidth / 2f, 0,
                number.color, OUTLINE_COLOR,
                matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT
        );
        matrices.pop();
    }

    @Unique
    private float[] computePopupPosition(StatusTextPopup popup, float exactAge) {
        float x = popup.startX + popup.vx * exactAge;
        float z = popup.startZ + popup.vz * exactAge;
        float y;

        if (popup.isCritical) {
            y = popup.startY + popup.vy * exactAge - (0.5f * GRAVITY * exactAge * exactAge);
        } else {
            y = popup.startY + popup.vy * exactAge;
            x += (float) Math.sin(exactAge * 0.2f) * 0.1f;
        }

        return new float[]{x, y, z};
    }

    @Unique
    private float[] computeNumberPosition(FloatingNumber number, float exactAge) {
        float x = number.startX + number.vx * exactAge;
        float z = number.startZ + number.vz * exactAge;
        float y = number.isHeal
                ? number.startY + number.vy * exactAge
                : number.startY + number.vy * exactAge - (0.5f * GRAVITY * exactAge * exactAge);

        return new float[]{x, y, z};
    }

    @Unique
    private float[] computeCriticalJitter(FloatingNumber number, float progress) {
        if (number.isCritical && progress < 0.2f) {
            return new float[]{
                    (float) (Math.random() - 0.5f) * 0.06f,
                    (float) (Math.random() - 0.5f) * 0.06f
            };
        }
        return new float[]{0f, 0f};
    }

    @Unique
    private float computePopupScale(StatusTextPopup popup, float progress) {
        float   entry          = computeEntryScale(progress);
        float[] fadeAndShrink  = computeFadeAndShrink(progress, 1.0f, 0.5f);
        float   base           = popup.isCritical ? POPUP_CRITICAL_SCALE : POPUP_BASE_SCALE;
        return base * entry * fadeAndShrink[0] * fadeAndShrink[1];
    }

    @Unique
    private float computeNumberScale(FloatingNumber number, float progress) {
        if (number.isHeal) return computeHealScale(progress);

        float   entry         = computeEntryScale(progress);
        float[] fadeAndShrink = computeFadeAndShrink(progress, 1.0f, 0.4f);
        float   base          = number.isCritical ? NUMBER_CRITICAL_SCALE : NUMBER_BASE_SCALE;
        return base * entry * fadeAndShrink[0] * fadeAndShrink[1];
    }

    @Unique
    private float computeHealScale(float progress) {
        // Entrada muito lenta: 0.0 → 1.0 nos primeiros 40%
        float entry = progress < 0.4f
                ? MathHelper.lerp(progress / 0.4f, 0.0f, 1.0f)
                : 1.0f;

        // Fade longo e gentil: começa em 60%, some devagar até o fim
        float fade = progress > 0.6f
                ? MathHelper.clamp(1.0f - (progress - 0.6f) / 0.4f, 0f, 1f)
                : 1.0f;

        return NUMBER_HEAL_SCALE * entry * fade;
    }

    @Unique
    private float computeEntryScale(float progress) {
        if (progress < 0.1f) return MathHelper.lerp(progress / 0.1f, 0.5f, 1.4f);
        if (progress < 0.2f) return MathHelper.lerp((progress - 0.1f) / 0.1f, 1.4f, 1.0f);
        return 1.0f;
    }

    @Unique
    private float[] computeFadeAndShrink(float progress, float fullShrink, float minShrink) {
        if (progress > 0.5f) {
            float fadeProgress = (progress - 0.5f) / 0.5f;
            float fade         = MathHelper.clamp(1.0f - fadeProgress, 0f, 1f);
            float shrink       = MathHelper.lerp(fadeProgress, fullShrink, minShrink);
            return new float[]{fade, shrink};
        }
        return new float[]{1.0f, fullShrink};
    }

    @Unique
    private int computePopupColor(StatusTextPopup popup, float progress) {
        float fade  = computeFadeAndShrink(progress, 1.0f, 0.5f)[0];
        int   alpha = (int) (fade * 255);
        return (popup.color & 0xFFFFFF) | (alpha << 24);
    }
}