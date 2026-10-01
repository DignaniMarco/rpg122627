package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

/**
 * Azione difensiva: l'attore salta il proprio attacco per prepararsi
 * a parare quello avversario.
 * <p>
 * In questa versione minimale, l'effetto meccanico sulla difesa non è
 * ancora implementato (richiederebbe uno stato temporaneo sul personaggio);
 * verrà aggiunto quando introdurremo gli effetti di stato.
 */
public class DefendAction implements Action {

    @Override
    public ActionResult execute(Character actor, Character target) {
        String message = "%s si mette in difesa.".formatted(actor.getName());
        return ActionResult.of(message, 0);
    }

    @Override
    public String getName() {
        return "Difenditi";
    }
}