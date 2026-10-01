package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

/**
 * Stanza vuota: entrare non produce effetti meccanici.
 * Utile come stanza di passaggio o per dare respiro tra combattimenti.
 */
public class EmptyRoom implements Room {

    private final String name;
    private final String description;

    public EmptyRoom(String name, String description) {
        this.name = name;
        this.description = description;
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
        return RoomEvent.nothing(hero.getName() + " entra in " + name + ". " + description);
    }
}