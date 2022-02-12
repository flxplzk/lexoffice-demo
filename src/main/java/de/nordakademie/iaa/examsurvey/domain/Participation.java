package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;

/**
 * Base Entity for User Answers to a survey.
 *
 * @author felix plazek
 */
@Entity
@Table(name = "participations")
public class Participation extends AuditModel {
    private User user;
    private Survey survey;
    private Set<Option> options;

    @ManyToOne
    @NaturalId
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @NaturalId
    @JsonIgnore
    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    public Set<Option> getOptions() {
        return options;
    }

    public void setOptions(Set<Option> options) {
        this.options = options;
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
        Participation that = (Participation) o;
        return Objects.equal(user, that.user) &&
                Objects.equal(survey, that.survey) &&
                Objects.equal(options, that.options);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), user, survey, options);
    }
}
