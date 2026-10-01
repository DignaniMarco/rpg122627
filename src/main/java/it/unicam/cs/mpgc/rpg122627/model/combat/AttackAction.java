package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

/**
 * Azione di attacco base: l'attore infligge al bersaglio danno pari al proprio
 * valore di attacco meno la difesa del bersaglio, con un minimo di 1
 * (un attacco va comunque sempre a segno almeno in parte).
 */
public class AttackAction implements Action {

    private static final int MIN_DAMAGE = 1;

    @Override
    public ActionResult execute(Character actor, Character target) {
        if (target == null) {
            throw new IllegalArgumentException("AttackAction requires a target");
        }
        int rawDamage = actor.getAttackDamage();
        int effective = Math.max(MIN_DAMAGE, rawDamage - target.getDefense());
        target.takeDamage(effective);
        String message = "%s attacca %s e infligge %d danni.".formatted(
                actor.getName(), target.getName(), effective);
        return ActionResult.of(message, effective);
    }

    @Override
    public String getName() {
        return "Attacca";
    }
}