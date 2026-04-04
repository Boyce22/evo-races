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
import java.util.Random;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Unique
    private static final int FULL_BRIGHT = 15728880;

    @Unique
    private static final int OUTLINE_COLOR = 0xFF000000;

    @Unique
    private static final float GRAVITY = 0.03f;

    @Unique
    private static final float POPUP_BASE_SCALE = -0.010f;

    @Unique
    private static final float POPUP_CRITICAL_SCALE = -0.015f;

    @Unique
    private static final float NUMBER_BASE_SCALE = -0.018f;

    @Unique
    private static final float NUMBER_CRITICAL_SCALE = -0.030f;

    @Unique
    private static final float NUMBER_HEAL_SCALE = -0.020f;

    @Unique
    private static final float ENTRY_PHASE_1_END = 0.1f;

    @Unique
    private static final float ENTRY_PHASE_2_END = 0.2f;

    @Unique
    private static final float FADE_START = 0.5f;

    @Unique
    private static final float HEAL_ENTRY_END = 0.4f;

    @Unique
    private static final float HEAL_FADE_START = 0.6f;

    @Unique
    private static final float JITTER_THRESHOLD = 0.2f;

    @Unique
    private static final float JITTER_AMPLITUDE = 0.06f;

    @Unique
    private final float[] reusablePos = new float[3];

    @Unique
    private final float[] reusableJitter = new float[2];

    @Unique
    private static final Random JITTER_RANDOM = new Random();

    @Inject(method = "render*", at = @At("TAIL"))
    private void renderRpgPopups(T entity, float entityYaw, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            int light, CallbackInfo ci) {
        List<StatusTextPopup> popups = StatusTextRegistry.getActiveFor(entity.getId());
        List<FloatingNumber> numbers = FloatingNumberRegistry.getActiveFor(entity.getId());

        if (popups.isEmpty() && numbers.isEmpty())
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Quaternionf cameraRotation = client.gameRenderer.getCamera().getRotation();
        float entityHeight = entity.getHeight();

        for (StatusTextPopup popup : popups) {
            renderStatusTextPopup(popup, tickDelta, matrices, vertexConsumers,
                    textRenderer, cameraRotation, entityHeight);
        }
        for (FloatingNumber number : numbers) {
            renderFloatingNumber(number, tickDelta, matrices, vertexConsumers,
                    textRenderer, cameraRotation, entityHeight);
        }
    }

    @Unique
    private void renderStatusTextPopup(StatusTextPopup popup, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            TextRenderer textRenderer, Quaternionf cameraRotation,
            float entityHeight) {
        float exactAge = popup.age + tickDelta;
        float progress = popup.progress(tickDelta);

        computePopupPosition(popup, exactAge, reusablePos);

        float fade = computeFade(progress);
        float scale = computePopupScale(popup, progress, fade);
        int color = applyAlpha(popup.color, fade);

        matrices.push();
        matrices.translate(reusablePos[0], entityHeight + 0.3f + reusablePos[1], reusablePos[2]);
        matrices.multiply(cameraRotation);
        matrices.scale(scale, scale, scale);
        textRenderer.drawWithOutline(
                popup.text, -popup.textWidth / 2f, 0,
                color, OUTLINE_COLOR,
                matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT);
        matrices.pop();
    }

    @Unique
    private void renderFloatingNumber(FloatingNumber number, float tickDelta,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers,
            TextRenderer textRenderer, Quaternionf cameraRotation,
            float entityHeight) {
        float exactAge = number.age + tickDelta;
        float progress = number.progress(tickDelta);

        computeNumberPosition(number, exactAge, reusablePos);
        computeCriticalJitter(number, progress, reusableJitter);

        float scale = computeNumberScale(number, progress);

        matrices.push();
        matrices.translate(
                reusablePos[0] + reusableJitter[0],
                entityHeight + 0.3f + reusablePos[1] + reusableJitter[1],
                reusablePos[2]);
        matrices.multiply(cameraRotation);
        matrices.scale(scale, scale, scale);
        textRenderer.drawWithOutline(
                number.text, -number.textWidth / 2f, 0,
                number.color, OUTLINE_COLOR,
                matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT);
        matrices.pop();
    }

    @Unique
    private void computePopupPosition(StatusTextPopup popup, float exactAge, float[] out) {
        out[0] = popup.startX + popup.vx * exactAge;
        out[2] = popup.startZ + popup.vz * exactAge;

        if (popup.isCritical) {
            out[1] = popup.startY + popup.vy * exactAge - (0.5f * GRAVITY * exactAge * exactAge);
        } else {
            out[1] = popup.startY + popup.vy * exactAge;
            out[0] += (float) Math.sin(exactAge * 0.2f) * 0.1f;
        }
    }

    @Unique
    private void computeNumberPosition(FloatingNumber number, float exactAge, float[] out) {
        out[0] = number.startX + number.vx * exactAge;
        out[2] = number.startZ + number.vz * exactAge;
        out[1] = number.isHeal
                ? number.startY + number.vy * exactAge
                : number.startY + number.vy * exactAge - (0.5f * GRAVITY * exactAge * exactAge);
    }

    @Unique
    private void computeCriticalJitter(FloatingNumber number, float progress, float[] out) {
        if (number.isCritical && progress < JITTER_THRESHOLD) {
            out[0] = (JITTER_RANDOM.nextFloat() - 0.5f) * JITTER_AMPLITUDE;
            out[1] = (JITTER_RANDOM.nextFloat() - 0.5f) * JITTER_AMPLITUDE;
        } else {
            out[0] = 0f;
            out[1] = 0f;
        }
    }

    @Unique
    private float computePopupScale(StatusTextPopup popup, float progress, float fade) {
        float base = popup.isCritical ? POPUP_CRITICAL_SCALE : POPUP_BASE_SCALE;
        float entry = computeEntryScale(progress);
        float shrink = computeShrink(progress, 1.0f, 0.5f);
        return base * entry * fade * shrink;
    }

    @Unique
    private float computeNumberScale(FloatingNumber number, float progress) {
        if (number.isHeal)
            return computeHealScale(progress);

        float base = number.isCritical ? NUMBER_CRITICAL_SCALE : NUMBER_BASE_SCALE;
        float entry = computeEntryScale(progress);
        float fade = computeFade(progress);
        float shrink = computeShrink(progress, 1.0f, 0.4f);
        return base * entry * fade * shrink;
    }

    @Unique
    private float computeHealScale(float progress) {
        float entry = progress < HEAL_ENTRY_END
                ? MathHelper.lerp(progress / HEAL_ENTRY_END, 0.0f, 1.0f)
                : 1.0f;
        float fade = progress > HEAL_FADE_START
                ? MathHelper.clamp(1.0f - (progress - HEAL_FADE_START) / HEAL_ENTRY_END, 0f, 1f)
                : 1.0f;
        return NUMBER_HEAL_SCALE * entry * fade;
    }

    @Unique
    private float computeEntryScale(float progress) {
        if (progress < ENTRY_PHASE_1_END)
            return MathHelper.lerp(progress / ENTRY_PHASE_1_END, 0.5f, 1.4f);
        if (progress < ENTRY_PHASE_2_END)
            return MathHelper.lerp((progress - ENTRY_PHASE_1_END) / ENTRY_PHASE_1_END, 1.4f, 1.0f);
        return 1.0f;
    }

    @Unique
    private float computeFade(float progress) {
        return progress > FADE_START
                ? MathHelper.clamp(1.0f - (progress - FADE_START) / FADE_START, 0f, 1f)
                : 1.0f;
    }

    @Unique
    private float computeShrink(float progress, float fullShrink, float minShrink) {
        return progress > FADE_START
                ? MathHelper.lerp((progress - FADE_START) / FADE_START, fullShrink, minShrink)
                : fullShrink;
    }

    @Unique
    private int applyAlpha(int color, float fade) {
        return (color & 0xFFFFFF) | ((int) (fade * 255) << 24);
    }
}