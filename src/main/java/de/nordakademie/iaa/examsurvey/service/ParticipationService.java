package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.exception.SurveyNotOpenForParticipationException;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRED)
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final SurveyRepository surveyRepository;

    public ParticipationService(final SurveyRepository surveyRepository,
                                    final ParticipationRepository repository) {
        this.surveyRepository = surveyRepository;
        this.participationRepository = repository;
    }

    /**
     * Method delete all Participations for given {@param survey}
     *
     * @param survey for which the participations shall be deleted
     */
    public void deleteAllParticipationsForSurvey(final Survey survey) {
        final List<Participation> participationsToDelete = participationRepository.findAllWithSurvey(survey);
        participationRepository.deleteAll(participationsToDelete);
    }

    /**
     * Loads all Participations for {@param survey} if
     * the survey is visible to the user.
     *
     * @param identifier        of the survey
     * @param authenticatedUser that requests
     * @return all participations for the survey
     */
    public List<Participation> loadAllParticipationsOfSurveyForUser(final Long identifier,
                                                                    final User authenticatedUser) {
        final Survey survey = surveyRepository.findOneByIdAndVisibleForUser(identifier, authenticatedUser)
                .orElseThrow(ResourceNotFoundException::new);
        return participationRepository.findAllWithSurvey(survey);
    }

    /**
     * Saves or updates the {@param participation} if the persisted
     * {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus}
     * is {@link de.nordakademie.iaa.examsurvey.domain.SurveyStatus#OPEN}
     *
     * @param participation     to save
     * @param surveyId        of the survey
     * @param authenticatedUser that requests
     * @return persisted participation
     */
    public Participation saveParticipationForSurveyWithAuthenticatedUser(final Participation participation,
                                                                         final Long surveyId,
                                                                         final User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
        final Survey survey = surveyRepository.findOneByIdAndVisibleForUser(surveyId, authenticatedUser)
                .orElseThrow(ResourceNotFoundException::new);
        requireNonInitiator(survey, authenticatedUser);

        if (!survey.isOpen()) {
            throw new SurveyNotOpenForParticipationException("Participation for this Survey is not Allowed");
        }

        return participationRepository.save(findOrCreateParticipation(
                survey,
                participation,
                authenticatedUser
        ));
    }

    private void requireNonInitiator(final Survey survey, final User authenticatedUser) {
        if (survey.isInitiator(authenticatedUser)) {
            throw new PermissionDeniedException("Initiator may not participate to own survey");
        }
    }

    /**
     * checks if a participation of user for survey exits.
     *
     * @param survey            of participation
     * @param newParticipation  to save
     * @param authenticatedUser to search participation for
     * @return returns persisted one with copied properties, new one if no participation was found
     */
    private Participation findOrCreateParticipation(final Survey survey,
                                                    final Participation newParticipation,
                                                    final User authenticatedUser) {
        final Participation participation = participationRepository.findOneBySurveyAndUser(survey, authenticatedUser)
                .orElse(newParticipation);
        participation.setUser(authenticatedUser);
        participation.setSurvey(survey);
        participation.setOptions(newParticipation.getOptions());
        return participation;
    }
}
