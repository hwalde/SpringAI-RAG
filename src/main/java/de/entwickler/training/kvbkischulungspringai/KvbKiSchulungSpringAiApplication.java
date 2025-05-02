package de.entwickler.training.kvbkischulungspringai;

import org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {OpenAiEmbeddingAutoConfiguration.class})
public class KvbKiSchulungSpringAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KvbKiSchulungSpringAiApplication.class, args);
    }

}
