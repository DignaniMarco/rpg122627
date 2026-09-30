package it.unicam.cs.mpgc.rpg122627.model.character;

import java.util.Objects;

/**
 * Implementazione base condivisa da tutti i personaggi (eroi e nemici).
 * <p>
 * Fornisce la gestione di nome, punti vita correnti e massimi, e i metodi
 * per subire danno, curarsi e verificare la morte. Le sottoclassi devono
 * decidere come calcolare danno di attacco e difesa, poiché tali valori
 * dipendono dallo specifico tipo di personaggio (es. equipaggiamento
 * per l'eroe, statistiche fisse o comportamento AI per i nemici).
 */
public abstract class AbstractCharacter implements Character {

    private final String name;
    private final int maxHp;
    private int currentHp;

    /**
     * Costruisce un personaggio con nome e punti vita massimi.
     * All'inizializzazione, gli HP correnti sono uguali a quelli massimi.
     *
     * @param name  nome del personaggio (non nullo, non vuoto)
     * @param maxHp punti vita massimi (positivi)
     * @throws NullPointerException     se name è nullo
     * @throws IllegalArgumentException se name è vuoto o maxHp non è positivo
     */
    protected AbstractCharacter(String name, int maxHp) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp must be positive, got " + maxHp);
        }
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int getCurrentHp() {
        return currentHp;
    }

    @Override
    public final int getMaxHp() {
        return maxHp;
    }

    @Override
    public final void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("damage must not be negative, got " + amount);
        }
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    @Override
    public final void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("heal amount must not be negative, got " + amount);
        }
        this.currentHp = Math.min(maxHp, this.currentHp + amount);
    }

    @Override
    public final boolean isDead() {
        return currentHp == 0;
    }

    @Override
    public String toString() {
        return "%s [HP: %d/%d]".formatted(name, currentHp, maxHp);
    }
}