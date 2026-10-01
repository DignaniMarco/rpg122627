package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

/**
 * Singola stanza di un {@link Dungeon}. Ogni stanza ha una propria logica
 * che si attiva quando l'eroe entra.
 * <p>
 * L'interfaccia è pensata per il polimorfismo: il Dungeon chiede alla
 * stanza corrente di attivarsi tramite {@link #onEnter(Hero)} senza
 * conoscerne il tipo specifico. Nuovi tipi di stanza possono essere
 * aggiunti implementando questa interfaccia senza modificare Dungeon
 * o le altre stanze (Open/Closed Principle).
 */
public interface Room {

    /**
     * @return nome identificativo della stanza, mostrato nella GUI
     *         (es. "Corridoio buio", "Sala del trono")
     */
    String getName();

    /**
     * @return descrizione testuale della stanza, mostrata quando l'eroe entra
     */
    String getDescription();

    /**
     * Attiva l'effetto della stanza al momento dell'ingresso dell'eroe.
     * <p>
     * Il metodo non esegue direttamente combattimenti o modifiche durature:
     * restituisce un {@link RoomEvent} che descrive cosa dovrebbe succedere,
     * delegando la gestione al chiamante (facciata di gioco o GUI).
     *
     * @param hero eroe che entra nella stanza (non nullo)
     * @return evento che descrive l'esito dell'ingresso
     */
    RoomEvent onEnter(Hero hero);
}