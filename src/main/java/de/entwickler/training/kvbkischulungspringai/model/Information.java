package de.entwickler.training.kvbkischulungspringai.model;

import de.entwickler.training.kvbkischulungspringai.config.VectorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Information {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 200, message = "Content must be less than 200 characters")
    @Column(length = 200)
    private String content;

    @Type(VectorType.class)
    @Column(columnDefinition = "vector(768)")
    private float[] titleEmbedding;

    @Type(VectorType.class)
    @Column(columnDefinition = "vector(768)")
    private float[] contentEmbedding;
}
