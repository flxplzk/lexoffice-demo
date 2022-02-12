package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.exception.SurveyNotOpenForParticipationException;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;
import de.nordakademie.iaa.examsurvey.service.ParticipationService;

import java.util.List;

/**
 * @author felix plazek
 */
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final SurveyRepository surveyRepository;

    public ParticipationServiceImpl(final SurveyRepository surveyRepository,
                                    final ParticipationRepository repository) {
        this.surveyRepository = surveyRepository;
        this.participationRepository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllParticipationsForSurvey(final Survey survey) {
        final List<Participation> participationsToDelete = participationRepository.findAllWithSurvey(survey);
        participationRepository.deleteAll(participationsToDelete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Participation> loadAllParticipationsOfSurveyForUser(final Long identifier,
                                                                    final User authenticatedUser) {
        final Survey survey = surveyRepository.findOneByIdAndVisibleForUser(identifier, authenticatedUser)
                .orElseThrow(ResourceNotFoundException::new);
        return participationRepository.findAllWithSurvey(survey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
