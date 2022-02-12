package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepositoryCustom {
    List<Participation> findAllBySurvey(final Survey survey);

    List<Participation> findAllWithSurvey(final Survey survey);

    Optional<Participation> findOneBySurveyAndUser(final Survey survey, final User authenticatedUser);
}
