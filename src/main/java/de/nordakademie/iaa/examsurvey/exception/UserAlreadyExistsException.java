package de.nordakademie.iaa.examsurvey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author felix plazek
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends SurveyApplicationException {
    public UserAlreadyExistsException() {
        super();
    }
}
