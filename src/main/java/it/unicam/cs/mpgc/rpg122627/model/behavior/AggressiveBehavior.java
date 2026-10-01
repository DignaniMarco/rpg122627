package it.unicam.cs.mpgc.rpg122627.model.behavior;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

/**
 * Comportamento base: il nemico attacca sempre con la propria potenza piena,
 * senza mai difendersi o ritirarsi.
 * <p>
 * Adatto a nemici semplici e privi di intelligenza tattica
 * (es. goblin, scheletri, zombi).
 */
public class AggressiveBehavior implements EnemyBehavior {

    @Override
    public int decideDamage(Character self, Character target) {
        return self.getAttackDamage();
    }

    @Override
    public String describeAction() {
        return "attacca con ferocia";
    }
}