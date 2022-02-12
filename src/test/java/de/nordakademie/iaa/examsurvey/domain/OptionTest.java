package de.nordakademie.iaa.examsurvey.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OptionTest {

    @Test
    public void equalsAndHashcode() {
        Survey first = new Survey();
        first.setTitle("lalala");
        Survey seccond = new Survey();
        seccond.setTitle("lololo");

        EqualsVerifier.forClass(Option.class)
                .withRedefinedSuperclass()
                .withPrefabValues(Survey.class, first, seccond)
                .usingGetClass()
                .verify();
    }
}