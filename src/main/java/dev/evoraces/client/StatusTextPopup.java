package dev.evoraces.client;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class StatusTextPopup {

    public final int entityId;
    public final OrderedText text; // Texto já processado
    public final int color;
    public final boolean isCritical;

    public final float offsetX;
    public final float offsetZ;

    public int age = 0;
    private static final int LIFETIME_TICKS = 40;

    public StatusTextPopup(int entityId, String message, int color, boolean isCritical) {
        this.entityId = entityId;
        this.color = color;
        this.isCritical = isCritical;

        this.offsetX = (float) (Math.random() - 0.5) * 1.4f;
        this.offsetZ = (float) (Math.random() - 0.5) * 1.4f;

        this.text = Text.literal(message).asOrderedText();
    }

    public void tick() { age++; }

    public float progress(float tickDelta) {
        return (age + tickDelta) / LIFETIME_TICKS;
    }

    public boolean isExpired() { return age >= LIFETIME_TICKS; }
}