package de.nordakademie.iaa.examsurvey.configuration;

import de.nordakademie.iaa.examsurvey.persistence.SpecificationExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class PersistenceConfiguration {

    @Bean
    @PersistenceContext
    public SpecificationExecutor specificationExecutor(final EntityManager entityManager) {
        return new SpecificationExecutor(entityManager);
    }
}
