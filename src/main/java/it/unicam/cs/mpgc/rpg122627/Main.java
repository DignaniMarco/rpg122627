package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

public class Main {
    public static void main(String[] args) {
        Hero marco = new Hero("Marco l'Intrepido", 30, 5, 2);
        Enemy goblin = new Enemy("Goblin Puzzolente", 12, 3, 1, 25, new AggressiveBehavior());

        System.out.println("Eroe: " + marco);
        System.out.println("Nemico: " + goblin);
        System.out.println();

        // Simulazione manuale di 3 round di combattimento
        for (int round = 1; round <= 3 && !marco.isDead() && !goblin.isDead(); round++) {
            System.out.println("--- Round " + round + " ---");

            // Turno eroe
            int heroDmg = marco.getAttackDamage();
            goblin.takeDamage(heroDmg);
            System.out.println(marco.getName() + " infligge " + heroDmg + " danni. " + goblin);

            if (goblin.isDead()) {
                System.out.println(goblin.getName() + " è sconfitto!");
                marco.gainExperience(goblin.getXpReward());
                System.out.println("Guadagnati " + goblin.getXpReward() + " XP → " + marco);
                break;
            }

            // Turno nemico
            int enemyDmg = goblin.getBehavior().decideDamage(goblin, marco);
            marco.takeDamage(enemyDmg);
            System.out.println(goblin.getName() + " " + goblin.getBehavior().describeAction()
                    + " e infligge " + enemyDmg + " danni. " + marco);
        }
    }
}