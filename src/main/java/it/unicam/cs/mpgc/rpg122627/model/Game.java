package it.unicam.cs.mpgc.rpg122627.model;

import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.combat.*;
import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.Consumable;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import it.unicam.cs.mpgc.rpg122627.model.world.*;
import it.unicam.cs.mpgc.rpg122627.persistence.GameState;
import it.unicam.cs.mpgc.rpg122627.persistence.ItemCatalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Facciata del modello di gioco: unico punto d'ingresso che la GUI usa
 * per interagire con eroe, dungeon e combattimenti.
 * <p>
 * Il Game gestisce internamente la macchina a stati (esplorazione,
 * scelta, combattimento, vittoria, sconfitta), coordina {@link Dungeon}
 * e {@link Combat}, e restituisce ad ogni azione un {@link GameUpdate}
 * che contiene stato aggiornato e messaggi per il log.
 * <p>
 * Dalla migrazione a Dungeon come grafo, il flusso di esplorazione
 * si articola in tre API separate (approccio esplicito):
 * <ul>
 *   <li>{@link #advanceToNextRoom()} funziona solo se il nodo corrente
 *       ha un unico successore (corridoi lineari);</li>
 *   <li>{@link #getAvailableChoices()} restituisce le opzioni quando
 *       la stanza corrente è una {@link ChoiceRoom};</li>
 *   <li>{@link #chooseAndAdvance(ChoiceOption)} applica la scelta e
 *       passa al nodo di destinazione.</li>
 * </ul>
 * Ogni metodo ha un contratto chiaro e lancia eccezione se chiamato
 * nel momento sbagliato (fail fast).
 */
public class Game {

    private static final AttackAction ATTACK = new AttackAction();
    private static final DefendAction DEFEND = new DefendAction();

    private final Hero hero;
    private final Dungeon dungeon;
    private GameStatus status;
    private Combat currentCombat;
    private List<ChoiceOption> pendingChoices;

    public Game(Hero hero, Dungeon dungeon) {
        this.hero = Objects.requireNonNull(hero, "hero must not be null");
        this.dungeon = Objects.requireNonNull(dungeon, "dungeon must not be null");
        this.status = GameStatus.EXPLORING;
        this.currentCombat = null;
        this.pendingChoices = List.of();
    }

    // ========== Query ==========

    public Hero getHero() { return hero; }
    public Dungeon getDungeon() { return dungeon; }
    public GameStatus getStatus() { return status; }
    public Room getCurrentRoom() { return dungeon.getCurrentRoom(); }
    public Combat getCurrentCombat() { return currentCombat; }

    /**
     * @return le opzioni disponibili se siamo in uno stato di scelta,
     *         altrimenti lista vuota. Non lancia eccezione: la GUI può
     *         chiamarlo come query a scopo di refresh.
     */
    public List<ChoiceOption> getAvailableChoices() {
        return Collections.unmodifiableList(pendingChoices);
    }

    // ========== Azioni di esplorazione ==========

    /**
     * Fa entrare l'eroe nella stanza corrente e gestisce l'evento conseguente.
     * Lo stato risultante può essere EXPLORING, AWAITING_CHOICE o IN_COMBAT
     * a seconda del tipo di stanza.
     *
     * @return update che descrive l'esito dell'ingresso
     */
    public GameUpdate enterCurrentRoom() {
        if (status != GameStatus.EXPLORING) {
            throw new IllegalStateException(
                    "enterCurrentRoom requires EXPLORING, current is " + status);
        }
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
            case CHOICE_AWAITING -> {
                this.pendingChoices = event.getChoices();
                this.status = GameStatus.AWAITING_CHOICE;
                List<String> msgs = new ArrayList<>();
                msgs.add(event.getMessage());
                for (ChoiceOption opt : pendingChoices) {
                    msgs.add("• " + opt.getLabel() + " — " + opt.getDescription());
                }
                return new GameUpdate(status, msgs);
            }
        }
        throw new IllegalStateException("unhandled RoomEvent type: " + event.getType());
    }

    /**
     * Avanza al nodo successivo quando è univocamente determinato.
     * Non entra nella nuova stanza: la GUI dovrà chiamare poi
     * {@link #enterCurrentRoom()}.
     *
     * @throws IllegalStateException se non si è in EXPLORING, se il
     *         nodo è terminale o se ha più successori (serve
     *         {@link #chooseAndAdvance(ChoiceOption)})
     */
    public GameUpdate advanceToNextRoom() {
        if (status != GameStatus.EXPLORING) {
            throw new IllegalStateException(
                    "advanceToNextRoom requires EXPLORING, current is " + status);
        }
        if (dungeon.isAtTerminalNode()) {
            throw new IllegalStateException("already at a terminal node");
        }
        DungeonNode next = dungeon.advance();
        return new GameUpdate(status,
                hero.getName() + " avanza verso " + next.getRoom().getName() + ".");
    }

    /**
     * Applica una scelta durante una {@link ChoiceRoom}: avanza al nodo
     * di destinazione corrispondente e torna in EXPLORING.
     * La GUI dovrà poi chiamare {@link #enterCurrentRoom()} per
     * attivare la nuova stanza.
     *
     * @throws IllegalStateException se non si è in AWAITING_CHOICE
     * @throws IllegalArgumentException se l'opzione non è tra quelle disponibili
     */
    public GameUpdate chooseAndAdvance(ChoiceOption option) {
        if (status != GameStatus.AWAITING_CHOICE) {
            throw new IllegalStateException(
                    "chooseAndAdvance requires AWAITING_CHOICE, current is " + status);
        }
        Objects.requireNonNull(option, "option must not be null");
        if (!pendingChoices.contains(option)) {
            throw new IllegalArgumentException("option is not among current choices");
        }
        dungeon.advanceTo(option.getDestination());
        this.pendingChoices = List.of();
        this.status = GameStatus.EXPLORING;
        return new GameUpdate(status,
                hero.getName() + " sceglie: " + option.getLabel() + ".");
    }

    // ========== Azioni di combattimento ==========

    public GameUpdate playerAttack()                  { return executePlayerAction(ATTACK); }
    public GameUpdate playerDefend()                  { return executePlayerAction(DEFEND); }
    public GameUpdate playerUseItem(Consumable item)  { return executePlayerAction(new UseItemAction(item)); }
    public GameUpdate playerFlee()                    { return executePlayerAction(new FleeAction()); }

    public GameUpdate enemyTurn() {
        if (status != GameStatus.IN_COMBAT) {
            throw new IllegalStateException("enemyTurn requires IN_COMBAT, current is " + status);
        }
        if (currentCombat.isOver()) {
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
        if (status != GameStatus.IN_COMBAT) {
            throw new IllegalStateException(
                    "player action requires IN_COMBAT, current is " + status);
        }
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

    private void resolveCombatEnd() {
        if (hero.isDead()) {
            this.status = GameStatus.DEFEAT;
            this.currentCombat = null;
            return;
        }
        if (currentCombat.heroWon()) {
            currentCombat.awardRewards();
            // Se era un boss alla fine del dungeon → vittoria
            if (dungeon.isAtTerminalNode()) {
                this.status = GameStatus.VICTORY;
            } else {
                this.status = GameStatus.EXPLORING;
            }
            this.currentCombat = null;
            return;
        }
        // Fuga riuscita
        this.status = GameStatus.EXPLORING;
        this.currentCombat = null;
    }

    // ========== Persistenza ==========

    /**
     * Produce una fotografia serializzabile dello stato corrente.
     * Salva l'id del nodo corrente invece dell'indice (il dungeon
     * è ora un grafo, non più una lista).
     */
    public GameState exportState() {
        GameState s = new GameState();
        s.setHeroName(hero.getName());
        s.setHeroLevel(hero.getLevel());
        s.setHeroExperience(hero.getExperience());
        s.setHeroCurrentHp(hero.getCurrentHp());
        s.setHeroMaxHp(hero.getMaxHp());
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
        s.setCurrentNodeId(dungeon.getCurrentNode().getId());
        return s;
    }

    /**
     * Ricostruisce un {@link Game} da uno stato serializzato.
     * Il dungeon fornito deve avere nodi coerenti con quelli al momento
     * del salvataggio (stessa topologia e stessi id).
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
        hero.restoreFromSave(state.getHeroLevel(), state.getHeroExperience());

        int damage = state.getHeroMaxHp() - state.getHeroCurrentHp();
        if (damage > 0) {
            hero.takeDamage(damage);
        }

        for (String itemName : state.getInventoryItemNames()) {
            Item item = catalog.getByName(itemName)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "unknown item in save: " + itemName));
            hero.getInventory().add(item);
        }

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

        if (state.getCurrentNodeId() != null) {
            dungeon.jumpTo(state.getCurrentNodeId());
        }

        return new Game(hero, dungeon);
    }
}