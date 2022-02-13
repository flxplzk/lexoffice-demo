package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.*;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.persistence.EventRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(propagation = Propagation.REQUIRED)
public class EventService {
    private final SurveyService surveyService;
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final ParticipationRepository participationRepository;

    public EventService(final SurveyService surveyService,
                        final EventRepository eventRepository,
                        final NotificationService notificationService,
                        final ParticipationRepository participationRepository) {
        this.surveyService = surveyService;
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
        this.participationRepository = participationRepository;
    }

    /**
     * creates event with the authenticated user.
     * {@link Event#getSurvey()} must be non {@code null}
     * There can be only one {@link Event } for each {@link Survey}
     *
     * @param event             to create
     * @param authenticatedUser that requests
     * @return created event
     */
    public Event createEvent(final Event event, final User authenticatedUser) {
        surveyService.closeSurvey(event.getSurvey(), authenticatedUser);
        notificationService.notifyUsersWithNotificationType(NotificationType.EVENT_PLANNED, event.getSurvey());
        event.setParticipants(collectParticipants(event));
        return eventRepository.save(event);
    }

    /**
     * finds nd returns all events for {@param authenticated}
     *
     * @param authenticatedUser that requests
     * @return events
     */
    public List<Event> loadAllEventsForAuthenticatedUser(final User authenticatedUser) {
        requireNonNullUser(authenticatedUser);
        return eventRepository.findAllByUser(authenticatedUser);
    }

    private Set<User> collectParticipants(Event event) {
        return participationRepository.findAllBySurvey(event.getSurvey())
                .stream()
                .filter(participation -> participation.getOptions()
                        .stream()
                        .map(Option::getDateTime)
                        .collect(Collectors.toSet())
                        .contains(event.getTime()))
                .map(Participation::getUser)
                .collect(Collectors.toSet());
    }

    private void requireNonNullUser(final User user) {
        if (user == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
    }
}
