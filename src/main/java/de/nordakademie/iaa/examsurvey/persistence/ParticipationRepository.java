package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link Participation}.
 *
 * @author Felix Plazek
 */
public interface ParticipationRepository extends CrudRepository<Participation, Long>,
        JpaSpecificationExecutor<Participation> {
}
