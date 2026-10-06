package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;

import java.util.Objects;
import java.util.Random;

/**
 * Stanza con un NPC (Non-Player Character) che può offrire un oggetto
 * all'eroe con una certa probabilità.
 * <p>
 * Entrando, la stanza esegue una verifica probabilistica: se il tiro
 * va a segno, l'oggetto viene aggiunto all'inventario dell'eroe
 * (se c'è spazio); altrimenti, l'NPC non ha nulla da offrire in questa
 * occasione. Una volta visitata, l'NPC resta inattivo (il flag
 * {@code visited} evita drop ripetuti se l'eroe tornasse).
 * <p>
 * Il generatore di numeri casuali è iniettato tramite costruttore
 * (test seam): il costruttore pubblico senza argomenti crea un
 * {@link Random} standard per il gioco reale, mentre nei test è
 * possibile iniettare un {@code Random} con seed noto per rendere
 * il comportamento deterministico e verificabile.
 */
public class NpcRoom implements Room {

    private final String name;
    private final String description;
    private final String npcName;
    private final Item possibleDrop;
    private final double dropProbability;
    private final Random random;
    private boolean visited;

    /**
     * Costruttore principale.
     *
     * @param name            nome della stanza
     * @param description     descrizione narrativa
     * @param npcName         nome dell'NPC (es. "Mercante errante")
     * @param possibleDrop    oggetto che l'NPC potrebbe offrire (non nullo)
     * @param dropProbability probabilità di drop, nell'intervallo [0.0, 1.0]
     */
    public NpcRoom(String name, String description, String npcName,
                   Item possibleDrop, double dropProbability) {
        this(name, description, npcName, possibleDrop, dropProbability, new Random());
    }

    /**
     * Costruttore completo con generatore di numeri casuali iniettabile.
     * Riservato ai test o a scenari in cui si vuole controllare la
     * sequenza di risultati casuali.
     */
    public NpcRoom(String name, String description, String npcName,
                   Item possibleDrop, double dropProbability, Random random) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(npcName, "npcName must not be null");
        if (npcName.isBlank()) {
            throw new IllegalArgumentException("npcName must not be blank");
        }
        Objects.requireNonNull(possibleDrop, "possibleDrop must not be null");
        if (dropProbability < 0.0 || dropProbability > 1.0) {
            throw new IllegalArgumentException(
                    "dropProbability must be in [0.0, 1.0], got " + dropProbability);
        }
        this.name = name;
        this.description = description;
        this.npcName = npcName;
        this.possibleDrop = possibleDrop;
        this.dropProbability = dropProbability;
        this.random = Objects.requireNonNull(random, "random must not be null");
        this.visited = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getNpcName() {
        return npcName;
    }

    @Override
    public RoomEvent onEnter(Hero hero) {
        if (visited) {
            return RoomEvent.nothing(
                    "%s è già passato: l'NPC %s non è più qui.".formatted(hero.getName(), npcName));
        }
        visited = true;

        boolean dropsItem = random.nextDouble() < dropProbability;
        if (!dropsItem) {
            return RoomEvent.nothing(
                    "%s incontra %s, che però non ha nulla da offrire oggi."
                            .formatted(hero.getName(), npcName));
        }

        boolean added = hero.getInventory().add(possibleDrop);
        if (!added) {
            return RoomEvent.nothing(
                    "%s offre %s a %s, ma l'inventario è pieno."
                            .formatted(npcName, possibleDrop.getName(), hero.getName()));
        }

        boolean equipped = hero.tryAutoEquip(possibleDrop);
        String message = "%s incontra %s, che gli dona %s!"
                .formatted(hero.getName(), npcName, possibleDrop.getName());
        if (equipped) {
            message += " L'oggetto è stato equipaggiato automaticamente.";
        }
        return RoomEvent.item(message, possibleDrop);
    }
}