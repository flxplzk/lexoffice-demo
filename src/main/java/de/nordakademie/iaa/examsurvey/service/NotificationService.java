package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.NotificationType;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.persistence.NotificationRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(propagation = Propagation.REQUIRED)
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ParticipationRepository participationRepository;

    public NotificationService(final NotificationRepository notificationRepository,
                                   final ParticipationRepository participationRepository) {
        this.notificationRepository = notificationRepository;
        this.participationRepository = participationRepository;
    }

    /**
     * reads all Notifications for the user.
     *
     * @param authenticatedUser must be not {@code null}
     * @return notifications for user
     */
    public List<Notification> getNotificationsForUser(User authenticatedUser) {
        return notificationRepository.findAllByUser(authenticatedUser);
    }

    /**
     * creates new notifications for the given user with the {@link NotificationType}
     * for the passed users.
     *
     * @param type         of the notification
     * @param targetSurvey on which the user will be notified
     */
    public void notifyUsersWithNotificationType(final NotificationType type,
                                                final Survey targetSurvey) {
        final List<Notification> notifications = participationRepository.findAllBySurvey(targetSurvey).stream()
                .map(participation -> new Notification(participation.getUser(), targetSurvey, type))
                .collect(Collectors.toList());
        notificationRepository.saveAll(notifications);
    }

    /**
     * deletes all corresponding {@link Notification}'s for {@param survey}
     *
     * @param survey for which the  {@link Notification}'s shall be deleted
     */
    public void deleteAllNotificationsForSurvey(final Survey survey) {
        final List<Notification> notifications = notificationRepository.findAllBySurvey(survey);
        notificationRepository.deleteAll(notifications);
    }

    /**
     * deletes notification with {@param notificationId} if existent and {@link Notification#getUser()}
     * equals {@param user}
     */
    public void deleteNotificationWithUser(final Long notificationId, final User user) {
        final Notification existentNotification = notificationRepository.findById(notificationId)
                .orElseThrow(ResourceNotFoundException::new);
        if (user == null || !user.equals(existentNotification.getUser())) {
            throw new PermissionDeniedException("User must be non null or affected user.");
        }
        notificationRepository.deleteById(notificationId);
    }
}
