package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
public class SurveyDTO implements Serializable {
    @JsonProperty(value = "_id")
    private Long id;
    private String title;
    private String description;
    private UserDTO initiator;
    private String surveyStatus;
    @JsonProperty(access = WRITE_ONLY)
    private Set<OptionDTO> options;
    private boolean isOpen;
}
