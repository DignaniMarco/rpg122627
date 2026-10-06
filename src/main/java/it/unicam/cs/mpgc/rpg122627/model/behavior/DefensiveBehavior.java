package it.unicam.cs.mpgc.rpg122627.model.behavior;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

/**
 * Comportamento tattico: il nemico attacca normalmente a HP pieni,
 * ma riduce la propria aggressività quando è ferito gravemente.
 * <p>
 * Se gli HP correnti sono sotto il 50% del massimo, infligge solo
 * metà del danno (si "difende" invece di attaccare a tutta forza).
 * Adatto a nemici intelligenti o cauti (es. elfi oscuri, umani veterani).
 */
public class DefensiveBehavior implements EnemyBehavior {

    @Override
    public int decideDamage(Character self, Character target) {
        boolean badlyHurt = self.getCurrentHp() * 2 < self.getMaxHp();
        int baseDamage = self.getAttackDamage();
        return badlyHurt ? baseDamage / 2 : baseDamage;
    }

    @Override
    public String describeAction() {
        return "attacca con cautela";
    }
}