# SpringAI RAG

Eine Retrieval Augmented Generation (RAG) Anwendung mit Spring AI, PostgreSQL und pgvector.

## Überblick

Diese Anwendung demonstriert die Implementierung eines RAG-Systems (Retrieval Augmented Generation) mit Spring AI. Sie ermöglicht es Benutzern, Informationen zu speichern und Fragen zu stellen, die auf Basis dieser gespeicherten Informationen beantwortet werden.

## Installation und Einrichtung

### Voraussetzungen

- Java 24
- Maven 3.8+
- PostgreSQL mit pgvector-Erweiterung
- Ollama (lokal laufend)
- OpenAI API-Schlüssel
- Node.js und npm (für das Frontend)

### Datenbank-Einrichtung

1. Installieren Sie PostgreSQL und die pgvector-Erweiterung
2. Erstellen Sie eine Datenbank mit dem Namen `rag_example`:
   ```sql
   CREATE DATABASE rag_example;
   ```

### Backend-Konfiguration

1. Klonen Sie das Repository
2. Öffnen Sie die Datei `src/main/resources/application.properties` und passen Sie folgende Einstellungen an:
   - Datenbank-Verbindung (URL, Benutzername, Passwort)
   - OpenAI API-Schlüssel
   - Ollama-Konfiguration (falls nötig)

### Frontend-Installation

1. Navigieren Sie zum Projektverzeichnis
2. Installieren Sie die Frontend-Abhängigkeiten:
   ```bash
   npm install
   ```
3. Kompilieren Sie die Frontend-Assets:
   ```bash
   npm run build
   ```

### Anwendung starten

1. Starten Sie die Anwendung mit Maven:
   ```bash
   mvn spring-boot:run
   ```
2. Öffnen Sie einen Browser und navigieren Sie zu `http://localhost:8080`

## Funktionalität

### Technologien

Die Anwendung verwendet folgende Technologien:

- **Backend**:
  - Spring Boot 3.4.5
  - Spring AI 1.0.0-M8
  - Spring Data JPA
  - PostgreSQL mit pgvector für Vektorspeicherung
  - Ollama für Embedding-Generierung
  - OpenAI für Chat-Completion
  - Flyway für Datenbankmigrationen

- **Frontend**:
  - Thymeleaf für Server-seitiges Rendering
  - Tailwind CSS für Styling
  - Vanilla JavaScript für Interaktivität

### RAG-Implementierung

Die Anwendung implementiert den RAG-Ansatz wie folgt:

1. **Retrieval**: Wenn ein Benutzer eine Frage stellt, werden relevante Informationen aus der Datenbank abgerufen. Dies geschieht durch:
   - Umwandlung der Frage in einen Embedding-Vektor mit Ollama
   - Suche nach ähnlichen Informationen in der Datenbank mittels pgvector
   - Gewichtete Kombination von Titel- und Inhalts-Ähnlichkeit (70% Titel, 30% Inhalt)

2. **Augmentation**: Die abgerufenen Informationen werden in einen strukturierten Prompt eingebettet, der:
   - Kontext über die Rolle des Assistenten enthält
   - Die abgerufenen Informationen in einem XML-ähnlichen Format bereitstellt
   - Die Frage des Benutzers enthält
   - Detaillierte Anweisungen zur Beantwortung gibt

3. **Generation**: Der Prompt wird an das OpenAI-Modell gesendet, das eine Antwort generiert, die:
   - Nur auf den bereitgestellten Informationen basiert
   - Die Quellen der verwendeten Informationen angibt
   - In einem strukturierten XML-Format zurückgegeben wird

### Benutzeroberfläche

Die Anwendung bietet zwei Hauptseiten:

1. **Informationsverwaltung** (`/manage`):
   - Hinzufügen neuer Informationen (Titel und Inhalt)
   - Bearbeiten bestehender Informationen
   - Löschen von Informationen
   - Anzeigen aller gespeicherten Informationen

2. **Frage-Antwort** (`/rag`):
   - Eingabe einer Frage
   - Anzeige der generierten Antwort
   - Anzeige der verwendeten Quellen
   - Optional: Anzeige des generierten Prompts und der Rohausgabe

## Prozessablauf

### Hinzufügen oder Bearbeiten von Informationen

1. Der Benutzer gibt einen Titel und Inhalt in das Formular ein
2. Nach dem Absenden werden die Daten an den `/api/information` Endpunkt gesendet
3. Der Server:
   - Validiert die Eingaben
   - Generiert Embeddings für Titel und Inhalt mit Ollama
   - Speichert die Information mit den Embeddings in der Datenbank
4. Die Informationstabelle wird aktualisiert

### Stellen einer Frage

1. Der Benutzer gibt eine Frage in das Formular ein
2. Nach dem Absenden wird die Frage an den `/api/rag/query` Endpunkt gesendet
3. Der Server:
   - Generiert ein Embedding für die Frage
   - Sucht nach ähnlichen Informationen in der Datenbank
   - Erstellt einen Prompt mit den relevanten Informationen
   - Sendet den Prompt an das OpenAI-Modell
   - Erhält eine Antwort vom Modell
4. Die Antwort wird geparst und dem Benutzer angezeigt, zusammen mit den verwendeten Quellen

## Entwicklungshinweise

- Die Anwendung verwendet Flyway für Datenbankmigrationen
- Die Embeddings werden mit dem Modell `jina/jina-embeddings-v2-base-de` generiert
- Die Antworten werden mit dem Modell `gpt-4.1-mini` generiert
- Die Vektordimension ist auf 768 festgelegt
- Die maximale Länge für Titel ist 100 Zeichen, für Inhalt 200 Zeichen
