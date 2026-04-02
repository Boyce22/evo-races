package dev.evoraces.client.race;

import dev.evoraces.race.Race;
import dev.evoraces.race.RaceRegistry;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RaceDataCache {
    private static RaceDataCache INSTANCE;
    private List<Race> races;
    private boolean loaded = false;
    
    private final ExecutorService preloadExecutor = Executors.newSingleThreadExecutor();
    
    private RaceDataCache() {}
    
    public static RaceDataCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RaceDataCache();
        }
        return INSTANCE;
    }
    
    public void loadFromServer(List<Race> races) {
        this.races = List.copyOf(races);
        this.loaded = true;
    }
    
    public List<Race> getRaces() {
        return races;
    }
    
    public Race getRace(int index) {
        if (races != null && index >= 0 && index < races.size()) {
            return races.get(index);
        }
        return null;
    }
    
    public Race getRaceById(String id) {
        return races.stream()
            .filter(r -> r.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public int getRaceCount() {
        return races != null ? races.size() : 0;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public void clear() {
        this.races = null;
        this.loaded = false;
    }
    
    public void shutdown() {
        preloadExecutor.shutdown();
    }
}
