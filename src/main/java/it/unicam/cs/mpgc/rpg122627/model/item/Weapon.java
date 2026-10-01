package it.unicam.cs.mpgc.rpg122627.model.item;

/**
 * Arma equipaggiabile dall'eroe. Quando equipaggiata, somma il proprio
 * bonus di attacco al danno base dell'eroe.
 * <p>
 * Non implementa {@link Consumable}: equipaggiare non è "usare" e l'arma
 * non si consuma. L'operazione di equipaggiamento è responsabilità
 * dell'eroe stesso.
 */
public class Weapon implements Item {

    private final String name;
    private final int attackBonus;

    public Weapon(String name, int attackBonus) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (attackBonus < 0) {
            throw new IllegalArgumentException("attackBonus must not be negative");
        }
        this.name = name;
        this.attackBonus = attackBonus;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Arma. Bonus attacco: +" + attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}