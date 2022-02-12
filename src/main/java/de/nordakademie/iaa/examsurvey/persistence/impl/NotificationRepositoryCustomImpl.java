package de.nordakademie.iaa.examsurvey.persistence.impl;

import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.Notification_;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.persistence.NotificationRepositoryCustom;
import de.nordakademie.iaa.examsurvey.persistence.SpecificationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {
    private final SpecificationExecutor specificationExecutor;
    private final Class<Notification> entityClazz;

    @Autowired
    public NotificationRepositoryCustomImpl(final SpecificationExecutor specificationExecutor) {
        this.specificationExecutor = specificationExecutor;
        this.entityClazz = Notification.class;
    }

    @Override
    public List<Notification> findAllByUser(User authenticatedUser) {
        return this.specificationExecutor.findAllBy(
                Notification_.byUser(authenticatedUser),
                this.entityClazz
        );
    }

    @Override
    public List<Notification> findAllBySurvey(Survey survey) {
        return this.specificationExecutor.findAllBy(
                Notification_.bySurvey(survey),
                this.entityClazz
        );
    }
}
