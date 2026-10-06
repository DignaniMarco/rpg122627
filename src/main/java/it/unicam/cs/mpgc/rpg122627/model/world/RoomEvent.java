package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;

import java.util.Objects;
import java.util.Optional;
import java.util.List;

/**
 * Descrive cosa succede quando l'eroe entra in una {@link Room}.
 * <p>
 * Oggetto immutabile che trasporta sia il tipo di evento sia i dati
 * ad esso associati (il nemico da affrontare, l'oggetto trovato, ecc.).
 * Permette alla stanza di comunicare l'esito dell'ingresso senza eseguire
 * direttamente combattimenti o interagire con la GUI: chi riceve l'evento
 * decide come reagire.
 */
public final class RoomEvent {

    public enum Type {
        /** Stanza vuota, nessuna conseguenza. */
        NOTHING,
        /** Combattimento innescato: {@link #getEnemy()} contiene il nemico. */
        COMBAT_STARTED,
        /** Oggetto trovato: {@link #getItem()} contiene l'oggetto raccolto. */
        ITEM_FOUND,
        /** Stanza finale del boss raggiunta. */
        BOSS_ENCOUNTER,
        /** L'eroe deve scegliere tra più opzioni: {@link #getChoices()}. */
        CHOICE_AWAITING
    }

    private final Type type;
    private final String message;
    private final Enemy enemy;   // null se non pertinente
    private final Item item;     // null se non pertinente
    private final List<ChoiceOption> choices;

    private RoomEvent(Type type, String message, Enemy enemy, Item item, List<ChoiceOption> choices) {
        this.type = Objects.requireNonNull(type);
        this.message = Objects.requireNonNull(message);
        this.enemy = enemy;
        this.item = item;
        this.choices = (choices == null) ? List.of() : List.copyOf(choices);
    }

    public static RoomEvent nothing(String message) {
        return new RoomEvent(Type.NOTHING, message, null, null, null);
    }

    public static RoomEvent combat(String message, Enemy enemy) {
        Objects.requireNonNull(enemy, "enemy must not be null for COMBAT_STARTED");
        return new RoomEvent(Type.COMBAT_STARTED, message, enemy, null, null);
    }

    public static RoomEvent boss(String message, Enemy boss) {
        Objects.requireNonNull(boss, "boss must not be null for BOSS_ENCOUNTER");
        return new RoomEvent(Type.BOSS_ENCOUNTER, message, boss, null, null);
    }

    public static RoomEvent item(String message, Item item) {
        Objects.requireNonNull(item, "item must not be null for ITEM_FOUND");
        return new RoomEvent(Type.ITEM_FOUND, message, null, item, null);
    }
    /**
     * Costruisce un evento di scelta: la stanza propone opzioni e attende
     * che l'utente ne selezioni una.
     *
     * @param message testo introduttivo mostrato prima delle opzioni
     * @param choices le opzioni disponibili (almeno due, altrimenti non è una scelta)
     */
    public static RoomEvent choice(String message, List<ChoiceOption> choices) {
        Objects.requireNonNull(choices, "choices must not be null");
        if (choices.size() < 2) {
            throw new IllegalArgumentException("a choice event must have at least 2 options");
        }
        return new RoomEvent(Type.CHOICE_AWAITING, message, null, null, choices);
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Optional<Enemy> getEnemy() {
        return Optional.ofNullable(enemy);
    }

    public Optional<Item> getItem() {
        return Optional.ofNullable(item);
    }
    /**
     * @return le opzioni di scelta (vuota se non è un evento CHOICE_AWAITING)
     */
    public List<ChoiceOption> getChoices() {
        return choices;
    }
}