package de.entwickler.training.kvbkischulungspringai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PgVectorIntegrationTest {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    @Qualifier("ollamaEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @Test
    void testPgVectorIntegration() {
        // Create test documents
        List<Document> documents = List.of(
                new Document("This is a test document about Spring AI", Map.of("title", "Spring AI Document")),
                new Document("This is a test document about PostgreSQL", Map.of("title", "PostgreSQL Document")),
                new Document("This is a test document about Vector Databases", Map.of("title", "Vector Database Document"))
        );

        // Add documents to vector store
        vectorStore.add(documents);

        // Search for documents similar to "Spring"
        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query("Spring")
                .topK(5)
                .build());

        // Verify results
        assertThat(results).isNotEmpty();
        System.out.println("Search results for 'Spring':");
        results.forEach(doc -> {
            System.out.println("Content: " + doc.getText());
            System.out.println("Metadata: " + doc.getMetadata());
            System.out.println("---");
        });

        // Search for documents similar to "Database"
        results = vectorStore.similaritySearch(SearchRequest.builder()
                .query("Database")
                .topK(5)
                .build());

        // Verify results
        assertThat(results).isNotEmpty();
        System.out.println("Search results for 'Database':");
        results.forEach(doc -> {
            System.out.println("Content: " + doc.getText());
            System.out.println("Metadata: " + doc.getMetadata());
            System.out.println("---");
        });
    }
}
