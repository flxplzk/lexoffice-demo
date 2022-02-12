package de.nordakademie.iaa.examsurvey.domain;

import de.nordakademie.iaa.examsurvey.controller.filtercriterion.FilterCriteria;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link StaticMetamodel} for type {@link Survey}
 *
 * @author felix plazek
 */
@StaticMetamodel(Survey.class)
public class Survey_ extends AuditModel_ {
    public static volatile SingularAttribute<Survey, String> title;
    public static volatile SingularAttribute<Survey, String> description;
    public static volatile SingularAttribute<Survey, User> initiator;
    public static volatile SingularAttribute<Survey, Option> event;
    public static volatile SingularAttribute<Survey, SurveyStatus> surveyStatus;

    /**
     * specifies surveys which the user is allowed to read. with initiator equals the user
     * or Status OPEN or CLOSED
     *
     * @param requestingUser to search for
     * @return specification for surveys visible for the passed user
     */
    public static Specification<Survey> isVisibleForUser(final User requestingUser) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                isInitiator(requestingUser).toPredicate(root, query, criteriaBuilder),
                isOpen().toPredicate(root, query, criteriaBuilder),
                isClosed().toPredicate(root, query, criteriaBuilder)
        );
    }

    public static Specification<Survey> isInitiator(User requestingUser) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Survey_.initiator), requestingUser);
    }

    public static Specification<Survey> isOpen() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(Survey_.surveyStatus), SurveyStatus.OPEN);
    }

    public static Specification<Survey> isClosed() {
        return (root, query, criteriaBuilder) ->
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
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = filterCriteria.stream()
                    .map((FilterCriteria filterCriterion) -> getPredicateFor(filterCriterion, requestingUser))
                    .filter(Objects::nonNull)
                    .map(surveySpecification -> surveySpecification.toPredicate(root, query, criteriaBuilder))
                    .collect(Collectors.toList());
            predicates.add(isVisibleForUser(requestingUser).toPredicate(root, query, criteriaBuilder));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Survey> getPredicateFor(final FilterCriteria filterCriterion,
                                                        final User user) {
        switch (filterCriterion.getFilterType()) {
            case OPEN:
                return isOpen();
            case OWN:
                return isInitiator(user);
            case PARTICIPATED:
                return participated(user);
        }
        throw new IllegalStateException("missing filerCriteria");
    }

    public static Specification<Survey> participated(User user) {
        return (root, query, criteriaBuilder) -> {
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
        return (root, query, criteriaBuilder) -> title == null
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
        return (root, query, criteriaBuilder) -> id == null
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
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                hasId(id).toPredicate(root, query, criteriaBuilder),
                isVisibleForUser(requestingUser).toPredicate(root, query, criteriaBuilder)
        );
    }}
