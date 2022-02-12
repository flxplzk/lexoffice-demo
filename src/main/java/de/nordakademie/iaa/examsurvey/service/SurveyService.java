package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.exception.SurveyAlreadyExistsException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author felix plazek
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface SurveyService {
    /**
     * Creates survey. Title must be unique otherwise an exeption will be thrown
     *
     * @param survey    to create
     * @param initiator of the survey
     * @return persisted survey
     * @throws SurveyAlreadyExistsException if title is not unique
     */
    Survey createSurvey(@NotNull final Survey survey,
                        @NotNull final User initiator);

    /**
     * Loads all surveys which are relevant for the given {@link User}.
     * Means: all surveys with {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus#OPEN}
     * or {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus#CLOSED} and all
     * survey where the user is the initiator with
     * {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus#PRIVATE}
     *
     * @param filterCriteria
     * @param requestingUser which requests
     * @return all surveys relevant for given {@link User}
     */
    List<Survey> loadAllSurveysWithFilterCriteriaAndUser(Set<FilterCriteria> filterCriteria, @NotNull final User requestingUser);

    /**
     * Loads the requested Survey with id = {@param identifier} for
     * {@param authenticatedUser}.
     *
     * @param identifier        of the requested Survey
     * @param authenticatedUser requesting User
     * @return requested Survey
     * @throws ResourceNotFoundException if the Survey was not found or is Private and
     *                                   therefore only visible for its initiator
     */
    Survey loadSurveyWithUser(@NotNull final Long identifier,
                              @NotNull final User authenticatedUser);

    /**
     * Updates the existing survey plus resets all corresponding {@link Participation}'s of the survey.
     * With Updating all participating user will be notified and then can participate again.
     *
     * @param survey            to be updated
     * @param authenticatedUser of the request
     * @return the updated {@link Survey}
     * @throws PermissionDeniedException if {@param authenticatedUser} is {@code null} or not the
     *                                   initiator of the {@link Survey}
     */
    Survey update(@NotNull final Survey survey,
                  @NotNull final User authenticatedUser);

    /**
     * Sets and persists the Survey with {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus#CLOSED}
     * Survey with status CLOSED can not be changed anymore
     *
     * @param survey            to close
     * @param authenticatedUser that requests
     * @throws PermissionDeniedException if {@param authenticatedUser} is {@code null} or not the
     *                                   initiator of the {@link Survey}
     */
    void closeSurvey(@NotNull final Survey survey,
                     @NotNull final User authenticatedUser);

    /**
     * Deletes the {@link Survey} that corresponds to the given {@param id} if the authenticated user
     * equals the {@link Survey#getInitiator()}
     *
     * @throws PermissionDeniedException if {@param authenticatedUser} is {@code null} or not the
     *                                   initiator of the {@link Survey}
     */
    void deleteSurvey(@NotNull final Long id,
                      @NotNull final User authenticatedUser);
}
