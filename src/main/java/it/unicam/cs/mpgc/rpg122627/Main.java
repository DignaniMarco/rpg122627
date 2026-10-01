package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.combat.AttackAction;
import it.unicam.cs.mpgc.rpg122627.model.combat.Combat;
import it.unicam.cs.mpgc.rpg122627.model.item.HealingPotion;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import it.unicam.cs.mpgc.rpg122627.model.world.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Eroe iniziale
        Hero marco = new Hero("Marco l'Intrepido", 30, 5, 2);
        Weapon pugnale = new Weapon("Pugnale Arrugginito", 2);
        marco.getInventory().add(pugnale);
        marco.equipWeapon(pugnale);

        // Dungeon lineare di 5 stanze
        Dungeon dungeon = new Dungeon(List.of(
                new EmptyRoom("Ingresso",
                        "L'aria è fredda e umida. Si sentono gocciolii in lontananza."),
                new CombatRoom("Sala delle guardie",
                        "Due torce illuminano la stanza.",
                        new Enemy("Goblin", 10, 3, 1, 20, new AggressiveBehavior())),
                new TreasureRoom("Camera del tesoro",
                        "Uno scrigno brilla nell'angolo.",
                        new HealingPotion("Pozione Rossa", 15)),
                new CombatRoom("Cripta",
                        "Ossa sparse ovunque. Un rumore secco.",
                        new Enemy("Scheletro Guerriero", 15, 5, 2, 40, new AggressiveBehavior())),
                new BossRoom("Antro del drago",
                        "Il calore è insopportabile. Un ruggito scuote le mura.",
                        new Enemy("Drago Rosso", 40, 10, 3, 150, new AggressiveBehavior()))
        ));

        AttackAction attack = new AttackAction();

        // Loop di esplorazione
        while (true) {
            Room room = dungeon.getCurrentRoom();
            System.out.println("\n=== " + room.getName() + " ===");
            RoomEvent event = room.onEnter(marco);
            System.out.println(event.getMessage());

            // Gestione evento
            switch (event.getType()) {
                case NOTHING, ITEM_FOUND -> {
                    // niente da fare oltre al messaggio
                }
                case COMBAT_STARTED, BOSS_ENCOUNTER -> {
                    Enemy enemy = event.getEnemy().orElseThrow();
                    Combat combat = new Combat(marco, enemy);
                    while (!combat.isOver()) {
                        System.out.println("  " + combat.heroTurn(attack).getMessage());
                        if (combat.isOver()) break;
                        System.out.println("  " + combat.enemyTurn().getMessage());
                    }
                    if (combat.heroWon()) {
                        combat.awardRewards();
                        System.out.println("  Vittoria! " + marco);
                    } else {
                        System.out.println("  Sei stato sconfitto da " + enemy.getName() + ".");
                        return;
                    }
                }
            }

            // Fine dungeon?
            if (dungeon.isAtLastRoom()) {
                System.out.println("\n>>> Hai completato il dungeon! <<<");
                break;
            }
            dungeon.advance();
        }
    }
}