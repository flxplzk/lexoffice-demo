package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.*;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.exception.SurveyAlreadyExistsException;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(propagation = Propagation.REQUIRED)
public class SurveyService {
    private final NotificationService notificationService;
    private final OptionService optionService;
    private final ParticipationService participationService;
    private final SurveyRepository surveyRepository;

    public SurveyService(final SurveyRepository surveyRepository,
                             final NotificationService notificationService,
                             final OptionService optionService,
                             final ParticipationService participationService) {
        this.surveyRepository = surveyRepository;
        this.notificationService = notificationService;
        this.optionService = optionService;
        this.participationService = participationService;
    }

    /**
     * Creates survey. Title must be unique otherwise an exeption will be thrown
     *
     * @param survey    to create
     * @param initiator of the survey
     * @return persisted survey
     * @throws SurveyAlreadyExistsException if title is not unique
     */
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
     * Updates the existing survey plus resets all corresponding {@link Participation}'s of the survey.
     * With Updating all participating user will be notified and then can participate again.
     *
     * @param survey            to be updated
     * @param authenticatedUser of the request
     * @return the updated {@link Survey}
     * @throws PermissionDeniedException if {@param authenticatedUser} is {@code null} or not the
     *                                   initiator of the {@link Survey}
     */
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
     * Sets and persists the Survey with {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus#CLOSED}
     * Survey with status CLOSED can not be changed anymore
     *
     * @param surveyToClose     to close
     * @param authenticatedUser that requests
     * @throws PermissionDeniedException if {@param authenticatedUser} is {@code null} or not the
     *                                   initiator of the {@link Survey}
     */
    public void closeSurvey(final Survey surveyToClose,
                            final User authenticatedUser) {
        final Survey persistedSurvey = findModifiableSurveyWithInitiator(surveyToClose, authenticatedUser);
        persistedSurvey.setSurveyStatus(SurveyStatus.CLOSED);
        surveyRepository.save(persistedSurvey);
    }

    /**
     * Deletes the {@link Survey} that corresponds to the given {@param id} if the authenticated user
     * equals the {@link Survey#getInitiator()}
     *
     * @throws PermissionDeniedException if {@param authenticatedUser} is {@code null} or not the
     *                                   initiator of the {@link Survey}
     */
    public void deleteSurvey(final Long id, final User authenticatedUser) {
        final Survey existentSurvey = findDeletableSurveyWithInitiator(id, authenticatedUser);
        participationService.deleteAllParticipationsForSurvey(existentSurvey);
        optionService.deleteAllOptionsForSurvey(existentSurvey);
        notificationService.deleteAllNotificationsForSurvey(existentSurvey);
        surveyRepository.delete(existentSurvey);
    }

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
    public List<Survey> loadAllSurveysWithFilterCriteriaAndUser(final Set<FilterCriteria> filterCriteria,
                                                                final User requestingUser) {
        requireInitiator(requestingUser);
        return surveyRepository.findAllByIsVisibleForUserWithFilterCriteria(requestingUser, filterCriteria);
    }

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

