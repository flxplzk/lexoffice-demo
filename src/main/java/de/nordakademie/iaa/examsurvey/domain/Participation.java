package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Set;

/**
 * Base Entity for User Answers to a survey.
 *
 * @author felix plazek
 */
@Entity
@Table(name = "participations")
@Getter
@Setter
public class Participation extends AuditModel {
    @ManyToOne
    @NaturalId
    private User user;
    @ManyToOne
    @NaturalId
    @JsonIgnore
    private Survey survey;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Option> options;

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
