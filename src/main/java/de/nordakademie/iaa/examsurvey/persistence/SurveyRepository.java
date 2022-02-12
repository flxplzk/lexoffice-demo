package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Survey;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link Survey}.
 *
 * @author Felix Plazek
 */
public interface SurveyRepository extends CrudRepository<Survey, Long>, JpaSpecificationExecutor<Survey> {
}
