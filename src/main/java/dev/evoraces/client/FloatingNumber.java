package dev.evoraces.client;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import java.util.Locale;

public class FloatingNumber {
    public final int entityId;
    public final OrderedText text;
    public final int color;
    public final boolean isCritical;
    public final float offsetX;
    public final float offsetZ;
    public int age = 0;
    private static final int LIFETIME_TICKS = 30;

    public FloatingNumber(int entityId, float amount, boolean isCritical, boolean isHeal) {
        this.entityId = entityId;
        this.isCritical = isCritical;

        this.offsetX = (float) (Math.random() - 0.5);
        this.offsetZ = (float) (Math.random() - 0.5);

        String amountStr = (amount % 1 == 0)
                ? String.valueOf((int) amount)
                : String.format(Locale.US, "%.1f", amount);

        String prefix = isHeal ? "+" : "";
        this.text = Text.literal(prefix + amountStr).asOrderedText();

        if (isHeal) this.color = 0xFF55FF55; // Verde para cura
        else if (isCritical) this.color = 0xFFFFD700; // Dourado
        else if (amount >= 15f) this.color = 0xFFFF3333; // Vermelho forte
        else if (amount >= 7f) this.color = 0xFFFFAA00; // Laranja
        else this.color = 0xFFFFFF55; // Amarelo
    }

    public void tick() { age++; }

    public float progress(float tickDelta) {
        return (age + tickDelta) / LIFETIME_TICKS;
    }

    public boolean isExpired() { return age >= LIFETIME_TICKS; }
}