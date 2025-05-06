package de.entwickler.training.spring.ai.rag.repository;

import de.entwickler.training.spring.ai.rag.model.Information;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InformationRepository extends JpaRepository<Information, UUID> {

    List<Information> findByTitleContainingIgnoreCase(String title);

    List<Information> findByContentContainingIgnoreCase(String content);

    /**
     * Find similar information based on vector similarity.
     * Uses a weighted combination of title and content embeddings.
     *
     * @param queryEmbeddingString the embedding vector to compare against, as a String "[f1,f2,...]"
     * @param limit the maximum number of results to return
     * @return a list of Information objects ordered by similarity
     */
    @Query(value = """
            SELECT *,
                0.7 * (1 - (title_embedding <=> CAST(:queryEmbeddingString AS vector))) +
                0.3 * (1 - (content_embedding <=> CAST(:queryEmbeddingString AS vector))) AS combined_score
            FROM information
            WHERE
                (1 - (title_embedding <=> CAST(:queryEmbeddingString AS vector))) > 0.1 OR
                (1 - (content_embedding <=> CAST(:queryEmbeddingString AS vector))) > 0.5
            ORDER BY combined_score DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Information> findSimilarInformation(@Param("queryEmbeddingString") String queryEmbeddingString, @Param("limit") int limit);
}
