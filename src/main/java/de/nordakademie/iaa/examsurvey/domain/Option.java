package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "options")
@Getter
@Setter
public class Option extends AuditModel {
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dateTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    @JsonIgnore
    private Survey survey;

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
