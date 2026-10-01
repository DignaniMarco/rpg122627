package it.unicam.cs.mpgc.rpg122627.model.character;

import it.unicam.cs.mpgc.rpg122627.model.behavior.EnemyBehavior;

import java.util.Objects;

/**
 * Nemico controllato dal sistema, che l'eroe affronta durante il gioco.
 * <p>
 * A differenza di {@link Hero}, l'Enemy non ha inventario né progressione
 * di livello: nasce con statistiche fisse (definite da chi lo istanzia,
 * tipicamente una factory che legge da file di configurazione) e delega
 * la propria logica di turno a un {@link EnemyBehavior}.
 * <p>
 * Questa classe è unica per tutti i tipi di nemico (goblin, orco, boss...):
 * la varietà nasce dalla combinazione di statistiche e comportamento,
 * non da una gerarchia di sottoclassi. In questo modo, aggiungere un nuovo
 * tipo di nemico è solo una questione di dati (una nuova riga di config),
 * non di codice — nel pieno rispetto dell'Open/Closed Principle.
 */
public class Enemy extends AbstractCharacter {

    private final int attackDamage;
    private final int defense;
    private final int xpReward;
    private final EnemyBehavior behavior;

    /**
     * Costruisce un nemico con le statistiche fornite e la strategia di turno indicata.
     *
     * @param name         nome del nemico (non nullo, non vuoto)
     * @param maxHp        punti vita massimi (positivi)
     * @param attackDamage danno inflitto in attacco base (non negativo)
     * @param defense      valore di difesa (non negativo)
     * @param xpReward     XP rilasciata all'eroe quando questo nemico viene sconfitto (non negativa)
     * @param behavior     strategia che decide le azioni durante il proprio turno (non nulla)
     * @throws IllegalArgumentException se un valore numerico è fuori dai limiti
     * @throws NullPointerException     se behavior è nullo
     */
    public Enemy(String name, int maxHp, int attackDamage, int defense,
                 int xpReward, EnemyBehavior behavior) {
        super(name, maxHp);
        if (attackDamage < 0) {
            throw new IllegalArgumentException("attackDamage must not be negative");
        }
        if (defense < 0) {
            throw new IllegalArgumentException("defense must not be negative");
        }
        if (xpReward < 0) {
            throw new IllegalArgumentException("xpReward must not be negative");
        }
        this.attackDamage = attackDamage;
        this.defense = defense;
        this.xpReward = xpReward;
        this.behavior = Objects.requireNonNull(behavior, "behavior must not be null");
    }

    @Override
    public int getAttackDamage() {
        return attackDamage;
    }

    @Override
    public int getDefense() {
        return defense;
    }

    /**
     * @return XP che l'eroe guadagna sconfiggendo questo nemico
     */
    public int getXpReward() {
        return xpReward;
    }

    /**
     * @return la strategia di comportamento del nemico
     */
    public EnemyBehavior getBehavior() {
        return behavior;
    }
}