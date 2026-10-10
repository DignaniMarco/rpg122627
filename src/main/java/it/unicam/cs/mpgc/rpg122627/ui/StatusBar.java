package it.unicam.cs.mpgc.rpg122627.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Barra di stato riutilizzabile per visualizzare valori come HP e XP.
 * <p>
 * Composita: contiene un'etichetta (es. "HP"), una progress bar e un
 * valore testuale sovrapposto (es. "25/35") per leggibilità.
 * <p>
 * Il colore della barra è scelto automaticamente in base alla
 * percentuale: la logica di scelta vive qui, nella View, e non nel
 * modello (separazione MVC).
 */
public class StatusBar extends VBox {

    /** Soglia sopra la quale la barra è verde (frazione del massimo). */
    private static final double HIGH_THRESHOLD = 0.60;
    /** Soglia sotto la quale la barra è rossa (frazione del massimo). */
    private static final double LOW_THRESHOLD = 0.30;

    private static final String COLOR_HIGH = "#4CAF50";   // verde
    private static final String COLOR_MID  = "#FFC107";   // giallo/ambra
    private static final String COLOR_LOW  = "#F44336";   // rosso
    private static final String COLOR_XP   = "#2196F3";   // blu (XP = non ha ratio critico)

    /** Modalità di colorazione della barra. */
    public enum Mode {
        /** Verde/giallo/rosso in base alla soglia (per HP). */
        HEALTH,
        /** Sempre blu (per XP e simili). */
        PROGRESS
    }

    private final Label labelText;
    private final ProgressBar progressBar;
    private final Label valueText;
    private final Mode mode;

    public StatusBar(String labelName, Mode mode) {
        this.mode = mode;
        this.labelText = new Label(labelName);
        this.progressBar = new ProgressBar(0.0);
        this.valueText = new Label("0/0");

        buildLayout();
    }

    private void buildLayout() {
        setSpacing(2);
        setFillWidth(true);

        labelText.setFont(Font.font("SansSerif", FontWeight.BOLD, 11));
        labelText.setStyle("-fx-text-fill: #E0E0E0;");

        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(16);

        valueText.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
        valueText.setStyle("-fx-text-fill: white;");

        // ProgressBar e valore sovrapposti con StackPane
        StackPane barStack = new StackPane(progressBar, valueText);
        StackPane.setAlignment(valueText, Pos.CENTER);

        getChildren().addAll(labelText, barStack);
    }

    /**
     * Aggiorna la barra con nuovi valori.
     *
     * @param current valore attuale (es. HP correnti)
     * @param max     valore massimo (es. HP massimi)
     */
    public void update(int current, int max) {
        if (max <= 0) {
            progressBar.setProgress(0.0);
            valueText.setText("0/0");
            return;
        }
        double ratio = (double) current / max;
        progressBar.setProgress(ratio);
        valueText.setText(current + "/" + max);
        progressBar.setStyle("-fx-accent: " + chooseColor(ratio) + ";");
    }

    private String chooseColor(double ratio) {
        if (mode == Mode.PROGRESS) {
            return COLOR_XP;
        }
        // Mode.HEALTH
        if (ratio > HIGH_THRESHOLD) return COLOR_HIGH;
        if (ratio < LOW_THRESHOLD) return COLOR_LOW;
        return COLOR_MID;
    }
}