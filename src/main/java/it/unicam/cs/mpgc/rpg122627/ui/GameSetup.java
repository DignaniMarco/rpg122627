package it.unicam.cs.mpgc.rpg122627.ui;

import it.unicam.cs.mpgc.rpg122627.model.Game;
import it.unicam.cs.mpgc.rpg122627.model.behavior.AggressiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.behavior.BossBehavior;
import it.unicam.cs.mpgc.rpg122627.model.behavior.DefensiveBehavior;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.*;
import it.unicam.cs.mpgc.rpg122627.model.world.*;

import java.util.List;

/**
 * Costruttore di partita: fornisce l'eroe iniziale e il dungeon
 * standard del gioco (grafo a forma di Diamond).
 * <p>
 * Il Diamond ha 7 nodi:
 * <pre>
 *                   [A] Ingresso
 *                        │
 *                   [B] Sala delle Guardie (3 nemici)
 *                        │
 *                   [C] Trivio Antico (ChoiceRoom)
 *                      /   \
 *                     /     \
 *       [D1] Camera del      [D2] Antro del
 *            Predatore            Negoziante (NPC)
 *            (2 nemici)
 *             │                     │
 *       [E1] Cripta           [E2] Sala dei Tomi
 *            Infestata              Dimenticati
 *            (2 nemici)             (tesoro)
 *             │                     │
 *             └─────────┬───────────┘
 *                       │
 *                   [F] Antro del Drago (boss)
 * </pre>
 * <p>
 * Nota: nel modello attuale ogni Room può contenere al massimo un nemico
 * ({@link CombatRoom}); per rappresentare "3 nemici in Sala delle Guardie"
 * usiamo 3 nodi collegati in cascata con lo stesso nome narrativo e
 * nemici diversi. Totale: 12 nemici distinti su 7 "aree" narrative.
 * <p>
 * Future estensioni naturali: lettura di questa configurazione da file
 * JSON (dungeons.json), rendendo il gioco data-driven senza modificare
 * il codice.
 */
public final class GameSetup {

    private GameSetup() {
        // utility class, no instances
    }

    public static Hero newHero(String name) {
        Hero hero = new Hero(name, 35, 5, 2);
        Weapon pugnale = new Weapon("Pugnale Arrugginito", 2);
        hero.getInventory().add(pugnale);
        hero.equipWeapon(pugnale);
        hero.getInventory().add(new HealingPotion("Pozione Minore", 10));
        return hero;
    }

    /**
     * Costruisce il dungeon Diamond: 7 nodi narrativi, 12 nemici,
     * una ChoiceRoom al centro, un NPC con drop probabilistico,
     * un boss finale.
     */
    public static Dungeon newDungeon() {
        // ===== Nodo A — Ingresso =====
        DungeonNode a = new DungeonNode("A",
                new EmptyRoom("Ingresso del Dungeon",
                        "L'aria è fredda e umida. In lontananza si sentono passi."));

        // ===== Nodi B1-B3 — Sala delle Guardie (3 nemici goblin) =====
        DungeonNode b1 = new DungeonNode("B1",
                new CombatRoom("Sala delle Guardie",
                        "Un goblin solitario fa la guardia.",
                        new Enemy("Goblin Esploratore", 8, 3, 1, 15, new AggressiveBehavior())));
        DungeonNode b2 = new DungeonNode("B2",
                new CombatRoom("Sala delle Guardie",
                        "Un altro goblin ti ha visto.",
                        new Enemy("Goblin Guerriero", 12, 4, 1, 20, new AggressiveBehavior())));
        DungeonNode b3 = new DungeonNode("B3",
                new CombatRoom("Sala delle Guardie",
                        "L'ultimo goblin, il capo del gruppo.",
                        new Enemy("Goblin Capotribù", 15, 5, 2, 30, new DefensiveBehavior())));

        // ===== Scorciatoia di sollievo prima del Trivio =====
        DungeonNode bHeal = new DungeonNode("Bh",
                new RestRoom("Fontana Dimenticata",
                        "Una fontanella di pietra sgorga acqua pura. Puoi sederti e riprendere fiato.",
                        0.70));

        // ===== Nodo C — Trivio (ChoiceRoom) =====
        ChoiceRoom trivioRoom = new ChoiceRoom("Trivio Antico",
                "Davanti a te si aprono due passaggi, scavati chissà quando.");
        DungeonNode c = new DungeonNode("C", trivioRoom);

        // ===== Ramo sinistro: D1 → D1b → cura → E1 → E1b =====
        DungeonNode d1 = new DungeonNode("D1",
                new CombatRoom("Camera del Predatore",
                        "Il ringhio di un lupo gigantesco riecheggia nella camera.",
                        new Enemy("Lupo Mannaro", 16, 5, 2, 45, new AggressiveBehavior())));
        DungeonNode d1b = new DungeonNode("D1b",
                new CombatRoom("Camera del Predatore",
                        "Un orco bruto si para davanti a te.",
                        new Enemy("Orco Bruto", 20, 6, 3, 55, new AggressiveBehavior())));
        DungeonNode dHeal2 = new DungeonNode("Dh2",
                new TreasureRoom("Nicchia del Guaritore",
                        "Un piccolo altare con una pozione dimenticata e del muschio curativo.",
                        new HealingPotion("Pozione Rossa", 15)));
        DungeonNode dHeal = new DungeonNode("Dh",
                new TreasureRoom("Cella dell'Alchimista",
                        "Fiale rotte sul pavimento. Una è intatta e brilla di rosso.",
                        new HealingPotion("Pozione Rossa", 15)));
        DungeonNode e1 = new DungeonNode("E1",
                new CombatRoom("Cripta Infestata",
                        "Ossa e teschi tappezzano le pareti. Qualcosa si muove.",
                        new Enemy("Scheletro Guerriero", 16, 6, 3, 50, new DefensiveBehavior())));
        DungeonNode eHeal = new DungeonNode("Eh",
                new TreasureRoom("Sarcofago Aperto",
                        "Un sarcofago violato. Tra le ossa, una fiala intatta.",
                        new HealingPotion("Pozione Minore", 10)));
        DungeonNode e1b = new DungeonNode("E1b",
                new CombatRoom("Cripta Infestata",
                        "Un cavaliere non-morto si rialza dal pavimento.",
                        new Enemy("Cavaliere Spettrale", 22, 7, 3, 70, new DefensiveBehavior())));

        // ===== Ramo destro: D2 (NPC) → E2 (tesoro) =====
        DungeonNode d2 = new DungeonNode("D2",
                new NpcRoom("Antro del Negoziante",
                        "Un vecchio incappucciato ti osserva da dietro un braciere.",
                        "Negoziante Errante",
                        new Weapon("Spada d'Argento", 4),
                        0.90));
        DungeonNode d2bHeal = new DungeonNode("D2h",
                new TreasureRoom("Angolo del Mercante",
                        "Il Negoziante ha lasciato una scorta: una pozione per il viaggio.",
                        new HealingPotion("Pozione Media", 15)));
        DungeonNode e2 = new DungeonNode("E2",
                new TreasureRoom("Sala dei Tomi Dimenticati",
                        "Scaffali crollati. In mezzo, una pozione luminosa.",
                        new HealingPotion("Pozione Maggiore", 25)));

        // ===== Scorciatoia di cura prima del boss (comune ai due rami) =====
        // ===== Stanza di riposo prima del boss (comune ai due rami) =====
        DungeonNode fRest = new DungeonNode("Fr",
                new RestRoom("Santuario del Pellegrino",
                        "Un santuario silenzioso con un braciere acceso. Puoi riposare qui prima dell'ultima sfida.",
                        0.75));
        // ===== Nodo F — Antro del Drago (boss) =====
        DungeonNode f = new DungeonNode("F",
                new BossRoom("Antro del Drago",
                        "Il calore è insopportabile. Un ruggito scuote le mura.",
                        new Enemy("Drago Rosso Antico", 36, 9, 3, 250, new BossBehavior())));

        // ===== Collegamenti (archi del grafo) =====
        a.addSuccessor(b1);
        b1.addSuccessor(b2);
        b2.addSuccessor(b3);
        b3.addSuccessor(bHeal);
        bHeal.addSuccessor(c);

        trivioRoom.addOption(new ChoiceOption(
                "Vai a sinistra",
                "Il passaggio puzza di sangue: ti aspettano combattimenti pesanti ma troverai scorte.",
                d1));
        trivioRoom.addOption(new ChoiceOption(
                "Vai a destra",
                "Senti una voce lontana: potresti incontrare qualcuno, ma la strada è più lunga.",
                d2));
        c.addSuccessor(d1);
        c.addSuccessor(d2);

        d1.addSuccessor(d1b);
        d1b.addSuccessor(dHeal);
        dHeal.addSuccessor(dHeal2);
        dHeal2.addSuccessor(e1);
        e1.addSuccessor(eHeal);
        eHeal.addSuccessor(e1b);
        e1b.addSuccessor(fRest);

        d2.addSuccessor(d2bHeal);
        d2bHeal.addSuccessor(e2);
        e2.addSuccessor(fRest);

        fRest.addSuccessor(f);

        // ===== Lista completa dei nodi =====
        List<DungeonNode> allNodes = List.of(
                a, b1, b2, b3, bHeal, c,
                d1, d1b, dHeal, dHeal2, e1, eHeal, e1b,
                d2, d2bHeal, e2,
                fRest, f);
        return new Dungeon(allNodes, a);
    }

    public static Game newGame(String heroName) {
        return new Game(newHero(heroName), newDungeon());
    }
}