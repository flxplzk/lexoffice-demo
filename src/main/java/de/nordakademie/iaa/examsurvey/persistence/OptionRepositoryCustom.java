package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Survey;

import java.util.List;

public interface OptionRepositoryCustom {
    List<Option> findAllBySurvey(final Survey survey, final boolean sorted);
}
