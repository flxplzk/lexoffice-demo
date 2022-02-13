package de.nordakademie.iaa.examsurvey.domain;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "surveys")
@Getter
@Setter
public class Survey extends AuditModel {
    @NaturalId
    @Size(max = 50)
    @Column(name = "title", nullable = false)
    private String title;
    @Size(max = 1000)
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @Column(name = "survey_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SurveyStatus surveyStatus;
    @OneToOne(
            mappedBy = "survey",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private Event event;
    @Transient
    private Set<Option> options;

    public boolean isInitiator(final User authenticatedUser) {
        return this.initiator != null &&
                this.initiator.equals(authenticatedUser);
    }

    public boolean isOpen() {
        return SurveyStatus.OPEN.equals(this.surveyStatus);
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
        Survey survey = (Survey) o;
        return Objects.equal(title, survey.title) &&
                Objects.equal(description, survey.description) &&
                Objects.equal(initiator, survey.initiator) &&
                surveyStatus == survey.surveyStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), title, description, initiator, surveyStatus);
    }
}
