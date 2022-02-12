package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author felix plazek
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface ParticipationService {

    /**
     * Method delete all Participations for given {@param survey}
     *
     * @param survey for which the participations shall be deleted
     */
    void deleteAllParticipationsForSurvey(@NotNull final Survey survey);

    /**
     * Loads all Participations for {@param survey} if
     * the survey is visible to the user.
     *
     * @param identifier        of the survey
     * @param authenticatedUser that requests
     * @return all participations for the survey
     */
    List<Participation> loadAllParticipationsOfSurveyForUser(@NotNull final Long identifier,
                                                             @NotNull final User authenticatedUser);

    /**
     * Saves or updates the {@param participation} if the persisted
     * {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus}
     * is {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus#OPEN}
     *
     * @param participation     to save
     * @param identifier        of the survey
     * @param authenticatedUser that requests
     * @return persisted participation
     */
    Participation saveParticipationForSurveyWithAuthenticatedUser(@NotNull final Participation participation,
                                                                  @NotNull final Long identifier,
                                                                  @NotNull final User authenticatedUser);
}
