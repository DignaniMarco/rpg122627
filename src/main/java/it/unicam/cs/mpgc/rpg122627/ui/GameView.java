package it.unicam.cs.mpgc.rpg122627.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Objects;

/**
 * Vista principale della GUI: definisce il layout e i componenti grafici,
 * esposti al {@link GameController} che li collega al modello.
 * <p>
 * La View è "passiva": non contiene logica di gioco, solo widget e metodi
 * per aggiornarli. Tutta la logica sta nel Controller (pattern MVC).
 */
public class GameView {

    public enum LogType {
        INFO, COMBAT, LOOT, CHOICE, SUCCESS, WARNING
    }

    private static final double HERO_SPRITE_SIZE = 96;
    private static final double ENEMY_SPRITE_SIZE = 160;

    // Sprite manager (carica le immagini una sola volta all'avvio)
    private final SpriteManager spriteManager = new SpriteManager();

    // Pannello superiore - info eroe
    private final Label heroInfoLabel = new Label();
    private final Label dungeonInfoLabel = new Label();
    private final StatusBar heroHpBar = new StatusBar("HP", StatusBar.Mode.HEALTH);
    private final StatusBar heroXpBar = new StatusBar("XP", StatusBar.Mode.PROGRESS);
    private final ImageView heroSprite = new ImageView();

    // Pannello centrale - stanza + log
    private final Label roomNameLabel = new Label();
    private final Label roomDescriptionLabel = new Label();
    private final TextFlow logFlow = new TextFlow();
    private final ScrollPane logScroll = new ScrollPane(logFlow);

    // Pannello inferiore - bottoni che cambiano
    private final VBox actionPanel = new VBox(StylePresets.SPACING);
    private final Label enemyInfoLabel = new Label();
    private final StatusBar enemyHpBar = new StatusBar("HP Nemico", StatusBar.Mode.HEALTH);
    private final ImageView enemySprite = new ImageView();
    private final HBox enemyPanel = new HBox(StylePresets.SPACING);

    // Bottoni esplorazione
    private final Button advanceButton = new Button("➡  Avanza");
    private final Button inventoryButton = new Button("🎒  Inventario");
    private final Button equipmentButton = new Button("🛡  Equipaggiamento");
    private final Button saveButton = new Button("💾  Salva partita");

    // Bottoni combattimento
    private final Button attackButton = new Button("⚔  Attacca");
    private final Button defendButton = new Button("🛡  Difenditi");
    private final Button useItemButton = new Button("🧪  Usa oggetto");
    private final Button fleeButton = new Button("🏃  Fuggi");

    // Bottoni fine partita
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

    private HBox buildTopPanel() {
        // Sprite dell'eroe a sinistra, info testuali e barre a destra
        heroSprite.setFitWidth(HERO_SPRITE_SIZE);
        heroSprite.setFitHeight(HERO_SPRITE_SIZE);
        heroSprite.setPreserveRatio(true);
        heroSprite.setSmooth(true);

        heroInfoLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        dungeonInfoLabel.setFont(Font.font("Monospaced", 12));
        heroInfoLabel.setWrapText(true);
        dungeonInfoLabel.setWrapText(true);

        VBox infoBox = new VBox(5, heroInfoLabel, heroHpBar, heroXpBar, dungeonInfoLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        HBox box = new HBox(StylePresets.SPACING, heroSprite, infoBox);
        box.setPadding(new Insets(0, 0, StylePresets.PADDING, 0));
        box.setAlignment(Pos.CENTER_LEFT);
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

        enemySprite.setFitWidth(ENEMY_SPRITE_SIZE);
        enemySprite.setFitHeight(ENEMY_SPRITE_SIZE);
        enemySprite.setPreserveRatio(true);
        enemySprite.setSmooth(true);

        // Info + barra a sinistra, sprite nemico a destra
        VBox enemyInfoBox = new VBox(4, enemyInfoLabel, enemyHpBar);
        enemyInfoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(enemyInfoBox, Priority.ALWAYS);

        enemyPanel.setAlignment(Pos.CENTER);
        enemyPanel.getChildren().addAll(enemyInfoBox, enemySprite);
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

        for (Button b : new Button[]{advanceButton, inventoryButton, equipmentButton, saveButton,
                attackButton, defendButton, useItemButton, fleeButton,
                newGameButton, exitButton}) {
            b.setMinWidth(220);
        }
    }

    // ========== API per il controller ==========

    public Scene buildScene() {
        Scene scene = new Scene(root, StylePresets.WINDOW_WIDTH, StylePresets.WINDOW_HEIGHT);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/css/styles.css"),
                        "Stylesheet /css/styles.css not found on classpath"
                ).toExternalForm());
        return scene;
    }

    public void setHeroInfo(String text)       { heroInfoLabel.setText(text); }
    public void setDungeonInfo(String text)    { dungeonInfoLabel.setText(text); }
    public void setRoomInfo(String name, String description) {
        roomNameLabel.setText(name);
        roomDescriptionLabel.setText(description);
    }
    public void setEnemyInfo(String text)      { enemyInfoLabel.setText(text); }

    public void updateHeroBars(int currentHp, int maxHp, int currentXp, int xpPerLevel) {
        heroHpBar.update(currentHp, maxHp);
        heroXpBar.update(currentXp, xpPerLevel);
    }

    public void updateEnemyBar(int currentHp, int maxHp) {
        enemyHpBar.update(currentHp, maxHp);
    }

    public void setEnemyPanelVisible(boolean visible) {
        enemyPanel.setVisible(visible);
        enemyPanel.setManaged(visible);
    }

    /**
     * Aggiorna lo sprite dell'eroe dato il suo nome logico.
     * Se lo sprite non è registrato nel {@link SpriteManager}, nasconde l'ImageView.
     */
    public void setHeroSpriteByName(String name) {
        Image img = spriteManager.getCharacter(name);
        heroSprite.setImage(img);
        heroSprite.setVisible(img != null);
        heroSprite.setManaged(img != null);
    }

    /**
     * Aggiorna lo sprite del nemico dato il suo nome logico.
     * Se lo sprite non è registrato, nasconde l'ImageView.
     */
    public void setEnemySpriteByName(String name) {
        Image img = spriteManager.getCharacter(name);
        enemySprite.setImage(img);
        enemySprite.setVisible(img != null);
        enemySprite.setManaged(img != null);
    }

    public void appendToLog(String message) {
        appendToLog(message, LogType.INFO);
    }

    public void appendToLog(String message, LogType type) {
        String prefixed = iconForType(type) + " " + message + "\n";
        Text text = new Text(prefixed);
        text.setFill(colorForType(type));
        text.setFont(Font.font("Monospaced", 12));
        logFlow.getChildren().add(text);
        javafx.application.Platform.runLater(() -> logScroll.setVvalue(1.0));
    }

    public void clearLog() {
        logFlow.getChildren().clear();
    }

    private Color colorForType(LogType type) {
        return switch (type) {
            case INFO    -> Color.web("#E0E0E0");
            case COMBAT  -> Color.web("#EF5350");
            case LOOT    -> Color.web("#66BB6A");
            case CHOICE  -> Color.web("#FFCA28");
            case SUCCESS -> Color.web("#42A5F5");
            case WARNING -> Color.web("#FF7043");
        };
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

    public void showChoiceButtons(List<Button> optionButtons) {
        actionPanel.getChildren().clear();
        VBox box = new VBox(StylePresets.SPACING);
        box.setAlignment(Pos.CENTER);
        for (Button b : optionButtons) {
            b.setMinWidth(400);
            b.setWrapText(true);
            box.getChildren().add(b);
        }
        actionPanel.getChildren().add(box);
    }

    // Getter dei bottoni
    public Button getAdvanceButton()    { return advanceButton; }
    public Button getInventoryButton()  { return inventoryButton; }
    public Button getEquipmentButton()  { return equipmentButton; }
    public Button getSaveButton()       { return saveButton; }
    public Button getAttackButton()     { return attackButton; }
    public Button getDefendButton()     { return defendButton; }
    public Button getUseItemButton()    { return useItemButton; }
    public Button getFleeButton()       { return fleeButton; }
    public Button getNewGameButton()    { return newGameButton; }
    public Button getExitButton()       { return exitButton; }
}