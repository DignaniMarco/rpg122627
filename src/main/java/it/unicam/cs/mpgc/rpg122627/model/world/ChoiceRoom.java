package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Stanza che offre all'eroe una scelta tra più percorsi alternativi
 * nel grafo del dungeon.
 * <p>
 * Entrando, la stanza non produce effetti meccanici: restituisce un
 * {@link RoomEvent} di tipo {@link RoomEvent.Type#CHOICE_AWAITING} con
 * la lista delle opzioni. Sarà il chiamante (facciata di gioco e GUI)
 * a mostrare le scelte all'utente e a reindirizzare il dungeon
 * verso il nodo scelto.
 * <p>
 * Le opzioni vengono registrate dopo la costruzione tramite
 * {@link #addOption(ChoiceOption)}, così che la ChoiceRoom possa
 * essere creata prima dei nodi di destinazione nel grafo (necessario
 * perché i nodi, non la Room, formano la struttura del grafo).
 * <p>
 * Scelta di design: le opzioni sono "informate" — ogni
 * {@link ChoiceOption} include una descrizione di cosa aspetta sulla
 * strada, così da offrire al giocatore una scelta tattica e non un
 * coin flip.
 */
public class ChoiceRoom implements Room {

    private final String name;
    private final String description;
    private final List<ChoiceOption> options;

    public ChoiceRoom(String name, String description) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        Objects.requireNonNull(description, "description must not be null");
        this.name = name;
        this.description = description;
        this.options = new ArrayList<>();
    }

    /**
     * Aggiunge un'opzione di scelta. Da chiamare in fase di
     * costruzione del dungeon, prima che qualche eroe entri nella stanza.
     *
     * @param option opzione da aggiungere (non nulla)
     */
    public void addOption(ChoiceOption option) {
        Objects.requireNonNull(option, "option must not be null");
        options.add(option);
    }

    /**
     * @return vista non modificabile delle opzioni disponibili
     */
    public List<ChoiceOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public RoomEvent onEnter(Hero hero) {
        if (options.size() < 2) {
            throw new IllegalStateException(
                    "ChoiceRoom '" + name + "' needs at least 2 options to be entered");
        }
        String message = "%s raggiunge %s. %s"
                .formatted(hero.getName(), name, description);
        return RoomEvent.choice(message, options);
    }
}