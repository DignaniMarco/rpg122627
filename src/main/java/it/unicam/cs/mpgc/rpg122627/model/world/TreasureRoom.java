package it.unicam.cs.mpgc.rpg122627.model.world;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;
import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;

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

        // Auto-equip se è un'arma/armatura migliore di quella corrente.
        // Scelta pragmatica MVP: nel gioco non c'è ancora una GUI di equipaggiamento,
        // quindi l'eroe equipaggia automaticamente l'oggetto se migliora le statistiche.
        // Una futura estensione sostituirà questo auto-equip con una scelta esplicita del giocatore.
        String bonusMessage = autoEquipIfBetter(hero);

        boolean equipped = hero.tryAutoEquip(treasure);
        String message = "%s trova %s!".formatted(hero.getName(), treasure.getName());
        if (equipped) {
            message += " L'oggetto è stato equipaggiato automaticamente.";
        }
        return RoomEvent.item(message, treasure);
    }

    private String autoEquipIfBetter(Hero hero) {
        if (treasure instanceof Weapon w) {
            int currentBonus = (hero.getEquippedWeapon() != null)
                    ? hero.getEquippedWeapon().getAttackBonus() : 0;
            if (w.getAttackBonus() > currentBonus) {
                hero.equipWeapon(w);
                return " (equipaggiata automaticamente)";
            }
        }
        if (treasure instanceof Armor a) {
            int currentBonus = (hero.getEquippedArmor() != null)
                    ? hero.getEquippedArmor().getDefenseBonus() : 0;
            if (a.getDefenseBonus() > currentBonus) {
                hero.equipArmor(a);
                return " (equipaggiata automaticamente)";
            }
        }
        return "";
    }
}