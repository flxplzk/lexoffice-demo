package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification extends AuditModel {
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    protected Notification() {
        // JPA constructor
    }

    public Notification(User user, Survey targetSurvey, NotificationType type) {
        this.user = user;
        this.survey = targetSurvey;
        this.notificationType = type;
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
