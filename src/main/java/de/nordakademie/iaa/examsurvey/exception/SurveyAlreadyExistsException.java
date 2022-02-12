package de.nordakademie.iaa.examsurvey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author felix plazek
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class SurveyAlreadyExistsException extends SurveyApplicationException {
    public SurveyAlreadyExistsException() {
        super();
    }
}
