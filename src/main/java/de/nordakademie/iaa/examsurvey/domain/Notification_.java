package de.nordakademie.iaa.examsurvey.domain;

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
}
