package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for {@link User}.
 *
 * @author Felix Plazek
 */
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {
}
