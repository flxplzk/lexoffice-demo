package de.nordakademie.iaa.examsurvey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author felix plazek
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingDataException extends SurveyApplicationException {
    public MissingDataException(String message) {
        super(message);
    }
}
