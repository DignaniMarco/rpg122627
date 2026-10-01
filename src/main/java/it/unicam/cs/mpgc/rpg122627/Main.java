package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.combat.AttackAction;
import it.unicam.cs.mpgc.rpg122627.model.combat.Combat;
import it.unicam.cs.mpgc.rpg122627.model.combat.UseItemAction;
import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.HealingPotion;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;

public class Main {
    public static void main(String[] args) {
        Hero marco = new Hero("Marco l'Intrepido", 30, 5, 2);

        // Equipaggiamento
        Weapon spada = new Weapon("Spada Lunga", 3);
        Armor cotta = new Armor("Cotta di Maglia", 2);
        HealingPotion pozione = new HealingPotion("Pozione Minore", 10);

        marco.getInventory().add(spada);
        marco.getInventory().add(cotta);
        marco.getInventory().add(pozione);
        marco.equipWeapon(spada);
        marco.equipArmor(cotta);

        System.out.println("Eroe: " + marco);
        System.out.println("Oggetti in inventario: " + marco.getInventory().size());
        System.out.println();

        // Combattimento contro un nemico più forte, così la pozione serve davvero
        Enemy orco = new Enemy("Orco Brutale", 25, 8, 2, 60, new AggressiveBehavior());
        System.out.println("Nemico: " + orco);
        System.out.println();

        Combat combat = new Combat(marco, orco);
        AttackAction attack = new AttackAction();
        boolean potionUsed = false;
        int round = 1;

        while (!combat.isOver()) {
            System.out.println("--- Round " + round + " ---");

            // L'eroe beve la pozione se è sotto il 50% HP e non l'ha ancora usata
            if (!potionUsed && marco.getCurrentHp() < marco.getMaxHp() / 2) {
                System.out.println(combat.heroTurn(new UseItemAction(pozione)).getMessage());
                potionUsed = true;
            } else {
                System.out.println(combat.heroTurn(attack).getMessage());
            }

            if (combat.isOver()) break;
            System.out.println(combat.enemyTurn().getMessage());
            round++;
        }

        System.out.println();
        if (combat.heroWon()) {
            System.out.println("Vittoria!");
            combat.awardRewards();
            System.out.println("Stato finale: " + marco);
            System.out.println("Oggetti rimasti: " + marco.getInventory().size());
        } else {
            System.out.println("Sconfitta! Stato finale: " + marco);
        }
    }
}