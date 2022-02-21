package de.nordakademie.iaa.examsurvey.domain;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

/**
 * {@link StaticMetamodel} for type {@link Option}
 *
 * @author felix plazek
 */
@StaticMetamodel(Option.class)
public class Option_ extends AuditModel_ {
    public static volatile SingularAttribute<Option, Date> dateTime;
    public static volatile SingularAttribute<Option, Survey> survey;

    public static Specification<Option> hasSurvey(final Survey survey, final boolean sorted) {
        return (root, query, criteriaBuilder) -> {
            if (sorted) {
                query.orderBy(criteriaBuilder.asc(root.get(Option_.dateTime)));
            }
            return criteriaBuilder.equal(root.get(Option_.survey), survey);
        };
    }

    public static Specification<Option> hasDateTime(final Date dateTime) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Option_.dateTime), dateTime);
    }
}
