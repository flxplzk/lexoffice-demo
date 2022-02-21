package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Data
public class EventDTO implements Serializable {
    @JsonProperty("_id")
    private Long id;
    private String title;
    private Date time;
    private SurveyDTO survey;
    private Set<UserDTO> participants;
}
