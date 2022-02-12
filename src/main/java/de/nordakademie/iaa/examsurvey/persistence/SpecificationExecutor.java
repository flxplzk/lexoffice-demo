package de.nordakademie.iaa.examsurvey.persistence;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class SpecificationExecutor {

    private final EntityManager entityManager;

    public SpecificationExecutor(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> findAllBy(final Specification<T> specification,
                                 final Class<T> entityClazz) {
        return this.getQuery(specification, entityClazz).getResultList();
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> findOneBy(final Specification<T> specification,
                                     final Class<T> entityClazz) {
        final List resultList = this.getQuery(specification, entityClazz).getResultList();
        if (resultList.size() > 1) {
            throw new IllegalStateException("more than one records found");
        } else if (resultList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((T) resultList.get(0));
    }

    private <T> Query getQuery(final Specification<T> specification,
                               final Class<T> entityClazz) {
        final CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> query = builder.createQuery(entityClazz);
        final Root<T> root = this.applySpecificationToCriteria(specification, entityClazz, query);
        query.select(root);
        return this.entityManager.createQuery(query);
    }

    private <S, U> Root<U> applySpecificationToCriteria(final Specification<U> spec,
                                                        final Class<U> domainClass,
                                                        final CriteriaQuery<S> query) {
        Assert.notNull(domainClass, "Domain class must not be null!");
        Assert.notNull(query, "CriteriaQuery must not be null!");
        Root<U> root = query.from(domainClass);
        if (spec == null) {
            return root;
        } else {
            CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
            Predicate predicate = spec.toPredicate(root, query, builder);
            if (predicate != null) {
                query.where(predicate);
            }

            return root;
        }
    }
}
