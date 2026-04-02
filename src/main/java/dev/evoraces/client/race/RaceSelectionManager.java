package dev.evoraces.client.race;

import dev.evoraces.EvoRaces;
import dev.evoraces.race.Race;
import dev.evoraces.race.RaceRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import java.io.File;
import java.io.IOException;

public class RaceSelectionManager {
    private static RaceSelectionManager INSTANCE;
    
    private boolean hasSelectedRace = false;
    private Race selectedRace = null;
    private boolean hasReceivedServerData = false;
    
    private RaceSelectionManager() {}
    
    public static RaceSelectionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RaceSelectionManager();
        }
        return INSTANCE;
    }
    
    public void onWorldJoin(File worldDir) {
        if (hasSelectedRace) return;
        
        File nbtFile = new File(worldDir, "evoraces_race.dat");
        if (nbtFile.exists()) {
            try {
                NbtCompound nbt = NbtIo.read(nbtFile);
                if (nbt != null && nbt.contains("race_id")) {
                    String raceId = nbt.getString("race_id");
                    Race race = RaceRegistry.getInstance().getRace(raceId);
                    if (race != null) {
                        selectedRace = race;
                        hasSelectedRace = true;
                        EvoRaces.LOGGER.info("Raça carregada do mundo: {}", race.getName());
                        return;
                    }
                }
            } catch (IOException e) {
                EvoRaces.LOGGER.error("Erro ao carregar dados de raça: {}", e.getMessage());
            }
        }
    }
    
    public void saveRaceSelection(File worldDir, String raceId) {
        try {
            File nbtFile = new File(worldDir, "evoraces_race.dat");
            NbtCompound nbt = new NbtCompound();
            nbt.putString("race_id", raceId);
            NbtIo.write(nbt, nbtFile);
            hasSelectedRace = true;
            selectedRace = RaceRegistry.getInstance().getRace(raceId);
            EvoRaces.LOGGER.info("Raça salva: {}", raceId);
        } catch (IOException e) {
            EvoRaces.LOGGER.error("Erro ao salvar dados de raça: {}", e.getMessage());
        }
    }
    
    public boolean shouldShowSelection() {
        return !hasSelectedRace;
    }
    
    public Race getSelectedRace() {
        return selectedRace;
    }
    
    public void setSelectedRace(Race race) {
        this.selectedRace = race;
        this.hasSelectedRace = true;
    }
    
    public boolean hasSelectedRace() {
        return hasSelectedRace;
    }
    
    public boolean hasReceivedServerData() {
        return hasReceivedServerData;
    }
    
    public void setReceivedServerData(boolean value) {
        this.hasReceivedServerData = value;
    }
    
    public void reset() {
        hasSelectedRace = false;
        selectedRace = null;
    }
}
