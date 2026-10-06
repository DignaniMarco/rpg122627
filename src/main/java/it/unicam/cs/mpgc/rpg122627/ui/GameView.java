package it.unicam.cs.mpgc.rpg122627.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

/**
 * Vista principale della GUI: definisce il layout e i componenti grafici,
 * esposti al {@link GameController} che li collega al modello.
 * <p>
 * La View è "passiva": non contiene logica di gioco, solo widget e metodi
 * per aggiornarli. Tutta la logica sta nel Controller (pattern MVC).
 */
public class GameView {

    // Pannello superiore - info eroe
    private final Label heroInfoLabel = new Label();
    private final Label dungeonInfoLabel = new Label();

    // Pannello centrale - stanza + log
    private final Label roomNameLabel = new Label();
    private final Label roomDescriptionLabel = new Label();
    private final TextArea logArea = new TextArea();

    // Pannello inferiore - bottoni che cambiano
    private final VBox actionPanel = new VBox(StylePresets.SPACING);
    private final Label enemyInfoLabel = new Label();

    // Bottoni esplorazione
    private final Button advanceButton = new Button("Avanza alla prossima stanza");
    private final Button inventoryButton = new Button("Inventario");
    private final Button saveButton = new Button("Salva partita");

    // Bottoni combattimento
    private final Button attackButton = new Button("Attacca");
    private final Button defendButton = new Button("Difenditi");
    private final Button useItemButton = new Button("Usa oggetto");
    private final Button fleeButton = new Button("Fuggi");

    // Bottoni fine partita
    private final Button newGameButton = new Button("Nuova partita");
    private final Button exitButton = new Button("Esci");

    private final BorderPane root = new BorderPane();

    public GameView() {
        buildLayout();
        applyStyles();
    }

    private void buildLayout() {
        root.setPadding(new Insets(StylePresets.PADDING));
        root.setStyle("-fx-background-color: " + StylePresets.BG_MAIN + ";");

        root.setTop(buildTopPanel());
        root.setCenter(buildCenterPanel());
        root.setBottom(buildBottomPanel());
    }

    private VBox buildTopPanel() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(0, 0, StylePresets.PADDING, 0));
        heroInfoLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        dungeonInfoLabel.setFont(Font.font("Monospaced", 12));
        heroInfoLabel.setWrapText(true);
        dungeonInfoLabel.setWrapText(true);
        box.getChildren().addAll(heroInfoLabel, dungeonInfoLabel);
        return box;
    }

    private VBox buildCenterPanel() {
        VBox box = new VBox(StylePresets.SPACING);
        roomNameLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        roomDescriptionLabel.setFont(Font.font("SansSerif", 13));
        roomDescriptionLabel.setWrapText(true);

        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(12);
        logArea.setFont(Font.font("Monospaced", 12));

        box.getChildren().addAll(roomNameLabel, roomDescriptionLabel,
                new Label("— Diario —"), logArea);
        return box;
    }

    private VBox buildBottomPanel() {
        VBox box = new VBox(StylePresets.SPACING);
        box.setPadding(new Insets(StylePresets.PADDING, 0, 0, 0));
        box.setAlignment(Pos.CENTER);
        enemyInfoLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        enemyInfoLabel.setWrapText(true);
        box.getChildren().addAll(enemyInfoLabel, actionPanel);
        return box;
    }

    private void applyStyles() {
        String labelStyle = "-fx-text-fill: " + StylePresets.TEXT_PRIMARY + ";";
        heroInfoLabel.setStyle(labelStyle);
        dungeonInfoLabel.setStyle("-fx-text-fill: " + StylePresets.TEXT_SECONDARY + ";");
        roomNameLabel.setStyle("-fx-text-fill: " + StylePresets.ACCENT + ";");
        roomDescriptionLabel.setStyle(labelStyle);
        enemyInfoLabel.setStyle("-fx-text-fill: " + StylePresets.DANGER + ";");

        String buttonStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 8 16; -fx-cursor: hand;",
                StylePresets.BG_PANEL, StylePresets.TEXT_PRIMARY);
        for (Button b : new Button[]{advanceButton, inventoryButton, saveButton,
                attackButton, defendButton, useItemButton, fleeButton,
                newGameButton, exitButton}) {
            b.setStyle(buttonStyle);
            b.setMinWidth(220);
        }
    }

    // ========== API per il controller ==========

    public Scene buildScene() {
        return new Scene(root, StylePresets.WINDOW_WIDTH, StylePresets.WINDOW_HEIGHT);
    }

    public void setHeroInfo(String text) {
        heroInfoLabel.setText(text);
    }

    public void setDungeonInfo(String text) {
        dungeonInfoLabel.setText(text);
    }

    public void setRoomInfo(String name, String description) {
        roomNameLabel.setText(name);
        roomDescriptionLabel.setText(description);
    }

    public void setEnemyInfo(String text) {
        enemyInfoLabel.setText(text);
    }

    public void appendToLog(String message) {
        logArea.appendText(message + "\n");
    }

    public void clearLog() {
        logArea.clear();
    }

    public void showExplorationButtons(boolean canAdvance) {
        actionPanel.getChildren().clear();
        advanceButton.setDisable(!canAdvance);
        HBox row = new HBox(StylePresets.SPACING, advanceButton, inventoryButton, saveButton);
        row.setAlignment(Pos.CENTER);
        actionPanel.getChildren().add(row);
    }

    public void showCombatButtons() {
        actionPanel.getChildren().clear();
        HBox row1 = new HBox(StylePresets.SPACING, attackButton, defendButton);
        HBox row2 = new HBox(StylePresets.SPACING, useItemButton, fleeButton);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);
        actionPanel.getChildren().addAll(row1, row2);
    }

    public void showEndButtons() {
        actionPanel.getChildren().clear();
        HBox row = new HBox(StylePresets.SPACING, newGameButton, exitButton);
        row.setAlignment(Pos.CENTER);
        actionPanel.getChildren().add(row);
    }

    /**
     * Mostra una scelta tra più opzioni (per le ChoiceRoom).
     * Il controller fornisce le etichette; i click vengono gestiti via callback.
     */
    public void showChoiceButtons(List<javafx.scene.control.Button> optionButtons) {
        actionPanel.getChildren().clear();
        VBox box = new VBox(StylePresets.SPACING);
        box.setAlignment(Pos.CENTER);
        for (javafx.scene.control.Button b : optionButtons) {
            String buttonStyle = String.format(
                    "-fx-background-color: %s; -fx-text-fill: %s; -fx-padding: 8 16; -fx-cursor: hand;",
                    StylePresets.BG_PANEL, StylePresets.TEXT_PRIMARY);
            b.setStyle(buttonStyle);
            b.setMinWidth(400);
            b.setWrapText(true);
            box.getChildren().add(b);
        }
        actionPanel.getChildren().add(box);
    }

    // Getter dei bottoni (per il controller)
    public Button getAdvanceButton() { return advanceButton; }
    public Button getInventoryButton() { return inventoryButton; }
    public Button getSaveButton() { return saveButton; }
    public Button getAttackButton() { return attackButton; }
    public Button getDefendButton() { return defendButton; }
    public Button getUseItemButton() { return useItemButton; }
    public Button getFleeButton() { return fleeButton; }
    public Button getNewGameButton() { return newGameButton; }
    public Button getExitButton() { return exitButton; }
}