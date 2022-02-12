package de.nordakademie.iaa.examsurvey.exception;

/**
 * ParentException for this application all {@link RuntimeException}'s will be thrown as type of this Exception
 *
 * @author Felix Plazek
 */
abstract class SurveyApplicationException extends RuntimeException {
    SurveyApplicationException() {
    }

    SurveyApplicationException(String message) {
        super(message);
    }
}
