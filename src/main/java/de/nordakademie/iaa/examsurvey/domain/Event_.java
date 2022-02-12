package de.nordakademie.iaa.examsurvey.domain;

import org.springframework.data.jpa.domain.Specification;

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

    public static Specification<Event> byUser(final User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                user == null ? criteriaBuilder.isNull(root.get(Event_.survey).get(Survey_.initiator))
                        : criteriaBuilder.equal(root.get(Event_.survey).get(Survey_.initiator), user),
                criteriaBuilder.isMember(user, root.get(Event_.participants)));
    }

}
