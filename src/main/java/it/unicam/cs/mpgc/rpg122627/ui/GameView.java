package it.unicam.cs.mpgc.rpg122627.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Vista principale della GUI: definisce il layout e i componenti grafici,
 * esposti al {@link GameController} che li collega al modello.
 * <p>
 * La View è "passiva": non contiene logica di gioco, solo widget e metodi
 * per aggiornarli. Tutta la logica sta nel Controller (pattern MVC).
 */
public class GameView {

    /**
     * Tipi di messaggio per il log, mappati a colori diversi.
     * Il controller sceglie il tipo in base al contesto; la View decide
     * il colore corrispondente (separazione MVC).
     */
    public enum LogType {
        /** Informazione neutra. */
        INFO,
        /** Evento di combattimento (attacchi, danni). */
        COMBAT,
        /** Oggetto trovato o ricompensa. */
        LOOT,
        /** Scelta disponibile per il giocatore. */
        CHOICE,
        /** Evento positivo importante (level up, vittoria). */
        SUCCESS,
        /** Evento critico (sconfitta, errore). */
        WARNING
    }

    // Pannello superiore - info eroe
    private final Label heroInfoLabel = new Label();
    private final Label dungeonInfoLabel = new Label();
    private final StatusBar heroHpBar = new StatusBar("HP", StatusBar.Mode.HEALTH);
    private final StatusBar heroXpBar = new StatusBar("XP", StatusBar.Mode.PROGRESS);

    // Pannello centrale - stanza + log
    private final Label roomNameLabel = new Label();
    private final Label roomDescriptionLabel = new Label();
    private final TextFlow logFlow = new TextFlow();
    private final ScrollPane logScroll = new ScrollPane(logFlow);

    // Pannello inferiore - bottoni che cambiano
    private final VBox actionPanel = new VBox(StylePresets.SPACING);
    private final Label enemyInfoLabel = new Label();
    private final StatusBar enemyHpBar = new StatusBar("HP Nemico", StatusBar.Mode.HEALTH);
    private final VBox enemyPanel = new VBox(4);

    // Bottoni esplorazione
    private final Button advanceButton = new Button("➡  Avanza");
    private final Button inventoryButton = new Button("🎒  Inventario");
    private final Button equipmentButton = new Button("🛡  Equipaggiamento");
    private final Button saveButton = new Button("💾  Salva partita");

    private final Button attackButton = new Button("⚔  Attacca");
    private final Button defendButton = new Button("🛡  Difenditi");
    private final Button useItemButton = new Button("🧪  Usa oggetto");
    private final Button fleeButton = new Button("🏃  Fuggi");

    private final Button newGameButton = new Button("🔄  Nuova partita");
    private final Button exitButton = new Button("❌  Esci");
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
        // Riga 1: info eroe (nome, livello, stats testuali)
        // Riga 2: barra HP
        // Riga 3: barra XP
        // Riga 4: info dungeon
        box.getChildren().addAll(heroInfoLabel, heroHpBar, heroXpBar, dungeonInfoLabel);
        return box;
    }

    private VBox buildCenterPanel() {
        VBox box = new VBox(StylePresets.SPACING);
        roomNameLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        roomDescriptionLabel.setFont(Font.font("SansSerif", 13));
        roomDescriptionLabel.setWrapText(true);

        logFlow.setStyle("-fx-background-color: " + StylePresets.BG_PANEL
                + "; -fx-padding: 8;");
        logFlow.setPrefWidth(600);

        logScroll.setFitToWidth(true);
        logScroll.setPrefHeight(250);
        logScroll.setStyle("-fx-background: " + StylePresets.BG_PANEL
                + "; -fx-background-color: transparent;");
        logScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        logScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Label diaryLabel = new Label("📜 Diario");
        diaryLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        diaryLabel.setStyle("-fx-text-fill: " + StylePresets.TEXT_SECONDARY + ";");
        box.getChildren().addAll(roomNameLabel, roomDescriptionLabel,
                diaryLabel, logScroll);
        return box;
    }

    private VBox buildBottomPanel() {
        VBox box = new VBox(StylePresets.SPACING);
        box.setPadding(new Insets(StylePresets.PADDING, 0, 0, 0));
        box.setAlignment(Pos.CENTER);

        enemyInfoLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        enemyInfoLabel.setWrapText(true);

        // Pannello nemico: nome/stat + barra HP, mostrato solo in combat
        enemyPanel.setAlignment(Pos.CENTER);
        enemyPanel.getChildren().addAll(enemyInfoLabel, enemyHpBar);
        enemyPanel.setVisible(false);
        enemyPanel.setManaged(false);

        box.getChildren().addAll(enemyPanel, actionPanel);
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
        for (Button b : new Button[]{advanceButton, inventoryButton, equipmentButton, saveButton,
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

    /**
     * Aggiorna le barre HP e XP dell'eroe.
     */
    public void updateHeroBars(int currentHp, int maxHp, int currentXp, int xpPerLevel) {
        heroHpBar.update(currentHp, maxHp);
        heroXpBar.update(currentXp, xpPerLevel);
    }

    /**
     * Aggiorna la barra HP del nemico. Da chiamare solo durante il combat;
     * la visibilità del pannello si controlla con {@link #setEnemyPanelVisible(boolean)}.
     */
    public void updateEnemyBar(int currentHp, int maxHp) {
        enemyHpBar.update(currentHp, maxHp);
    }

    /**
     * Mostra o nasconde il pannello nemico (nome + barra HP).
     * Il nemico esiste solo durante il combat.
     */
    public void setEnemyPanelVisible(boolean visible) {
        enemyPanel.setVisible(visible);
        enemyPanel.setManaged(visible);
    }

    /**
     * Aggiunge un messaggio al log con stile INFO (default).
     */
    public void appendToLog(String message) {
        appendToLog(message, LogType.INFO);
    }

    /**
     * Aggiunge un messaggio al log con il colore corrispondente al tipo.
     */
    public void appendToLog(String message, LogType type) {
        String prefixed = iconForType(type) + " " + message + "\n";
        Text text = new Text(prefixed);
        text.setFill(colorForType(type));
        text.setFont(Font.font("Monospaced", 12));
        logFlow.getChildren().add(text);
        javafx.application.Platform.runLater(() -> logScroll.setVvalue(1.0));
    }

    private String iconForType(LogType type) {
        return switch (type) {
            case INFO    -> "·";
            case COMBAT  -> "⚔️";
            case LOOT    -> "💰";
            case CHOICE  -> "🔀";
            case SUCCESS -> "✨";
            case WARNING -> "💀";
        };
    }

    public void clearLog() {
        logFlow.getChildren().clear();
    }

    private Color colorForType(LogType type) {
        return switch (type) {
            case INFO    -> Color.web("#E0E0E0");  // grigio chiaro
            case COMBAT  -> Color.web("#EF5350");  // rosso corallo
            case LOOT    -> Color.web("#66BB6A");  // verde
            case CHOICE  -> Color.web("#FFCA28");  // giallo ambra
            case SUCCESS -> Color.web("#42A5F5");  // azzurro brillante
            case WARNING -> Color.web("#FF7043");  // arancio scuro
        };
    }

    public void showExplorationButtons(boolean canAdvance) {
        actionPanel.getChildren().clear();
        advanceButton.setDisable(!canAdvance);
        HBox row1 = new HBox(StylePresets.SPACING, advanceButton, inventoryButton);
        HBox row2 = new HBox(StylePresets.SPACING, equipmentButton, saveButton);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);
        actionPanel.getChildren().addAll(row1, row2);
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
    public Button getEquipmentButton() { return equipmentButton; }
    public Button getSaveButton() { return saveButton; }
    public Button getAttackButton() { return attackButton; }
    public Button getDefendButton() { return defendButton; }
    public Button getUseItemButton() { return useItemButton; }
    public Button getFleeButton() { return fleeButton; }
    public Button getNewGameButton() { return newGameButton; }
    public Button getExitButton() { return exitButton; }
}