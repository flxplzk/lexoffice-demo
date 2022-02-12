package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.*;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.persistence.EventRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.service.EventService;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventServiceImpl implements EventService {
    private final SurveyService surveyService;
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final ParticipationRepository participationRepository;

    public EventServiceImpl(final SurveyService surveyService,
                            final EventRepository eventRepository,
                            final NotificationService notificationService,
                            final ParticipationRepository participationRepository) {
        this.surveyService = surveyService;
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
        this.participationRepository = participationRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event createEvent(final Event event, final User authenticatedUser) {
        surveyService.closeSurvey(event.getSurvey(), authenticatedUser);
        notificationService.notifyUsersWithNotificationType(NotificationType.EVENT_PLANNED, event.getSurvey());
        event.setParticipants(collectParticipants(event));
        return eventRepository.save(event);
    }

    @Override
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
