package dev.evoraces.client;

import net.minecraft.util.math.Vec3d;

public class DamageIndicator {

    public final int entityId; // associa ao mob correto
    public final int damage;
    public final int color;
    public final float glowIntensity;
    public final String effectText;
    public final boolean isCritical;

    public int age = 0;
    private static final int LIFETIME_TICKS = 40;

    public DamageIndicator(int entityId, int damage, int color) {
        this(entityId, damage, color, 1.0f, null, false);
    }

    public DamageIndicator(int entityId, int damage, int color, float glowIntensity, String effectText, boolean isCritical) {
        this.entityId = entityId;
        this.damage = damage;
        this.color = color;
        this.glowIntensity = glowIntensity;
        this.effectText = effectText;
        this.isCritical = isCritical;
    }

    public void tick() { age++; }

    public float progress(float tickDelta) {
        return (age + tickDelta) / LIFETIME_TICKS;
    }

    public boolean isExpired() { return age >= LIFETIME_TICKS; }

    public boolean hasGlow() { return glowIntensity > 1.0f; }

    public boolean hasEffectText() { return effectText != null; }

    public boolean isEffectTextVisible(float tickDelta) {
        return hasEffectText() && progress(tickDelta) < 0.15f;
    }
}