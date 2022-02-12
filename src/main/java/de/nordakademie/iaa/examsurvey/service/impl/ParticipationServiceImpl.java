package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.SurveyStatus;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.SurveyNotOpenForParticipationException;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;
import de.nordakademie.iaa.examsurvey.service.ParticipationService;

import java.util.List;

import static de.nordakademie.iaa.examsurvey.persistence.specification.ParticipationSpecifications.withSurvey;
import static de.nordakademie.iaa.examsurvey.persistence.specification.ParticipationSpecifications.withSurveyAndUser;

/**
 * @author felix plazek
 */
public class ParticipationServiceImpl extends AbstractAuditModelService implements ParticipationService {
    private final ParticipationRepository participationRepository;

    public ParticipationServiceImpl(final SurveyRepository surveyRepository,
                                    final ParticipationRepository repository) {
        super(surveyRepository);
        this.participationRepository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllParticipationsForSurvey(final Survey survey) {
        List<Participation> participationsToDelete = participationRepository.findAll(withSurvey(survey));
        participationRepository.deleteAll(participationsToDelete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Participation> loadAllParticipationsOfSurveyForUser(final Long identifier,
                                                                    final User authenticatedUser) {
        Survey survey = getSurveyVisibleForUser(identifier, authenticatedUser);
        return participationRepository.findAll(withSurvey(survey));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participation saveParticipationForSurveyWithAuthenticatedUser(Participation participation,
                                                                         final Long surveyId,
                                                                         final User authenticatedUser) {
        requireNonNullUser(authenticatedUser);
        final Survey survey = getSurveyVisibleForUser(surveyId, authenticatedUser);
        requireNonInitiator(survey, authenticatedUser);

        requireOpenForParticipation(survey);
        participation = requireOne(survey, participation, authenticatedUser);

        return participationRepository.save(participation);
    }

    // #################################### VALIDATION METHODS #########################################

    private void requireNonInitiator(final Survey survey, final User authenticatedUser) {
        if (survey.getInitiator().equals(authenticatedUser)) {
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
    private Participation requireOne(final Survey survey,
                                     final Participation newParticipation,
                                     final User authenticatedUser) {
        Participation participation = participationRepository.findOne(withSurveyAndUser(survey, authenticatedUser))
                .orElse(newParticipation);
        participation.setUser(authenticatedUser);
        participation.setSurvey(survey);
        participation.setOptions(newParticipation.getOptions());
        return participation;
    }

    private void requireOpenForParticipation(final Survey survey) {
        if (SurveyStatus.OPEN.equals(survey.getSurveyStatus())) {
            return;
        }
        throw new SurveyNotOpenForParticipationException("Participation for this Survey is not Allowed");
    }

}
