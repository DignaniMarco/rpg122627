package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

import java.util.Objects;

/**
 * Stanza finale del dungeon, contenente il boss.
 * <p>
 * Semanticamente distinta da {@link CombatRoom}: la GUI può rappresentarla
 * diversamente (musica diversa, schermata speciale) e la logica di
 * fine-gioco si attiva alla vittoria sul boss.
 */
public class BossRoom implements Room {

    private final String name;
    private final String description;
    private final Enemy boss;

    public BossRoom(String name, String description, Enemy boss) {
        this.name = name;
        this.description = description;
        this.boss = Objects.requireNonNull(boss, "boss must not be null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public Enemy getBoss() {
        return boss;
    }

    @Override
    public RoomEvent onEnter(Hero hero) {
        String message = "%s raggiunge %s e si trova faccia a faccia con %s!".formatted(
                hero.getName(), name, boss.getName());
        return RoomEvent.boss(message, boss);
    }
}