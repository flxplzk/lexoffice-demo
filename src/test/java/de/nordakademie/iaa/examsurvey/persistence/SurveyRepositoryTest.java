package de.nordakademie.iaa.examsurvey.persistence;

import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.SurveyStatus;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.persistence.specification.SurveySpecifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SurveyRepositoryTest {
    private static final String TEST_SURVEY_FELIX_OPEN = "Test Survey Felix OPEN";
    private static final SurveyStatus[] SURVEY_STATUSES_FOR_INITIATOR = {
            SurveyStatus.CLOSED, SurveyStatus.PRIVATE, SurveyStatus.OPEN};
    private static final SurveyStatus[] SURVEY_STATUSES_FOR_NON_INITIATOR = {SurveyStatus.CLOSED,  SurveyStatus.OPEN};
    private static final Long USER_BENGT_ID = -1L;
    private static final Long USER_ROBERT_ID = -2L;
    private static final Long USER_FELIX_ID = -4L;
    private User USER_BENGT;
    private User USER_ROBERT;
    private User USER_FELIX;

    @Autowired
    private SurveyRepository surveyRepository;

    @Before
    public void setUp() throws Exception {
        USER_BENGT = mock(User.class);
        when(USER_BENGT.getId()).thenReturn(USER_BENGT_ID);

        USER_ROBERT = mock(User.class);
        when(USER_ROBERT.getId()).thenReturn(USER_ROBERT_ID);

        USER_FELIX = mock(User.class);
        when(USER_FELIX.getId()).thenReturn(USER_FELIX_ID);
    }

    @Test
    public void findAll_visibleForUser() {
        // GIVEN

        // WHEN
        List<Survey> all = surveyRepository.findAll(SurveySpecifications.isVisibleForUser(USER_BENGT));

        // THEN
        assertThat(all.size(), is(11));
        all.forEach(survey -> {
            if (USER_BENGT_ID.equals(survey.getInitiator().getId())){
                assertThat(survey.getSurveyStatus(), isIn(SURVEY_STATUSES_FOR_INITIATOR));
            } else {
                assertThat(survey.getSurveyStatus(), isIn(SURVEY_STATUSES_FOR_NON_INITIATOR));
            }
        });
    }

    @Test
    public void findAll_visibleForUser_withOtherUser() {
        // GIVEN

        // WHEN
        List<Survey> all = surveyRepository.findAll(SurveySpecifications.isVisibleForUser(USER_ROBERT));

        // THEN
        assertThat(all.size(), is(11));
        all.forEach(survey -> {
            if (USER_ROBERT_ID.equals(survey.getInitiator().getId())){
                assertThat(survey.getSurveyStatus(), isIn(SURVEY_STATUSES_FOR_INITIATOR));
            } else {
                assertThat(survey.getSurveyStatus(), isIn(SURVEY_STATUSES_FOR_NON_INITIATOR));
            }
        });
    }

    @Test
    public void findOne_byTitle_nonExistent() {
        // GIVEN

        // WHEN
        Optional<Survey> nonExistentTitle = surveyRepository.findOne(SurveySpecifications.hasTitle("NonExistentTitle"));

        // THEN
        assertThat(nonExistentTitle.isPresent(), is(false));
    }

    @Test
    public void findOne_byTitle_existent() {
        // GIVEN

        // WHEN
        Optional<Survey> nonExistentTitle = surveyRepository.findOne(SurveySpecifications.hasTitle(TEST_SURVEY_FELIX_OPEN));

        // THEN
        assertThat(nonExistentTitle.isPresent(), is(true));
    }

    @Test
    public void findOne_byHasIdAndVisible_WithPrivateAndNonInitiator() {
        // GIVEN

        // WHEN
        Optional<Survey> optionalSurvey = surveyRepository.findOne(SurveySpecifications.hasIdAndVisibleForUser(-11L, USER_FELIX));

        // THEN
        assertThat(optionalSurvey.isPresent(), is(false));
    }

    @Test
    public void findOne_byHasIdAndVisible_WithPrivateAndInitiator() {
        // GIVEN

        // WHEN
        Optional<Survey> optionalSurvey = surveyRepository.findOne(SurveySpecifications.hasIdAndVisibleForUser(-11L, USER_BENGT));

        // THEN
        assertThat(optionalSurvey.isPresent(), is(true));
    }

    @Test
    public void findOne_byHasIdAndVisible_WithNonPrivateAndNonInitiator() {
        // GIVEN

        // WHEN
        Optional<Survey> optionalSurvey = surveyRepository.findOne(SurveySpecifications.hasIdAndVisibleForUser(-1L, USER_FELIX));

        // THEN
        assertThat(optionalSurvey.isPresent(), is(true));
    }

    @Test
    public void findOne_byHasIdAndVisible_WithNonPrivateAndInitiator() {
        // GIVEN

        // WHEN
        Optional<Survey> optionalSurvey = surveyRepository.findOne(SurveySpecifications.hasIdAndVisibleForUser(-1L, USER_BENGT));

        // THEN
        assertThat(optionalSurvey.isPresent(), is(true));
    }

    @Test
    public void findOne_byHasIdAndVisible_WithPrivateAndInitiator_AfterDeleting() {
        // GIVEN
        Optional<Survey> optionalSurvey = surveyRepository.findOne(SurveySpecifications.hasIdAndVisibleForUser(-14L, USER_FELIX));

        // WHEN
        assertThat(optionalSurvey.isPresent(), is(true));
        surveyRepository.delete(optionalSurvey.get());

        optionalSurvey = surveyRepository.findOne(SurveySpecifications.hasIdAndVisibleForUser(-14L, USER_FELIX));

        // THEN
        assertThat(optionalSurvey.isPresent(), is(false));

    }
}
