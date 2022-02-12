package de.nordakademie.iaa.examsurvey.persistence.impl;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.Event_;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.persistence.EventRepositoryCustom;
import de.nordakademie.iaa.examsurvey.persistence.SpecificationExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    private final SpecificationExecutor specificationExecutor;
    private final Class<Event> entityClazz;

    @Autowired
    public EventRepositoryCustomImpl(final SpecificationExecutor specificationExecutor) {
        this.specificationExecutor = specificationExecutor;
        this.entityClazz = Event.class;
    }

    @Override
    public List<Event> findAllByUser(User authenticatedUser) {
        return this.specificationExecutor.findAllBy(
                Event_.byUser(authenticatedUser),
                this.entityClazz
        );
    }
}
