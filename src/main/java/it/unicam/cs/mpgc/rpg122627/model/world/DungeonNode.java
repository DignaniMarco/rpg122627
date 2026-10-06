package it.unicam.cs.mpgc.rpg122627.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Nodo di un {@link Dungeon} rappresentato come grafo diretto.
 * <p>
 * Un nodo tiene insieme tre informazioni:
 * <ul>
 *   <li>un identificatore stabile ({@code id}), usato per riferirsi
 *       al nodo in modo univoco (persistenza, scelte dell'utente,
 *       logging);</li>
 *   <li>la {@link Room} che il nodo contiene, da cui dipende la logica
 *       di gioco all'ingresso;</li>
 *   <li>la lista dei nodi successori, cioè dove si può andare dal
 *       nodo corrente.</li>
 * </ul>
 * Un nodo è terminale se la sua lista di successori è vuota: è il
 * caso del boss, oltre il quale il gioco finisce.
 * <p>
 * I successori vengono registrati dopo la costruzione tramite
 * {@link #addSuccessor(DungeonNode)}: questo permette di costruire il
 * grafo in modo incrementale e di risolvere cicli di riferimento
 * (es. due nodi che si ricongiungono su un terzo).
 */
public final class DungeonNode {

    private final String id;
    private final Room room;
    private final List<DungeonNode> successors;

    public DungeonNode(String id, Room room) {
        Objects.requireNonNull(id, "id must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        this.id = id;
        this.room = Objects.requireNonNull(room, "room must not be null");
        this.successors = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    /**
     * @return vista non modificabile dei nodi raggiungibili da questo.
     *         Chi la riceve può leggere ma non alterare la struttura
     *         del grafo (incapsulamento).
     */
    public List<DungeonNode> getSuccessors() {
        return Collections.unmodifiableList(successors);
    }

    /**
     * Aggiunge un nodo come successore. Da chiamare in fase di
     * costruzione del grafo.
     *
     * @param next nodo raggiungibile da questo (non nullo, diverso da sé stesso)
     * @throws IllegalArgumentException se {@code next} è nullo o è sé stesso
     */
    public void addSuccessor(DungeonNode next) {
        Objects.requireNonNull(next, "next must not be null");
        if (next == this) {
            throw new IllegalArgumentException("a node cannot be its own successor");
        }
        successors.add(next);
    }

    /**
     * @return true se non ci sono nodi raggiungibili da questo
     *         (tipicamente l'ultimo nodo del dungeon, oltre il boss)
     */
    public boolean isTerminal() {
        return successors.isEmpty();
    }

    @Override
    public String toString() {
        return "Node[" + id + " -> " + room.getName() + "]";
    }
}