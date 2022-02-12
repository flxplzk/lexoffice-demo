package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author felix plazek
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface EventService {

    /**
     * creates event with the authenticated user.
     * {@link Event#getSurvey()} must be non {@code null}
     * There can be only one {@link Event } for each {@link Survey}
     *
     * @param event             to create
     * @param authenticatedUser that requests
     * @return created event
     */
    Event createEvent(@NotNull final Event event,
                      @NotNull final User authenticatedUser);

    /**
     * finds nd returns all events for {@param authenticated}
     *
     * @param authenticatedUser that requests
     * @return events
     */
    List<Event> loadAllEventsForAuthenticatedUser(@NotNull final User authenticatedUser);
}
