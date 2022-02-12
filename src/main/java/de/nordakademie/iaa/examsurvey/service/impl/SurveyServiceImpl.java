package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.NotificationType;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.SurveyStatus;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.exception.SurveyAlreadyExistsException;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.OptionService;
import de.nordakademie.iaa.examsurvey.service.ParticipationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;

import java.util.List;
import java.util.Set;

/**
 * UserService implementation.
 *
 * @author felix plazek
 */
public class SurveyServiceImpl implements SurveyService {
    private final NotificationService notificationService;
    private final OptionService optionService;
    private final ParticipationService participationService;
    private final SurveyRepository surveyRepository;

    public SurveyServiceImpl(final SurveyRepository surveyRepository,
                             final NotificationService notificationService,
                             final OptionService optionService,
                             final ParticipationService participationService) {
        this.surveyRepository = surveyRepository;
        this.notificationService = notificationService;
        this.optionService = optionService;
        this.participationService = participationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Survey createSurvey(final Survey survey, final User initiator) {
        requireInitiator(initiator);
        requireNonExistent(survey);
        survey.setInitiator(initiator);
        Survey createdSurvey = surveyRepository.save(survey);
        optionService.saveOptionsForSurvey(survey.getOptions(), createdSurvey);
        return createdSurvey;
    }

    private void requireInitiator(User initiator) {
        if (initiator == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Survey update(final Survey survey, final User authenticatedUser) {
        final Survey persistedSurvey = findModifiableSurveyWithInitiator(survey, authenticatedUser);
        notificationService.notifyUsersWithNotificationType(NotificationType.SURVEY_CHANGE, survey);
        participationService.deleteAllParticipationsForSurvey(survey);
        optionService.updateOptionsForSurvey(survey);
        // For not getting trouble with JPA, only modifiable field values are copied to the persisted survey
        persistedSurvey.setDescription(survey.getDescription());
        persistedSurvey.setSurveyStatus(survey.getSurveyStatus());
        return surveyRepository.save(persistedSurvey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeSurvey(final Survey surveyToClose, final User authenticatedUser) {
        final Survey persistedSurvey = findModifiableSurveyWithInitiator(surveyToClose, authenticatedUser);
        persistedSurvey.setSurveyStatus(SurveyStatus.CLOSED);
        surveyRepository.save(persistedSurvey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSurvey(final Long id, final User authenticatedUser) {
        final Survey existentSurvey = findDeletableSurveyWithInitiator(id, authenticatedUser);
        participationService.deleteAllParticipationsForSurvey(existentSurvey);
        optionService.deleteAllOptionsForSurvey(existentSurvey);
        notificationService.deleteAllNotificationsForSurvey(existentSurvey);
        surveyRepository.delete(existentSurvey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Survey> loadAllSurveysWithFilterCriteriaAndUser(final Set<FilterCriteria> filterCriteria,
                                                                final User requestingUser) {
        requireInitiator(requestingUser);
        return surveyRepository.findAllByIsVisibleForUserWithFilterCriteria(requestingUser, filterCriteria);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Survey loadSurveyWithUser(final Long identifier, final User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
        return surveyRepository.findOneByIdAndVisibleForUser(identifier, authenticatedUser)
                .orElseThrow(ResourceNotFoundException::new);
    }

    // ########################################## VALIDATION METHODS ###################################################

    private Survey findDeletableSurveyWithInitiator(Long id, User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
        Survey existentSurvey = surveyRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        requireInitiator(authenticatedUser, existentSurvey);
        return existentSurvey;
    }

    private Survey findModifiableSurveyWithInitiator(Survey survey, User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
        final Survey persistedSurvey = getExistent(survey);
        requireInitiator(authenticatedUser, persistedSurvey);
        requireValidStatus(persistedSurvey.getSurveyStatus());
        if (isSurveyClose(survey, persistedSurvey)) {
            throw new PermissionDeniedException("Manual closing of survey prohibited. You must create an event for this Survey");
        }
        return persistedSurvey;
    }

    private boolean isSurveyClose(Survey survey, Survey persistedSurvey) {
        return SurveyStatus.CLOSED.equals(survey.getSurveyStatus())
                && !SurveyStatus.CLOSED.equals(persistedSurvey.getSurveyStatus());
    }

    private void requireValidStatus(SurveyStatus persistedState) {
        if (SurveyStatus.CLOSED.equals(persistedState)) {
            throw new PermissionDeniedException("Surveys with status CLOSED may not be modified. Surveys must be closed us");
        }
    }

    private Survey getExistent(Survey survey) {
        return surveyRepository.findById(survey.getId())
                .orElseThrow(ResourceNotFoundException::new);
    }

    private void requireNonExistent(final Survey survey) {
        // if survey with title already exists; throw exception
        if (surveyRepository.findOneByTitle(survey.getTitle()).isPresent()) {
            throw new SurveyAlreadyExistsException();
        }
    }

    private void requireInitiator(final User requestingUser, final Survey survey) {
        if (!survey.getInitiator().equals(requestingUser)) {
            throw new PermissionDeniedException("User must be initiator of the survey");
        }
    }
}

