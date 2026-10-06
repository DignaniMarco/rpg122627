package it.unicam.cs.mpgc.rpg122627.model.behavior;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

/**
 * Comportamento da boss: il nemico infligge danno base nella prima
 * metà del combattimento, ma scatena un attacco potenziato quando
 * scende sotto il 50% degli HP ("fase rabbiosa").
 * <p>
 * Riservato ai boss finali: aumenta la tensione del combattimento
 * conclusivo e richiede al giocatore di gestire bene le risorse
 * (pozioni, difesa) nelle fasi avanzate dello scontro.
 */
public class BossBehavior implements EnemyBehavior {

    private static final double ENRAGE_THRESHOLD = 0.5;
    private static final double ENRAGE_MULTIPLIER = 1.3;
    @Override
    public int decideDamage(Character self, Character target) {
        double hpRatio = (double) self.getCurrentHp() / self.getMaxHp();
        int baseDamage = self.getAttackDamage();
        if (hpRatio < ENRAGE_THRESHOLD) {
            return (int) Math.ceil(baseDamage * ENRAGE_MULTIPLIER);
        }
        return baseDamage;
    }

    @Override
    public String describeAction() {
        return "ruggisce e colpisce con furia";
    }
}
