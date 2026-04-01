package dev.evoraces.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class StatusTextPopup {

    public final int entityId;
    public final OrderedText text;
    public final int color;
    public final boolean isCritical;
    public final int textWidth;

    // Física
    public final float startX, startY, startZ;
    public final float vx, vy, vz;

    public int age = 0;
    private static final int LIFETIME_TICKS = 25;

    public StatusTextPopup(int entityId, String message, int color, boolean isCritical) {
        this.entityId = entityId;
        this.color = color;
        this.isCritical = isCritical;

        this.startX = (float) (Math.random() - 0.5f) * 0.8f;
        this.startY = (float) (Math.random() * 0.3f);
        this.startZ = (float) (Math.random() - 0.5f) * 0.8f;

        if (isCritical) {
            // Explosão pesada
            double angle = Math.random() * Math.PI * 2;
            double speed = 0.05 + Math.random() * 0.02;
            this.vx = (float) (Math.cos(angle) * speed);
            this.vz = (float) (Math.sin(angle) * speed);
            this.vy = 0.38f;
        } else {
            // Flutuar como fumaça (drift)
            this.vx = (float) (Math.random() - 0.5f) * 0.01f;
            this.vz = (float) (Math.random() - 0.5f) * 0.01f;
            this.vy = 0.06f;
        }

        this.text = Text.literal(message).asOrderedText();
        this.textWidth = MinecraftClient.getInstance().textRenderer.getWidth(this.text);
    }

    public void tick() { age++; }

    public float progress(float tickDelta) {
        return (age + tickDelta) / LIFETIME_TICKS;
    }

    public boolean isExpired() { return age >= LIFETIME_TICKS; }
}