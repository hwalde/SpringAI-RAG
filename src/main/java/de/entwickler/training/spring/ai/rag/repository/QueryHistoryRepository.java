package de.entwickler.training.spring.ai.rag.repository;

import de.entwickler.training.spring.ai.rag.model.QueryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, UUID> {

    /**
     * Find the most recent queries, ordered by timestamp (newest first)
     *
     * @param limit the maximum number of results to return
     * @return a list of QueryHistory objects ordered by timestamp desc
     */
    @Query(value = "SELECT * FROM query_history ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<QueryHistory> findMostRecentQueries(int limit);

    /**
     * Find queries that contain the given text, ordered by timestamp (newest first)
     *
     * @param queryText the text to search for
     * @param limit the maximum number of results to return
     * @return a list of QueryHistory objects that contain the given text
     */
    @Query(value = "SELECT * FROM query_history WHERE query_text ILIKE CONCAT('%', :queryText, '%') ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<QueryHistory> findQueriesContainingText(String queryText, int limit);

    /**
     * Find distinct query texts for auto-completion
     *
     * @param limit the maximum number of results to return
     * @return a list of distinct query texts
     */
    @Query(value = "SELECT query_text FROM query_history GROUP BY query_text ORDER BY MAX(timestamp) DESC LIMIT :limit", nativeQuery = true)
    List<String> findDistinctQueryTexts(int limit);
}
