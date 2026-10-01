package it.unicam.cs.mpgc.rpg122627.model.item;

/**
 * Armatura equipaggiabile dall'eroe. Quando equipaggiata, somma il proprio
 * bonus di difesa al valore di difesa base dell'eroe.
 */
public class Armor implements Item {

    private final String name;
    private final int defenseBonus;

    public Armor(String name, int defenseBonus) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (defenseBonus < 0) {
            throw new IllegalArgumentException("defenseBonus must not be negative");
        }
        this.name = name;
        this.defenseBonus = defenseBonus;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Armatura. Bonus difesa: +" + defenseBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }
}