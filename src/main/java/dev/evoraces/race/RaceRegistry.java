package dev.evoraces.race;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RaceRegistry {

    private static final RaceRegistry INSTANCE = new RaceRegistry();

    private final Map<String, Race> races = new HashMap<>();

    private RaceRegistry() {
    }

    public static RaceRegistry getInstance() {
        return INSTANCE;
    }

    public void registerRace(Race race) {
        if (hasRace(race.getId())) {
            throw new IllegalArgumentException("Raça já registrada: " + race.getId());
        }
        races.put(race.getId(), race);
    }

    public void clear() {
        races.clear();
    }

    public Race getRace(String id) {
        Race race = races.get(id);
        if (race == null)
            throw new IllegalArgumentException("Raça não encontrada: " + id);
        return race;
    }

    public Map<String, Race> getAllRaces() {
        return Collections.unmodifiableMap(races);
    }

    public boolean hasRace(String id) {
        return races.containsKey(id);
    }

    public int size() {
        return races.size();
    }
}