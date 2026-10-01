package it.unicam.cs.mpgc.rpg122627.model.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Collezione di oggetti posseduti da un personaggio.
 * <p>
 * Gestisce aggiunta, rimozione e consultazione degli oggetti.
 * Ha un limite di capacità configurabile: aggiunte oltre il limite
 * vengono rifiutate (ritornano {@code false}) invece di lanciare
 * eccezioni, così la GUI può mostrare un messaggio all'utente
 * ("Inventario pieno") senza complicazioni.
 */
public class Inventory {

    private static final int DEFAULT_CAPACITY = 20;

    private final List<Item> items;
    private final int capacity;

    public Inventory() {
        this(DEFAULT_CAPACITY);
    }

    public Inventory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    /**
     * Aggiunge un oggetto all'inventario, se c'è spazio.
     *
     * @param item oggetto da aggiungere (non nullo)
     * @return true se è stato aggiunto, false se l'inventario è pieno
     */
    public boolean add(Item item) {
        Objects.requireNonNull(item, "item must not be null");
        if (items.size() >= capacity) {
            return false;
        }
        items.add(item);
        return true;
    }

    /**
     * Rimuove un oggetto dall'inventario.
     *
     * @param item oggetto da rimuovere
     * @return true se era presente ed è stato rimosso
     */
    public boolean remove(Item item) {
        return items.remove(item);
    }

    public int size() {
        return items.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return items.size() >= capacity;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * @return vista non modificabile della collezione di oggetti.
     *         Chi la riceve può leggere ma non modificare direttamente
     *         l'inventario — l'unico modo è passare per i metodi
     *         {@link #add(Item)} e {@link #remove(Item)}.
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
}