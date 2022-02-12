package de.nordakademie.iaa.examsurvey.controller;

import com.google.common.collect.Sets;
import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.service.AuthenticationService;
import de.nordakademie.iaa.examsurvey.service.OptionService;
import de.nordakademie.iaa.examsurvey.service.ParticipationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * Controller for resources concerning {@link Survey}
 *
 * @author felix plazek
 */
@RestController
@RequestMapping(value = "/api")
public class SurveyController {
    private static final String PATH_V_IDENTIFIER = "identifier";
    private static final String PATH_SURVEYS = "/surveys";
    private static final String PATH_SURVEY_PARTICIPATIONS = "/surveys/{identifier}/participations";
    private static final String PATH_SURVEY_OPTIONS = "/surveys/{identifier}/options";
    private static final String PATH_SURVEYS_IDENTIFIER = "/surveys/{identifier}";

    private final SurveyService surveyService;
    private final AuthenticationService authenticationService;
    private final OptionService optionService;
    private final ParticipationService participationService;

    @Autowired
    public SurveyController(final SurveyService surveyService,
                            final AuthenticationService authenticationService,
                            final OptionService optionService,
                            final ParticipationService participationService) {
        this.surveyService = surveyService;
        this.authenticationService = authenticationService;
        this.optionService = optionService;
        this.participationService = participationService;
    }

    /**
     * creates and returns Survey.
     *
     * @param survey to create
     * @return persisted Survey
     */
    @RequestMapping(value = PATH_SURVEYS,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Survey createSurvey(@RequestBody Survey survey) {
        return surveyService.createSurvey(survey, getAuthenticatedUser());
    }

    /**
     * updates Survey.
     *
     * @param id     of survey
     * @param survey to update
     * @return updated Survey
     */
    @RequestMapping(value = PATH_SURVEYS + "/{" + PATH_V_IDENTIFIER + "}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Survey updateSurvey(@PathVariable(name = PATH_V_IDENTIFIER) Long id,
                               @RequestBody Survey survey) {
        survey.setId(id);
        return surveyService.update(survey, getAuthenticatedUser());
    }

    /**
     * deletes Survey with {@param id}
     *
     * @param id of survey to delete
     */
    @RequestMapping(value = PATH_SURVEYS + "/{" + PATH_V_IDENTIFIER + "}",
            method = RequestMethod.DELETE)
    public void deleteSurvey(@PathVariable(name = PATH_V_IDENTIFIER) Long id) {
        surveyService.deleteSurvey(id, getAuthenticatedUser());
    }

    /**
     * Loads all Surveys matching optional {@param filterParams}.
     *
     * @param filterParams to filter result with
     * @return retrieved surveys
     */
    @RequestMapping(value = PATH_SURVEYS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Survey> loadSurveys(@RequestParam(name = "filter", required = false) final List<String> filterParams) {
        Set<FilterCriteria> filterCriteria = FilterCriteria.of(
                filterParams != null ? Sets.newHashSet(filterParams) : Sets.newHashSet());
        return surveyService.loadAllSurveysWithFilterCriteriaAndUser(filterCriteria, getAuthenticatedUser());
    }

    /**
     * finds and returns Survey with id equals {@param id}
     *
     * @param id of survey to load
     * @return survey with id {@param id}
     */
    @RequestMapping(value = PATH_SURVEYS_IDENTIFIER,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Survey loadSurvey(@PathVariable(value = PATH_V_IDENTIFIER) final Long id) {
        return surveyService.loadSurveyWithUser(id, getAuthenticatedUser());
    }

    /**
     * loads all options for survey with {@param id}
     *
     * @param id of survey to load the options
     * @return options for the survey
     */
    @RequestMapping(value = PATH_SURVEY_OPTIONS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Option> loadOptions(@PathVariable(value = PATH_V_IDENTIFIER) Long id) {
        return optionService.loadAllOptionsOfSurveyForUser(id, getAuthenticatedUser());
    }

    /**
     * loads all participations for survey with {@param id}
     *
     * @param id of survey to load the participations
     * @return participations for the survey
     */
    @RequestMapping(value = PATH_SURVEY_PARTICIPATIONS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Participation> loadParticipations(@PathVariable(name = PATH_V_IDENTIFIER) Long id) {
        return participationService.loadAllParticipationsOfSurveyForUser(id, getAuthenticatedUser());
    }

    /**
     * crates a new Participation for a survey
     *
     * @param participation    to create
     * @param surveyIdentifier of participation
     * @return created Participation
     */
    @RequestMapping(value = PATH_SURVEY_PARTICIPATIONS,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Participation createParticipationForSurvey(@RequestBody Participation participation,
                                                      @PathVariable(name = PATH_V_IDENTIFIER) Long surveyIdentifier) {
        return participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, surveyIdentifier,
                getAuthenticatedUser());
    }

    /**
     * saves existent participation.
     *
     * @param participation           to update
     * @param surveyIdentifier        of participation
     * @param participationIdentifier of participation
     * @return updated participation
     */
    @RequestMapping(value = PATH_SURVEY_PARTICIPATIONS + "/{participation}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Participation saveParticipationForSurvey(@RequestBody Participation participation,
                                                    @PathVariable(name = PATH_V_IDENTIFIER) Long surveyIdentifier,
                                                    @PathVariable(name = "participation") Long participationIdentifier) {
        participation.setId(participationIdentifier);
        return participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, surveyIdentifier, getAuthenticatedUser());
    }

    private User getAuthenticatedUser() {
        return authenticationService.getCurrentAuthenticatedUser();
    }
}
