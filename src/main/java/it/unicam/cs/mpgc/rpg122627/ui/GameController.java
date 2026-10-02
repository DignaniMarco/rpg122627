package it.unicam.cs.mpgc.rpg122627.ui;

import it.unicam.cs.mpgc.rpg122627.model.Game;
import it.unicam.cs.mpgc.rpg122627.model.GameStatus;
import it.unicam.cs.mpgc.rpg122627.model.GameUpdate;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.Consumable;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;
import it.unicam.cs.mpgc.rpg122627.persistence.ItemCatalog;
import it.unicam.cs.mpgc.rpg122627.persistence.JsonSaveManager;
import it.unicam.cs.mpgc.rpg122627.persistence.SaveManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller della GUI: gestisce gli input dell'utente, chiama la facciata
 * {@link Game} e aggiorna la {@link GameView}.
 * <p>
 * Il Controller è l'unica classe della GUI che conosce il modello: la View
 * resta "passiva" (solo componenti grafici). Questa separazione permette
 * di sostituire la GUI con un'altra tecnologia (web, mobile, CLI) senza
 * toccare la logica di gioco.
 */
public class GameController {

    private final GameView view;
    private final SaveManager saveManager;
    private final ItemCatalog itemCatalog;

    private Game game;

    public GameController(GameView view) {
        this.view = view;
        this.saveManager = new JsonSaveManager();
        this.itemCatalog = new ItemCatalog();
        wireButtons();
    }

    private void wireButtons() {
        view.getAdvanceButton().setOnAction(e -> onAdvance());
        view.getInventoryButton().setOnAction(e -> onInventory());
        view.getSaveButton().setOnAction(e -> onSave());
        view.getAttackButton().setOnAction(e -> onPlayerAction(game.playerAttack()));
        view.getDefendButton().setOnAction(e -> onPlayerAction(game.playerDefend()));
        view.getUseItemButton().setOnAction(e -> onUseItem());
        view.getFleeButton().setOnAction(e -> onPlayerAction(game.playerFlee()));
        view.getNewGameButton().setOnAction(e -> onNewGame());
        view.getExitButton().setOnAction(e -> Platform.exit());
    }

    /**
     * Avvia una nuova partita (chiamato all'avvio dell'applicazione).
     */
    public void start() {
        if (saveManager.hasSave()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Trovata partita salvata. Vuoi caricarla?");
            alert.setHeaderText(null);
            alert.setTitle("Carica partita");
            Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                tryLoad();
                return;
            }
        }
        startNewGame();
    }

    private void startNewGame() {
        game = GameSetup.newGame("Marco l'Intrepido");
        view.clearLog();
        view.appendToLog("Benvenuto in RPG122627!");
        enterCurrentRoomAndUpdate();
    }

    private void tryLoad() {
        try {
            game = Game.fromState(saveManager.load(), GameSetup.newDungeon(), itemCatalog);
            view.clearLog();
            view.appendToLog("Partita caricata.");
            refreshUI();
        } catch (IOException ex) {
            showError("Impossibile caricare la partita: " + ex.getMessage());
            startNewGame();
        }
    }

    // ========== Handler azioni ==========

    private void onAdvance() {
        GameUpdate update = game.advanceToNextRoom();
        view.appendToLog(update.getJoinedMessage());
        enterCurrentRoomAndUpdate();
    }

    private void enterCurrentRoomAndUpdate() {
        GameUpdate enter = game.enterCurrentRoom();
        for (String msg : enter.getMessages()) view.appendToLog(msg);
        refreshUI();
    }

    private void onPlayerAction(GameUpdate update) {
        for (String msg : update.getMessages()) view.appendToLog(msg);
        refreshUI();
        // Se dopo l'azione dell'eroe siamo ancora in combattimento, tocca al nemico
        if (game.getStatus() == GameStatus.IN_COMBAT) {
            GameUpdate enemyUpdate = game.enemyTurn();
            for (String msg : enemyUpdate.getMessages()) view.appendToLog(msg);
            refreshUI();
        }
    }

    private void onUseItem() {
        Hero hero = game.getHero();
        List<Consumable> consumables = hero.getInventory().getItems().stream()
                .filter(i -> i instanceof Consumable)
                .map(i -> (Consumable) i)
                .toList();
        if (consumables.isEmpty()) {
            showInfo("Non hai oggetti consumabili.");
            return;
        }

        // Costruisco le etichette "nome — descrizione" e le mostro al dialog.
        // Poi, in base all'indice scelto, ricavo l'oggetto Consumable corrispondente.
        List<String> labels = consumables.stream()
                .map(c -> c.getName() + " — " + c.getDescription())
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(labels.getFirst(), labels);
        dialog.setTitle("Usa oggetto");
        dialog.setHeaderText("Scegli un oggetto da usare");
        dialog.setContentText("Oggetto:");

        Optional<String> choice = dialog.showAndWait();
        choice.ifPresent(label -> {
            int index = labels.indexOf(label);
            Consumable selected = consumables.get(index);
            onPlayerAction(game.playerUseItem(selected));
        });
    }

    private void onInventory() {
        Hero hero = game.getHero();
        StringBuilder sb = new StringBuilder("Inventario (" + hero.getInventory().size() + "/"
                + hero.getInventory().getCapacity() + "):\n");
        for (Item item : hero.getInventory().getItems()) {
            sb.append("• ").append(item.getName()).append(" - ").append(item.getDescription())
                    .append("\n");
        }
        showInfo(sb.toString());
    }

    private void onSave() {
        try {
            saveManager.save(game.exportState());
            showInfo("Partita salvata.");
        } catch (IOException ex) {
            showError("Errore nel salvataggio: " + ex.getMessage());
        }
    }

    private void onNewGame() {
        startNewGame();
    }

    // ========== Aggiornamento View ==========

    private void refreshUI() {
        Hero h = game.getHero();
        view.setHeroInfo(formatHeroInfo(h));
        view.setDungeonInfo("Stanza " + (game.getDungeon().getCurrentIndex() + 1)
                + " di " + game.getDungeon().getSize());
        view.setRoomInfo(game.getCurrentRoom().getName(),
                game.getCurrentRoom().getDescription());

        switch (game.getStatus()) {
            case EXPLORING -> {
                view.setEnemyInfo("");
                view.showExplorationButtons(!game.getDungeon().isAtLastRoom());
            }
            case IN_COMBAT -> {
                view.setEnemyInfo(formatEnemyInfo(game.getCurrentCombat().getEnemy()));
                view.showCombatButtons();
            }
            case VICTORY -> {
                view.setEnemyInfo("");
                view.appendToLog(">>> VITTORIA! Hai completato il dungeon. <<<");
                view.showEndButtons();
            }
            case DEFEAT -> {
                view.setEnemyInfo("");
                view.appendToLog(">>> SCONFITTA. L'eroe è caduto. <<<");
                view.showEndButtons();
            }
        }
    }

    /**
     * Formatta le statistiche dell'eroe su più righe, in modo leggibile per la GUI.
     */
    private String formatHeroInfo(Hero h) {
        String weapon = (h.getEquippedWeapon() != null) ? h.getEquippedWeapon().getName() : "—";
        String armor = (h.getEquippedArmor() != null) ? h.getEquippedArmor().getName() : "—";
        return "%s — Livello %d%nHP: %d/%d  |  ATK: %d  |  DEF: %d  |  XP: %d/100%nArma: %s  |  Armatura: %s"
                .formatted(h.getName(), h.getLevel(),
                        h.getCurrentHp(), h.getMaxHp(),
                        h.getAttackDamage(), h.getDefense(),
                        h.getExperience(),
                        weapon, armor);
    }

    /**
     * Formatta le statistiche del nemico in combattimento, mostrando anche ATK, DEF e XP.
     */
    private String formatEnemyInfo(Enemy e) {        return "Nemico: %s%nHP: %d/%d  |  ATK: %d  |  DEF: %d  |  Ricompensa: %d XP"
                .formatted(e.getName(),
                        e.getCurrentHp(), e.getMaxHp(),
                        e.getAttackDamage(), e.getDefense(),
                        e.getXpReward());
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}