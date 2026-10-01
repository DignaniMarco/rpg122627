package it.unicam.cs.mpgc.rpg122627.model.combat;

import it.unicam.cs.mpgc.rpg122627.model.character.Character;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.Consumable;

import java.util.Objects;

/**
 * Azione che usa un oggetto consumabile dall'inventario dell'eroe.
 * <p>
 * Il consumabile viene applicato all'eroe e rimosso dall'inventario.
 * Questa azione è pensata esclusivamente per l'eroe: lanciare se chiamata
 * con un attore che non sia un {@link Hero}.
 */
public class UseItemAction implements Action {

    private final Consumable item;

    public UseItemAction(Consumable item) {
        this.item = Objects.requireNonNull(item, "item must not be null");
    }

    @Override
    public ActionResult execute(Character actor, Character target) {
        if (!(actor instanceof Hero hero)) {
            throw new IllegalArgumentException("UseItemAction is only valid for Hero");
        }
        if (!hero.getInventory().getItems().contains(item)) {
            throw new IllegalStateException("item is not in hero's inventory");
        }
        String message = item.applyTo(hero);
        hero.getInventory().remove(item);
        return ActionResult.of(message, 0);
    }

    @Override
    public String getName() {
        return "Usa " + item.getName();
    }
}