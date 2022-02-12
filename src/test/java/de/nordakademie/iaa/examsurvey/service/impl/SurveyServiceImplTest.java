package de.nordakademie.iaa.examsurvey.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.SurveyAlreadyExistsException;
import de.nordakademie.iaa.examsurvey.persistence.OptionRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.persistence.SurveyRepository;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.OptionService;
import de.nordakademie.iaa.examsurvey.service.ParticipationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SurveyServiceImplTest {
    private SurveyService surveyService;
    private OptionRepository optionRepository;
    private SurveyRepository surveyRepository;
    private ParticipationRepository participationRepository;
    private NotificationService notificationService;
    private OptionService optionService;
    private ParticipationService participationService;

    @Before
    public void setUp() throws Exception {
        optionRepository = mock(OptionRepository.class);
        surveyRepository = mock(SurveyRepository.class);
        participationRepository = mock(ParticipationRepository.class);
        notificationService = mock(NotificationService.class);
        optionService = mock(OptionService.class);
        participationService = mock(ParticipationService.class);
        surveyService = new SurveyServiceImpl(surveyRepository,
                notificationService, optionService, participationService);
    }

    @Test(expected = PermissionDeniedException.class)
    public void loadAllSurveysForUser_PermissionDeniedException() {
        // GIVEN
        // WHEN
        surveyService.loadAllSurveysWithFilterCriteriaAndUser(Sets.newHashSet(), null);

        // THEN
        fail();
    }

    @Test
    public void loadAllSurveysForUser_success() {
        // GIVEN
        final User user = mock(User.class);
        final List<Survey> surveys = Lists.newArrayList(mock(Survey.class), mock(Survey.class));
        when(surveyRepository.findAllByIsVisibleForUserWithFilterCriteria(eq(user), any())).thenReturn(surveys);

        // WHEN
        List<Survey> loadAllSurveysWithUser = surveyService.loadAllSurveysWithFilterCriteriaAndUser(Sets.newHashSet(), user);

        // THEN
        assertThat(loadAllSurveysWithUser, is(surveys));
    }

    @Test(expected = PermissionDeniedException.class)
    public void createSurvey_PermissionDeniedException() {
        // GIVEN
        Survey survey = mock(Survey.class);

        // WHEN
        surveyService.createSurvey(survey, null);

        // THEN
        fail();
    }

    @Test(expected = SurveyAlreadyExistsException.class)
    @SuppressWarnings("unchecked")
    public void createSurvey_SurveyAlreadyExistsException() {
        // GIVEN
        Survey survey = mock(Survey.class);
        User initiator = mock(User.class);
        Survey anotherSurvey = mock(Survey.class);

        when(survey.getTitle()).thenReturn("title");
        when(surveyRepository.findOneByTitle("title")).thenReturn(Optional.of(anotherSurvey));

        // WHEN
        surveyService.createSurvey(survey, initiator);

        // THEN
        fail();

    }

    @Test
    @SuppressWarnings("unchecked")
    public void createSurvey_success() {
        // GIVEN
        Survey survey = mock(Survey.class);
        User initiator = mock(User.class);
        Survey returnedSurvey = mock(Survey.class);

        when(survey.getTitle()).thenReturn("title");
        when(surveyRepository.findOneByTitle("title")).thenReturn(Optional.empty());
        when(surveyRepository.save(survey)).thenReturn(returnedSurvey);

        // WHEN
        Survey createdSurvey = surveyService.createSurvey(survey, initiator);

        // THEN
        assertThat(createdSurvey, is(returnedSurvey));
        verify(survey, times(1)).setInitiator(initiator);

    }
}
