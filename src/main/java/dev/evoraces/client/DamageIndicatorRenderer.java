package dev.evoraces.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class DamageIndicatorRenderer {

    private static final int FULL_BRIGHT = 15728880;
    private static final int OUTLINE_COLOR = 0xFF000000;
    private static final float DAMAGE_HIGH = 8f;
    private static final float DAMAGE_MEDIUM = 4f;
    private static final int COLOR_HIGH = 0xFFFF5555;
    private static final int COLOR_MEDIUM = 0xFFFFAA55;
    private static final int COLOR_LOW = 0xFFFFFF55;

    public static int resolveColor(float amount) {
        if (amount >= DAMAGE_HIGH) return COLOR_HIGH;
        if (amount >= DAMAGE_MEDIUM) return COLOR_MEDIUM;
        return COLOR_LOW;
    }

    public static void renderSingle(MatrixStack matrices, MinecraftClient client, VertexConsumerProvider vertexConsumers, DamageIndicator indicator, float tickDelta) {
        renderDamageText(matrices, client, vertexConsumers, indicator);
    }

    private static void renderDamageText(MatrixStack matrices, MinecraftClient client, VertexConsumerProvider vertexConsumers, DamageIndicator indicator) {
        String damageStr = String.valueOf(indicator.damage);
        OrderedText text = Text.literal(damageStr).asOrderedText();
        float x = -client.textRenderer.getWidth(text) / 2f;

        drawTextWithOutline(matrices, client, vertexConsumers, text, x, indicator.color);
    }

    private static void drawTextWithOutline(MatrixStack matrices, MinecraftClient client, VertexConsumerProvider vertexConsumers, OrderedText text, float x, int color) {
        client.textRenderer.drawWithOutline(
                text, x, 0, color, OUTLINE_COLOR,
                matrices.peek().getPositionMatrix(), vertexConsumers, FULL_BRIGHT
        );
    }
}