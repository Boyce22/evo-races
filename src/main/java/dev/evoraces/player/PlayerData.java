package dev.evoraces.player;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerData {
    private String raceId;

    public PlayerData() {
        this.raceId = null;
    }

    public void setRace(String raceId) {
        this.raceId = raceId;
    }

    public String getRaceId() {
        return raceId;
    }

    public boolean hasRace() {
        return raceId != null && !raceId.isEmpty();
    }

    public void clearRace() {
        this.raceId = null;
    }

    // Lê direto do campo injetado pelo mixin
    public static PlayerData get(ServerPlayerEntity player) {
        PlayerData data = new PlayerData();
        data.raceId = ((PlayerDataHolder) player).evoraces$getRaceId();
        return data;
    }

    // Escreve direto no campo injetado pelo mixin (Minecraft persiste automaticamente via writeCustomDataToNbt)
    public static void save(ServerPlayerEntity player, PlayerData data) {
        ((PlayerDataHolder) player).evoraces$setRaceId(data.getRaceId());
    }

    public static void setPlayerRace(ServerPlayerEntity player, String raceId) {
        ((PlayerDataHolder) player).evoraces$setRaceId(raceId);
    }

    public static String getPlayerRaceId(ServerPlayerEntity player) {
        return ((PlayerDataHolder) player).evoraces$getRaceId();
    }

    public static boolean playerHasRace(ServerPlayerEntity player) {
        String id = getPlayerRaceId(player);
        return id != null && !id.isEmpty();
    }
}