package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;

import java.util.List;

public interface NotificationRepositoryCustom {
    List<Notification> findAllByUser(final User authenticatedUser);

    List<Notification> findAllBySurvey(final Survey survey);
}
