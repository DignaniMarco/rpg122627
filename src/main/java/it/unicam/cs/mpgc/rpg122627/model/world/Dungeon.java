package it.unicam.cs.mpgc.rpg122627.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Sequenza ordinata di {@link Room} che compongono il livello di gioco.
 * <p>
 * L'eroe parte dalla stanza 0 e avanza linearmente verso l'ultima stanza
 * (tipicamente una {@link BossRoom}). Il Dungeon tiene traccia della
 * posizione corrente ed espone metodi per avanzare, interrogare lo stato
 * e sapere se il dungeon è stato completato.
 * <p>
 * La struttura lineare è una scelta per l'MVP: aggiungere ramificazioni
 * (grafo di stanze) richiederà di sostituire la {@code List} con una
 * struttura a grafo, ma non cambierà il contratto pubblico di questa
 * classe verso il resto del sistema.
 */
public class Dungeon {

    private final List<Room> rooms;
    private int currentIndex;

    public Dungeon(List<Room> rooms) {
        Objects.requireNonNull(rooms, "rooms must not be null");
        if (rooms.isEmpty()) {
            throw new IllegalArgumentException("dungeon must have at least one room");
        }
        this.rooms = new ArrayList<>(rooms); // copia difensiva
        this.currentIndex = 0;
    }

    public Room getCurrentRoom() {
        return rooms.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getSize() {
        return rooms.size();
    }

    /**
     * @return true se la stanza corrente è l'ultima
     */
    public boolean isAtLastRoom() {
        return currentIndex == rooms.size() - 1;
    }

    /**
     * Avanza alla stanza successiva.
     *
     * @return la nuova stanza corrente
     * @throws IllegalStateException se non ci sono stanze successive
     */
    public Room advance() {
        if (isAtLastRoom()) {
            throw new IllegalStateException("already at last room");
        }
        currentIndex++;
        return getCurrentRoom();
    }

    /**
     * @return vista non modificabile dell'elenco delle stanze
     */
    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }
}