package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.model.Game;
import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.HealingPotion;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import it.unicam.cs.mpgc.rpg122627.model.world.*;
import it.unicam.cs.mpgc.rpg122627.persistence.*;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        // --- Setup eroe ---
        Hero marco = new Hero("Marco l'Intrepido", 30, 5, 2);
        Weapon pugnale = new Weapon("Pugnale Arrugginito", 2);
        marco.getInventory().add(pugnale);
        marco.equipWeapon(pugnale);
        marco.getInventory().add(new HealingPotion("Pozione Rossa", 15));
        marco.takeDamage(7);
        marco.gainExperience(45);

        // --- Setup dungeon ---
        Dungeon dungeon = buildDungeon();

        // --- Partita ---
        Game game = new Game(marco, dungeon);
        game.advanceToNextRoom(); // andiamo alla stanza 1 per testare save/load con posizione != 0

        System.out.println("Stato prima del save:");
        System.out.println("  " + marco);
        System.out.println("  Posizione: " + game.getDungeon().getCurrentIndex()
                + " (" + game.getCurrentRoom().getName() + ")");
        System.out.println("  Inventario: " + marco.getInventory().getItems().size() + " oggetti");

        // --- Save ---
        SaveManager saveManager = new JsonSaveManager();
        saveManager.save(game.exportState());
        System.out.println("\n>>> Partita salvata in: "
                + ((JsonSaveManager) saveManager).getSaveFile().getAbsolutePath());

        // --- Load ---
        GameState loaded = saveManager.load();
        Game restoredGame = Game.fromState(loaded, buildDungeon(), new ItemCatalog());
        Hero restoredHero = restoredGame.getHero();

        System.out.println("\nStato dopo il load:");
        System.out.println("  " + restoredHero);
        System.out.println("  Posizione: " + restoredGame.getDungeon().getCurrentIndex()
                + " (" + restoredGame.getCurrentRoom().getName() + ")");
        System.out.println("  Inventario: " + restoredHero.getInventory().getItems().size() + " oggetti");
    }

    private static Dungeon buildDungeon() {
        return new Dungeon(List.of(
                new EmptyRoom("Ingresso", "Aria fredda."),
                new CombatRoom("Sala delle guardie", "Torce accese.",
                        new Enemy("Goblin", 10, 3, 1, 20, new AggressiveBehavior())),
                new TreasureRoom("Camera del tesoro", "Scrigno.",
                        new HealingPotion("Pozione Rossa", 15)),
                new CombatRoom("Cripta", "Ossa.",
                        new Enemy("Scheletro", 15, 5, 2, 40, new AggressiveBehavior())),
                new BossRoom("Antro del drago", "Caldo.",
                        new Enemy("Drago Rosso", 40, 10, 3, 150, new AggressiveBehavior()))
        ));
    }
}