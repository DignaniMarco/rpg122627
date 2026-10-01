package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;

import java.util.Random;

/**
 * Azione di fuga: l'attore tenta di scappare dal combattimento.
 * La fuga ha una probabilità di riuscita; se riesce, il combattimento termina.
 */
public class FleeAction implements Action {

    private static final double FLEE_CHANCE = 0.5;

    private final Random random;

    public FleeAction() {
        this(new Random());
    }

    /**
     * Costruttore che accetta un {@link Random} dall'esterno:
     * utile nei test per iniettare un generatore con seed noto e
     * rendere il risultato deterministico.
     */
    public FleeAction(Random random) {
        this.random = random;
    }

    @Override
    public ActionResult execute(Character actor, Character target) {
        if (random.nextDouble() < FLEE_CHANCE) {
            return ActionResult.ending(
                    "%s riesce a fuggire!".formatted(actor.getName()));
        }
        return ActionResult.of(
                "%s tenta di fuggire ma fallisce.".formatted(actor.getName()), 0);
    }

    @Override
    public String getName() {
        return "Fuggi";
    }
}