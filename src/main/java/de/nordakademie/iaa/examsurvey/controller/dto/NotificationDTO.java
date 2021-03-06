package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationDTO implements Serializable {
    @JsonProperty(value = "_id")
    private Long id;
    private UserDTO user;
    private SurveyDTO survey;
    private String notificationType;
}
