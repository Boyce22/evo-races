package dev.evoraces.network;

import dev.evoraces.EvoRaces;
import dev.evoraces.player.PlayerDataHolder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier SYNC_RACE_ID = new Identifier(EvoRaces.MOD_ID, "sync_race");

    public static void sendRaceSync(ServerPlayerEntity player, String raceId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(raceId != null ? raceId : "none");
        ServerPlayNetworking.send(player, SYNC_RACE_ID, buf);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_RACE_ID, (client, handler, buf, responseSender) -> {
            String raceId = buf.readString();
            String finalRaceId = raceId.equals("none") ? null : raceId;

            client.execute(() -> {
                if (client.player != null) {
                    ((PlayerDataHolder) client.player).evoraces$setRaceId(finalRaceId);

                    // ESSA LINHA É VITAL: Ela força o seu jogo a encolher o boneco na hora
                    client.player.calculateDimensions();

                    // Dica: Se 'calculateDimensions' der erro de novo, use:
                    // client.getNetworkHandler().sendPacket(new net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket(client.player, net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                }
            });
        });
    }
}