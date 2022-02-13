package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.nordakademie.iaa.examsurvey.domain.Notification;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class NotificationDTO implements Serializable {
    @JsonProperty(value = "_id")
    private Long id;
    private UserDTO user;
    private SurveyDTO survey;
    private String notificationType;
}
