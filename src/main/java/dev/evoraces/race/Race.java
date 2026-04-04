package dev.evoraces.race;

import java.util.List;

import dev.evoraces.race.effect.RacialEffect;

public class Race {
    private final String id;
    private final String name;
    private final RaceAttributes attributes;
    private final List<String> abilities;
    private final List<String> weaknesses;
    private final List<String> evolutionPaths;
    private final List<RacialEffect> racialEffects;
    
    public Race(String id, String name, RaceAttributes attributes, 
                List<String> abilities, List<String> weaknesses, 
                List<String> evolutionPaths, List<RacialEffect> racialEffects) {
        this.id = id;
        this.name = name;
        this.attributes = attributes;
        this.abilities = abilities;
        this.weaknesses = weaknesses;
        this.evolutionPaths = evolutionPaths;
        this.racialEffects = racialEffects;
    }
    
    public List<RacialEffect> getRacialEffects() {
        return racialEffects;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
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