package de.nordakademie.iaa.examsurvey.domain;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * {@link StaticMetamodel} for type {@link Notification}
 *
 * @author felix plazek
 */
@StaticMetamodel(Notification.class)
public class Notification_ extends AuditModel_ {
    protected Notification_() {
    }

    public static volatile SingularAttribute<Notification, User> user;
    public static volatile SingularAttribute<Notification, Survey> survey;
    public static volatile SingularAttribute<Notification, String> notificationText;
    public static volatile SingularAttribute<Notification, NotificationType> notificationType;

    public static Specification<Notification> byUser(User authenticatedUser) {
        return (root, query, criteriaBuilder) -> authenticatedUser == null
                ? criteriaBuilder.isNull(root.get(Notification_.user))
                : criteriaBuilder.equal(root.get(Notification_.user), authenticatedUser);
    }

    public static Specification<Notification> bySurvey(final Survey survey) {
        return (root, query, criteriaBuilder) -> survey == null
                ? criteriaBuilder.isNull(root.get(Notification_.survey))
                : criteriaBuilder.equal(root.get(Notification_.survey), survey);
    }
}
