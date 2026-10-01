package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.model.Game;
import it.unicam.cs.mpgc.rpg122627.model.GameStatus;
import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.HealingPotion;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import it.unicam.cs.mpgc.rpg122627.model.world.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // --- Setup eroe ---
        Hero marco = new Hero("Marco l'Intrepido", 30, 5, 2);
        Weapon pugnale = new Weapon("Pugnale Arrugginito", 2);
        marco.getInventory().add(pugnale);
        marco.equipWeapon(pugnale);

        // --- Setup dungeon ---
        Dungeon dungeon = new Dungeon(List.of(
                new EmptyRoom("Ingresso", "Aria fredda e umida."),
                new CombatRoom("Sala delle guardie", "Due torce.",
                        new Enemy("Goblin", 10, 3, 1, 20, new AggressiveBehavior())),
                new TreasureRoom("Camera del tesoro", "Scrigno brillante.",
                        new HealingPotion("Pozione Rossa", 15)),
                new CombatRoom("Cripta", "Ossa sparse.",
                        new Enemy("Scheletro", 15, 5, 2, 40, new AggressiveBehavior())),
                new BossRoom("Antro del drago", "Calore insopportabile.",
                        new Enemy("Drago Rosso", 40, 10, 3, 150, new AggressiveBehavior()))
        ));

        // --- Partita ---
        Game game = new Game(marco, dungeon);
        System.out.println(game.enterCurrentRoom().getJoinedMessage());

        while (game.getStatus() != GameStatus.VICTORY && game.getStatus() != GameStatus.DEFEAT) {
            if (game.getStatus() == GameStatus.IN_COMBAT) {
                System.out.println("  " + game.playerAttack().getJoinedMessage());
                if (game.getStatus() == GameStatus.IN_COMBAT) {
                    System.out.println("  " + game.enemyTurn().getJoinedMessage());
                }
            } else { // EXPLORING
                if (game.getDungeon().isAtLastRoom()) break;
                System.out.println(game.advanceToNextRoom().getJoinedMessage());
                System.out.println(game.enterCurrentRoom().getJoinedMessage());
            }
        }

        System.out.println();
        switch (game.getStatus()) {
            case VICTORY -> System.out.println(">>> VITTORIA! <<< " + marco);
            case DEFEAT  -> System.out.println(">>> SCONFITTA <<< " + marco);
            default      -> System.out.println("Partita in stato inatteso: " + game.getStatus());
        }
    }
}