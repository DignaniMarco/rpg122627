package it.unicam.cs.mpgc.rpg122627.model;

/**
 * Stato globale di una partita, usato dal {@link Game} per segnalare
 * alla GUI quale schermata/azioni mostrare.
 */
public enum GameStatus {
    /** L'eroe è in una stanza e può scegliere di avanzare o usare l'inventario. */
    EXPLORING,
    /** Combattimento in corso: l'eroe deve scegliere un'azione per il proprio turno. */
    IN_COMBAT,
    /** Il dungeon è stato completato (boss sconfitto). */
    VICTORY,
    /** L'eroe è morto. */
    DEFEAT
}