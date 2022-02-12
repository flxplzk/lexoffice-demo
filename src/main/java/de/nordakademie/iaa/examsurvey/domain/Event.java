package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.Set;

/**
 * BAse Entity for Events
 *
 * @author Felix Plazek
 */
@Entity
@Table(name = "event")
public class Event extends AuditModel {
    private String title;
    private Date time;
    private Survey survey;
    private Set<User> participants;

    @Column(name = "event_title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_time", nullable = false)
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @OneToOne
    @JoinColumn(name = "survey_id")
    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
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
