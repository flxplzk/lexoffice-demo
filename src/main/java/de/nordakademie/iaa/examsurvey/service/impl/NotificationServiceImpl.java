package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.NotificationType;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.persistence.NotificationRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final ParticipationRepository participationRepository;

    public NotificationServiceImpl(final NotificationRepository notificationRepository,
                                   final ParticipationRepository participationRepository) {
        this.notificationRepository = notificationRepository;
        this.participationRepository = participationRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> getNotificationsForUser(User authenticatedUser) {
        return notificationRepository.findAllByUser(authenticatedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyUsersWithNotificationType(final NotificationType type,
                                                final Survey targetSurvey) {
        final List<Notification> notifications = participationRepository.findAllBySurvey(targetSurvey).stream()
                .map(participation -> new Notification(participation.getUser(), targetSurvey, type))
                .collect(Collectors.toList());
        notificationRepository.saveAll(notifications);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllNotificationsForSurvey(final Survey survey) {
        final List<Notification> notifications = notificationRepository.findAllBySurvey(survey);
        notificationRepository.deleteAll(notifications);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteNotificationWithUser(final Long notificationId, final User user) {
        final Notification existentNotification = notificationRepository.findById(notificationId)
                .orElseThrow(ResourceNotFoundException::new);
        if (user == null || !user.equals(existentNotification.getUser())) {
            throw new PermissionDeniedException("User must be non null or affected user.");
        }
        notificationRepository.deleteById(notificationId);
    }
}
