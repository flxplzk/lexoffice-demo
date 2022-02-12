package de.nordakademie.iaa.examsurvey.persistence.specification;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.Event_;
import de.nordakademie.iaa.examsurvey.domain.Survey_;
import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author Felix Plazek
 */
public class EventSpecifications {

    @SuppressWarnings("unchecked")
    public static Specification<Event> byUser(final User user) {
        return (Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.or(
                user == null ? criteriaBuilder.isNull(root.get(Event_.survey).get(Survey_.initiator))
                        : criteriaBuilder.equal(root.get(Event_.survey).get(Survey_.initiator), user),
                criteriaBuilder.isMember(user, root.get(Event_.participants)));
    }
}
