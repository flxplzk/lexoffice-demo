package de.nordakademie.iaa.examsurvey.persistence.impl;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Participation_;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepositoryCustom;
import de.nordakademie.iaa.examsurvey.persistence.SpecificationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParticipationRepositoryCustomImpl implements ParticipationRepositoryCustom {
    private final SpecificationExecutor specificationExecutor;
    private final Class<Participation> entityClazz;

    @Autowired
    public ParticipationRepositoryCustomImpl(final SpecificationExecutor specificationExecutor) {
        this.specificationExecutor = specificationExecutor;
        this.entityClazz = Participation.class;
    }

    @Override
    public List<Participation> findAllBySurvey(final Survey survey) {
        return this.specificationExecutor.findAllBy(
                Participation_.bySurvey(survey),
                this.entityClazz
        );
    }

    @Override
    public List<Participation> findAllWithSurvey(Survey survey) {
        return this.specificationExecutor.findAllBy(
                Participation_.withSurvey(survey),
                this.entityClazz
        );
    }

    @Override
    public Optional<Participation> findOneBySurveyAndUser(Survey survey, User authenticatedUser) {
        return this.specificationExecutor.findOneBy(
                Participation_.withSurveyAndUser(survey, authenticatedUser),
                this.entityClazz);
    }
}
