package it.unicam.cs.mpgc.rpg122627.persistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Fotografia serializzabile dello stato di una partita.
 * <p>
 * Oggetto puramente dati (DTO - Data Transfer Object), pensato per
 * essere letto/scritto da {@link SaveManager}. Contiene lo stato
 * dinamico della partita (eroe, posizione nel dungeon, inventario,
 * equipaggiamento) ma non le definizioni statiche (statistiche dei
 * nemici, descrizioni delle stanze, ecc.), che restano nei file di
 * configurazione del gioco e vengono ricombinate al caricamento.
 * <p>
 * Campi pubblici e setter sono presenti per compatibilità con
 * {@code Jackson}, che richiede getter/setter o campi accessibili
 * per la deserializzazione.
 */
public class GameState {

    // --- Eroe ---
    private String heroName;
    private int heroLevel;
    private int heroExperience;
    private int heroCurrentHp;
    private int heroMaxHp;
    private int heroBaseAttack;
    private int heroBaseDefense;

    // --- Inventario (nomi degli oggetti) ---
    private List<String> inventoryItemNames = new ArrayList<>();

    // --- Equipaggiamento (nomi, null se niente equipaggiato) ---
    private String equippedWeaponName;
    private String equippedArmorName;

    // --- Dungeon ---
    private String currentNodeId;
    // Costruttore vuoto richiesto da Jackson
    public GameState() {
    }

    // --- Getters e setters ---

    public String getHeroName() { return heroName; }
    public void setHeroName(String heroName) { this.heroName = heroName; }

    public int getHeroLevel() { return heroLevel; }
    public void setHeroLevel(int heroLevel) { this.heroLevel = heroLevel; }

    public int getHeroExperience() { return heroExperience; }
    public void setHeroExperience(int heroExperience) { this.heroExperience = heroExperience; }

    public int getHeroCurrentHp() { return heroCurrentHp; }
    public void setHeroCurrentHp(int heroCurrentHp) { this.heroCurrentHp = heroCurrentHp; }

    public int getHeroMaxHp() { return heroMaxHp; }
    public void setHeroMaxHp(int heroMaxHp) { this.heroMaxHp = heroMaxHp; }

    public int getHeroBaseAttack() { return heroBaseAttack; }
    public void setHeroBaseAttack(int heroBaseAttack) { this.heroBaseAttack = heroBaseAttack; }

    public int getHeroBaseDefense() { return heroBaseDefense; }
    public void setHeroBaseDefense(int heroBaseDefense) { this.heroBaseDefense = heroBaseDefense; }

    public List<String> getInventoryItemNames() { return inventoryItemNames; }
    public void setInventoryItemNames(List<String> inventoryItemNames) {
        this.inventoryItemNames = inventoryItemNames;
    }

    public String getEquippedWeaponName() { return equippedWeaponName; }
    public void setEquippedWeaponName(String equippedWeaponName) {
        this.equippedWeaponName = equippedWeaponName;
    }

    public String getEquippedArmorName() { return equippedArmorName; }
    public void setEquippedArmorName(String equippedArmorName) {
        this.equippedArmorName = equippedArmorName;
    }

    public String getCurrentNodeId() { return currentNodeId; }
    public void setCurrentNodeId(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }
}