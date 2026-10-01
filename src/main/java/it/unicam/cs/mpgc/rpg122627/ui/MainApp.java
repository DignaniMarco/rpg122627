package it.unicam.cs.mpgc.rpg122627.ui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Entry point JavaFX dell'applicazione.
 * <p>
 * Istanzia la {@link GameView} e il {@link GameController}, poi delega
 * tutto al controller.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameView view = new GameView();
        GameController controller = new GameController(view, primaryStage);

        primaryStage.setTitle("RPG122627 — Dungeon Crawler");
        primaryStage.setScene(view.buildScene());
        primaryStage.show();

        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}