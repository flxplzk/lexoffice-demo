package de.nordakademie.iaa.examsurvey.domain;

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
}
