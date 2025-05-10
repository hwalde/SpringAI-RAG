package de.entwickler.training.spring.ai.rag.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryHistory {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @NotBlank(message = "Query text is required")
    @Size(max = 400, message = "Query text must be less than 400 characters")
    @Column(length = 400)
    private String queryText;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructor for creating a new query history entry
    public QueryHistory(String queryText) {
        this.queryText = queryText;
        this.timestamp = LocalDateTime.now();
    }
}