package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class ParticipationDTO implements Serializable {
    @JsonProperty(value = "_id")
    private Long id;
    private UserDTO user;
    private Set<OptionDTO> options;
}
