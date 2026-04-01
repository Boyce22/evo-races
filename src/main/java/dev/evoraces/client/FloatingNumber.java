package dev.evoraces.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import java.util.Locale;

public class FloatingNumber {

    public final int entityId;
    public OrderedText text;
    public int color;
    public boolean isCritical;
    public int textWidth;
    private float totalAmount; // Agora rastreamos o valor bruto

    public final float startX, startY, startZ;
    public final float vx, vy, vz;

    public int age = 0;
    private static final int LIFETIME_TICKS = 25; // Aumentado levemente para dar tempo de acumular

    public FloatingNumber(int entityId, float amount, boolean isCritical, boolean isHeal) {
        this.entityId = entityId;
        this.isCritical = isCritical;
        this.totalAmount = amount;

        this.startX = (float) (Math.random() - 0.5f) * 0.5f;
        this.startY = (float) (Math.random() * 0.3f);
        this.startZ = (float) (Math.random() - 0.5f) * 0.5f;

        double angle = Math.random() * Math.PI * 2;
        double speed = 0.03 + Math.random() * 0.02;
        this.vx = (float) (Math.cos(angle) * speed);
        this.vz = (float) (Math.sin(angle) * speed);
        this.vy = isCritical ? 0.30f : 0.22f;

        updateDisplay();
    }

    // Método chamado pelo Registry para somar dano
    public void addAmount(float extraAmount, boolean critical) {
        this.totalAmount += extraAmount;
        if (critical) this.isCritical = true; // Se um dos hits for crítico, o acumulado vira crítico
        this.age = 2;
        updateDisplay();
    }

    private void updateDisplay() {
        String amountStr = (totalAmount % 1 == 0)
                ? String.valueOf((int) totalAmount)
                : String.format(Locale.US, "%.1f", totalAmount);

        this.text = Text.literal(amountStr).asOrderedText();

        // Cor dinâmica: o número muda de cor conforme o dano acumulado cresce!
        if (isCritical) this.color = 0xFFFFD700; // Ouro para Crítico
        else if (totalAmount >= 20f) this.color = 0xFFFF0000; // Vermelho Sangue
        else if (totalAmount >= 10f) this.color = 0xFFFFAA00; // Laranja
        else this.color = 0xFFFFFF55; // Amarelo Padrão

        this.textWidth = MinecraftClient.getInstance().textRenderer.getWidth(this.text);
    }

    public void tick() { age++; }
    public float progress(float tickDelta) { return (age + tickDelta) / LIFETIME_TICKS; }
    public boolean isExpired() { return age >= LIFETIME_TICKS; }
}