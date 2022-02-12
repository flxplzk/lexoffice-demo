package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author felix plazek
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface AuthenticationService {

    /**
     * determines the current authenticated {@link User}
     *
     * @return current user if one exists {@code null} otherwise
     */
    User getCurrentAuthenticatedUser();
}
