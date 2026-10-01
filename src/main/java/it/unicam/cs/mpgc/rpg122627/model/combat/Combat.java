package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.behavior.EnemyBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

import java.util.Objects;

/**
 * Orchestratore di un combattimento 1-contro-1 tra un eroe e un nemico.
 * <p>
 * Il Combat non conosce i dettagli delle azioni ({@link Action}): si limita
 * a chiederle di eseguirsi, raccogliere i risultati e aggiornare lo stato
 * del combattimento. In questo modo, nuove azioni possono essere aggiunte
 * (incantesimi, oggetti, abilità speciali) senza modificare questa classe.
 * <p>
 * Il flusso di un turno è:
 * <ol>
 *   <li>L'eroe sceglie un'azione tramite {@link #heroTurn(Action)}.</li>
 *   <li>Se il combattimento non è terminato e il nemico è vivo,
 *       il nemico esegue automaticamente la propria azione.</li>
 * </ol>
 */
public class Combat {

    private final Hero hero;
    private final Enemy enemy;
    private boolean combatEnded;

    public Combat(Hero hero, Enemy enemy) {
        this.hero = Objects.requireNonNull(hero, "hero must not be null");
        this.enemy = Objects.requireNonNull(enemy, "enemy must not be null");
        this.combatEnded = false;
    }

    public Hero getHero() {
        return hero;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    /**
     * @return true se il combattimento è concluso (per morte di uno dei due
     *         partecipanti o per fuga riuscita)
     */
    public boolean isOver() {
        return combatEnded || hero.isDead() || enemy.isDead();
    }

    /**
     * @return true se l'eroe ha vinto (il nemico è morto e il combattimento
     *         non è finito per fuga)
     */
    public boolean heroWon() {
        return enemy.isDead() && !hero.isDead();
    }

    /**
     * Esegue il turno dell'eroe con l'azione fornita.
     *
     * @param action azione scelta dall'eroe (non nulla)
     * @return risultato dell'azione, per il log della GUI
     * @throws IllegalStateException se il combattimento è già terminato
     */
    public ActionResult heroTurn(Action action) {
        Objects.requireNonNull(action, "action must not be null");
        if (isOver()) {
            throw new IllegalStateException("combat is already over");
        }
        ActionResult result = action.execute(hero, enemy);
        if (result.endsCombat()) {
            this.combatEnded = true;
        }
        return result;
    }

    /**
     * Esegue il turno del nemico: delega al suo {@link EnemyBehavior} la
     * scelta del danno, poi applica l'effetto.
     *
     * @return risultato dell'azione, per il log della GUI
     * @throws IllegalStateException se il combattimento è già terminato
     */
    public ActionResult enemyTurn() {
        if (isOver()) {
            throw new IllegalStateException("combat is already over");
        }
        int rawDamage = enemy.getBehavior().decideDamage(enemy, hero);
        int effective = Math.max(0, rawDamage - hero.getDefense());
        if (effective > 0) {
            hero.takeDamage(effective);
        }
        String message = "%s %s e infligge %d danni.".formatted(
                enemy.getName(), enemy.getBehavior().describeAction(), effective);
        return ActionResult.of(message, effective);
    }

    /**
     * Da chiamare al termine del combattimento, se l'eroe ha vinto:
     * assegna l'XP del nemico all'eroe. Non ha effetto se il combattimento
     * non è terminato o se l'eroe non ha vinto.
     */
    public void awardRewards() {
        if (isOver() && heroWon()) {
            hero.gainExperience(enemy.getXpReward());
        }
    }
}