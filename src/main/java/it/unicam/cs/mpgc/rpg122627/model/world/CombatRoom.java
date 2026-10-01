package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

import java.util.Objects;

/**
 * Stanza che contiene un nemico. Entrando, l'eroe si trova in combattimento.
 * <p>
 * La stanza non esegue il combattimento: restituisce un {@link RoomEvent}
 * di tipo {@link RoomEvent.Type#COMBAT_STARTED} con il riferimento al nemico;
 * sarà il chiamante a istanziare un {@code Combat} e gestirne il flusso.
 */
public class CombatRoom implements Room {

    private final String name;
    private final String description;
    private final Enemy enemy;

    public CombatRoom(String name, String description, Enemy enemy) {
        this.name = name;
        this.description = description;
        this.enemy = Objects.requireNonNull(enemy, "enemy must not be null");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    @Override
    public RoomEvent onEnter(Hero hero) {
        String message = "%s entra in %s e incontra %s!".formatted(
                hero.getName(), name, enemy.getName());
        return RoomEvent.combat(message, enemy);
    }
}