package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.NotificationType;
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
public interface NotificationService {

    /**
     * reads all Notifications for the user.
     *
     * @param authenticatedUser must be not {@code null}
     * @return notifications for user
     */
    List<Notification> getNotificationsForUser(@NotNull final User authenticatedUser);

    /**
     * creates new notifications for the given user with the {@link NotificationType}
     * for the passed users.
     *
     * @param type         of the notification
     * @param targetSurvey on which the user will be notified
     */
    void notifyUsersWithNotificationType(@NotNull final NotificationType type,
                                         @NotNull final Survey targetSurvey);

    /**
     * deletes all corresponding {@link Notification}'s for {@param survey}
     *
     * @param survey for which the  {@link Notification}'s shall be deleted
     */
    void deleteAllNotificationsForSurvey(@NotNull final Survey survey);

    /**
     * deletes notification with {@param notificationId} if existent and {@link Notification#getUser()}
     * equals {@param user}
     */
    void deleteNotificationWithUser(@NotNull final Long notificationId,
                                    @NotNull final User user);
}
