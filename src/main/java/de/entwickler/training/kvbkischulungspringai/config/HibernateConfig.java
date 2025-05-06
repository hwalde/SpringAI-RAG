package de.entwickler.training.kvbkischulungspringai.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Hibernate.
 * Registers custom types with Hibernate.
 */
@Configuration
public class HibernateConfig {

    /**
     * Custom PostgreSQL dialect that registers the vector type.
     */
    public static class PostgreSQLVectorDialect extends PostgreSQLDialect {
        @Override
        public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
            super.contributeTypes(typeContributions, serviceRegistry);
            typeContributions.contributeType(new VectorType());
        }
    }

    /**
     * Bean to register the custom dialect.
     */
    @Bean
    public String hibernateDialect() {
        return PostgreSQLVectorDialect.class.getName();
    }
}