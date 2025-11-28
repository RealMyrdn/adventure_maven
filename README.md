# Adventure Game

Ein textbasiertes Dungeon-Crawler Adventure-Spiel, entwickelt in Java mit Terminal-UI.

## Architektur

```mermaid
graph TD;
    Main-->Game;
    Game-->House;
    Game-->Player;
    Game-->Renderer;
    Game-->Generator;
    House-->Room
```

## Features

- Prozedural generierte Dungeons
- Textbasierte Terminal-Benutzeroberfläche (Lanterna)
- Inventar-System
- Raum-Erkundung und Objekt-Interaktion
- Auto-Save Funktionalität
- Konfigurierbare Spieleinstellungen
- Umfassendes Logging-System

## Technologie-Stack

- **Java**: 21
- **Build Tool**: Maven 3.x
- **UI Framework**: Lanterna 3.1.1
- **Logging**: SLF4J + Logback
- **Testing**: JUnit 5 + Mockito

## Voraussetzungen

- Java 21 oder höher
- Maven 3.6 oder höher

## Build-Anleitung

### Projekt kompilieren

```bash
mvn clean compile
```

### Tests ausführen

```bash
mvn test
```

### JAR-Datei erstellen

```bash
mvn clean package
```

Die ausführbare JAR-Datei wird in `target/adventure-1.0-SNAPSHOT.jar` erstellt.

### Code Coverage Report generieren

```bash
mvn clean test jacoco:report
```

Der Coverage Report ist verfügbar unter `target/site/jacoco/index.html`

### Code Quality Check (Checkstyle)

```bash
mvn checkstyle:check
```

## Spiel starten

### Mit Maven

```bash
mvn exec:java -Dexec.mainClass="org.myrdn.adventure.Main"
```

### Mit JAR-Datei

```bash
java -jar target/adventure-1.0-SNAPSHOT.jar
```

## Spielanleitung

### Verfügbare Befehle

| Befehl | Beschreibung | Beispiel |
|--------|--------------|----------|
| `gehe [richtung]` | Bewege dich in eine Richtung | `gehe nord` |
| `untersuche [objekt/raum]` | Untersuche einen Gegenstand oder Raum | `untersuche truhe` |
| `nimm [gegenstand]` | Nimm einen Gegenstand auf | `nimm schlüssel` |
| `inventar` | Zeige dein Inventar an | `inventar` |
| `benutze [gegenstand]` | Benutze einen Gegenstand aus dem Inventar | `benutze trank` |
| `hilfe` | Zeige die Hilfe an | `hilfe` |
| `exit` | Spiel speichern und beenden | `exit` |

### Richtungen

- **nord** - Nach Norden gehen
- **süd** - Nach Süden gehen
- **ost** - Nach Osten gehen
- **west** - Nach Westen gehen

### Tipps

- Untersuche jeden Raum gründlich mit `untersuche raum`
- Sammle Gegenstände, die du findest
- Das Spiel speichert automatisch alle 5 Minuten
- Verwende `exit` zum Beenden, um deinen Fortschritt zu speichern

## Konfiguration

Das Spiel kann über die Datei `src/main/resources/game.properties` konfiguriert werden:

```properties
# Dungeon-Größe
dungeon.width=13
dungeon.height=7

# Spieler-Einstellungen
player.name=Myrdn
player.start.x=3
player.start.y=3

# Auto-Save Einstellungen
autosave.enabled=true
autosave.interval.minutes=5

# Spiel-Einstellungen
game.max.command.length=80
```

## Projektstruktur

```
adventure/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/myrdn/adventure/
│   │   │       ├── config/         # Konfigurationsklassen
│   │   │       ├── datahandler/    # Datenmodelle und Persistenz
│   │   │       ├── gamecontroller/ # Spiellogik und Controller
│   │   │       └── renderer/       # UI-Rendering
│   │   └── resources/
│   │       ├── game.properties     # Spielkonfiguration
│   │       └── logback.xml         # Logging-Konfiguration
│   └── test/
│       └── java/                   # Unit Tests
├── pom.xml                         # Maven-Konfiguration
└── README.md
```

## Logs

Spiel-Logs werden in `logs/adventure.log` gespeichert. Die Logs enthalten:
- Spiel-Events
- Fehler und Exceptions
- Debug-Informationen
- Auto-Save Aktivitäten

## Entwicklung

### Code-Stil

Das Projekt verwendet Google Java Style Guide (über Checkstyle Plugin).

### Tests schreiben

Tests sollten in `src/test/java` platziert werden und JUnit 5 verwenden:

```java
@Test
void testPlayerMovement() {
    // Test implementation
}
```

### Debugging

Setze das Log-Level auf DEBUG in `logback.xml`:

```xml
<logger name="org.myrdn.adventure" level="DEBUG" />
```

## Lizenz

Dieses Projekt ist Open Source.

## Autor

Myrdn

## Beiträge

Beiträge sind willkommen! Bitte erstelle einen Pull Request oder öffne ein Issue.
