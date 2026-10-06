package it.unicam.cs.mpgc.rpg122627.model.character;

import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.Inventory;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;
import java.util.Objects;


/**
 * Personaggio giocante controllato dall'utente.
 * <p>
 * Un Hero possiede un livello, punti esperienza (XP), un {@link Inventory}
 * di oggetti, e due slot di equipaggiamento (arma e armatura) che
 * modificano rispettivamente il danno inflitto e la difesa.
 * <p>
 * Il calcolo di attacco e difesa delega all'equipaggiamento: in questo
 * modo, introdurre nuovi tipi di arma o armatura richiede solo nuove
 * istanze delle rispettive classi, mai modifiche a {@code Hero}
 * (Open/Closed Principle).
 */
public class Hero extends AbstractCharacter {

    private static final int XP_PER_LEVEL = 70;
    private static final int HP_GAIN_PER_LEVEL = 5;
    private static final int STAT_GAIN_PER_LEVEL = 1;

    private int level;
    private int experience;
    private int baseAttack;
    private int baseDefense;

    private final Inventory inventory;
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    public Hero(String name, int maxHp, int baseAttack, int baseDefense) {
        super(name, maxHp);
        if (baseAttack < 0) {
            throw new IllegalArgumentException("baseAttack must not be negative");
        }
        if (baseDefense < 0) {
            throw new IllegalArgumentException("baseDefense must not be negative");
        }
        this.level = 1;
        this.experience = 0;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.inventory = new Inventory();
    }

    @Override
    public int getAttackDamage() {
        int weaponBonus = (equippedWeapon != null) ? equippedWeapon.getAttackBonus() : 0;
        return baseAttack + weaponBonus;
    }

    @Override
    public int getDefense() {
        int armorBonus = (equippedArmor != null) ? equippedArmor.getDefenseBonus() : 0;
        return baseDefense + armorBonus;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public int getXpPerLevel() {
        return XP_PER_LEVEL;
    }

    /**
     * Equipaggia un'arma. L'arma deve essere già nell'inventario.
     * L'arma precedentemente equipaggiata (se presente) resta nell'inventario.
     *
     * @param weapon arma da equipaggiare (nullo = disequipaggia)
     * @throws IllegalArgumentException se l'arma non è nell'inventario
     */
    public void equipWeapon(Weapon weapon) {
        if (weapon != null && !inventory.getItems().contains(weapon)) {
            throw new IllegalArgumentException("weapon must be in inventory before equipping");
        }
        this.equippedWeapon = weapon;
    }

    /**
     * Equipaggia un'armatura. L'armatura deve essere già nell'inventario.
     *
     * @param armor armatura da equipaggiare (nullo = disequipaggia)
     * @throws IllegalArgumentException se l'armatura non è nell'inventario
     */
    public void equipArmor(Armor armor) {
        if (armor != null && !inventory.getItems().contains(armor)) {
            throw new IllegalArgumentException("armor must be in inventory before equipping");
        }
        this.equippedArmor = armor;
    }

    /**
     * Prova a equipaggiare automaticamente l'oggetto se è un upgrade.
     * <ul>
     *   <li>Arma: equipaggia se il bonus di attacco è strettamente maggiore
     *       di quello dell'arma attualmente equipaggiata (o se nessuna
     *       arma è equipaggiata).</li>
     *   <li>Armatura: equipaggia se il bonus di difesa è strettamente
     *       maggiore di quello dell'armatura attualmente equipaggiata (o
     *       se nessuna armatura è equipaggiata).</li>
     *   <li>Altri oggetti (pozioni, ecc.): nessun effetto.</li>
     * </ul>
     * L'oggetto deve essere già presente nell'inventario.
     *
     * @param item oggetto appena ottenuto
     * @return true se un auto-equip è avvenuto, false altrimenti
     */
    public boolean tryAutoEquip(Item item) {
        Objects.requireNonNull(item, "item must not be null");
        if (item instanceof Weapon newWeapon) {
            int currentBonus = (equippedWeapon != null) ? equippedWeapon.getAttackBonus() : 0;
            if (newWeapon.getAttackBonus() > currentBonus) {
                equipWeapon(newWeapon);
                return true;
            }
            return false;
        }
        if (item instanceof Armor newArmor) {
            int currentBonus = (equippedArmor != null) ? equippedArmor.getDefenseBonus() : 0;
            if (newArmor.getDefenseBonus() > currentBonus) {
                equipArmor(newArmor);
                return true;
            }
            return false;
        }
        return false;
    }

    public void gainExperience(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("experience gain must not be negative");
        }
        this.experience += amount;
        while (this.experience >= XP_PER_LEVEL) {
            this.experience -= XP_PER_LEVEL;
            levelUp();
        }
    }

    /**
     * Ripristina livello ed esperienza da un salvataggio.
     * <p>
     * Metodo pensato esclusivamente per la persistenza: imposta direttamente
     * i valori senza passare dalla progressione normale (che farebbe salire
     * di livello e aggiungerebbe stat), così lo stato ricostruito è identico
     * a quello salvato. Non va mai usato dalla logica di gioco: durante la
     * partita, l'XP si guadagna solo con {@link #gainExperience(int)}.
     *
     * @param level      livello da ripristinare (>= 1)
     * @param experience XP residua verso il livello successivo (0 <= xp < 100)
     */
    public void restoreFromSave(int level, int experience) {
        if (level < 1) {
            throw new IllegalArgumentException("level must be >= 1");
        }
        if (experience < 0 || experience >= XP_PER_LEVEL) {
            throw new IllegalArgumentException(
                    "experience must be in [0, " + XP_PER_LEVEL + "), got " + experience);
        }
        this.level = level;
        this.experience = experience;
    }

    private void levelUp() {
        this.level++;
        this.baseAttack += STAT_GAIN_PER_LEVEL;
        this.baseDefense += STAT_GAIN_PER_LEVEL;
        increaseMaxHp(HP_GAIN_PER_LEVEL);
    }

    @Override
    public String toString() {
        String weapon = (equippedWeapon != null) ? equippedWeapon.getName() : "—";
        String armor = (equippedArmor != null) ? equippedArmor.getName() : "—";
        return "%s [Lv%d, HP: %d/%d, ATK: %d, DEF: %d, XP: %d/%d, Arma: %s, Armatura: %s]"
                .formatted(getName(), level, getCurrentHp(), getMaxHp(),
                        getAttackDamage(), getDefense(), experience, XP_PER_LEVEL,
                        weapon, armor);
    }
}