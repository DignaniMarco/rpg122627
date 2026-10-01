package it.unicam.cs.mpgc.rpg122627.model.behavior;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

/**
 * Strategia che decide cosa fa un nemico durante il proprio turno di combattimento.
 * <p>
 * Implementazioni diverse rappresentano diversi stili di AI:
 * aggressivo (attacca sempre), difensivo (si protegge quando ferito),
 * boss (schemi più complessi), ecc.
 * <p>
 * Questa interfaccia isola la logica decisionale del nemico dalla classe
 * {@link it.unicam.cs.mpgc.rpg122627.model.character.Enemy}, permettendo di
 * aggiungere nuovi comportamenti senza modificare quella classe (Open/Closed).
 */
public interface EnemyBehavior {

    /**
     * Decide e restituisce la quantità di danno che il nemico infligge
     * al bersaglio in questo turno.
     * <p>
     * L'implementazione può tenere conto dello stato attuale del nemico
     * (per esempio: attaccare più forte se ferito, oppure rinunciare
     * all'attacco per difendersi).
     *
     * @param self   il nemico che sta agendo (mai nullo)
     * @param target il bersaglio dell'azione, tipicamente l'eroe (mai nullo)
     * @return danno da infliggere al bersaglio (non negativo);
     *         zero indica che il nemico salta l'attacco (es. si difende)
     */
    int decideDamage(Character self, Character target);

    /**
     * @return breve descrizione testuale dell'azione compiuta,
     *         usata dalla GUI per mostrare cosa sta facendo il nemico
     *         (es. "attacca ferocemente", "si mette in difesa")
     */
    String describeAction();
}