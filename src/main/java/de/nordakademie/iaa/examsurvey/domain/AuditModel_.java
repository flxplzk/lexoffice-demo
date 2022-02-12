package de.nordakademie.iaa.examsurvey.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;

/**
 * {@link StaticMetamodel} for type {@link AuditModel}
 *
 * @author felix plazek
 */
@StaticMetamodel(AuditModel.class)
public class AuditModel_ {
    public static volatile SingularAttribute<AuditModel, Long> id;
    public static volatile SingularAttribute<Date, Long> updatedAt;
    public static volatile SingularAttribute<Date, Long> createdAt;
}
