package de.nordakademie.iaa.examsurvey.persistence.specification;

import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Option_;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

/**
 * Specifications for {@link Option}.
 *
 * @author Felix Plazek
 */
public class OptionSpecifications {
    public static Specification<Option> hasSurvey(final Survey survey) {
        return (Specification<Option>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Option_.survey), survey);
    }

    public static Specification<Option> hasDateTime(final Date dateTime) {
        return (Specification<Option>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Option_.dateTime), dateTime);
    }
}
