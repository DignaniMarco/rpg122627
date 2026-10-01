package it.unicam.cs.mpgc.rpg122627.ui;

import it.unicam.cs.mpgc.rpg122627.model.Game;
import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.HealingPotion;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import it.unicam.cs.mpgc.rpg122627.model.world.*;

import java.util.List;

/**
 * Costruttore di partita: fornisce l'eroe iniziale e il dungeon
 * standard del gioco.
 * <p>
 * Centralizza la configurazione dei dati statici del gioco in un unico
 * posto, così che possa essere riusata sia per iniziare una nuova
 * partita sia per fornire un dungeon al caricamento da save.
 * <p>
 * Una futura estensione naturale è leggere queste definizioni da file
 * JSON di configurazione (dungeons.json, enemies.json), rendendo il
 * gioco data-driven senza modificare il codice.
 */
public final class GameSetup {

    private GameSetup() {
        // utility class, no instances
    }

    /**
     * @return un eroe di livello 1 con equipaggiamento base
     */
    public static Hero newHero(String name) {
        Hero hero = new Hero(name, 30, 5, 2);
        Weapon pugnale = new Weapon("Pugnale Arrugginito", 2);
        hero.getInventory().add(pugnale);
        hero.equipWeapon(pugnale);
        hero.getInventory().add(new HealingPotion("Pozione Minore", 10));
        return hero;
    }

    /**
     * @return il dungeon standard del gioco (sequenza lineare di 5 stanze)
     */
    public static Dungeon newDungeon() {
        return new Dungeon(List.of(
                new EmptyRoom("Ingresso",
                        "L'aria è fredda e umida. Si sentono gocciolii in lontananza."),
                new CombatRoom("Sala delle guardie",
                        "Due torce accese illuminano la stanza.",
                        new Enemy("Goblin", 10, 3, 1, 20, new AggressiveBehavior())),
                new TreasureRoom("Camera del tesoro",
                        "Uno scrigno brilla nell'angolo.",
                        new HealingPotion("Pozione Rossa", 15)),
                new TreasureRoom("Armeria abbandonata",
                        "Spade arrugginite appese alle pareti. Una brilla ancora.",
                        new Weapon("Spada Lunga", 3)),
                new CombatRoom("Cripta",
                        "Ossa sparse ovunque. Un rumore secco rompe il silenzio.",
                        new Enemy("Scheletro Guerriero", 15, 5, 2, 40, new AggressiveBehavior())),
                new TreasureRoom("Santuario dimenticato",
                        "Un altare di pietra. Sopra, una pozione luminosa.",
                        new HealingPotion("Pozione Maggiore", 25)),
                new BossRoom("Antro del drago",
                        "Il calore è insopportabile. Un ruggito scuote le mura.",
                        new Enemy("Drago Rosso", 30, 8, 2, 200, new AggressiveBehavior()))
        ));
    }

    /**
     * @return una nuova partita pronta all'uso con eroe e dungeon di default
     */
    public static Game newGame(String heroName) {
        return new Game(newHero(heroName), newDungeon());
    }
}