package it.unicam.cs.mpgc.rpg122627.model.world;

import java.util.Objects;

/**
 * Una singola opzione di scelta all'interno di una {@link ChoiceRoom}.
 * <p>
 * Rappresenta un "ramo" del grafo del dungeon dal punto di vista
 * dell'utente: etichetta sintetica, descrizione, nodo di destinazione
 * e opzionalmente il nome di un'icona (es. una porta) con cui la GUI
 * può rappresentare visualmente l'opzione.
 * <p>
 * Oggetto immutabile.
 */
public final class ChoiceOption {

    private final String label;
    private final String description;
    private final DungeonNode destination;
    private final String iconName;

    /**
     * Costruttore completo con icona.
     *
     * @param iconName nome logico dell'icona (es. "door_danger"), o {@code null}
     *                 se l'opzione non ha un'icona associata. La GUI risolverà
     *                 il nome in un'immagine tramite il proprio registro sprite.
     */
    public ChoiceOption(String label, String description, DungeonNode destination, String iconName) {
        Objects.requireNonNull(label, "label must not be null");
        if (label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
        Objects.requireNonNull(description, "description must not be null");
        this.label = label;
        this.description = description;
        this.destination = Objects.requireNonNull(destination, "destination must not be null");
        this.iconName = iconName;
    }

    /**
     * Costruttore senza icona (retrocompatibilità con il codice esistente).
     */
    public ChoiceOption(String label, String description, DungeonNode destination) {
        this(label, description, destination, null);
    }

    public String getLabel()            { return label; }
    public String getDescription()      { return description; }
    public DungeonNode getDestination() { return destination; }

    /**
     * @return nome logico dell'icona, o {@code null} se l'opzione non ne ha una
     */
    public String getIconName() {
        return iconName;
    }

    @Override
    public String toString() {
        return label + " — " + description;
    }
}