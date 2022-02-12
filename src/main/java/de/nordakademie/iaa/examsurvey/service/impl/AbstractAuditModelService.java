package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;

import static de.nordakademie.iaa.examsurvey.persistence.specification.SurveySpecifications.hasIdAndVisibleForUser;

/**
 * @author felix plazek
 */
abstract class AbstractAuditModelService {

    protected final SurveyRepository surveyRepository;

    AbstractAuditModelService(final SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    void requireNonNullUser(final User initiator) {
        if (initiator == null) {
            throw new PermissionDeniedException("initiator must be non null");
        }
    }

    protected Survey getSurveyVisibleForUser(final Long identifier, final User authenticatedUser) {
        return surveyRepository.findOne(hasIdAndVisibleForUser(identifier, authenticatedUser))
                .orElseThrow(ResourceNotFoundException::new);
    }
}
