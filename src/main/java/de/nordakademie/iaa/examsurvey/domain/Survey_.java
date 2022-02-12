package de.nordakademie.iaa.examsurvey.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

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
}
