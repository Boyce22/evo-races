package dev.evoraces.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record DamagePayload(int entityId, float amount) implements FabricPacket {

    public static final PacketType<DamagePayload> TYPE =
            PacketType.create(
                    new Identifier("evoraces", "damage_indicator"),
                    DamagePayload::read
            );

    public static DamagePayload read(PacketByteBuf buf) {
        return new DamagePayload(buf.readInt(), buf.readFloat());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeFloat(amount);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}