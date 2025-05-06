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

### Test-Einrichtung

Für das Testen der Anwendung werden folgende Komponenten benötigt:

1. Eine separate Test-Datenbank:
   ```sql
   CREATE DATABASE rag_example_test;
   ```

2. PostgreSQL mit pgvector-Erweiterung (wie für die Hauptanwendung)

3. Ollama mit dem konfigurierten Embedding-Modell:
   - Stellen Sie sicher, dass Ollama läuft
   - Das Modell `jina/jina-embeddings-v2-base-de` muss verfügbar sein

4. Ein gültiger OpenAI API-Schlüssel

5. Konfigurieren Sie die Test-Eigenschaften in `src/test/resources/application-test.properties`:
   - Datenbank-Verbindung zur Test-Datenbank
   - OpenAI API-Schlüssel
   - Ollama-Konfiguration

Die Tests umfassen:

- Basis-Anwendungstests: Überprüfen, dass der Spring-Kontext korrekt geladen wird
- Vektor-Speicher-Tests: Testen der pgvector-Integration
- Embedding-Tests: Testen der Ollama-Embedding-Funktionalität
- Chat-Modell-Tests: Testen der OpenAI-Chat-Integration

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

### Vektorähnlichkeitssuche

Die Suche nach ähnlichen Informationen in der Datenbank ist ein zentraler Bestandteil des RAG-Ansatzes. Die Implementierung in `InformationRepository.findSimilarInformation()` verwendet PostgreSQL mit der pgvector-Erweiterung:

```sql
SELECT *,
    0.7 * (1 - (title_embedding <=> CAST(:queryEmbeddingString AS vector))) +
    0.3 * (1 - (content_embedding <=> CAST(:queryEmbeddingString AS vector))) AS combined_score
FROM information
WHERE
    (1 - (title_embedding <=> CAST(:queryEmbeddingString AS vector))) > 0.1 OR
    (1 - (content_embedding <=> CAST(:queryEmbeddingString AS vector))) > 0.5
ORDER BY combined_score DESC
LIMIT :limit
```

Diese SQL-Abfrage:

1. **Berechnet Ähnlichkeitswerte**:
   - Der Operator `<=>` ist der Cosinus-Distanz-Operator von pgvector
   - Er gibt Werte zwischen 0 (identisch) und 2 (entgegengesetzt) zurück
   - Die Formel `1 - (vector1 <=> vector2)` wandelt dies in einen Ähnlichkeitswert um (1 = identisch, -1 = entgegengesetzt)

2. **Gewichtet die Ähnlichkeit**:
   - 70% Gewichtung für die Titelähnlichkeit
   - 30% Gewichtung für die Inhaltsähnlichkeit
   - Diese Gewichtung priorisiert Treffer im Titel höher als im Inhalt

3. **Filtert Ergebnisse**:
   - Nur Informationen mit einer Titelähnlichkeit > 0.1 ODER
   - Einer Inhaltsähnlichkeit > 0.5 werden berücksichtigt
   - Diese Schwellenwerte verhindern, dass irrelevante Informationen zurückgegeben werden

4. **Sortiert nach kombinierter Ähnlichkeit**:
   - Die Ergebnisse werden nach dem kombinierten Ähnlichkeitswert absteigend sortiert
   - Die relevantesten Informationen erscheinen zuerst

5. **Begrenzt die Ergebnisse**:
   - Die Anzahl der zurückgegebenen Ergebnisse wird durch den Parameter `:limit` begrenzt
   - Standardmäßig werden die 5 ähnlichsten Informationen verwendet

Diese Implementierung ermöglicht eine effiziente semantische Suche, die über einfache Schlüsselwortsuchen hinausgeht und den Kontext und die Bedeutung der Frage berücksichtigt.

## Entwicklungshinweise

- Die Anwendung verwendet Flyway für Datenbankmigrationen
- Die Embeddings werden mit dem Modell `jina/jina-embeddings-v2-base-de` generiert
- Die Antworten werden mit dem Modell `gpt-4.1-mini` generiert
- Die Vektordimension ist auf 768 festgelegt
- Die maximale Länge für Titel ist 100 Zeichen, für Inhalt 200 Zeichen

### Modell-Konfiguration

Die Anwendung unterstützt verschiedene KI-Modelle für Embeddings und Chat-Completion:

#### Embedding-Modelle (Ollama)

Die Embedding-Generierung wird über Ollama konfiguriert:

```properties
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.init.pull-model-strategy=when_missing
spring.ai.ollama.embedding.options.model=jina/jina-embeddings-v2-base-de
```

Sie können das Embedding-Modell ändern, indem Sie ein anderes Modell in `spring.ai.ollama.embedding.options.model` angeben. Stellen Sie sicher, dass das gewählte Modell:
- Von Ollama unterstützt wird
- Die gleiche Vektordimension (768) erzeugt oder passen Sie die Dimension in der Datenbank an

#### Chat-Modelle

Die Anwendung ist so konfiguriert, dass sie zwischen verschiedenen Chat-Modell-Anbietern wechseln kann:

```properties
spring.ai.model.chat=openai
spring.ai.openai.api-key=IhrOpenAIKey
spring.ai.openai.chat.options.model=gpt-4.1-mini
```

Um z.B. zu Ollama für Chat-Completion zu wechseln, ändern Sie die Konfiguration:

```properties
spring.ai.model.chat=ollama
spring.ai.ollama.chat.options.model=llama3
```

Unterstützte Chat-Modell-Anbieter:
- OpenAI (Standard)
- Ollama (lokale Modelle)
- Andere Spring AI-unterstützte Anbieter (Azure OpenAI, Anthropic, etc.)
