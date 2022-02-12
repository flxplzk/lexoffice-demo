package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Option;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link Option}.
 *
 * @author Felix Plazek
 */
public interface OptionRepository extends CrudRepository<Option, Long>, JpaSpecificationExecutor<Option> {
}
