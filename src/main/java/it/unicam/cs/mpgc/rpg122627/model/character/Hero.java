package it.unicam.cs.mpgc.rpg122627.model.character;

/**
 * Personaggio giocante controllato dall'utente.
 * <p>
 * Un Hero possiede un livello, punti esperienza (XP) e statistiche base
 * di attacco e difesa. Salendo di livello, le sue statistiche aumentano.
 * <p>
 * In questa versione l'attacco e la difesa dipendono solo dalle statistiche
 * base del personaggio. Nelle prossime iterazioni verranno estesi per
 * tenere conto dell'arma equipaggiata e dell'armatura (senza modificare
 * questa classe: il contratto pubblico rimarrà invariato).
 */
public class Hero extends AbstractCharacter {

    private static final int XP_PER_LEVEL = 100;
    private static final int HP_GAIN_PER_LEVEL = 5;
    private static final int STAT_GAIN_PER_LEVEL = 1;

    private int level;
    private int experience;
    private int baseAttack;
    private int baseDefense;

    /**
     * Costruisce un eroe di livello 1 con le statistiche iniziali fornite.
     *
     * @param name        nome dell'eroe (non nullo, non vuoto)
     * @param maxHp       punti vita massimi iniziali (positivi)
     * @param baseAttack  valore di attacco base (non negativo)
     * @param baseDefense valore di difesa base (non negativo)
     * @throws IllegalArgumentException se baseAttack o baseDefense sono negativi
     */
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
    }

    @Override
    public int getAttackDamage() {
        return baseAttack;
    }

    @Override
    public int getDefense() {
        return baseDefense;
    }

    /**
     * @return il livello corrente dell'eroe
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return i punti esperienza correnti
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Aggiunge punti esperienza all'eroe, gestendo automaticamente
     * eventuali passaggi di livello.
     *
     * @param amount XP da aggiungere (non negativa)
     * @throws IllegalArgumentException se amount è negativa
     */
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
     * Applica un passaggio di livello: aumenta livello, HP massimi
     * (curando l'eroe della differenza) e statistiche base.
     */
    private void levelUp() {
        this.level++;
        this.baseAttack += STAT_GAIN_PER_LEVEL;
        this.baseDefense += STAT_GAIN_PER_LEVEL;
        increaseMaxHp(HP_GAIN_PER_LEVEL);
    }

    @Override
    public String toString() {
        return "%s [Lv%d, HP: %d/%d, ATK: %d, DEF: %d, XP: %d/%d]".formatted(
                getName(), level, getCurrentHp(), getMaxHp(),
                baseAttack, baseDefense, experience, XP_PER_LEVEL);
    }
}