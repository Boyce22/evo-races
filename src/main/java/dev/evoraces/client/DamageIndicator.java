package dev.evoraces.client;

import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import java.util.Locale;

public class DamageIndicator {
    public final int entityId;
    public final OrderedText text;
    public final int color;
    public final boolean isCritical;

    public final float offsetX;
    public final float offsetZ;

    public int age = 0;
    private static final int LIFETIME_TICKS = 30;

    public DamageIndicator(int entityId, float damage, boolean isCritical) {
        this.entityId = entityId;
        this.isCritical = isCritical;


        this.offsetX = (float) (Math.random() - 0.5);
        this.offsetZ = (float) (Math.random() - 0.5);

        String damageStr = (damage % 1 == 0)
                ? String.valueOf((int) damage)
                : String.format(Locale.US, "%.1f", damage);

        this.text = Text.literal(damageStr).asOrderedText();

        if (isCritical) this.color = 0xFFFFD700;
        else if (damage >= 15f) this.color = 0xFFFF3333;
        else if (damage >= 7f) this.color = 0xFFFFAA00;
        else this.color = 0xFFFFFF55;
    }

    public void tick() { age++; }

    public float progress(float tickDelta) {
        return (age + tickDelta) / LIFETIME_TICKS;
    }

    public boolean isExpired() { return age >= LIFETIME_TICKS; }
}