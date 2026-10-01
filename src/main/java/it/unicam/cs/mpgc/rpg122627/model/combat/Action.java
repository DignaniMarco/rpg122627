package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

/**
 * Rappresenta una singola azione eseguibile da un partecipante al combattimento
 * durante il proprio turno.
 * <p>
 * Pattern Command: ogni Action incapsula una scelta ("attacca", "difenditi",
 * "usa oggetto", "fuggi") come oggetto autonomo che sa eseguirsi da solo.
 * {@link Combat} non deve conoscere i dettagli di cosa fa un'azione, solo
 * chiederle di eseguirsi: nuove azioni possono essere aggiunte senza
 * modificare il codice del combattimento (Open/Closed Principle).
 */
public interface Action {

    /**
     * Esegue l'azione, modificando lo stato dei partecipanti di conseguenza.
     *
     * @param actor  chi compie l'azione (mai nullo)
     * @param target bersaglio dell'azione, se applicabile (può essere nullo
     *               per azioni che non hanno bersaglio, come la fuga)
     * @return un {@link ActionResult} con descrizione testuale e esito,
     *         usato dalla GUI per aggiornare il log di combattimento
     */
    ActionResult execute(Character actor, Character target);

    /**
     * @return nome breve dell'azione, mostrato nei bottoni della GUI
     *         (es. "Attacca", "Difenditi")
     */
    String getName();
}