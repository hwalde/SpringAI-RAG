package de.entwickler.training.spring.ai.rag.service;

import de.entwickler.training.spring.ai.rag.model.Information;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final EmbeddingModel embeddingModel;
    private final OpenAiChatModel chatModel;
    private final InformationService informationService;

    @Autowired
    public RagService(
            EmbeddingModel embeddingModel,
            OpenAiChatModel chatModel,
            InformationService informationService) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
        this.informationService = informationService;
    }

    public String generatePrompt(String question) {
        // Step 1: Find relevant information using vector similarity search
        List<Information> relevantInfo = informationService.findSimilarInformation(question, 5);

        // Step 2: Create the prompt with the information list
        StringBuilder informationListBuilder = new StringBuilder();
        for (Information info : relevantInfo) {
            informationListBuilder.append("<information title=\"")
                    .append(escapeXml(info.getTitle()))
                    .append("\">")
                    .append(escapeXml(info.getContent()))
                    .append("</information>\n");
        }

        return String.format("""
                Du bist ein KI-Assistent, der Fragen basierend auf bereitgestellten Informationen beantwortet. Deine Aufgabe ist es, die gegebene Frage ausschließlich mit den zur Verfügung gestellten Informationen zu beantworten. Ignoriere jegliches Vorwissen zu dem Thema und konzentriere dich nur auf die gegebenen Informationen.

Heute ist der 06.05.2025

                Hier ist die Liste der Informationen, die du verwenden darfst:

                <information_list>
                %s
                </information_list>

                Die zu beantwortende Frage lautet:

                <question>
                %s
                </question>

                Gehe wie folgt vor, um die Frage zu beantworten:

                1. Lies die Frage sorgfältig durch.
                2. Überprüfe die bereitgestellten Informationen und identifiziere die relevanten Teile zur Beantwortung der Frage.
                3. Formuliere eine Antwort basierend ausschließlich auf den relevanten Informationen aus der Liste.
                4. Wenn die Frage nicht vollständig mit den gegebenen Informationen beantwortet werden kann, gib an, welche Aspekte der Frage du nicht beantworten kannst.
                5. Verwende keine externen Informationen oder dein eigenes Vorwissen, um die Antwort zu ergänzen.

                Formatiere deine Antwort wie folgt:

                <antwort>
                Deine formulierte Antwort hier.
                </antwort>

                <quellen>
                Liste hier die Titel der Informationen auf, die du zur Beantwortung der Frage verwendet hast.
                </quellen>

                Wichtige Hinweise:
                - Beantworte die Frage nur mit den gegebenen Informationen, auch wenn du zusätzliches Wissen zu dem Thema hast.
                - Wenn die Informationen widersprüchlich sind, weise in deiner Antwort darauf hin.
                - Wenn du die Frage mit den gegebenen Informationen nicht beantworten kannst, sage das deutlich.
                - Verwende keine Platzhalter oder erfundene Informationen, um Lücken zu füllen.
                """, informationListBuilder.toString(), escapeXml(question));
    }

    public String generateResponse(String question, String prompt) {

        // Debugging: Log before calling the chat model
        System.out.println("Sending prompt to chat model...");

        // Generate response using the configured Chat Model (OpenAI in this case)
        String response = chatModel.call(prompt);

        // Debugging: Log the response from the chat model
        System.out.println("Received response from chat model.");

        return response;
    }

    // Simple XML escaping - consider using a library for robustness if needed
    private String escapeXml(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;") // Must be first
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
