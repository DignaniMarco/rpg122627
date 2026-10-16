package it.unicam.cs.mpgc.rpg122627.persistence;

import java.io.IOException;

/**
 * Astrazione per il salvataggio e il caricamento dello stato di una partita.
 * <p>
 * Il Game dipende da questa interfaccia (Dependency Inversion Principle):
 * una diversa implementazione (database, cloud, formato alternativo)
 * può essere sostituita senza modificare il codice del modello.
 */
public interface SaveManager {

    /**
     * Salva lo stato della partita.
     *
     * @param state lo stato da persistere (non nullo)
     * @throws IOException se il salvataggio fallisce
     */
    void save(GameState state) throws IOException;

    /**
     * Carica l'ultimo stato salvato.
     *
     * @return lo stato ricostruito, mai nullo
     * @throws IOException se il caricamento fallisce o nessun salvataggio esiste
     */
    GameState load() throws IOException;

    /**
     * @return true se esiste un salvataggio da cui caricare
     */
    boolean hasSave();

    /**
     * Cancella il file di salvataggio se esiste. Chiamato automaticamente
     * al termine di una partita (vittoria o sconfitta): una partita
     * conclusa non ha senso essere ricaricata, e lasciando il file su
     * disco si costringerebbe il giocatore a vedere il dialog
     * "Carica partita?" ad ogni avvio successivo.
     */
    void delete();
}