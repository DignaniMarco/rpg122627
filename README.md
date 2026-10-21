<div align="center">

# 🐉 A casa del Drago

*Non è una visita di cortesia.*

![Schermata iniziale](docs/images/screenshot-home.png)

</div>

---

**A casa del Drago** è un dungeon crawler a turni single-player sviluppato in Java 21 con interfaccia grafica JavaFX. Il giocatore impersona Marco l'Intrepido, scende in un sotterraneo articolato come grafo di stanze a bivi, affronta nemici in combattimenti a turni, raccoglie oggetti ed equipaggiamento, e punta a sconfiggere il **Drago Rosso Antico** nell'antro finale.

Progetto d'esame per il corso di *Modellazione e Gestione della Conoscenza / Metodologie di Programmazione* — Prof. Fabrizio Fornari — UNICAM, A.A. 2025/2026.

## 🚀 Come avviarlo

Serve **Java 21 (Temurin JDK)** installato: [adoptium.net](https://adoptium.net/temurin/releases/?version=21&package=jdk).

**macOS / Linux**
```bash
git clone https://github.com/DignaniMarco/rpg122627.git
cd rpg122627
./gradlew run
```

**Windows (PowerShell)**
```powershell
git clone https://github.com/DignaniMarco/rpg122627.git
cd rpg122627
.\gradlew.bat run
```

**IntelliJ IDEA**: *File → New → Project from Version Control...* → incolla l'URL del repo → aperta la Gradle Tool Window esegui `rpg122627 → application → run`.

Al primo avvio Gradle scarica le dipendenze (~1 minuto); dall'avvio successivo il gioco parte in pochi secondi.

## 📖 Documentazione

La documentazione estesa del progetto è nella **[Wiki del repository](https://github.com/DignaniMarco/rpg122627/wiki)** (o nel file [`docs/Home.md`](docs/Home.md) incluso nel repo), con 13 sezioni che coprono:

- Architettura MVC + albero dei package
- Classi e interfacce principali
- Pattern di progettazione applicati (Facade, Strategy, State machine, DTO, Registry, Factory Method)
- Applicazione dei principi SOLID
- Persistenza su JSON tramite Jackson
- Interfaccia grafica JavaFX, sprite, sfondi tematici
- Strategia di test (95 test JUnit 5)
- Estendibilità verso mobile/web/CLI
- Dichiarazione estesa sull'uso dell'IA
- Credits e risorse

## 🛠 Stack tecnico

- **Java** 21 (Temurin JDK)
- **JavaFX** 21.0.8 — framework GUI
- **Jackson Databind** 2.22.4 — persistenza JSON
- **JUnit** 5 (Jupiter) — testing
- **Gradle** 9.6 (Kotlin DSL) — build tool

## 👤 Autore

**Marco Dignani** — Matricola 122627
Corso di Laurea in Informatica (L-31), Università degli Studi di Camerino
GitHub: [@DignaniMarco](https://github.com/DignaniMarco)
