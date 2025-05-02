package de.entwickler.training.kvbkischulungspringai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OpenAiChatIntegrationTest {

    @Autowired
    private OpenAiChatModel chatModel;

    @Test
    void testOpenAiChatIntegration() {
        // Create a simple message
        String message = "Tell me a short joke about programming";

        // Call OpenAI
        String response = chatModel.call(message);

        // Verify response
        System.out.println("OpenAI Response: " + response);

        assertThat(response).isNotEmpty();
    }

    @Test
    void testOpenAiChatWithXmlPrompt() {
        // Create an XML prompt similar to what we'll use in the application
        String xmlPrompt = """
            Du bist ein KI-Assistent, der Fragen basierend auf bereitgestellten Informationen beantwortet.

            Hier ist die Liste der Informationen, die du verwenden darfst:

            <information_list>
            <information title="Spring Boot">Spring Boot ist ein Framework, das die Entwicklung von Spring-Anwendungen vereinfacht.</information>
            <information title="Java">Java ist eine objektorientierte Programmiersprache.</information>
            </information_list>

            Die zu beantwortende Frage lautet:

            <question>
            Was ist Spring Boot?
            </question>

            Bitte antworte nur basierend auf den gegebenen Informationen.
            """;

        // Call OpenAI
        String response = chatModel.call(xmlPrompt);

        // Verify response
        System.out.println("OpenAI XML Prompt Response: " + response);

        assertThat(response).isNotEmpty();
    }
}
