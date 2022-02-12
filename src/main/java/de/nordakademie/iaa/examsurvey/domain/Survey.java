package de.nordakademie.iaa.examsurvey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.hibernate.annotations.NaturalId;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

/**
 * Base Entity for Survey, containing basic information.
 *
 * @author felix plazek
 */
@Entity
@Table(name = "surveys")
public class Survey extends AuditModel {
    private String title;
    private String description;
    private User initiator;
    private Event event;
    private SurveyStatus surveyStatus;
    private Set<Option> options;

    @NaturalId
    @Size(max = 50)
    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Size(max = 1000)
    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    public User getInitiator() {
        return initiator;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    @Column(name = "survey_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    public SurveyStatus getSurveyStatus() {
        return surveyStatus;
    }

    public void setSurveyStatus(SurveyStatus surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    /**
     * This field is only used for data transfer purpose. When Saving the options are saved separately to options table
     *
     * @return set of Options or {@code null} when loading from database
     */
    @Transient
    @JsonProperty(access = WRITE_ONLY)
    public Set<Option> getOptions() {
        return options;
    }

    public void setOptions(Set<Option> options) {
        this.options = options;
    }

    @JsonIgnore
    @OneToOne(mappedBy = "survey", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
