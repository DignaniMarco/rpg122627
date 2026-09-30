package it.unicam.cs.mpgc.rpg122627.model.character;

/**
 * Rappresenta una qualsiasi entità che può partecipare al combattimento.
 * <p>
 * Un Character ha un nome, punti vita e può subire danno.
 * Ogni entità che può stare su un campo di battaglia (eroe, nemico, boss)
 * deve implementare questa interfaccia.
 */
public interface Character {

    /**
     * @return il nome del personaggio
     */
    String getName();

    /**
     * @return i punti vita correnti
     */
    int getCurrentHp();

    /**
     * @return i punti vita massimi
     */
    int getMaxHp();

    /**
     * Applica una quantità di danno al personaggio.
     * Gli HP correnti non possono scendere sotto zero.
     *
     * @param amount quantità di danno da infliggere (non negativa)
     * @throws IllegalArgumentException se amount è negativo
     */
    void takeDamage(int amount);

    /**
     * Ripristina una quantità di punti vita.
     * Gli HP correnti non possono superare quelli massimi.
     *
     * @param amount quantità di HP da ripristinare (non negativa)
     * @throws IllegalArgumentException se amount è negativo
     */
    void heal(int amount);

    /**
     * @return true se gli HP correnti sono zero
     */
    boolean isDead();

    /**
     * Calcola il danno che questo personaggio infligge in un attacco base,
     * tenendo conto di eventuali equipaggiamenti o modificatori.
     *
     * @return danno base dell'attacco
     */
    int getAttackDamage();

    /**
     * @return valore di difesa che riduce il danno subito
     */
    int getDefense();
}