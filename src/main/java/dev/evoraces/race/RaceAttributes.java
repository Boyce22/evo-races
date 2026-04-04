package dev.evoraces.race;

public class RaceAttributes {
    private final int vitality;
    private final int strength;
    private final int agility;
    private final int intellect;
    private final int resistance;
    private final int dexterity;  // Dano à distância
    private final int luck;       // Crítico

    public RaceAttributes(int vitality, int strength, int agility,
                          int intellect, int resistance, int dexterity, int luck) {
        this.vitality = vitality;
        this.strength = strength;
        this.agility = agility;
        this.intellect = intellect;
        this.resistance = resistance;
        this.dexterity = dexterity;
        this.luck = luck;
    }

    public int getVitality() { return vitality; }
    public int getStrength() { return strength; }
    public int getAgility() { return agility; }
    public int getIntellect() { return intellect; }
    public int getResistance() { return resistance; }
    public int getDexterity() { return dexterity; }
    public int getLuck() { return luck; }

    public String toDisplayString() {
        return String.format(
                "VIT %s  STR %s  AGI %s  INT %s  DEX %s  LCK %s  RES %s",
                formatDelta(vitality),
                formatDelta(strength),
                formatDelta(agility),
                formatDelta(intellect),
                formatDelta(dexterity),
                formatDelta(luck),
                formatDelta(resistance));
    }

    private String formatDelta(int value) {
        int delta = value - 100;
        if (delta > 0) return "+" + delta;
        if (delta < 0) return String.valueOf(delta);
        return "±0";
    }

    @Override
    public String toString() {
        return String.format("Attributes{VIT=%d, STR=%d, AGI=%d, INT=%d, DEX=%d, LCK=%d, RES=%d}",
                vitality, strength, agility, intellect, dexterity, luck, resistance);
    }
}