package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserCreationDTO implements Serializable {
    @JsonProperty(value = "_id")
    private Long id;
    private String username;
    private String firstName;
    private String password;
    private String lastName;
}
