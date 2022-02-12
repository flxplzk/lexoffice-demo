package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Base Entity for notifications.
 *
 * @author felix plazek
 */

@Entity
@Table(name = "notifications")
public class Notification extends AuditModel {
    private User user;
    private Survey survey;
    private NotificationType notificationType;

    public Notification() {
        // JPA constructor
    }

    public Notification(User user, Survey targetSurvey, NotificationType type) {
        this.user = user;
        this.survey = targetSurvey;
        this.notificationType = type;
    }

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @Enumerated(value = EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Notification that = (Notification) o;
        return Objects.equal(user, that.user) &&
                Objects.equal(survey, that.survey) &&
                notificationType == that.notificationType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), user, survey, notificationType);
    }
}
