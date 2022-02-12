package de.nordakademie.iaa.examsurvey.domain;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * {@link StaticMetamodel} for type {@link Participation}
 *
 * @author felix plazek
 */
@StaticMetamodel(Participation.class)
public class Participation_ extends AuditModel_ {
    public static volatile SingularAttribute<Participation, User> user;
    public static volatile SingularAttribute<Participation, Survey> survey;
    public static volatile SetAttribute<Participation, Option> options;

    public static Specification<Participation> withSurvey(final Survey survey) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Participation_.survey), survey);
    }

    public static Specification<Participation> withSurveyAndUser(final Survey survey, final User user) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(Participation_.survey), survey),
                        criteriaBuilder.equal(root.get(Participation_.user), user)
                );
    }

    public static Specification<Participation> bySurvey(final Survey survey) {
        return (root, query, criteriaBuilder) -> survey == null
                ? criteriaBuilder.isNull(root.get(Participation_.survey))
                : criteriaBuilder.equal(root.get(Participation_.survey), survey);
    }
}
