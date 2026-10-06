package it.unicam.cs.mpgc.rpg122627.model.world;

import java.util.*;

/**
 * Grafo diretto delle stanze che compone un livello di gioco.
 * <p>
 * Il dungeon è modellato come un grafo diretto aciclico (DAG) di
 * {@link DungeonNode}: ogni nodo contiene una {@link Room} e la
 * lista dei nodi raggiungibili da esso. Un {@code startNode}
 * identifica il punto d'ingresso.
 * <p>
 * Rispetto alla precedente modellazione lineare (lista di stanze
 * con indice corrente), questa rappresentazione permette:
 * <ul>
 *   <li>bivi e ricongiunzioni (es. due percorsi che portano
 *       entrambi al boss);</li>
 *   <li>futuri ampliamenti come percorsi segreti, loop
 *       controllati, dungeon generati dinamicamente;</li>
 *   <li>una serializzazione pulita: lo stato della partita è
 *       solo l'id del nodo corrente, non una posizione in una lista.</li>
 * </ul>
 * <p>
 * Le modifiche riguardano solo questa classe e {@link ChoiceRoom}:
 * tutte le altre {@link Room} e il resto del modello restano invariate,
 * dimostrando come il design precedente già supportasse l'estensione
 * (Open/Closed Principle).
 */
public class Dungeon {

    private final Map<String, DungeonNode> nodesById;
    private final DungeonNode startNode;
    private DungeonNode currentNode;

    /**
     * Costruisce un dungeon dato l'elenco di tutti i nodi e il nodo iniziale.
     * I collegamenti tra i nodi (archi del grafo) sono mantenuti dentro
     * i nodi stessi tramite {@link DungeonNode#getSuccessors()}: il
     * costruttore si limita a indicizzarli per id e a validare l'ingresso.
     *
     * @param nodes     tutti i nodi del dungeon (non vuota, nessun id duplicato)
     * @param startNode nodo di partenza (deve essere presente in {@code nodes})
     * @throws IllegalArgumentException se la validazione fallisce
     */
    public Dungeon(Collection<DungeonNode> nodes, DungeonNode startNode) {
        Objects.requireNonNull(nodes, "nodes must not be null");
        Objects.requireNonNull(startNode, "startNode must not be null");
        if (nodes.isEmpty()) {
            throw new IllegalArgumentException("dungeon must have at least one node");
        }
        this.nodesById = new LinkedHashMap<>();
        for (DungeonNode node : nodes) {
            if (this.nodesById.containsKey(node.getId())) {
                throw new IllegalArgumentException("duplicate node id: " + node.getId());
            }
            this.nodesById.put(node.getId(), node);
        }
        if (!this.nodesById.containsValue(startNode)) {
            throw new IllegalArgumentException("startNode must be in the nodes collection");
        }
        this.startNode = startNode;
        this.currentNode = startNode;
    }

    // ========== Query ==========

    public DungeonNode getCurrentNode() {
        return currentNode;
    }

    public Room getCurrentRoom() {
        return currentNode.getRoom();
    }

    public DungeonNode getStartNode() {
        return startNode;
    }

    public int getSize() {
        return nodesById.size();
    }

    /**
     * @return i nodi raggiungibili dal nodo corrente
     */
    public List<DungeonNode> getAvailableNextNodes() {
        return currentNode.getSuccessors();
    }

    /**
     * @return true se il nodo corrente non ha successori (fine del dungeon)
     */
    public boolean isAtTerminalNode() {
        return currentNode.isTerminal();
    }

    /**
     * Cerca un nodo per id. Usato dalla persistenza per ripristinare
     * la posizione nel dungeon da uno stato salvato.
     *
     * @param id id del nodo da cercare
     * @return il nodo se esiste
     */
    public Optional<DungeonNode> findNodeById(String id) {
        return Optional.ofNullable(nodesById.get(id));
    }

    // ========== Mutazioni ==========

    /**
     * Avanza al nodo successivo quando è univocamente determinato
     * (il nodo corrente ha un solo successore).
     *
     * @return il nuovo nodo corrente
     * @throws IllegalStateException se non c'è un successore univoco:
     *         nessun successore (nodo terminale) o più di uno
     *         (richiede {@link #advanceTo(DungeonNode)})
     */
    public DungeonNode advance() {
        List<DungeonNode> next = currentNode.getSuccessors();
        if (next.isEmpty()) {
            throw new IllegalStateException(
                    "cannot advance: node '" + currentNode.getId() + "' is terminal");
        }
        if (next.size() > 1) {
            throw new IllegalStateException(
                    "cannot advance: node '" + currentNode.getId() + "' has multiple successors, " +
                            "use advanceTo(DungeonNode) with an explicit choice");
        }
        currentNode = next.get(0);
        return currentNode;
    }

    /**
     * Avanza al nodo scelto esplicitamente. Il nodo deve essere uno
     * dei successori del nodo corrente.
     *
     * @param target nodo di destinazione (deve essere un successore del corrente)
     * @return il nuovo nodo corrente
     * @throws IllegalArgumentException se {@code target} non è un successore
     *                                  del nodo corrente
     */
    public DungeonNode advanceTo(DungeonNode target) {
        Objects.requireNonNull(target, "target must not be null");
        if (!currentNode.getSuccessors().contains(target)) {
            throw new IllegalArgumentException(
                    "node '" + target.getId() + "' is not a successor of '" + currentNode.getId() + "'");
        }
        currentNode = target;
        return currentNode;
    }

    /**
     * Riporta il dungeon a uno specifico nodo per id. Usato dalla
     * persistenza al caricamento di una partita salvata.
     *
     * @param nodeId id del nodo da impostare come corrente
     * @throws IllegalArgumentException se non esiste un nodo con quell'id
     */
    public void jumpTo(String nodeId) {
        DungeonNode target = nodesById.get(nodeId);
        if (target == null) {
            throw new IllegalArgumentException("unknown node id: " + nodeId);
        }
        currentNode = target;
    }
}