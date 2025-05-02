package de.entwickler.training.kvbkischulungspringai.service;

import de.entwickler.training.kvbkischulungspringai.model.Information;
import de.entwickler.training.kvbkischulungspringai.repository.InformationRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class InformationService {

    private final InformationRepository informationRepository;
    private final EmbeddingModel embeddingModel;

    @Autowired
    public InformationService(InformationRepository informationRepository, EmbeddingModel embeddingModel) {
        this.informationRepository = informationRepository;
        this.embeddingModel = embeddingModel;
    }

    public List<Information> getAllInformation() {
        return informationRepository.findAll();
    }

    public Optional<Information> getInformationById(UUID id) {
        return informationRepository.findById(id);
    }

    @Transactional
    public Information saveInformation(Information information) {
        // Generate embeddings for title and content
        information.setTitleEmbedding(embeddingModel.embed(information.getTitle()));
        information.setContentEmbedding(embeddingModel.embed(information.getContent()));

        return informationRepository.save(information);
    }

    public void deleteInformation(UUID id) {
        informationRepository.deleteById(id);
    }

    public List<Information> searchByTitle(String title) {
        return informationRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Information> searchByContent(String content) {
        return informationRepository.findByContentContainingIgnoreCase(content);
    }

    /**
     * Find information similar to the query text.
     *
     * @param queryText the text to find similar information for
     * @param limit the maximum number of results to return
     * @return a list of Information objects ordered by similarity
     */
    public List<Information> findSimilarInformation(String queryText, int limit) {
        float[] queryEmbedding = embeddingModel.embed(queryText);

        // Convert float[] to String format "[f1,f2,f3]"
        String queryEmbeddingString = floatArrayToString(queryEmbedding);

        return informationRepository.findSimilarInformation(queryEmbeddingString, limit);
    }

    /**
     * Converts a float array to the string format "[f1,f2,f3]" expected by pgvector.
     * @param array The float array.
     * @return The string representation.
     */
    private String floatArrayToString(float[] array) {
        if (array == null) {
            return null; // Or handle as needed, e.g., return "[]" or throw exception
        }
        return IntStream.range(0, array.length)
                .mapToObj(i -> array[i])
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
