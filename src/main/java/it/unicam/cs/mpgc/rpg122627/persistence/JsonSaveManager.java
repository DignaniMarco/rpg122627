package it.unicam.cs.mpgc.rpg122627.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Implementazione di {@link SaveManager} che persiste lo stato della
 * partita come file JSON tramite la libreria Jackson.
 * <p>
 * Il file di salvataggio è configurabile: di default risiede nella
 * directory home dell'utente in {@code ~/rpg122627-save.json}, in modo
 * che il salvataggio sopravviva a cancellazioni della cartella del
 * progetto.
 */
public class JsonSaveManager implements SaveManager {

    private static final String DEFAULT_FILENAME = "rpg122627-save.json";

    private final File saveFile;
    private final ObjectMapper mapper;

    public JsonSaveManager() {
        this(defaultSaveFile());
    }

    public JsonSaveManager(File saveFile) {
        this.saveFile = Objects.requireNonNull(saveFile, "saveFile must not be null");
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private static File defaultSaveFile() {
        String home = System.getProperty("user.home");
        return new File(home, DEFAULT_FILENAME);
    }

    @Override
    public void save(GameState state) throws IOException {
        Objects.requireNonNull(state, "state must not be null");
        mapper.writeValue(saveFile, state);
    }

    @Override
    public GameState load() throws IOException {
        if (!hasSave()) {
            throw new IOException("no save file found at " + saveFile.getAbsolutePath());
        }
        return mapper.readValue(saveFile, GameState.class);
    }

    @Override
    public boolean hasSave() {
        return saveFile.exists() && saveFile.isFile();
    }

    /**
     * @return percorso del file di salvataggio in uso (utile per debug e GUI)
     */
    public File getSaveFile() {
        return saveFile;
    }
}