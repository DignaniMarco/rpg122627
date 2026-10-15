# Crediti e licenze delle risorse

Questo file documenta la provenienza e la licenza di tutte le risorse
non-code incluse nel progetto RPG122627.

## Sprite grafici (personaggi e oggetti)

Tutte le immagini contenute in `src/main/resources/sprites/` sono state
**generate con Google Gemini** come assistente di immagine generativa,
appositamente per questo progetto didattico.

### Pipeline di produzione

1. **Generazione**: ogni sprite è stato prodotto da un prompt testuale
   specifico (es. "goblin guerriero, pelle verde scura, armatura di
   cuoio, pixel art retro"), con l'obiettivo di mantenere uno stile
   grafico coerente attraverso l'intera galleria.
2. **Rimozione sfondo**: per ottenere PNG con sfondo trasparente,
   adatti all'uso come sprite su un tema scuro, le immagini sono state
   elaborate con [rembg](https://github.com/danielgatis/rembg) (modello
   `u2net`) per la maggior parte, e con il servizio web
   [erase.bg](https://www.erase.bg) per gli sprite in cui rembg aveva
   dato risultati non soddisfacenti.
3. **Ridimensionamento**: tutte le immagini sono state ridotte a
   dimensioni gestibili tramite il tool macOS `sips` (o dallo stesso
   SpriteManager a runtime).

### Licenza

Gli sprite generati con AI, in base alla posizione del U.S. Copyright
Office (Marzo 2023) e secondo i termini d'uso di Google Gemini, non
sono soggetti a copyright dell'autore umano e sono qui considerati di
pubblico dominio ai fini didattici del presente progetto.

### Elenco

Personaggi (`src/main/resources/sprites/characters/`):
- `hero.png` — Eroe giocante
- `goblin_scout.png` / `goblin_warrior.png` / `goblin_chief.png` —
  tre varianti di goblin con sprite distinti
- `wolf.png` — Lupo Mannaro
- `orc.png` — Orco Bruto
- `skeleton.png` — Scheletro Guerriero
- `knight.png` — Cavaliere Spettrale
- `npc.png` — Negoziante Errante
- `dragon.png` — Drago Rosso Antico (boss finale)

Oggetti (`src/main/resources/sprites/items/`):
- `potion_small.png` — Pozione Minore (+10 HP)
- `potion_medium.png` — Pozione Media / Rossa (+15 HP)
- `potion_large.png` — Pozione Maggiore (+25 HP)
- `dagger.png` — Pugnale Arrugginito (arma iniziale)
- `silver_sword.png` — Spada d'Argento (ricompensa NPC)
- `chest.png` — Scrigno (icona generica tesoro)

## Font

Il gioco usa i font di sistema (`Monospaced`, `SansSerif`) senza font
custom, quindi nessuna licenza aggiuntiva è necessaria.

## Librerie software

Le dipendenze Java sono dichiarate in `build.gradle.kts`; le loro
licenze sono riportate nei rispettivi repository ufficiali
(JavaFX 21, Jackson 2.18, JUnit 5).

## Utilizzo di assistenti AI nello sviluppo

L'uso di assistenti AI nello sviluppo del codice è documentato
dettagliatamente nel README.md e nella Wiki del repository.