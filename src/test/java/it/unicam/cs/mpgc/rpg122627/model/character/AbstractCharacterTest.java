package it.unicam.cs.mpgc.rpg122627.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del contratto di {@link AbstractCharacter}, verificato attraverso
 * la sua sottoclasse concreta più semplice: {@link Hero}.
 * <p>
 * Si testano gli invarianti condivisi: HP correnti nell'intervallo
 * [0, maxHp], validazione dei parametri, stato di morte e cura
 * oltre il massimo.
 */
class AbstractCharacterTest {

    private Hero hero;

    @BeforeEach
    void setUp() {
        hero = new Hero("Test", 20, 5, 2);
    }

    @Test
    @DisplayName("newly created character has full HP")
    void newCharacter_hasFullHp() {
        assertEquals(20, hero.getCurrentHp());
        assertEquals(20, hero.getMaxHp());
        assertFalse(hero.isDead());
    }

    @Test
    @DisplayName("takeDamage reduces current HP")
    void takeDamage_reducesCurrentHp() {
        hero.takeDamage(7);
        assertEquals(13, hero.getCurrentHp());
    }

    @Test
    @DisplayName("takeDamage does not drop HP below zero")
    void takeDamage_floorsAtZero() {
        hero.takeDamage(100);
        assertEquals(0, hero.getCurrentHp());
        assertTrue(hero.isDead());
    }

    @Test
    @DisplayName("heal increases current HP")
    void heal_restoresHp() {
        hero.takeDamage(10);
        hero.heal(5);
        assertEquals(15, hero.getCurrentHp());
    }

    @Test
    @DisplayName("heal does not exceed max HP")
    void heal_cappedAtMaxHp() {
        hero.takeDamage(5);
        hero.heal(100);
        assertEquals(20, hero.getCurrentHp());
    }

    @Test
    @DisplayName("takeDamage rejects negative amount")
    void takeDamage_rejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> hero.takeDamage(-1));
    }

    @Test
    @DisplayName("heal rejects negative amount")
    void heal_rejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> hero.heal(-1));
    }

    @Test
    @DisplayName("constructor rejects blank name")
    void constructor_rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Hero("", 10, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new Hero("   ", 10, 1, 1));
    }

    @Test
    @DisplayName("constructor rejects null name")
    void constructor_rejectsNullName() {
        assertThrows(NullPointerException.class, () -> new Hero(null, 10, 1, 1));
    }

    @Test
    @DisplayName("constructor rejects non-positive maxHp")
    void constructor_rejectsBadMaxHp() {
        assertThrows(IllegalArgumentException.class, () -> new Hero("A", 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new Hero("A", -5, 1, 1));
    }
}