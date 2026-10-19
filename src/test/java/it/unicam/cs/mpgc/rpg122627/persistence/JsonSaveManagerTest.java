package it.unicam.cs.mpgc.rpg122627.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test della classe {@link JsonSaveManager}: scrittura, lettura,
 * esistenza, cancellazione e roundtrip completo di {@link GameState}.
 * <p>
 * I test usano {@link TempDir}: JUnit 5 crea una directory temporanea
 * per ciascun test e la cancella automaticamente al termine. Così:
 * <ul>
 *   <li>i test non toccano il save reale dell'utente in {@code ~};</li>
 *   <li>ogni test parte da uno stato pulito senza interferenze;</li>
 *   <li>non serve manual cleanup, l'isolamento è gestito dal framework.</li>
 * </ul>
 * <p>
 * Il fatto che JsonSaveManager accetti un {@link File} personalizzato nel
 * costruttore è un classico esempio di <em>test seam</em>: la dipendenza
 * dal filesystem è iniettabile, non hardcodata.
 */
class JsonSaveManagerTest {

    @TempDir
    Path tempDir;

    private File saveFile;
    private JsonSaveManager manager;

    @BeforeEach
    void setUp() {
        saveFile = tempDir.resolve("test-save.json").toFile();
        manager = new JsonSaveManager(saveFile);
    }

    // ========== hasSave ==========

    @Test
    @DisplayName("hasSave è false se il file non esiste")
    void hasSave_falseIfFileAbsent() {
        assertFalse(manager.hasSave());
    }

    @Test
    @DisplayName("hasSave è true dopo un salvataggio")
    void hasSave_trueAfterSave() throws IOException {
        manager.save(newBasicState());
        assertTrue(manager.hasSave());
    }

    // ========== save ==========

    @Test
    @DisplayName("save crea il file su disco")
    void save_createsFile() throws IOException {
        assertFalse(saveFile.exists());
        manager.save(newBasicState());
        assertTrue(saveFile.exists());
        assertTrue(saveFile.length() > 0);
    }

    @Test
    @DisplayName("save rifiuta GameState null")
    void save_rejectsNullState() {
        assertThrows(NullPointerException.class, () -> manager.save(null));
    }

    @Test
    @DisplayName("save successivi sovrascrivono il file precedente")
    void save_overwritesPreviousContent() throws IOException {
        GameState first = newBasicState();
        first.setHeroName("Primo");
        manager.save(first);

        GameState second = newBasicState();
        second.setHeroName("Secondo");
        manager.save(second);

        GameState loaded = manager.load();
        assertEquals("Secondo", loaded.getHeroName());
    }

    // ========== load ==========

    @Test
    @DisplayName("load lancia IOException se il file non esiste")
    void load_throwsIfNoSaveExists() {
        assertFalse(manager.hasSave());
        assertThrows(IOException.class, () -> manager.load());
    }

    @Test
    @DisplayName("load restituisce un GameState con i dati salvati")
    void load_returnsSavedState() throws IOException {
        GameState original = newBasicState();
        manager.save(original);

        GameState loaded = manager.load();
        assertEquals(original.getHeroName(), loaded.getHeroName());
        assertEquals(original.getHeroLevel(), loaded.getHeroLevel());
        assertEquals(original.getCurrentNodeId(), loaded.getCurrentNodeId());
    }

    // ========== delete ==========

    @Test
    @DisplayName("delete cancella il file se esiste")
    void delete_removesFileIfPresent() throws IOException {
        manager.save(newBasicState());
        assertTrue(saveFile.exists());

        manager.delete();
        assertFalse(saveFile.exists());
        assertFalse(manager.hasSave());
    }

    @Test
    @DisplayName("delete su file inesistente non lancia eccezione (no-op)")
    void delete_noOpIfFileAbsent() {
        assertFalse(saveFile.exists());
        assertDoesNotThrow(() -> manager.delete());
    }

    // ========== Roundtrip completo ==========

    @Test
    @DisplayName("Roundtrip save→load preserva tutti i campi dell'eroe")
    void roundtrip_preservesAllHeroFields() throws IOException {
        GameState original = new GameState();
        original.setHeroName("Marco il Testato");
        original.setHeroLevel(3);
        original.setHeroExperience(42);
        original.setHeroCurrentHp(25);
        original.setHeroMaxHp(45);
        original.setHeroBaseAttack(8);
        original.setHeroBaseDefense(4);
        original.setCurrentNodeId("E1b");

        manager.save(original);
        GameState loaded = manager.load();

        assertEquals("Marco il Testato", loaded.getHeroName());
        assertEquals(3, loaded.getHeroLevel());
        assertEquals(42, loaded.getHeroExperience());
        assertEquals(25, loaded.getHeroCurrentHp());
        assertEquals(45, loaded.getHeroMaxHp());
        assertEquals(8, loaded.getHeroBaseAttack());
        assertEquals(4, loaded.getHeroBaseDefense());
        assertEquals("E1b", loaded.getCurrentNodeId());
    }

    @Test
    @DisplayName("Roundtrip save→load preserva inventario e equipaggiamento")
    void roundtrip_preservesInventoryAndEquipment() throws IOException {
        GameState original = newBasicState();
        original.setInventoryItemNames(List.of(
                "Pozione Minore", "Pozione Rossa", "Spada d'Argento"));
        original.setEquippedWeaponName("Spada d'Argento");
        original.setEquippedArmorName("Cotta di Maglia");

        manager.save(original);
        GameState loaded = manager.load();

        assertEquals(3, loaded.getInventoryItemNames().size());
        assertTrue(loaded.getInventoryItemNames().contains("Spada d'Argento"));
        assertEquals("Spada d'Argento", loaded.getEquippedWeaponName());
        assertEquals("Cotta di Maglia", loaded.getEquippedArmorName());
    }

    @Test
    @DisplayName("Roundtrip gestisce correttamente inventario vuoto e equip null")
    void roundtrip_handlesEmptyInventoryAndNullEquipment() throws IOException {
        GameState original = newBasicState();
        // inventario vuoto di default, equip null di default
        manager.save(original);
        GameState loaded = manager.load();

        assertTrue(loaded.getInventoryItemNames().isEmpty());
        assertNull(loaded.getEquippedWeaponName());
        assertNull(loaded.getEquippedArmorName());
    }

    // ========== Costruttore ==========

    @Test
    @DisplayName("Costruttore rifiuta File null")
    void construction_rejectsNullFile() {
        assertThrows(NullPointerException.class, () -> new JsonSaveManager(null));
    }

    @Test
    @DisplayName("Costruttore senza argomenti usa un path default (non nullo)")
    void defaultConstructor_hasNonNullSaveFile() {
        JsonSaveManager defaultMgr = new JsonSaveManager();
        assertNotNull(defaultMgr.getSaveFile());
        // Non testiamo il percorso esatto per non dipendere dall'utente OS
    }

    // ========== Helper ==========

    /**
     * Crea un {@link GameState} minimale ma valido per i test che
     * non si interessano del contenuto specifico, solo del fatto
     * che il save funzioni.
     */
    private GameState newBasicState() {
        GameState s = new GameState();
        s.setHeroName("TestHero");
        s.setHeroLevel(1);
        s.setHeroExperience(0);
        s.setHeroCurrentHp(35);
        s.setHeroMaxHp(35);
        s.setHeroBaseAttack(5);
        s.setHeroBaseDefense(2);
        s.setCurrentNodeId("A");
        return s;
    }
}