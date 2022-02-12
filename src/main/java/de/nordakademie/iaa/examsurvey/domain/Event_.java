package de.nordakademie.iaa.examsurvey.domain;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * {@link StaticMetamodel} for type {@link Event}
 *
 * @author felix plazek
 */
@StaticMetamodel(Event.class)
public class Event_ extends AuditModel_ {
    public static volatile SingularAttribute<Event, String> title;
    public static volatile SingularAttribute<Event, Survey> survey;
    public static volatile SetAttribute<Event, User> participants;
}
