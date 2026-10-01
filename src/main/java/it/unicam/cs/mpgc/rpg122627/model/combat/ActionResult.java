package it.unicam.cs.mpgc.rpg122627.model.combat;

import java.util.Objects;

/**
 * Risultato dell'esecuzione di un'{@link Action}.
 * <p>
 * Oggetto immutabile che trasporta la descrizione testuale di cosa è
 * successo (per il log di combattimento) e informazioni opzionali come
 * la quantità di danno inflitta o se l'azione ha concluso il combattimento
 * (es. una fuga riuscita).
 */
public final class ActionResult {

    private final String message;
    private final int damageDealt;
    private final boolean endsCombat;

    private ActionResult(String message, int damageDealt, boolean endsCombat) {
        this.message = Objects.requireNonNull(message, "message must not be null");
        if (damageDealt < 0) {
            throw new IllegalArgumentException("damageDealt must not be negative");
        }
        this.damageDealt = damageDealt;
        this.endsCombat = endsCombat;
    }

    /**
     * Costruisce un risultato di azione standard (il combattimento continua).
     */
    public static ActionResult of(String message, int damageDealt) {
        return new ActionResult(message, damageDealt, false);
    }

    /**
     * Costruisce un risultato di azione che termina il combattimento,
     * come una fuga riuscita.
     */
    public static ActionResult ending(String message) {
        return new ActionResult(message, 0, true);
    }

    public String getMessage() {
        return message;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public boolean endsCombat() {
        return endsCombat;
    }
}