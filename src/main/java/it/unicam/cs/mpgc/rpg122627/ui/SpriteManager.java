package it.unicam.cs.mpgc.rpg122627.ui;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Registro degli sprite del gioco: carica tutte le immagini una sola volta
 * all'avvio e le espone al resto della UI tramite nome logico.
 * <p>
 * Mappa i nomi dei nemici e degli oggetti del modello (es. "Goblin
 * Esploratore", "Pozione Minore") al file sprite corrispondente nel
 * classpath ({@code /sprites/characters/...} e {@code /sprites/items/...}).
 * Nemici con lo stesso sprite possono condividere il file (nel Diamond
 * i 3 goblin hanno sprite distinti, generati separatamente).
 * <p>
 * Le immagini vengono caricate <em>una sola volta</em> alla costruzione
 * e ridimensionate alla dimensione target per risparmiare memoria:
 * i file originali sono tra 50 e 200 KB, caricati a {@value #CHARACTER_SIZE}
 * px per i personaggi e {@value #ITEM_SIZE} px per gli oggetti.
 * <p>
 * Fallback graceful: se un nome non è mappato, {@link #getCharacter(String)}
 * e {@link #getItem(String)} ritornano {@code null} invece di lanciare,
 * così la GUI può nascondere l'ImageView senza crashare.
 */
public class SpriteManager {

    private static final int CHARACTER_SIZE = 128;
    private static final int ITEM_SIZE = 48;
    private static final int DOOR_SIZE = 150;

    private final Map<String, Image> characterSprites = new HashMap<>();
    private final Map<String, Image> itemSprites = new HashMap<>();
    private final Map<String, Image> doorSprites = new HashMap<>();
    public SpriteManager() {
        loadCharacterSprites();
        loadItemSprites();
        loadDoorSprites();   // ← aggiungi questa riga
    }

    private void loadCharacterSprites() {
        // Eroe
        registerCharacter("Marco l'Intrepido", "hero.png");

        // Goblin: ogni variante ha il proprio sprite
        registerCharacter("Goblin Esploratore", "goblin_scout.png");
        registerCharacter("Goblin Guerriero", "goblin_warrior.png");
        registerCharacter("Goblin Capotribù", "goblin_chief.png");

        // Nemici del ramo sinistro
        registerCharacter("Lupo Mannaro", "wolf.png");
        registerCharacter("Orco Bruto", "orc.png");
        registerCharacter("Scheletro Guerriero", "skeleton.png");
        registerCharacter("Cavaliere Spettrale", "knight.png");

        // Ramo destro
        registerCharacter("Negoziante Errante", "npc.png");

        // Boss finale
        registerCharacter("Drago Rosso Antico", "dragon.png");
    }

    private void loadItemSprites() {
        registerItem("Pozione Minore", "potion_small.png");
        registerItem("Pozione Media", "potion_medium.png");
        registerItem("Pozione Rossa", "potion_medium.png");  // alias stesso sprite
        registerItem("Pozione Maggiore", "potion_large.png");
        registerItem("Pugnale Arrugginito", "dagger.png");
        registerItem("Spada d'Argento", "silver_sword.png");
    }

    private void loadDoorSprites() {
        registerDoor("door_danger", "door_danger.png");
        registerDoor("door_mystery", "door_mystery.png");
    }

    private void registerDoor(String name, String fileName) {
        Image img = loadImage("/sprites/doors/" + fileName, DOOR_SIZE);
        if (img != null) {
            doorSprites.put(name, img);
        }
    }

    private void registerCharacter(String name, String fileName) {
        Image img = loadImage("/sprites/characters/" + fileName, CHARACTER_SIZE);
        if (img != null) {
            characterSprites.put(name, img);
        }
    }

    private void registerItem(String name, String fileName) {
        Image img = loadImage("/sprites/items/" + fileName, ITEM_SIZE);
        if (img != null) {
            itemSprites.put(name, img);
        }
    }

    /**
     * Carica un'immagine dal classpath ridimensionandola al lato richiesto.
     * Mantiene le proporzioni; usa smoothing per qualità migliore.
     *
     * @return l'Image caricata, o {@code null} se il file non esiste
     */
    private Image loadImage(String path, int targetSize) {
        InputStream in = getClass().getResourceAsStream(path);
        if (in == null) {
            System.err.println("[SpriteManager] sprite non trovato: " + path);
            return null;
        }
        // Image(stream, requestedWidth, requestedHeight, preserveRatio, smooth)
        return new Image(in, targetSize, targetSize, true, true);
    }

    /**
     * @param characterName il nome del personaggio come compare nel modello
     * @return l'Image corrispondente, o null se non registrata
     */
    public Image getCharacter(String characterName) {
        return characterSprites.get(characterName);
    }

    /**
     * @param itemName il nome dell'oggetto come compare nel modello
     * @return l'Image corrispondente, o null se non registrata
     */
    public Image getItem(String itemName) {
        return itemSprites.get(itemName);
    }

    /**
     * @param doorName nome logico dell'icona porta (es. "door_danger")
     * @return l'Image corrispondente, o null se non registrata
     */
    public Image getDoor(String doorName) {
        if (doorName == null) return null;
        return doorSprites.get(doorName);
    }
}
