package it.unicam.cs.mpgc.rpg122627.model;

import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.combat.*;
import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.Consumable;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import it.unicam.cs.mpgc.rpg122627.model.world.Dungeon;
import it.unicam.cs.mpgc.rpg122627.model.world.Room;
import it.unicam.cs.mpgc.rpg122627.model.world.RoomEvent;
import it.unicam.cs.mpgc.rpg122627.persistence.GameState;
import it.unicam.cs.mpgc.rpg122627.persistence.ItemCatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Facciata del modello di gioco: unico punto d'ingresso che la GUI usa
 * per interagire con eroe, dungeon e combattimenti.
 * <p>
 * Il Game gestisce internamente la macchina a stati (esplorazione,
 * combattimento, vittoria, sconfitta), coordina {@link Dungeon} e
 * {@link Combat}, e restituisce ad ogni azione un {@link GameUpdate}
 * che contiene stato aggiornato e messaggi per il log.
 * <p>
 * In questo modo la GUI non conosce le classi del modello interno:
 * dipende solo dal contratto pubblico di questa facciata e dai suoi
 * dati immutabili di stato, rispettando il Dependency Inversion Principle
 * e lasciando libertà di sostituire la GUI (desktop, mobile, web, CLI)
 * senza toccare nessuna classe del modello.
 */
public class Game {

    private static final AttackAction ATTACK = new AttackAction();
    private static final DefendAction DEFEND = new DefendAction();

    private final Hero hero;
    private final Dungeon dungeon;
    private GameStatus status;
    private Combat currentCombat;

    public Game(Hero hero, Dungeon dungeon) {
        this.hero = Objects.requireNonNull(hero, "hero must not be null");
        this.dungeon = Objects.requireNonNull(dungeon, "dungeon must not be null");
        this.status = GameStatus.EXPLORING;
        this.currentCombat = null;
    }

    // ========== Query (consultazione dello stato) ==========

    public Hero getHero() {
        return hero;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Room getCurrentRoom() {
        return dungeon.getCurrentRoom();
    }

    /**
     * @return il combattimento in corso, se lo stato è IN_COMBAT; null altrimenti.
     *         La GUI lo usa per mostrare le info del nemico corrente.
     */
    public Combat getCurrentCombat() {
        return currentCombat;
    }

    // ========== Azioni di esplorazione ==========

    /**
     * Fa entrare l'eroe nella stanza corrente e gestisce l'evento conseguente.
     * Da chiamare all'inizio della partita e dopo ogni {@link #advanceToNextRoom()}.
     *
     * @return update che descrive l'esito dell'ingresso
     * @throws IllegalStateException se lo stato non è EXPLORING
     */
    public GameUpdate enterCurrentRoom() {
        requireStatus(GameStatus.EXPLORING);
        Room room = dungeon.getCurrentRoom();
        RoomEvent event = room.onEnter(hero);

        switch (event.getType()) {
            case NOTHING, ITEM_FOUND -> {
                return new GameUpdate(status, event.getMessage());
            }
            case COMBAT_STARTED, BOSS_ENCOUNTER -> {
                Enemy enemy = event.getEnemy().orElseThrow();
                this.currentCombat = new Combat(hero, enemy);
                this.status = GameStatus.IN_COMBAT;
                return new GameUpdate(status, event.getMessage());
            }
        }
        throw new IllegalStateException("unhandled RoomEvent type: " + event.getType());
    }

    /**
     * Avanza alla stanza successiva del dungeon.
     * Non entra nella nuova stanza automaticamente: va chiamato
     * {@link #enterCurrentRoom()} subito dopo (la separazione permette
     * alla GUI di mostrare una transizione se vuole).
     *
     * @throws IllegalStateException se non si è in esplorazione o se
     *         non c'è una stanza successiva
     */
    public GameUpdate advanceToNextRoom() {
        requireStatus(GameStatus.EXPLORING);
        if (dungeon.isAtLastRoom()) {
            throw new IllegalStateException("no next room available");
        }
        Room next = dungeon.advance();
        return new GameUpdate(status, hero.getName() + " avanza verso " + next.getName() + ".");
    }

    // ========== Azioni di combattimento ==========

    public GameUpdate playerAttack() {
        return executePlayerAction(ATTACK);
    }

    public GameUpdate playerDefend() {
        return executePlayerAction(DEFEND);
    }

    public GameUpdate playerUseItem(Consumable item) {
        return executePlayerAction(new UseItemAction(item));
    }

    public GameUpdate playerFlee() {
        return executePlayerAction(new FleeAction());
    }

    /**
     * Esegue il turno del nemico. La GUI deve chiamarlo dopo ogni azione
     * del giocatore che non ha concluso il combattimento, così può mostrare
     * i due turni separatamente nel log (prima quello dell'eroe, poi
     * quello del nemico).
     */
    public GameUpdate enemyTurn() {
        requireStatus(GameStatus.IN_COMBAT);
        if (currentCombat.isOver()) {
            // caso difensivo: non dovrebbe mai accadere nel flusso normale
            resolveCombatEnd();
            return new GameUpdate(status, "Il combattimento è già terminato.");
        }
        List<String> messages = new ArrayList<>();
        Enemy enemy = currentCombat.getEnemy();

        ActionResult result = currentCombat.enemyTurn();
        messages.add(result.getMessage());

        if (currentCombat.isOver()) {
            messages.addAll(buildCombatEndMessages(enemy));
            resolveCombatEnd();
        }
        return new GameUpdate(status, messages);
    }

    // ========== Logica interna ==========

    private GameUpdate executePlayerAction(Action action) {
        requireStatus(GameStatus.IN_COMBAT);
        List<String> messages = new ArrayList<>();
        Enemy enemy = currentCombat.getEnemy();

        ActionResult result = currentCombat.heroTurn(action);
        messages.add(result.getMessage());

        if (currentCombat.isOver()) {
            messages.addAll(buildCombatEndMessages(enemy));
            resolveCombatEnd();
        }
        return new GameUpdate(status, messages);
    }

    /**
     * Genera i messaggi testuali che descrivono la fine del combattimento:
     * sconfitta del nemico, XP guadagnata, oppure morte dell'eroe.
     * <p>
     * Chiamato prima di {@link #resolveCombatEnd()} così da avere ancora
     * accesso al Combat corrente.
     */
    private List<String> buildCombatEndMessages(Enemy enemy) {
        List<String> messages = new ArrayList<>();
        if (hero.isDead()) {
            messages.add("%s è stato sconfitto da %s.".formatted(hero.getName(), enemy.getName()));
            return messages;
        }
        if (currentCombat.heroWon()) {
            messages.add("%s è sconfitto!".formatted(enemy.getName()));
            messages.add("%s guadagna %d XP.".formatted(hero.getName(), enemy.getXpReward()));
        }
        return messages;
    }

    /**
     * Determina l'esito finale del combattimento e aggiorna lo stato del gioco.
     * Chiamato automaticamente non appena il combattimento termina.
     */
    private void resolveCombatEnd() {
        if (hero.isDead()) {
            this.status = GameStatus.DEFEAT;
            this.currentCombat = null;
            return;
        }
        if (currentCombat.heroWon()) {
            currentCombat.awardRewards();
            // Se era un boss e dungeon finito → vittoria
            if (dungeon.isAtLastRoom()) {
                this.status = GameStatus.VICTORY;
            } else {
                this.status = GameStatus.EXPLORING;
            }
            this.currentCombat = null;
            return;
        }
        // Fuga riuscita: torna in esplorazione nella stessa stanza
        this.status = GameStatus.EXPLORING;
        this.currentCombat = null;
    }

    private void requireStatus(GameStatus expected) {
        if (status != expected) {
            throw new IllegalStateException(
                    "operation requires status " + expected + " but current is " + status);
        }
    }

    // ========== Persistenza ==========

    /**
     * Produce una fotografia serializzabile dello stato corrente della partita.
     */
    public GameState exportState() {
        GameState s = new GameState();
        s.setHeroName(hero.getName());
        s.setHeroLevel(hero.getLevel());
        s.setHeroExperience(hero.getExperience());
        s.setHeroCurrentHp(hero.getCurrentHp());
        s.setHeroMaxHp(hero.getMaxHp());
        // Nota: salviamo i valori "base" non quelli con bonus arma/armatura,
        // perché gli equipaggiamenti vengono ricreati separatamente.
        int baseAttack = hero.getAttackDamage()
                - (hero.getEquippedWeapon() != null ? hero.getEquippedWeapon().getAttackBonus() : 0);
        int baseDefense = hero.getDefense()
                - (hero.getEquippedArmor() != null ? hero.getEquippedArmor().getDefenseBonus() : 0);
        s.setHeroBaseAttack(baseAttack);
        s.setHeroBaseDefense(baseDefense);

        List<String> itemNames = new ArrayList<>();
        for (Item item : hero.getInventory().getItems()) {
            itemNames.add(item.getName());
        }
        s.setInventoryItemNames(itemNames);
        s.setEquippedWeaponName(
                hero.getEquippedWeapon() != null ? hero.getEquippedWeapon().getName() : null);
        s.setEquippedArmorName(
                hero.getEquippedArmor() != null ? hero.getEquippedArmor().getName() : null);
        s.setCurrentRoomIndex(dungeon.getCurrentIndex());
        return s;
    }

    /**
     * Ricostruisce un {@link Game} da uno stato serializzato.
     * <p>
     * Richiede un {@link Dungeon} pre-costruito (le definizioni delle stanze
     * sono considerate dati statici del gioco) e un {@link ItemCatalog}
     * per ricreare gli oggetti dall'elenco di nomi salvato.
     * <p>
     * Il metodo porta il dungeon all'indice salvato ma non richiama
     * {@code onEnter} sulla stanza corrente: sarà la GUI a decidere se
     * ri-mostrare l'evento della stanza o riprendere direttamente.
     *
     * @throws IllegalArgumentException se un oggetto nel save non esiste nel catalogo
     */
    public static Game fromState(GameState state, Dungeon dungeon, ItemCatalog catalog) {
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(dungeon, "dungeon must not be null");
        Objects.requireNonNull(catalog, "catalog must not be null");

        Hero hero = new Hero(
                state.getHeroName(),
                state.getHeroMaxHp(),
                state.getHeroBaseAttack(),
                state.getHeroBaseDefense());

        // Ripristina livello ed XP esattamente come erano salvati
        hero.restoreFromSave(state.getHeroLevel(), state.getHeroExperience());

        // Ripristina HP correnti: curiamo al max, poi applichiamo danno
        int damage = state.getHeroMaxHp() - state.getHeroCurrentHp();
        if (damage > 0) {
            hero.takeDamage(damage);
        }

        // Ripristina inventario
        for (String itemName : state.getInventoryItemNames()) {
            Item item = catalog.getByName(itemName)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "unknown item in save: " + itemName));
            hero.getInventory().add(item);
        }

        // Ripristina equipaggiamento (riusa le stesse istanze dell'inventario)
        if (state.getEquippedWeaponName() != null) {
            Weapon w = hero.getInventory().getItems().stream()
                    .filter(i -> i instanceof Weapon && i.getName().equals(state.getEquippedWeaponName()))
                    .map(i -> (Weapon) i)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "equipped weapon not in inventory: " + state.getEquippedWeaponName()));
            hero.equipWeapon(w);
        }
        if (state.getEquippedArmorName() != null) {
            Armor a = hero.getInventory().getItems().stream()
                    .filter(i -> i instanceof Armor && i.getName().equals(state.getEquippedArmorName()))
                    .map(i -> (Armor) i)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "equipped armor not in inventory: " + state.getEquippedArmorName()));
            hero.equipArmor(a);
        }

        // Riporta il dungeon all'indice salvato
        while (dungeon.getCurrentIndex() < state.getCurrentRoomIndex()) {
            dungeon.advance();
        }

        return new Game(hero, dungeon);
    }
}