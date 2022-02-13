package de.nordakademie.iaa.examsurvey.controller;

import com.google.common.collect.Sets;
import de.nordakademie.iaa.examsurvey.controller.dto.OptionDTO;
import de.nordakademie.iaa.examsurvey.controller.dto.ParticipationDTO;
import de.nordakademie.iaa.examsurvey.controller.dto.SurveyDTO;
import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.service.AuthenticationService;
import de.nordakademie.iaa.examsurvey.service.OptionService;
import de.nordakademie.iaa.examsurvey.service.ParticipationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final ModelMapper modelMapper;

    @Autowired
    public SurveyController(final SurveyService surveyService,
                            final AuthenticationService authenticationService,
                            final OptionService optionService,
                            final ParticipationService participationService,
                            final ModelMapper modelMapper) {
        this.surveyService = surveyService;
        this.authenticationService = authenticationService;
        this.optionService = optionService;
        this.participationService = participationService;
        this.modelMapper = modelMapper;
    }

    /**
     * creates and returns Survey.
     *
     * @param survey to create
     * @return persisted Survey
     */
    @PostMapping(
            value = PATH_SURVEYS,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<SurveyDTO> createSurvey(@RequestBody final SurveyDTO survey) {
        final Survey createdSurvey = surveyService.createSurvey(asSurvey(survey), getAuthenticatedUser());

        return ResponseEntity.ok(asSurveyDto(createdSurvey));
    }

    private SurveyDTO asSurveyDto(Survey createdSurvey) {
        return this.modelMapper.map(createdSurvey, SurveyDTO.class);
    }

    private Survey asSurvey(final SurveyDTO survey) {
        return this.modelMapper.map(survey, Survey.class);
    }

    /**
     * updates Survey.
     *
     * @param id     of survey
     * @param survey to update
     * @return updated Survey
     */
    @PutMapping(
            value = PATH_SURVEYS + "/{" + PATH_V_IDENTIFIER + "}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SurveyDTO> updateSurvey(
            @PathVariable(name = PATH_V_IDENTIFIER) Long id,
            @RequestBody SurveyDTO survey) {
        survey.setId(id);
        final Survey updatedSurvey = surveyService.update(
                asSurvey(survey),
                getAuthenticatedUser()
        );

        return ResponseEntity.ok(asSurveyDto(updatedSurvey));
    }

    /**
     * deletes Survey with {@param id}
     *
     * @param id of survey to delete
     */
    @DeleteMapping(value = PATH_SURVEYS + "/{" + PATH_V_IDENTIFIER + "}")
    public void deleteSurvey(@PathVariable(name = PATH_V_IDENTIFIER) Long id) {
        surveyService.deleteSurvey(id, getAuthenticatedUser());
    }

    /**
     * Loads all Surveys matching optional {@param filterParams}.
     *
     * @param filterParams to filter result with
     * @return retrieved surveys
     */
    @GetMapping(
            value = PATH_SURVEYS,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<SurveyDTO>> loadSurveys(@RequestParam(name = "filter", required = false) final List<String> filterParams) {
        final Set<FilterCriteria> filterCriteria = FilterCriteria.of(filterParams != null ? Sets.newHashSet(filterParams) : Sets.newHashSet());
        final List<SurveyDTO> surveys = surveyService.loadAllSurveysWithFilterCriteriaAndUser(filterCriteria, getAuthenticatedUser()).stream()
                .map(this::asSurveyDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(surveys);
    }

    /**
     * finds and returns Survey with id equals {@param id}
     *
     * @param id of survey to load
     * @return survey with id {@param id}
     */
    @GetMapping(
            value = PATH_SURVEYS_IDENTIFIER,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SurveyDTO> loadSurvey(@PathVariable(value = PATH_V_IDENTIFIER) final Long id) {
        final Survey survey = surveyService.loadSurveyWithUser(id, getAuthenticatedUser());

        return ResponseEntity.ok(asSurveyDto(survey));
    }

    /**
     * loads all options for survey with {@param id}
     *
     * @param id of survey to load the options
     * @return options for the survey
     */
    @GetMapping(
            value = PATH_SURVEY_OPTIONS,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<OptionDTO>> loadOptions(@PathVariable(value = PATH_V_IDENTIFIER) Long id) {
        final List<OptionDTO> optionsDtos = optionService.loadAllOptionsOfSurveyForUser(id, getAuthenticatedUser()).stream()
                .map(this::asOptionDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(optionsDtos);
    }

    private OptionDTO asOptionDto(final Option option) {
        return this.modelMapper.map(option, OptionDTO.class);
    }

    /**
     * loads all participations for survey with {@param id}
     *
     * @param id of survey to load the participations
     * @return participations for the survey
     */
    @GetMapping(
            value = PATH_SURVEY_PARTICIPATIONS,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<ParticipationDTO>> loadParticipations(@PathVariable(name = PATH_V_IDENTIFIER) Long id) {
        final List<ParticipationDTO> participations = participationService.loadAllParticipationsOfSurveyForUser(
                        id,
                        getAuthenticatedUser()
                ).stream()
                .map(this::asParticipationDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(participations);
    }

    private ParticipationDTO asParticipationDto(final Participation participation) {
        return this.modelMapper.map(participation, ParticipationDTO.class);
    }

    /**
     * crates a new Participation for a survey
     *
     * @param participation    to create
     * @param surveyIdentifier of participation
     * @return created Participation
     */
    @PostMapping(
            value = PATH_SURVEY_PARTICIPATIONS,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<ParticipationDTO> createParticipationForSurvey(@RequestBody Participation participation,
                                                      @PathVariable(name = PATH_V_IDENTIFIER) Long surveyIdentifier) {
        final Participation createdParticipation = participationService.saveParticipationForSurveyWithAuthenticatedUser(
                participation,
                surveyIdentifier,
                getAuthenticatedUser()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(asParticipationDto(createdParticipation));
    }

    /**
     * saves existent participation.
     *
     * @param participation           to update
     * @param surveyIdentifier        of participation
     * @param participationIdentifier of participation
     * @return updated participation
     */
    @PutMapping(
            value = PATH_SURVEY_PARTICIPATIONS + "/{participation}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<ParticipationDTO> saveParticipationForSurvey(@RequestBody ParticipationDTO participation,
                                                    @PathVariable(name = PATH_V_IDENTIFIER) Long surveyIdentifier,
                                                    @PathVariable(name = "participation") Long participationIdentifier) {
        participation.setId(participationIdentifier);
        final Participation savedParticipation = participationService.saveParticipationForSurveyWithAuthenticatedUser(
                asParticipation(participation),
                surveyIdentifier,
                getAuthenticatedUser()
        );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(asParticipationDto(savedParticipation));
    }

    private Participation asParticipation(ParticipationDTO participation) {
        return this.modelMapper.map(participation, Participation.class);
    }

    private User getAuthenticatedUser() {
        return authenticationService.getCurrentAuthenticatedUser();
    }
}
