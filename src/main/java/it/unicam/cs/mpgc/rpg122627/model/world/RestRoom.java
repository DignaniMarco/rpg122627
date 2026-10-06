package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

import java.util.Objects;

/**
 * Stanza di riposo: entrando, l'eroe viene curato fino a una soglia minima
 * di HP, espressa come percentuale del massimo.
 * <p>
 * La cura è un "pavimento" e non un "soffitto": se l'eroe ha già HP
 * superiori alla soglia, la stanza non ha effetto. Non toglie mai HP.
 * In questo modo la stanza garantisce un livello minimo di salute
 * prima di una sfida importante (es. boss), senza penalizzare chi
 * arriva in buone condizioni.
 * <p>
 * Si attiva una sola volta: visitando nuovamente la stanza, nessun
 * effetto viene applicato.
 */
public class RestRoom implements Room {

    private final String name;
    private final String description;
    private final double healToPercent;
    private boolean used;

    /**
     * @param name          nome della stanza
     * @param description   descrizione narrativa
     * @param healToPercent soglia minima di HP come frazione del massimo,
     *                      in (0.0, 1.0]
     */
    public RestRoom(String name, String description, double healToPercent) {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        Objects.requireNonNull(description, "description must not be null");
        if (healToPercent <= 0.0 || healToPercent > 1.0) {
            throw new IllegalArgumentException(
                    "healToPercent must be in (0.0, 1.0], got " + healToPercent);
        }
        this.name = name;
        this.description = description;
        this.healToPercent = healToPercent;
        this.used = false;
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
        if (used) {
            return RoomEvent.nothing(name + " è già stata usata: non c'è più nulla qui.");
        }
        used = true;

        int threshold = (int) Math.round(hero.getMaxHp() * healToPercent);
        int currentHp = hero.getCurrentHp();

        if (currentHp >= threshold) {
            return RoomEvent.nothing(
                    "%s si riposa in %s. Si sente già in forze, l'effetto è minimo."
                            .formatted(hero.getName(), name));
        }

        int healed = threshold - currentHp;
        hero.heal(healed);
        return RoomEvent.nothing(
                "%s si riposa in %s e recupera %d HP, arrivando a %d/%d."
                        .formatted(hero.getName(), name, healed,
                                hero.getCurrentHp(), hero.getMaxHp()));
    }
}