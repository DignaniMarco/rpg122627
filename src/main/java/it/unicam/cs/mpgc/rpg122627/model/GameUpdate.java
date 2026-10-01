package it.unicam.cs.mpgc.rpg122627.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Risultato immutabile di un'operazione sul {@link Game}: contiene lo stato
 * aggiornato della partita e la sequenza di messaggi testuali generati
 * dall'operazione (per il log della GUI).
 * <p>
 * Una singola azione può produrre più messaggi: ad esempio un attacco
 * finale può generare sia il messaggio di danno sia quello di vittoria.
 * Modellare i messaggi come lista, invece che come singola stringa
 * concatenata, mantiene strutturata l'informazione e lascia alla GUI
 * libertà di presentazione (una riga per messaggio, pop-up, ecc.).
 */
public final class GameUpdate {

    private final GameStatus status;
    private final List<String> messages;

    public GameUpdate(GameStatus status, List<String> messages) {
        this.status = Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(messages, "messages must not be null");
        this.messages = Collections.unmodifiableList(new ArrayList<>(messages));
    }

    /**
     * Costruttore comodo per update con un singolo messaggio.
     */
    public GameUpdate(GameStatus status, String message) {
        this(status, List.of(Objects.requireNonNull(message, "message must not be null")));
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<String> getMessages() {
        return messages;
    }

    /**
     * @return i messaggi concatenati con newline tra loro, utile per il log
     *         semplice del Main di test
     */
    public String getJoinedMessage() {
        return String.join("\n", messages);
    }
}