package de.entwickler.training.kvbkischulungspringai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OllamaEmbeddingIntegrationTest {

    @Autowired
    @Qualifier("ollamaEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @Test
    void testOllamaEmbeddingGeneration() {
        // Create a list of texts to embed
        List<String> texts = List.of(
                "This is a test text for embedding generation",
                "Spring AI is a framework for building AI applications"
        );

        // Generate embeddings
        EmbeddingResponse response = embeddingModel.embedForResponse(texts);

        // Verify response
        assertThat(response).isNotNull();
        assertThat(response.getResults()).hasSize(2);

        // Print the embedding dimensions
        System.out.println("Embedding dimensions: " + response.getResults().get(0).getOutput().length);

        // Print the first few values of each embedding
        for (int i = 0; i < response.getResults().size(); i++) {
            float[] embedding = response.getResults().get(i).getOutput();
            System.out.println("Embedding " + i + " (first 5 values): ");
            for (int j = 0; j < Math.min(5, embedding.length); j++) {
                System.out.print(embedding[j] + " ");
            }
            System.out.println();
        }
    }
}
