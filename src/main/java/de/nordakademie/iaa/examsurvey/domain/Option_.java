package de.nordakademie.iaa.examsurvey.domain;

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
}
