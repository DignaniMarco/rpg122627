package it.unicam.cs.mpgc.rpg122627.model.item;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

/**
 * Pozione di cura: ripristina una quantità fissa di HP all'eroe
 * (senza superare il massimo).
 */
public class HealingPotion implements Consumable {

    private final String name;
    private final int healAmount;

    public HealingPotion(String name, int healAmount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (healAmount <= 0) {
            throw new IllegalArgumentException("healAmount must be positive");
        }
        this.name = name;
        this.healAmount = healAmount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Ripristina " + healAmount + " HP.";
    }

    @Override
    public String applyTo(Hero hero) {
        int before = hero.getCurrentHp();
        hero.heal(healAmount);
        int actual = hero.getCurrentHp() - before;
        return "%s beve %s e recupera %d HP.".formatted(hero.getName(), name, actual);
    }
}