package it.unicam.cs.mpgc.rpg122627.persistence;

import it.unicam.cs.mpgc.rpg122627.model.item.Armor;
import it.unicam.cs.mpgc.rpg122627.model.item.HealingPotion;
import it.unicam.cs.mpgc.rpg122627.model.item.Item;
import it.unicam.cs.mpgc.rpg122627.model.item.Weapon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Catalogo degli oggetti riconosciuti dal gioco, indicizzati per nome.
 * <p>
 * Serve al caricamento della partita: dato il nome di un oggetto
 * (letto dal save file), restituisce l'istanza corrispondente. In
 * questa versione MVP il catalogo è popolato in modo hardcoded; una
 * futura estensione consiste nel leggerlo da un file JSON di
 * configurazione, senza modificare il resto del sistema (Open/Closed).
 * <p>
 * Il catalogo deve contenere TUTTI gli oggetti che il
 * {@code GameSetup} inserisce nel dungeon o nell'inventario iniziale
 * dell'eroe: se un oggetto trovato in gioco non è nel catalogo, il
 * caricamento di una partita salvata fallirà con un errore. In
 * particolare registra:
 * <ul>
 *   <li>Armi: Pugnale Arrugginito (iniziale), Spada d'Argento (NPC);</li>
 *   <li>Armature: nessuna nel dungeon corrente, ma registrate alcune
 *       voci di esempio per estensioni future;</li>
 *   <li>Pozioni: Minore, Media, Rossa, Maggiore (droppate in varie
 *       stanze del Diamond).</li>
 * </ul>
 */
public class ItemCatalog {

    private final Map<String, Item> items;

    public ItemCatalog() {
        this.items = new HashMap<>();
        registerDefaults();
    }

    private void registerDefaults() {
        // Armi effettivamente usate nel Diamond
        register(new Weapon("Pugnale Arrugginito", 2));
        register(new Weapon("Spada d'Argento", 4));

        // Armi di esempio pronte per estensioni future
        register(new Weapon("Spada Lunga", 3));
        register(new Weapon("Ascia da Battaglia", 5));

        // Armature di esempio pronte per estensioni future
        register(new Armor("Cotta di Maglia", 2));
        register(new Armor("Armatura di Piastre", 4));

        // Pozioni effettivamente usate nel Diamond
        register(new HealingPotion("Pozione Minore", 10));
        register(new HealingPotion("Pozione Media", 15));
        register(new HealingPotion("Pozione Rossa", 15));
        register(new HealingPotion("Pozione Maggiore", 25));
    }

    public void register(Item item) {
        items.put(item.getName(), item);
    }

    /**
     * Restituisce l'istanza dell'oggetto con il nome dato.
     * <p>
     * Nota: ogni chiamata ritorna la stessa istanza del catalogo.
     * Per l'MVP è accettabile perché gli oggetti del nostro gioco
     * sono immutabili dopo la creazione (nome e statistiche non cambiano).
     *
     * @param name nome dell'oggetto
     * @return oggetto corrispondente, se registrato
     */
    public Optional<Item> getByName(String name) {
        return Optional.ofNullable(items.get(name));
    }
}