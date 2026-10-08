package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.behavior.EnemyBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Character;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test della classe {@link Combat}: orchestrazione dei turni, condizioni di
 * fine combattimento, assegnazione delle ricompense.
 * <p>
 * I test usano azioni e comportamenti nemici "fake" che si comportano in modo
 * deterministico (danno fisso), per isolare l'orchestrazione di Combat dalla
 * logica delle action/behavior concrete. Le azioni concrete (AttackAction,
 * FleeAction) sono testate separatamente in {@link AttackActionTest} e
 * {@link FleeActionTest}.
 */
class CombatTest {

    private Hero hero;
    private Enemy weakEnemy;
    private Enemy strongEnemy;

    @BeforeEach
    void setUp() {
        hero = new Hero("TestHero", 30, 5, 2);
        // Nemico debole: 10 HP, muore in 2 colpi dell'eroe (5 ATK vs 1 DEF = 4 dmg)
        weakEnemy = new Enemy("Goblin Test", 10, 3, 1, 20, new AggressiveBehavior());
        // Nemico forte: 100 HP, non muore in questi test
        strongEnemy = new Enemy("Drago Test", 100, 8, 3, 50, new AggressiveBehavior());
    }

    // ========== Helper: azioni fake per test deterministici ==========

    /**
     * Azione che infligge danno fisso al bersaglio, senza casualità.
     * Rispetta la DEF del bersaglio (min 1 danno come AttackAction reale).
     */
    private Action fixedDamageAction(int amount) {
        return new Action() {
            @Override
            public ActionResult execute(Character actor, Character target) {
                int effective = Math.max(1, amount - target.getDefense());
                target.takeDamage(effective);
                return ActionResult.of("test damage " + effective, effective);
            }

            @Override
            public String getName() {
                return "FixedDamage";
            }
        };
    }

    /**
     * Azione che termina immediatamente il combat (simula una fuga riuscita).
     */
    private Action fleeSuccess() {
        return new Action() {
            @Override
            public ActionResult execute(Character actor, Character target) {
                return ActionResult.ending("fuga riuscita (test)");
            }

            @Override
            public String getName() {
                return "FleeSuccess";
            }
        };
    }

    /**
     * EnemyBehavior fake che restituisce sempre lo stesso danno.
     */
    private EnemyBehavior fixedBehavior(int damage, String description) {
        return new EnemyBehavior() {
            @Override
            public int decideDamage(Character self, Character target) {
                return damage;
            }

            @Override
            public String describeAction() {
                return description;
            }
        };
    }

    // ========== Costruzione e stato iniziale ==========

    @Test
    @DisplayName("Nuovo combat ha isOver() == false e nessuno ha vinto")
    void newCombat_isNotOverAndNoWinner() {
        Combat combat = new Combat(hero, weakEnemy);
        assertFalse(combat.isOver());
        assertFalse(combat.heroWon());
    }

    @Test
    @DisplayName("Costruzione rifiuta hero null")
    void construction_rejectsNullHero() {
        assertThrows(NullPointerException.class, () -> new Combat(null, weakEnemy));
    }

    @Test
    @DisplayName("Costruzione rifiuta enemy null")
    void construction_rejectsNullEnemy() {
        assertThrows(NullPointerException.class, () -> new Combat(hero, null));
    }

    @Test
    @DisplayName("getHero e getEnemy ritornano i partecipanti")
    void getters_returnParticipants() {
        Combat combat = new Combat(hero, weakEnemy);
        assertSame(hero, combat.getHero());
        assertSame(weakEnemy, combat.getEnemy());
    }

    // ========== heroTurn ==========

    @Test
    @DisplayName("heroTurn infligge danno al nemico")
    void heroTurn_damagesEnemy() {
        Combat combat = new Combat(hero, strongEnemy);
        int hpBefore = strongEnemy.getCurrentHp();
        combat.heroTurn(fixedDamageAction(10));
        assertTrue(strongEnemy.getCurrentHp() < hpBefore);
    }

    @Test
    @DisplayName("heroTurn uccide il nemico se il danno è sufficiente")
    void heroTurn_killsEnemyWithEnoughDamage() {
        Combat combat = new Combat(hero, weakEnemy); // 10 HP, 1 DEF
        combat.heroTurn(fixedDamageAction(100));
        assertTrue(weakEnemy.isDead());
        assertTrue(combat.isOver());
        assertTrue(combat.heroWon());
    }

    @Test
    @DisplayName("heroTurn lancia eccezione se combat già terminato")
    void heroTurn_throwsIfCombatAlreadyOver() {
        Combat combat = new Combat(hero, weakEnemy);
        combat.heroTurn(fixedDamageAction(100));  // uccide weakEnemy
        assertThrows(IllegalStateException.class,
                () -> combat.heroTurn(fixedDamageAction(5)));
    }

    @Test
    @DisplayName("heroTurn rifiuta action null")
    void heroTurn_rejectsNullAction() {
        Combat combat = new Combat(hero, weakEnemy);
        assertThrows(NullPointerException.class, () -> combat.heroTurn(null));
    }

    @Test
    @DisplayName("Fuga riuscita termina il combat anche se entrambi vivi")
    void heroTurn_fleeEndsCombatWithBothAlive() {
        Combat combat = new Combat(hero, strongEnemy);
        combat.heroTurn(fleeSuccess());
        assertTrue(combat.isOver());
        assertFalse(combat.heroWon()); // non ha vinto, è scappato
        assertFalse(hero.isDead());
        assertFalse(strongEnemy.isDead());
    }

    // ========== enemyTurn ==========

    @Test
    @DisplayName("enemyTurn infligge danno all'eroe secondo il behavior")
    void enemyTurn_damagesHeroByBehaviorAmount() {
        Enemy e = new Enemy("Fake", 20, 0, 0, 10, fixedBehavior(8, "colpisce"));
        Combat combat = new Combat(hero, e);
        int hpBefore = hero.getCurrentHp();
        combat.enemyTurn();
        // Behavior fa 8, DEF eroe 2 → effettivo 6
        assertEquals(hpBefore - 6, hero.getCurrentHp());
    }

    @Test
    @DisplayName("enemyTurn con danno sotto la DEF non toglie HP")
    void enemyTurn_zeroDamageIfDefenseIsHigher() {
        Enemy e = new Enemy("Fake", 20, 0, 0, 10, fixedBehavior(1, "colpisce debole"));
        Combat combat = new Combat(hero, e);
        int hpBefore = hero.getCurrentHp();
        combat.enemyTurn();
        // Behavior fa 1, DEF 2 → effettivo max(0, -1) = 0
        assertEquals(hpBefore, hero.getCurrentHp());
    }

    @Test
    @DisplayName("enemyTurn uccide l'eroe se danno sufficiente")
    void enemyTurn_killsHeroWithEnoughDamage() {
        Enemy e = new Enemy("Fake", 20, 0, 0, 10, fixedBehavior(1000, "annienta"));
        Combat combat = new Combat(hero, e);
        combat.enemyTurn();
        assertTrue(hero.isDead());
        assertTrue(combat.isOver());
        assertFalse(combat.heroWon());
    }

    @Test
    @DisplayName("enemyTurn lancia eccezione se combat già terminato")
    void enemyTurn_throwsIfCombatAlreadyOver() {
        Combat combat = new Combat(hero, weakEnemy);
        combat.heroTurn(fixedDamageAction(100));
        assertThrows(IllegalStateException.class, combat::enemyTurn);
    }

    // ========== awardRewards ==========

    @Test
    @DisplayName("awardRewards dà XP all'eroe se ha vinto")
    void awardRewards_givesXpIfHeroWon() {
        Combat combat = new Combat(hero, weakEnemy); // 20 XP reward
        int xpBefore = hero.getExperience();
        combat.heroTurn(fixedDamageAction(100));
        combat.awardRewards();
        assertEquals(xpBefore + 20, hero.getExperience());
    }

    @Test
    @DisplayName("awardRewards non fa nulla se combat non è terminato")
    void awardRewards_noEffectIfCombatNotOver() {
        Combat combat = new Combat(hero, strongEnemy);
        int xpBefore = hero.getExperience();
        combat.awardRewards();
        assertEquals(xpBefore, hero.getExperience());
    }

    @Test
    @DisplayName("awardRewards non dà XP se l'eroe è morto")
    void awardRewards_noXpIfHeroDead() {
        Enemy killer = new Enemy("Killer", 20, 0, 0, 10, fixedBehavior(1000, "annienta"));
        Combat combat = new Combat(hero, killer);
        combat.enemyTurn();  // eroe muore
        int xpBefore = hero.getExperience();
        combat.awardRewards();
        assertEquals(xpBefore, hero.getExperience());
    }

    // ========== Scenari compositi ==========

    @Test
    @DisplayName("Scambio di turni fino a vittoria dell'eroe")
    void fullCombat_heroWinsAfterExchange() {
        Combat combat = new Combat(hero, weakEnemy); // 10 HP, 3 ATK, 1 DEF
        int round = 0;
        while (!combat.isOver() && round < 10) {
            combat.heroTurn(fixedDamageAction(5)); // 5-1 = 4 danni al nemico
            if (!combat.isOver()) {
                combat.enemyTurn(); // AggressiveBehavior fa 3, DEF 2 → 1 danno
            }
            round++;
        }
        assertTrue(combat.isOver());
        assertTrue(combat.heroWon());
        assertTrue(hero.getCurrentHp() > 0);
    }
}