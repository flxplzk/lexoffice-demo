package de.nordakademie.iaa.examsurvey.persistence.impl;

import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Option_;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.persistence.OptionRepositoryCustom;
import de.nordakademie.iaa.examsurvey.persistence.SpecificationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OptionRepositoryCustomImpl implements OptionRepositoryCustom {
    private final SpecificationExecutor specificationExecutor;
    private final Class<Option> entityClazz;

    @Autowired
    public OptionRepositoryCustomImpl(final SpecificationExecutor specificationExecutor) {
        this.specificationExecutor = specificationExecutor;
        this.entityClazz = Option.class;
    }

    @Override
    public List<Option> findAllBySurvey(final Survey survey, final boolean sorted) {
        return this.specificationExecutor.findAllBy(
                Option_.hasSurvey(survey, sorted),
                this.entityClazz
        );
    }
}
