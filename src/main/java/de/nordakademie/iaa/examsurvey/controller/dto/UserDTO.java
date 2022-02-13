package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.nordakademie.iaa.examsurvey.domain.User;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
public class UserDTO implements Serializable {
    @JsonProperty(value = "_id")
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    @JsonProperty(access = WRITE_ONLY)
    private String password;
}
