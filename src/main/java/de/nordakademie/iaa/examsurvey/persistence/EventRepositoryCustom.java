package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.User;

import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findAllByUser(final User authenticatedUser);
}
