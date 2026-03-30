package dev.evoraces.network;

import dev.evoraces.client.DamageIndicatorRegistry;
import dev.evoraces.client.DamageIndicatorRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientPacketHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(DamagePayload.TYPE.getId(), (client, handler, buf, responseSender) -> {
            DamagePayload payload = DamagePayload.read(buf);

            float amount = payload.amount();
            int color = DamageIndicatorRenderer.resolveColor(amount);
            int displayDamage = (int) Math.ceil(amount);

            client.execute(() -> DamageIndicatorRegistry.add(payload.entityId(), displayDamage, color));
        });
    }
}