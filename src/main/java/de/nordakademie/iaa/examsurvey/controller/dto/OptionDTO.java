package de.nordakademie.iaa.examsurvey.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OptionDTO implements Serializable {
    @JsonProperty("_id")
    private Long id;
    private Date dateTime;
}
