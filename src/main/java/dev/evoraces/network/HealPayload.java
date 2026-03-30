package dev.evoraces.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record HealPayload(int entityId, float amount) implements FabricPacket {

    public static final PacketType<HealPayload> TYPE =
            PacketType.create(
                    new Identifier("evoraces", "heal_indicator"),
                    HealPayload::read
            );

    public static HealPayload read(PacketByteBuf buf) {
        return new HealPayload(buf.readInt(), buf.readFloat());
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