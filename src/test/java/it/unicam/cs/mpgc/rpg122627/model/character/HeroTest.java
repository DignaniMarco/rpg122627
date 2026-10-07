package it.unicam.cs.mpgc.rpg122627.model.character;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.HealingPotion;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test della classe {@link Hero}: progressione livello/XP, equipaggiamento,
 * auto-equip, ripristino da save. I test di HP/takeDamage/heal sono già
 * coperti da {@link AbstractCharacterTest} e non vengono ripetuti qui.
 */
class HeroTest {

    private Hero hero;

    @BeforeEach
    void setUp() {
        // Eroe standard: 35 HP, 5 ATK, 2 DEF, livello 1, 0 XP
        hero = new Hero("TestHero", 35, 5, 2);
    }

    // ========== Costruttore e stato iniziale ==========

    @Test
    @DisplayName("Nuovo eroe parte a livello 1 con 0 XP e inventario vuoto")
    void newHero_startsAtLevelOneWithZeroXpAndEmptyInventory() {
        assertEquals(1, hero.getLevel());
        assertEquals(0, hero.getExperience());
        assertEquals(0, hero.getInventory().size());
        assertNull(hero.getEquippedWeapon());
        assertNull(hero.getEquippedArmor());
    }

    @Test
    @DisplayName("Nuovo eroe ha ATK e DEF uguali al base (nessun equipaggiamento)")
    void newHero_hasBaseAttackAndDefenseWithoutEquipment() {
        assertEquals(5, hero.getAttackDamage());
        assertEquals(2, hero.getDefense());
    }

    // ========== Equipaggiamento manuale ==========

    @Test
    @DisplayName("equipWeapon aumenta ATK con il bonus dell'arma")
    void equipWeapon_increasesAttackByBonus() {
        Weapon sword = new Weapon("Spada di Test", 3);
        hero.getInventory().add(sword);
        hero.equipWeapon(sword);
        assertEquals(5 + 3, hero.getAttackDamage());
        assertSame(sword, hero.getEquippedWeapon());
    }

    @Test
    @DisplayName("equipArmor aumenta DEF con il bonus dell'armatura")
    void equipArmor_increasesDefenseByBonus() {
        Armor plate = new Armor("Armatura di Test", 2);
        hero.getInventory().add(plate);
        hero.equipArmor(plate);
        assertEquals(2 + 2, hero.getDefense());
        assertSame(plate, hero.getEquippedArmor());
    }

    @Test
    @DisplayName("equipWeapon rifiuta un'arma non presente nell'inventario")
    void equipWeapon_rejectsWeaponNotInInventory() {
        Weapon rogueWeapon = new Weapon("Arma Esterna", 10);
        assertThrows(IllegalArgumentException.class,
                () -> hero.equipWeapon(rogueWeapon));
    }

    @Test
    @DisplayName("Cambiare arma sostituisce il bonus (non si accumula)")
    void equipWeapon_replacesPreviousWeaponBonus() {
        Weapon dagger = new Weapon("Pugnale", 2);
        Weapon sword = new Weapon("Spada", 5);
        hero.getInventory().add(dagger);
        hero.getInventory().add(sword);

        hero.equipWeapon(dagger);
        assertEquals(5 + 2, hero.getAttackDamage());

        hero.equipWeapon(sword);
        assertEquals(5 + 5, hero.getAttackDamage()); // 5 + 5, non 5 + 2 + 5
    }

    // ========== Auto-equip ==========

    @Test
    @DisplayName("tryAutoEquip equipaggia arma migliore quando nessuna è equipaggiata")
    void tryAutoEquip_equipsWeaponWhenNoneEquipped() {
        Weapon sword = new Weapon("Spada", 3);
        hero.getInventory().add(sword);
        boolean equipped = hero.tryAutoEquip(sword);
        assertTrue(equipped);
        assertSame(sword, hero.getEquippedWeapon());
    }

    @Test
    @DisplayName("tryAutoEquip equipaggia arma solo se bonus strettamente maggiore")
    void tryAutoEquip_equipsOnlyIfStrictlyBetter() {
        Weapon dagger = new Weapon("Pugnale", 3);
        Weapon equalSword = new Weapon("Spada Uguale", 3);
        Weapon betterSword = new Weapon("Spada Migliore", 5);
        hero.getInventory().add(dagger);
        hero.getInventory().add(equalSword);
        hero.getInventory().add(betterSword);

        hero.equipWeapon(dagger);
        assertFalse(hero.tryAutoEquip(equalSword), "non deve equipaggiare un'arma pari");
        assertSame(dagger, hero.getEquippedWeapon());

        assertTrue(hero.tryAutoEquip(betterSword), "deve equipaggiare un'arma migliore");
        assertSame(betterSword, hero.getEquippedWeapon());
    }

    @Test
    @DisplayName("tryAutoEquip ignora oggetti non equipaggiabili (consumabili)")
    void tryAutoEquip_ignoresNonEquippableItems() {
        HealingPotion potion = new HealingPotion("Pozione", 10);
        hero.getInventory().add(potion);
        boolean equipped = hero.tryAutoEquip(potion);
        assertFalse(equipped);
    }

    @Test
    @DisplayName("tryAutoEquip lancia NullPointerException su argomento null")
    void tryAutoEquip_throwsOnNullItem() {
        assertThrows(NullPointerException.class, () -> hero.tryAutoEquip(null));
    }

    // ========== Progressione (XP e level up) ==========

    @Test
    @DisplayName("gainExperience accumula XP senza level up se sotto soglia")
    void gainExperience_accumulatesXpWithoutLevelUp() {
        hero.gainExperience(30);
        assertEquals(30, hero.getExperience());
        assertEquals(1, hero.getLevel());
    }

    @Test
    @DisplayName("gainExperience esattamente alla soglia porta a level up e XP a 0")
    void gainExperience_atThresholdTriggersLevelUp() {
        int threshold = hero.getXpPerLevel();
        hero.gainExperience(threshold);
        assertEquals(2, hero.getLevel());
        assertEquals(0, hero.getExperience());
    }

    @Test
    @DisplayName("Level up aumenta HP massimi di 5")
    void levelUp_increasesMaxHpByFive() {
        int maxHpBefore = hero.getMaxHp();
        hero.gainExperience(hero.getXpPerLevel());
        assertEquals(maxHpBefore + 5, hero.getMaxHp());
    }

    @Test
    @DisplayName("Level up aumenta ATK di 1")
    void levelUp_increasesAttackByOne() {
        int atkBefore = hero.getAttackDamage();
        hero.gainExperience(hero.getXpPerLevel());
        assertEquals(atkBefore + 1, hero.getAttackDamage());
    }

    @Test
    @DisplayName("Level up aumenta DEF di 1")
    void levelUp_increasesDefenseByOne() {
        int defBefore = hero.getDefense();
        hero.gainExperience(hero.getXpPerLevel());
        assertEquals(defBefore + 1, hero.getDefense());
    }

    @Test
    @DisplayName("gainExperience sopra la soglia conserva il resto come XP al nuovo livello")
    void gainExperience_carriesOverExtraXp() {
        int threshold = hero.getXpPerLevel();
        hero.gainExperience(threshold + 15);
        assertEquals(2, hero.getLevel());
        assertEquals(15, hero.getExperience());
    }

    @Test
    @DisplayName("gainExperience permette level up multipli in una sola chiamata")
    void gainExperience_allowsMultipleLevelUpsInOneCall() {
        int threshold = hero.getXpPerLevel();
        hero.gainExperience(threshold * 3);  // tre livelli esatti
        assertEquals(4, hero.getLevel());    // 1 → 4
        assertEquals(0, hero.getExperience());
    }

    @Test
    @DisplayName("gainExperience rifiuta importi negativi")
    void gainExperience_rejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> hero.gainExperience(-10));
    }

    @Test
    @DisplayName("gainExperience(0) non modifica stato")
    void gainExperience_withZeroHasNoEffect() {
        int xpBefore = hero.getExperience();
        int levelBefore = hero.getLevel();
        hero.gainExperience(0);
        assertEquals(xpBefore, hero.getExperience());
        assertEquals(levelBefore, hero.getLevel());
    }

    @Test
    @DisplayName("Level up non abbassa gli HP correnti")
    void levelUp_doesNotLowerCurrentHp() {
        // Scenario: l'eroe è ferito, poi sale di livello
        hero.takeDamage(10);  // 35 → 25
        int hpAfterDamage = hero.getCurrentHp();
        hero.gainExperience(hero.getXpPerLevel());
        assertTrue(hero.getCurrentHp() >= hpAfterDamage,
                "il level up non deve abbassare gli HP correnti");
    }
    // ========== Ripristino da save ==========

    @Test
    @DisplayName("restoreFromSave imposta livello e XP")
    void restoreFromSave_setsLevelAndExperience() {
        hero.restoreFromSave(3, 42);
        assertEquals(3, hero.getLevel());
        assertEquals(42, hero.getExperience());
    }
}