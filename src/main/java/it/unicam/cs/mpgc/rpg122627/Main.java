package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.combat.AttackAction;
import it.unicam.cs.mpgc.rpg122627.model.combat.Combat;

public class Main {
    public static void main(String[] args) {
        Hero marco = new Hero("Marco l'Intrepido", 30, 5, 2);
        Enemy goblin = new Enemy("Goblin Puzzolente", 12, 3, 1, 25, new AggressiveBehavior());

        Combat combat = new Combat(marco, goblin);
        AttackAction attack = new AttackAction();

        int round = 1;
        while (!combat.isOver()) {
            System.out.println("--- Round " + round + " ---");
            System.out.println(combat.heroTurn(attack).getMessage());
            if (combat.isOver()) break;
            System.out.println(combat.enemyTurn().getMessage());
            round++;
        }

        System.out.println();
        if (combat.heroWon()) {
            System.out.println("Vittoria!");
            combat.awardRewards();
            System.out.println("Stato finale: " + marco);
        } else {
            System.out.println("Sconfitta!");
        }
    }
}