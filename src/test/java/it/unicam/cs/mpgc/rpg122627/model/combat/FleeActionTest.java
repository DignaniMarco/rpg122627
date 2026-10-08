package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test della classe {@link FleeAction}: successo/fallimento in base al
 * generatore di numeri casuali iniettato.
 * <p>
 * I test usano {@link Random} con seed fissi: la prima chiamata a
 * {@code nextDouble()} di un {@code Random(seed)} produce sempre lo stesso
 * valore, consentendo di scegliere seed il cui primo valore cade sopra o
 * sotto la soglia di fuga (0.5), rendendo il test deterministico.
 * <p>
 * Esempio: {@code new Random(4096).nextDouble()} ≈ 0.0979 → fuga RIESCE;
 * {@code new Random(1).nextDouble()} ≈ 0.73 → fuga FALLISCE.
 */
class FleeActionTest {

    private Hero hero;
    private Enemy enemy;

    @BeforeEach
    void setUp() {
        hero = new Hero("Fuggitivo", 30, 5, 2);
        enemy = new Enemy("Inseguitore", 20, 3, 1, 10, new AggressiveBehavior());
    }

    @Test
    @DisplayName("Fuga riesce con seed che produce valore < 0.5")
    void execute_succeedsWhenRandomBelowThreshold() {
        FleeAction flee = new FleeAction(new Random(4096));  // nextDouble ≈ 0.47 → riesce;  // nextDouble ≈ 0.32
        ActionResult result = flee.execute(hero, enemy);
        assertTrue(result.endsCombat());
        assertTrue(result.getMessage().contains("riesce"));
    }

    @Test
    @DisplayName("Fuga fallisce con seed che produce valore >= 0.5")
    void execute_failsWhenRandomAboveThreshold() {
        FleeAction flee = new FleeAction(new Random(1));  // nextDouble ≈ 0.73
        ActionResult result = flee.execute(hero, enemy);
        assertFalse(result.endsCombat());
        assertTrue(result.getMessage().contains("fallisce"));
    }

    @Test
    @DisplayName("Fuga non infligge danno (sia che riesca sia che fallisca)")
    void execute_dealsNoDamage() {
        FleeAction fleeSuccess = new FleeAction(new Random(4096));
        ActionResult result = fleeSuccess.execute(hero, enemy);
        assertEquals(0, result.getDamageDealt());

        FleeAction fleeFail = new FleeAction(new Random(1));
        ActionResult result2 = fleeFail.execute(hero, enemy);
        assertEquals(0, result2.getDamageDealt());
    }

    @Test
    @DisplayName("Fuga non modifica HP di nessuno dei due partecipanti")
    void execute_doesNotChangeAnyoneHp() {
        int heroHp = hero.getCurrentHp();
        int enemyHp = enemy.getCurrentHp();
        FleeAction flee = new FleeAction(new Random(1));
        flee.execute(hero, enemy);
        assertEquals(heroHp, hero.getCurrentHp());
        assertEquals(enemyHp, enemy.getCurrentHp());
    }

    @Test
    @DisplayName("getName ritorna 'Fuggi'")
    void getName_returnsFuggi() {
        FleeAction flee = new FleeAction();
        assertEquals("Fuggi", flee.getName());
    }

    @Test
    @DisplayName("Costruttore senza argomenti usa un Random proprio")
    void defaultConstructor_usesOwnRandom() {
        // Non possiamo prevedere il risultato, ma possiamo verificare
        // che non lanci eccezioni e produca un risultato valido
        FleeAction flee = new FleeAction();
        ActionResult result = flee.execute(hero, enemy);
        assertNotNull(result);
        assertNotNull(result.getMessage());
    }
}
