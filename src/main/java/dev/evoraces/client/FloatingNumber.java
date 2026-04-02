package dev.evoraces.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import java.util.Locale;

public class FloatingNumber {

    public final int entityId;
    public OrderedText text;
    public int color;
    public boolean isHeal;
    public boolean isCritical;
    public int textWidth;
    private float totalAmount;

    public final float startX, startY, startZ;
    public final float vx, vy, vz;

    public int age = 0;
    private static final int LIFETIME_TICKS = 25;

    public FloatingNumber(int entityId, float amount, boolean isCritical, boolean isHeal) {
        this.entityId = entityId;
        this.isCritical = isCritical;
        this.totalAmount = amount;
        this.isHeal = isHeal;

        this.startX = (float) (Math.random() - 0.3f) * 0.5f;
        this.startY = (float) (Math.random() * 0.2f);
        this.startZ = (float) (Math.random() - 0.3f) * 0.5f;

        double angle = Math.random() * Math.PI * 2;
        double speed = isHeal ? 0.005 + Math.random() * 0.005 : 0.03 + Math.random() * 0.02;

        this.vx = (float) (Math.cos(angle) * speed);
        this.vz = (float) (Math.sin(angle) * speed);
        this.vy = isHeal ? 0.02f : (isCritical ? 0.30f : 0.22f);

        updateDisplay();
    }

    public void addAmount(float extraAmount, boolean critical) {
        this.totalAmount += extraAmount;
        if (critical) this.isCritical = true;
        this.age = 2;
        updateDisplay();
    }

    private void updateDisplay() {
        String amountStr = (totalAmount % 1 == 0)
                ? String.valueOf((int) totalAmount)
                : String.format(Locale.US, "%.1f", totalAmount);

        this.text = Text.literal(amountStr).asOrderedText();

        if (isHeal) this.color = 0xFF55FF55;
        else if (isCritical) this.color = 0xFFFFD700;
        else if (totalAmount >= 20f) this.color = 0xFFFF0000;
        else if (totalAmount >= 10f) this.color = 0xFFFFAA00;
        else this.color = 0xFFFFFF55;

        this.textWidth = MinecraftClient.getInstance().textRenderer.getWidth(this.text);
    }

    public void tick() { age++; }
    public float progress(float tickDelta) { return (age + tickDelta) / LIFETIME_TICKS; }
    public boolean isExpired() { return age >= LIFETIME_TICKS; }
}