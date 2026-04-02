package dev.evoraces.race;

import java.util.List;

/**
 * Representa uma raça no sistema EvoRaces.
 * Cada raça possui atributos base, habilidades iniciais, fraquezas e caminhos de evolução.
 */
public class Race {
    private final String id;
    private final String name;
    private final String description;
    private final RaceAttributes attributes;
    private final List<String> abilities;
    private final List<String> weaknesses;
    private final List<String> evolutionPaths;
    
    public Race(String id, String name, RaceAttributes attributes, 
                List<String> abilities, List<String> weaknesses, 
                List<String> evolutionPaths) {
        this(id, name, "", attributes, abilities, weaknesses, evolutionPaths);
    }
    
    public Race(String id, String name, String description, RaceAttributes attributes, 
                List<String> abilities, List<String> weaknesses, 
                List<String> evolutionPaths) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.attributes = attributes;
        this.abilities = abilities;
        this.weaknesses = weaknesses;
        this.evolutionPaths = evolutionPaths;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public RaceAttributes getAttributes() {
        return attributes;
    }
    
    public List<String> getAbilities() {
        return abilities;
    }
    
    public List<String> getWeaknesses() {
        return weaknesses;
    }
    
    public List<String> getEvolutionPaths() {
        return evolutionPaths;
    }
    
    @Override
    public String toString() {
        return "Race{id='" + id + "', name='" + name + "'}";
    }
}