package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.MissingDataException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.persistence.OptionRepository;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(propagation = Propagation.REQUIRED)
public class OptionService {
    private final OptionRepository optionRepository;
    private final SurveyRepository surveyRepository;

    public OptionService(final SurveyRepository surveyRepository,
                             final OptionRepository repository) {
        this.surveyRepository = surveyRepository;
        this.optionRepository = repository;
    }

    /**
     * updates all options of {@param survey}
     *
     * @param survey with options
     */
    public void updateOptionsForSurvey(final Survey survey) {
        if (survey.getOptions() == null) {
            throw new MissingDataException("User must provide at least one Option for a survey; aborted update!");
        }
        final Set<Long> updatedOptions = survey.getOptions().stream()
                .map(Option::getId)
                .collect(Collectors.toSet());

        // determine if the user deleted options
        final List<Option> toDelete = findOptionsBySurvey(survey).stream()
                .filter(option -> !updatedOptions.contains(option.getId()))
                .collect(Collectors.toList());
        // delete them
        optionRepository.deleteAll(toDelete);

        // create or update the others
        this.saveOptionsForSurvey(survey.getOptions(), survey);
    }

    /**
     * saves all option for {@param survey}
     *
     * @param options to save
     * @param survey  the {@param options} belong
     */
    public void saveOptionsForSurvey(final Set<Option> options,
                                     final Survey survey) {
        options.forEach(option -> option.setSurvey(survey));
        optionRepository.saveAll(options);
    }

    /**
     * deletes all option of survey
     *
     * @param existentSurvey to delete options of
     */
    public void deleteAllOptionsForSurvey(final Survey existentSurvey) {
        List<Option> persisted = findOptionsBySurvey(existentSurvey);
        optionRepository.deleteAll(persisted);
    }

    /**
     * retrieves all {@link Option}'s which are visible for the authenticated User.
     *
     * @param surveyId          of the Survey
     * @param authenticatedUser which requests
     * @return list of options belonging to the survey
     */
    public List<Option> loadAllOptionsOfSurveyForUser(final Long surveyId,
                                                      final User authenticatedUser) {
        final Survey survey = this.surveyRepository.findOneByIdAndVisibleForUser(surveyId, authenticatedUser)
                .orElseThrow(ResourceNotFoundException::new);
        return optionRepository.findAllBySurvey(survey, true);
    }

    private List<Option> findOptionsBySurvey(final Survey survey) {
        return optionRepository.findAllBySurvey(survey, false);
    }
}
