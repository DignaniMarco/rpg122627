package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

public class Main {
    public static void main(String[] args) {
        Hero marco = new Hero("Marco l'Intrepido", 30, 5, 2);
        System.out.println("Creato: " + marco);

        marco.takeDamage(10);
        System.out.println("Dopo aver subito 10 danni: " + marco);

        marco.heal(3);
        System.out.println("Dopo essersi curato di 3: " + marco);

        marco.gainExperience(250);
        System.out.println("Dopo aver guadagnato 250 XP: " + marco);

        System.out.println("È morto? " + marco.isDead());
        marco.takeDamage(1000);
        System.out.println("Dopo 1000 danni: " + marco);
        System.out.println("È morto? " + marco.isDead());
    }
}