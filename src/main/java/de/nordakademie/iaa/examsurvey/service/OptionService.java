package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author felix plazek
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface OptionService {
    /**
     * retrieves all {@link Option}'s which are visible for the authenticated User.
     *
     * @param surveyId          of the Survey
     * @param authenticatedUser which requests
     * @return list of options belonging to the survey
     */
    List<Option> loadAllOptionsOfSurveyForUser(@NotNull final Long surveyId,
                                               @NotNull final User authenticatedUser);

    /**
     * saves all option for {@param survey}
     *
     * @param options to save
     * @param survey  the {@param options} belong
     */
    void saveOptionsForSurvey(@NotNull final Set<Option> options,
                              @NotNull final Survey survey);

    /**
     * updates all options of {@param survey}
     *
     * @param survey with options
     */
    void updateOptionsForSurvey(@NotNull final Survey survey);

    /**
     * deletes all option of survey
     *
     * @param existentSurvey to delete options of
     */
    void deleteAllOptionsForSurvey(@NotNull final Survey existentSurvey);
}
