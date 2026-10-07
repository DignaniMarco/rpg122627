package it.unicam.cs.mpgc.rpg122627.model.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test della classe {@link Dungeon}: costruzione, query, navigazione.
 * <p>
 * I grafi di test sono costruiti in {@link #setUp()} con una topologia
 * nota: un corridoio A → B → C e un bivio D → E / D → F, per coprire
 * sia il caso lineare (advance) sia quello con scelta (advanceTo).
 */
class DungeonTest {

    private DungeonNode a, b, c;
    private Dungeon lineareDungeon;

    private DungeonNode d, e, f;
    private Dungeon dungeonConBivio;

    @BeforeEach
    void setUp() {
        // Grafo lineare: A → B → C
        a = new DungeonNode("A", new EmptyRoom("Stanza A", "Prima stanza"));
        b = new DungeonNode("B", new EmptyRoom("Stanza B", "Seconda stanza"));
        c = new DungeonNode("C", new EmptyRoom("Stanza C", "Terza stanza"));
        a.addSuccessor(b);
        b.addSuccessor(c);
        lineareDungeon = new Dungeon(List.of(a, b, c), a);

        // Grafo con bivio: D → E / D → F
        d = new DungeonNode("D", new EmptyRoom("Stanza D", "Trivio"));
        e = new DungeonNode("E", new EmptyRoom("Stanza E", "Ramo sinistro"));
        f = new DungeonNode("F", new EmptyRoom("Stanza F", "Ramo destro"));
        d.addSuccessor(e);
        d.addSuccessor(f);
        dungeonConBivio = new Dungeon(List.of(d, e, f), d);
    }

    // ========== Costruzione ==========

    @Test
    @DisplayName("Dungeon appena costruito ha currentNode == startNode")
    void newDungeon_hasCurrentNodeEqualToStart() {
        assertSame(a, lineareDungeon.getCurrentNode());
        assertSame(a, lineareDungeon.getStartNode());
    }

    @Test
    @DisplayName("getSize ritorna il numero di nodi")
    void getSize_returnsNodeCount() {
        assertEquals(3, lineareDungeon.getSize());
        assertEquals(3, dungeonConBivio.getSize());
    }

    @Test
    @DisplayName("Costruzione con nodi vuoti è rifiutata")
    void construction_rejectsEmptyNodes() {
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(List.of(), a));
    }

    @Test
    @DisplayName("Costruzione con startNode null è rifiutata")
    void construction_rejectsNullStartNode() {
        assertThrows(NullPointerException.class,
                () -> new Dungeon(List.of(a, b, c), null));
    }

    @Test
    @DisplayName("Costruzione con startNode non presente tra i nodi è rifiutata")
    void construction_rejectsStartNodeNotInNodes() {
        DungeonNode outsider = new DungeonNode("X", new EmptyRoom("X", "X"));
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(List.of(a, b), outsider));
    }

    @Test
    @DisplayName("Costruzione con id duplicati è rifiutata")
    void construction_rejectsDuplicateIds() {
        DungeonNode duplicate = new DungeonNode("A", new EmptyRoom("A bis", "duplicato"));
        assertThrows(IllegalArgumentException.class,
                () -> new Dungeon(List.of(a, duplicate), a));
    }

    // ========== Query ==========

    @Test
    @DisplayName("getCurrentRoom ritorna la Room del nodo corrente")
    void getCurrentRoom_returnsRoomOfCurrentNode() {
        assertSame(a.getRoom(), lineareDungeon.getCurrentRoom());
    }

    @Test
    @DisplayName("isAtTerminalNode è true solo sul nodo senza successori")
    void isAtTerminalNode_trueOnlyAtTerminal() {
        assertFalse(lineareDungeon.isAtTerminalNode()); // su A
        lineareDungeon.advance();
        assertFalse(lineareDungeon.isAtTerminalNode()); // su B
        lineareDungeon.advance();
        assertTrue(lineareDungeon.isAtTerminalNode());  // su C
    }

    @Test
    @DisplayName("getAvailableNextNodes ritorna i successori del nodo corrente")
    void getAvailableNextNodes_returnsSuccessors() {
        assertEquals(List.of(b), lineareDungeon.getAvailableNextNodes());
        assertEquals(List.of(e, f), dungeonConBivio.getAvailableNextNodes());
    }

    @Test
    @DisplayName("findNodeById trova nodo per id")
    void findNodeById_returnsNodeIfPresent() {
        Optional<DungeonNode> result = lineareDungeon.findNodeById("B");
        assertTrue(result.isPresent());
        assertSame(b, result.get());
    }

    @Test
    @DisplayName("findNodeById ritorna Optional.empty se id non esiste")
    void findNodeById_returnsEmptyIfAbsent() {
        assertTrue(lineareDungeon.findNodeById("INESISTENTE").isEmpty());
    }

    // ========== advance ==========

    @Test
    @DisplayName("advance sposta al successore quando ne esiste uno solo")
    void advance_movesToSingleSuccessor() {
        DungeonNode result = lineareDungeon.advance();
        assertSame(b, result);
        assertSame(b, lineareDungeon.getCurrentNode());
    }

    @Test
    @DisplayName("advance lancia eccezione se nodo è terminale")
    void advance_throwsAtTerminalNode() {
        lineareDungeon.advance(); // A → B
        lineareDungeon.advance(); // B → C (terminale)
        assertThrows(IllegalStateException.class, () -> lineareDungeon.advance());
    }

    @Test
    @DisplayName("advance lancia eccezione se nodo ha più successori")
    void advance_throwsAtBranchingNode() {
        assertThrows(IllegalStateException.class, () -> dungeonConBivio.advance());
    }

    // ========== advanceTo ==========

    @Test
    @DisplayName("advanceTo sposta al nodo scelto se è un successore")
    void advanceTo_movesToChosenSuccessor() {
        DungeonNode result = dungeonConBivio.advanceTo(f);
        assertSame(f, result);
        assertSame(f, dungeonConBivio.getCurrentNode());
    }

    @Test
    @DisplayName("advanceTo lancia eccezione se target non è un successore")
    void advanceTo_throwsIfTargetNotSuccessor() {
        // c non è successore di a
        assertThrows(IllegalArgumentException.class,
                () -> lineareDungeon.advanceTo(c));
    }

    @Test
    @DisplayName("advanceTo lancia eccezione su target null")
    void advanceTo_throwsOnNullTarget() {
        assertThrows(NullPointerException.class,
                () -> lineareDungeon.advanceTo(null));
    }

    // ========== jumpTo ==========

    @Test
    @DisplayName("jumpTo sposta a qualsiasi nodo per id")
    void jumpTo_movesToAnyNodeById() {
        lineareDungeon.jumpTo("C");
        assertSame(c, lineareDungeon.getCurrentNode());
    }

    @Test
    @DisplayName("jumpTo lancia eccezione se id inesistente")
    void jumpTo_throwsOnUnknownId() {
        assertThrows(IllegalArgumentException.class,
                () -> lineareDungeon.jumpTo("MISTERO"));
    }

    // ========== Scenari compositi ==========

    @Test
    @DisplayName("Navigazione completa del grafo lineare mantiene stato corretto")
    void fullTraversal_lineareDungeon() {
        assertSame(a, lineareDungeon.getCurrentNode());
        lineareDungeon.advance();
        assertSame(b, lineareDungeon.getCurrentNode());
        lineareDungeon.advance();
        assertSame(c, lineareDungeon.getCurrentNode());
        assertTrue(lineareDungeon.isAtTerminalNode());
    }

    @Test
    @DisplayName("Dopo advanceTo, i successori sono quelli del nuovo nodo")
    void afterAdvanceTo_successorsUpdate() {
        dungeonConBivio.advanceTo(e);
        assertTrue(dungeonConBivio.getAvailableNextNodes().isEmpty()); // E è terminale
    }
}