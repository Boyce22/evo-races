package dev.evoraces.network;

import dev.evoraces.client.FloatingNumberRegistry;
import dev.evoraces.client.StatusTextRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientPacketHandler {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(DamagePayload.TYPE.getId(), (client, handler, buf, responseSender) -> {
            DamagePayload payload = DamagePayload.read(buf);
            client.execute(() -> FloatingNumberRegistry.add(
                    payload.entityId(),
                    payload.amount(),
                    payload.isCritical(),
                    false // isHeal
            ));
        });

        ClientPlayNetworking.registerGlobalReceiver(HealPayload.TYPE.getId(), (client, handler, buf, responseSender) -> {
            HealPayload payload = HealPayload.read(buf);
            client.execute(() -> FloatingNumberRegistry.add(
                    payload.entityId(),
                    payload.amount(),
                    false,
                    true
            ));
        });

        ClientPlayNetworking.registerGlobalReceiver(EffectPayload.TYPE.getId(), (client, handler, buf, responseSender) -> {
            EffectPayload payload = EffectPayload.read(buf);
            client.execute(() -> StatusTextRegistry.add(
                    payload.entityId(),
                    payload.message(),
                    payload.color(),
                    payload.isCritical()
            ));
        });
    }
}