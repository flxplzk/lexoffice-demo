package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SurveyRepositoryCustom {
    Optional<Survey> findOneByIdAndVisibleForUser(final Long identifier,
                                                  final User authenticatedUser);

    List<Survey> findAllByIsVisibleForUserWithFilterCriteria(final User requestingUser,
                                                             final Set<FilterCriteria> filterCriteria);

    Optional<Survey> findOneByTitle(final String title);
}
