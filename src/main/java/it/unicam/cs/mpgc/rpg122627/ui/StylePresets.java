package it.unicam.cs.mpgc.rpg122627.ui;

/**
 * Costanti di stile condivise dalla GUI: colori, dimensioni, font.
 * <p>
 * Centralizzando i valori in un unico posto, modificare l'aspetto del
 * gioco richiede toccare un solo file (DRY — Don't Repeat Yourself).
 */
public final class StylePresets {

    private StylePresets() {
        // utility class, no instances
    }

    // Palette "moderno pulito"
    public static final String BG_MAIN = "#1e1e2e";
    public static final String BG_PANEL = "#2a2a3a";
    public static final String TEXT_PRIMARY = "#e0e0e8";
    public static final String TEXT_SECONDARY = "#9090a0";
    public static final String ACCENT = "#7aa2f7";
    public static final String DANGER = "#f7768e";
    public static final String SUCCESS = "#9ece6a";

    // Dimensioni finestra
    public static final double WINDOW_WIDTH = 900;
    public static final double WINDOW_HEIGHT = 650;

    // Spaziature
    public static final double PADDING = 15;
    public static final double SPACING = 10;
}