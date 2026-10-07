package it.unicam.cs.mpgc.rpg122627.ui;

import it.unicam.cs.mpgc.rpg122627.model.Game;
import it.unicam.cs.mpgc.rpg122627.model.GameStatus;
import it.unicam.cs.mpgc.rpg122627.model.GameUpdate;
import it.unicam.cs.mpgc.rpg122627.model.character.Enemy;
import it.unicam.cs.mpgc.rpg122627.model.character.Hero;
import it.unicam.cs.mpgc.rpg122627.model.item.Consumable;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;
import it.unicam.cs.mpgc.rpg122627.model.world.ChoiceOption;
import it.unicam.cs.mpgc.rpg122627.persistence.ItemCatalog;
import it.unicam.cs.mpgc.rpg122627.persistence.JsonSaveManager;
import it.unicam.cs.mpgc.rpg122627.persistence.SaveManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller della GUI: gestisce gli input dell'utente, chiama la facciata
 * {@link Game} e aggiorna la {@link GameView}.
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
        view.getEquipmentButton().setOnAction(e -> onEquipment());
        view.getSaveButton().setOnAction(e -> onSave());
        view.getAttackButton().setOnAction(e -> onPlayerAction(game.playerAttack()));
        view.getDefendButton().setOnAction(e -> onPlayerAction(game.playerDefend()));
        view.getUseItemButton().setOnAction(e -> onUseItem());
        view.getFleeButton().setOnAction(e -> onPlayerAction(game.playerFlee()));
        view.getNewGameButton().setOnAction(e -> onNewGame());
        view.getExitButton().setOnAction(e -> Platform.exit());
    }

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
        if (game.getStatus() == GameStatus.IN_COMBAT) {
            GameUpdate enemyUpdate = game.enemyTurn();
            for (String msg : enemyUpdate.getMessages()) view.appendToLog(msg);
            refreshUI();
        }
    }

    private void onChoiceSelected(ChoiceOption option) {
        GameUpdate update = game.chooseAndAdvance(option);
        view.appendToLog(update.getJoinedMessage());
        enterCurrentRoomAndUpdate();
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

    private void onEquipment() {
        Hero hero = game.getHero();

        List<Weapon> weapons = hero.getInventory().getItems().stream()
                .filter(i -> i instanceof Weapon)
                .map(i -> (Weapon) i)
                .toList();
        List<Armor> armors = hero.getInventory().getItems().stream()
                .filter(i -> i instanceof Armor)
                .map(i -> (Armor) i)
                .toList();

        if (weapons.isEmpty() && armors.isEmpty()) {
            showInfo("Non hai armi né armature nell'inventario.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Equipaggiamento");
        dialog.setHeaderText("Scegli cosa equipaggiare");

        ButtonType applyType = new ButtonType("Applica", ButtonData.OK_DONE);
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(applyType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        // Riga 0: arma attuale
        Weapon currentWeapon = hero.getEquippedWeapon();
        String currentWeaponLabel = (currentWeapon != null)
                ? currentWeapon.getName() + " (+" + currentWeapon.getAttackBonus() + " ATK)"
                : "nessuna";
        grid.add(new Label("Arma attuale:"), 0, 0);
        grid.add(new Label(currentWeaponLabel), 1, 0);

        // Riga 1: dropdown arma
        ComboBox<Weapon> weaponBox = new ComboBox<>();
        weaponBox.getItems().addAll(weapons);
        weaponBox.setDisable(weapons.isEmpty());
        weaponBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Weapon w, boolean empty) {
                super.updateItem(w, empty);
                setText(empty || w == null ? null : w.getName() + " (+" + w.getAttackBonus() + " ATK)");
            }
        });
        weaponBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Weapon w, boolean empty) {
                super.updateItem(w, empty);
                setText(empty || w == null ? "Scegli un'arma..." : w.getName() + " (+" + w.getAttackBonus() + " ATK)");
            }
        });
        grid.add(new Label("Nuova arma:"), 0, 1);
        grid.add(weaponBox, 1, 1);

        // Riga 2: armatura attuale
        Armor currentArmor = hero.getEquippedArmor();
        String currentArmorLabel = (currentArmor != null)
                ? currentArmor.getName() + " (+" + currentArmor.getDefenseBonus() + " DEF)"
                : "nessuna";
        grid.add(new Label("Armatura attuale:"), 0, 2);
        grid.add(new Label(currentArmorLabel), 1, 2);

        // Riga 3: dropdown armatura
        ComboBox<Armor> armorBox = new ComboBox<>();
        armorBox.getItems().addAll(armors);
        armorBox.setDisable(armors.isEmpty());
        armorBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Armor a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getName() + " (+" + a.getDefenseBonus() + " DEF)");
            }
        });
        armorBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Armor a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? "Scegli un'armatura..." : a.getName() + " (+" + a.getDefenseBonus() + " DEF)");
            }
        });
        grid.add(new Label("Nuova armatura:"), 0, 3);
        grid.add(armorBox, 1, 3);

        pane.setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == applyType) {
            Weapon chosenWeapon = weaponBox.getValue();
            Armor chosenArmor = armorBox.getValue();
            if (chosenWeapon != null && chosenWeapon != currentWeapon) {
                hero.equipWeapon(chosenWeapon);
                view.appendToLog(hero.getName() + " equipaggia " + chosenWeapon.getName() + ".");
            }
            if (chosenArmor != null && chosenArmor != currentArmor) {
                hero.equipArmor(chosenArmor);
                view.appendToLog(hero.getName() + " equipaggia " + chosenArmor.getName() + ".");
            }
            refreshUI();
        }
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
        view.setDungeonInfo("Nodo: " + game.getDungeon().getCurrentNode().getId()
                + "   (" + game.getDungeon().getSize() + " nodi totali)");
        view.setRoomInfo(game.getCurrentRoom().getName(),
                game.getCurrentRoom().getDescription());

        switch (game.getStatus()) {
            case EXPLORING -> {
                view.setEnemyInfo("");
                boolean canAdvance = !game.getDungeon().isAtTerminalNode();
                view.showExplorationButtons(canAdvance);
            }
            case AWAITING_CHOICE -> {
                view.setEnemyInfo("");
                List<Button> buttons = new ArrayList<>();
                for (ChoiceOption opt : game.getAvailableChoices()) {
                    Button b = new Button(opt.getLabel() + "\n" + opt.getDescription());
                    b.setOnAction(e -> onChoiceSelected(opt));
                    buttons.add(b);
                }
                view.showChoiceButtons(buttons);
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

    private String formatHeroInfo(Hero h) {
        String weapon = (h.getEquippedWeapon() != null) ? h.getEquippedWeapon().getName() : "—";
        String armor = (h.getEquippedArmor() != null) ? h.getEquippedArmor().getName() : "—";
        return "%s — Livello %d%nHP: %d/%d  |  ATK: %d  |  DEF: %d  |  XP: %d/%d%nArma: %s  |  Armatura: %s"
                .formatted(h.getName(), h.getLevel(),
                        h.getCurrentHp(), h.getMaxHp(),
                        h.getAttackDamage(), h.getDefense(),
                        h.getExperience(), h.getXpPerLevel(),
                        weapon, armor);
    }

    private String formatEnemyInfo(Enemy e) {
        return "Nemico: %s%nHP: %d/%d  |  ATK: %d  |  DEF: %d  |  Ricompensa: %d XP"
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