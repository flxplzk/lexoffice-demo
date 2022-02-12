package de.nordakademie.iaa.examsurvey.persistence.impl;

import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.Survey_;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepositoryCustom;
import de.nordakademie.iaa.examsurvey.persistence.SpecificationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class SurveyRepositoryCustomImpl implements SurveyRepositoryCustom {
    private final SpecificationExecutor specificationExecutor;
    private final Class<Survey> entityClazz;

    @Autowired
    public SurveyRepositoryCustomImpl(final SpecificationExecutor specificationExecutor) {
        this.specificationExecutor = specificationExecutor;
        this.entityClazz = Survey.class;
    }

    @Override
    public Optional<Survey> findOneByIdAndVisibleForUser(final Long identifier,
                                                         final User authenticatedUser) {
        return this.specificationExecutor.findOneBy(
                Survey_.hasIdAndVisibleForUser(identifier, authenticatedUser),
                this.entityClazz
        );
    }

    @Override
    public List<Survey> findAllByIsVisibleForUserWithFilterCriteria(final User requestingUser,
                                                                    final Set<FilterCriteria> filterCriteria) {
        return this.specificationExecutor.findAllBy(
                Survey_.isVisibleForUserWithFilterCriteria(requestingUser, filterCriteria),
                this.entityClazz
        );
    }

    @Override
    public Optional<Survey> findOneByTitle(final String title) {
        return this.specificationExecutor.findOneBy(
                Survey_.hasTitle(title),
                this.entityClazz
        );
    }
}
