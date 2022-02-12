package de.nordakademie.iaa.examsurvey.persistence.specification;

import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Participation_;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.SurveyStatus;
import de.nordakademie.iaa.examsurvey.domain.Survey_;
import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Specifications for {@link Survey}.
 *
 * @author Felix Plazek
 */
public class SurveySpecifications {
    private SurveySpecifications() {
        // prevent instantiation
    }

    /**
     * specifies surveys which the user is allowed to read. with initiator equals the user
     * or Status OPEN or CLOSED
     *
     * @param requestingUser to search for
     * @return specification for surveys visible for the passed user
     */
    public static Specification<Survey> isVisibleForUser(final User requestingUser) {
        return (Specification<Survey>) (root, query, criteriaBuilder) -> criteriaBuilder.or(
                isInitiator(requestingUser).toPredicate(root, query, criteriaBuilder),
                isOpen().toPredicate(root, query, criteriaBuilder),
                isClosed().toPredicate(root, query, criteriaBuilder)
        );
    }

    private static Specification<Survey> isInitiator(User requestingUser) {
        return (Specification<Survey>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Survey_.initiator), requestingUser);
    }

    private static Specification<Survey> isOpen() {
        return (Specification<Survey>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Survey_.surveyStatus), SurveyStatus.OPEN);
    }

    private static Specification<Survey> isClosed() {
        return (Specification<Survey>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Survey_.surveyStatus), SurveyStatus.CLOSED);
    }

    /**
     * specifies surveys which the user is allowed to read. with initiator equals the user
     * or Status OPEN or CLOSED
     *
     * @param requestingUser to search for
     * @return specification for surveys visible for the passed user
     */
    public static Specification<Survey> isVisibleForUserWithFilterCriteria(final User requestingUser,
                                                                           final Set<FilterCriteria> filterCriteria) {
        return (Specification<Survey>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = filterCriteria.stream()
                    .map((FilterCriteria filterCriterion) -> getPredicateFor(filterCriterion, requestingUser))
                    .filter(Objects::nonNull)
                    .map(surveySpecification -> surveySpecification.toPredicate(root, query, criteriaBuilder))
                    .collect(Collectors.toList());
            predicates.add(isVisibleForUser(requestingUser).toPredicate(root, query, criteriaBuilder));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    private static Specification<Survey> getPredicateFor(FilterCriteria filterCriterion, User user) {
        Specification<Survey> specification = null;
        switch (filterCriterion.getFilterType()) {
            case OPEN:
                specification = isOpen();
                break;
            case OWN:
                specification = isInitiator(user);
                break;
            case PARTICIPATED:
                specification = participated(user);
                break;
        }
        return specification;
    }

    private static Specification<Survey> participated(User user) {
        return (Specification<Survey>) (root, query, criteriaBuilder) -> {
            Root<Participation> participationRoot = query.from(Participation.class);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root, participationRoot.get(Participation_.survey)),
                    criteriaBuilder.equal(participationRoot.get(Participation_.user), user)
            );
        };
    }

    /**
     * specifies the survey with given title
     *
     * @param title for searched survey
     * @return specification for a survey title
     */
    public static Specification<Survey> hasTitle(final String title) {
        return (Specification<Survey>) (root, query, criteriaBuilder) -> title == null
                ? criteriaBuilder.isNull(root.get(Survey_.title))
                : criteriaBuilder.equal(root.get(Survey_.title), title);
    }

    /**
     * specifies a survey with given id
     *
     * @param id for searched survey
     * @return specification for a survey title
     */
    public static Specification<Survey> hasId(final Long id) {
        return (Specification<Survey>) (root, query, criteriaBuilder) -> id == null
                ? criteriaBuilder.isNull(root.get(Survey_.id))
                : criteriaBuilder.equal(root.get(Survey_.id), id);
    }

    /**
     * specifies the survey with given title
     *
     * @param id of the survey
     * @return specification for a survey title
     */
    public static Specification<Survey> hasIdAndVisibleForUser(final Long id, final User requestingUser) {
        return (Specification<Survey>) (root, query, criteriaBuilder) -> criteriaBuilder.and(
                hasId(id).toPredicate(root, query, criteriaBuilder),
                isVisibleForUser(requestingUser).toPredicate(root, query, criteriaBuilder)
        );
    }
}
