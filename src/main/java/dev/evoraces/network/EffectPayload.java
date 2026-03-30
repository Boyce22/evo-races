package dev.evoraces.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record EffectPayload(int entityId, String message, int color, boolean isCritical) implements FabricPacket {

    public static final PacketType<EffectPayload> TYPE =
            PacketType.create(
                    new Identifier("evoraces", "effect_text"),
                    EffectPayload::read
            );

    public static EffectPayload read(PacketByteBuf buf) {
        return new EffectPayload(buf.readInt(), buf.readString(), buf.readInt(), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeString(message);
        buf.writeInt(color);
        buf.writeBoolean(isCritical);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}