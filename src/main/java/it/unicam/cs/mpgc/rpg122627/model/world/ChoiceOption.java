package it.unicam.cs.mpgc.rpg122627.model.world;

import java.util.Objects;

/**
 * Una singola opzione di scelta all'interno di una {@link ChoiceRoom}.
 * <p>
 * Rappresenta un "ramo" del grafo del dungeon dal punto di vista
 * dell'utente: un'etichetta sintetica (es. "Vai a sinistra"), una
 * descrizione informativa di cosa aspetta su quella strada (coerente
 * con la scelta informata di design), e il {@link DungeonNode} di
 * destinazione a cui si viene condotti se si sceglie questa opzione.
 * <p>
 * Oggetto immutabile: una volta costruito, non può cambiare.
 */
public final class ChoiceOption {

    private final String label;
    private final String description;
    private final DungeonNode destination;

    public ChoiceOption(String label, String description, DungeonNode destination) {
        Objects.requireNonNull(label, "label must not be null");
        if (label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
        Objects.requireNonNull(description, "description must not be null");
        this.label = label;
        this.description = description;
        this.destination = Objects.requireNonNull(destination, "destination must not be null");
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public DungeonNode getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return label + " — " + description;
    }
}