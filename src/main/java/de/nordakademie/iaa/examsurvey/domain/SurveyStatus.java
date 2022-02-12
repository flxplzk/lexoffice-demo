package de.nordakademie.iaa.examsurvey.domain;

/**
 * Enum for Survey Status.
 *
 * @author Felix Plazek
 */
public enum SurveyStatus {

    /**
     * Status indicates, that the {@link Survey} is created but not open
     * for {@link Participation} and therefore only visible to the initiating
     * {@link User}
     */
    PRIVATE,

    /**
     * Status indicates, that {@link Participation} are highly welcome and the
     * {@link Survey} is visible to every {@link User}
     */
    OPEN,

    /**
     * Status indicates, that the {@link Survey} is over. No more {@link Participation}
     * is possible. But: the Participation is still visible to all users
     */
    CLOSED,
}
