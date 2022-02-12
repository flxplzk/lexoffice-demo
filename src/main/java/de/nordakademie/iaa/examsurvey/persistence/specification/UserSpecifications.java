package de.nordakademie.iaa.examsurvey.persistence.specification;

import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Participation_;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.domain.User_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;

/**
 * Specifications for {@link User}.
 *
 * @author Felix Plazek
 */
public class UserSpecifications {
    private UserSpecifications() {
        // prevent instantiation
    }

    public static Specification<User> byUsername(final String username) {
        return (Specification<User>)
                (root, query, criteriaBuilder) -> username == null
                        ? criteriaBuilder.isNull(root.get(User_.username))
                        : criteriaBuilder.equal(root.get(User_.username), username);
    }

    public static Specification<User> participatedSurvey(Survey targetSurvey) {
        return (Specification<User>) (root, query, criteriaBuilder) -> {
            Root<Participation> participationRoot = query.from(Participation.class);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(participationRoot.get(Participation_.survey), targetSurvey)
            );
        };
    }
}
