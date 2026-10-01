package it.unicam.cs.mpgc.rpg122627.model.item;

/**
 * Qualsiasi oggetto che possa stare in un {@link Inventory}.
 * <p>
 * Contratto minimo: ogni oggetto ha un nome identificativo e una
 * descrizione testuale per la GUI. Comportamenti specifici (equipaggiabile,
 * consumabile, ...) sono definiti da interfacce più specializzate che
 * estendono o implementano {@code Item}, nel rispetto dell'Interface
 * Segregation Principle: ogni implementazione dichiara solo ciò che
 * sa davvero fare.
 */
public interface Item {

    /**
     * @return nome dell'oggetto, mostrato nella GUI dell'inventario
     */
    String getName();

    /**
     * @return descrizione testuale, mostrata nella GUI quando l'oggetto
     *         è selezionato (es. "Una pozione rossa che restituisce 10 HP")
     */
    String getDescription();
}