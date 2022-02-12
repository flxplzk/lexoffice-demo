package de.nordakademie.iaa.examsurvey.persistence.specification;

import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.Notification_;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for {@link Notification}.
 *
 * @author Felix Plazek
 */
public class NotificationSpecifications {
    public static Specification<Notification> byUser(User authenticatedUser) {
        return (Specification<Notification>) (root, query, criteriaBuilder) -> authenticatedUser == null
                ? criteriaBuilder.isNull(root.get(Notification_.user))
                : criteriaBuilder.equal(root.get(Notification_.user), authenticatedUser);
    }

    public static Specification<Notification> bySurvey(final Survey survey) {
        return (Specification<Notification>) (root, query, criteriaBuilder) -> survey == null
                ? criteriaBuilder.isNull(root.get(Notification_.survey))
                : criteriaBuilder.equal(root.get(Notification_.survey), survey);
    }
}
