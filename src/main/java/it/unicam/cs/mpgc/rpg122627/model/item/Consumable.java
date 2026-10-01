package it.unicam.cs.mpgc.rpg122627.model.item;

import it.unicam.cs.mpgc.rpg122627.model.character.Hero;

/**
 * Oggetto consumabile: produce un effetto sull'eroe quando viene usato
 * e si esaurisce dopo l'uso.
 * <p>
 * Esempi: pozioni di cura, pergamene magiche, cibo. L'effetto specifico
 * è responsabilità di ogni implementazione.
 */
public interface Consumable extends Item {

    /**
     * Applica l'effetto dell'oggetto sull'eroe.
     * <p>
     * La rimozione dall'inventario dopo l'uso è responsabilità
     * dell'{@link Inventory}, non del Consumable.
     *
     * @param hero bersaglio dell'effetto (non nullo)
     * @return breve descrizione testuale dell'effetto applicato,
     *         usata dalla GUI per il log (es. "Marco recupera 10 HP")
     */
    String applyTo(Hero hero);
}