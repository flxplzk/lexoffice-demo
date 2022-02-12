package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Base Entity for Date-Options toDateTime choose from within a Survey.
 *
 * @author felix plazek
 */
@Entity
@Table(name = "options")
public class Option extends AuditModel {
    private Date dateTime;
    private Survey survey;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    @JsonIgnore
    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
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
        Option option = (Option) o;
        return Objects.equal(dateTime, option.dateTime) &&
                Objects.equal(survey, option.survey);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), dateTime, survey);
    }
}
