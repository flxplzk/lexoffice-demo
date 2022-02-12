package de.nordakademie.iaa.examsurvey.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author felix plazek
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class SurveyNotOpenForParticipationException extends SurveyApplicationException {
    public SurveyNotOpenForParticipationException(String message) {
        super(message);
    }
}
