package de.nordakademie.iaa.examsurvey.domain;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * {@link StaticMetamodel} for type {@link User}
 *
 * @author felix plazek
 */
@StaticMetamodel(User.class)
public class User_ extends AuditModel_ {
    protected User_() {
    }

    public static volatile SingularAttribute<User, String> firstName;
    public static volatile SingularAttribute<User, String> lastName;
    public static volatile SingularAttribute<User, String> password;
    public static volatile SingularAttribute<User, String> username;

    public static Specification<User> byUsername(final String username) {
        return (root, query, criteriaBuilder) -> username == null
                ? criteriaBuilder.isNull(root.get(User_.username))
                : criteriaBuilder.equal(root.get(User_.username), username);
    }

    public static Specification<User> participatedSurvey(Survey targetSurvey) {
        return (root, query, criteriaBuilder) -> {
            Root<Participation> participationRoot = query.from(Participation.class);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(participationRoot.get(Participation_.survey), targetSurvey)
            );
        };
    }
}
