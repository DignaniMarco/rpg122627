package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;

import java.util.Objects;

/**
 * Stanza con un tesoro: entrando, l'oggetto viene aggiunto all'inventario
 * dell'eroe (se c'è spazio).
 */
public class TreasureRoom implements Room {

    private final String name;
    private final String description;
    private final Item treasure;
    private boolean looted;

    public TreasureRoom(String name, String description, Item treasure) {
        this.name = name;
        this.description = description;
        this.treasure = Objects.requireNonNull(treasure, "treasure must not be null");
        this.looted = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public RoomEvent onEnter(Hero hero) {
        if (looted) {
            return RoomEvent.nothing(name + " è già stata saccheggiata.");
        }
        boolean added = hero.getInventory().add(treasure);
        if (!added) {
            return RoomEvent.nothing(
                    "Nello scrigno c'è %s ma l'inventario di %s è pieno."
                            .formatted(treasure.getName(), hero.getName()));
        }
        looted = true;
        return RoomEvent.item(
                "%s trova %s!".formatted(hero.getName(), treasure.getName()),
                treasure);
    }
}