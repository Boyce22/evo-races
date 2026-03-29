package dev.evoraces.race;

import java.util.HashMap;
import java.util.Map;

/**
 * Registro central de todas as raças disponíveis no sistema.
 * Responsável por carregar, armazenar e fornecer acesso às raças.
 */
public class RaceRegistry {
    private static final RaceRegistry INSTANCE = new RaceRegistry();
    private final Map<String, Race> races = new HashMap<>();
    
    private RaceRegistry() {
        // Construtor privado para singleton
    }
    
    public static RaceRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Registra uma nova raça no sistema.
     */
    public void registerRace(Race race) {
        if (races.containsKey(race.getId())) {
            throw new IllegalArgumentException("Raça já registrada: " + race.getId());
        }
        races.put(race.getId(), race);
    }
    
    /**
     * Obtém uma raça pelo seu ID.
     */
    public Race getRace(String id) {
        Race race = races.get(id);
        if (race == null) {
            throw new IllegalArgumentException("Raça não encontrada: " + id);
        }
        return race;
    }
    
    /**
     * Retorna todas as raças registradas.
     */
    public Map<String, Race> getAllRaces() {
        return new HashMap<>(races);
    }
    
    /**
     * Verifica se uma raça existe.
     */
    public boolean hasRace(String id) {
        return races.containsKey(id);
    }
    
    /**
     * Limpa o registro (útil para testes ou recarregamento).
     */
    public void clear() {
        races.clear();
    }
    
    /**
     * Retorna o número de raças registradas.
     */
    public int size() {
        return races.size();
    }
}