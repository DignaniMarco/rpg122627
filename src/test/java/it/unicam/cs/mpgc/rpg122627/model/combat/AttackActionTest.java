package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test della classe {@link AttackAction}: calcolo del danno, danno minimo
 * garantito, applicazione al bersaglio.
 */
class AttackActionTest {

    private Hero hero;
    private Enemy enemy;
    private AttackAction attack;

    @BeforeEach
    void setUp() {
        hero = new Hero("Attaccante", 30, 5, 2);
        enemy = new Enemy("Bersaglio", 20, 3, 1, 10, new AggressiveBehavior());
        attack = new AttackAction();
    }

    @Test
    @DisplayName("Attacco infligge danno pari a ATK - DEF del bersaglio")
    void execute_dealsAttackMinusDefense() {
        // ATK 5 - DEF 1 = 4
        ActionResult result = attack.execute(hero, enemy);
        assertEquals(4, result.getDamageDealt());
        assertEquals(20 - 4, enemy.getCurrentHp());
    }

    @Test
    @DisplayName("Danno minimo è 1 anche se DEF supera ATK")
    void execute_dealsMinimumOneDamageWhenDefenseExceedsAttack() {
        // Nemico con DEF altissima
        Enemy tankEnemy = new Enemy("Tank", 20, 3, 100, 10, new AggressiveBehavior());
        ActionResult result = attack.execute(hero, tankEnemy);
        assertEquals(1, result.getDamageDealt());
        assertEquals(19, tankEnemy.getCurrentHp());
    }

    @Test
    @DisplayName("Attacco riduce HP del bersaglio")
    void execute_reducesTargetHp() {
        int hpBefore = enemy.getCurrentHp();
        attack.execute(hero, enemy);
        assertTrue(enemy.getCurrentHp() < hpBefore);
    }

    @Test
    @DisplayName("Attacco con bersaglio null lancia eccezione")
    void execute_throwsOnNullTarget() {
        assertThrows(IllegalArgumentException.class,
                () -> attack.execute(hero, null));
    }

    @Test
    @DisplayName("getName ritorna 'Attacca'")
    void getName_returnsAttacca() {
        assertEquals("Attacca", attack.getName());
    }

    @Test
    @DisplayName("Danno non termina il combat (non è un ending action)")
    void execute_doesNotEndCombat() {
        ActionResult result = attack.execute(hero, enemy);
        assertFalse(result.endsCombat());
    }
}
