package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event extends AuditModel {
    @Column(name = "event_title", nullable = false)
    private String title;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_time", nullable = false)
    private Date time;
    @OneToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<User> participants;

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
        Event event = (Event) o;
        return Objects.equal(title, event.title) &&
                Objects.equal(time, event.time) &&
                Objects.equal(survey, event.survey) &&
                Objects.equal(participants, event.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), title, time, survey, participants);
    }
}
