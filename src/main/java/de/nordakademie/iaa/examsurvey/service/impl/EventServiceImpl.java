package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.NotificationType;
import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.persistence.EventRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.service.EventService;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;

import java.util.List;
import java.util.stream.Collectors;

import static de.nordakademie.iaa.examsurvey.persistence.specification.EventSpecifications.byUser;
import static de.nordakademie.iaa.examsurvey.persistence.specification.ParticipationSpecifications.bySurvey;

/**
 * @author felix plazek
 */
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
        event.setParticipants(
                participationRepository.findAll(bySurvey(event.getSurvey()))
                        .stream()
                        .filter(participation -> participation.getOptions()
                                .stream()
                                .map(Option::getDateTime)
                                .collect(Collectors.toSet())
                                .contains(event.getTime()))
                        .map(Participation::getUser)
                        .collect(Collectors.toSet())
        );
        return eventRepository.save(event);
    }

    @Override
    public List<Event> loadAllEventsForAuthenticatedUser(final User authenticatedUser) {
        requireNonNullUser(authenticatedUser);
        return eventRepository.findAll(byUser(authenticatedUser));
    }

    private void requireNonNullUser(final User user) {
        if (user == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
    }
}
