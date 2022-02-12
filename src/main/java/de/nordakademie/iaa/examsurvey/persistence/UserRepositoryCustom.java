package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findOneByUsername(final String userName);
}
