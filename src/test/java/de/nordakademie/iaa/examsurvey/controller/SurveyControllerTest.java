package de.nordakademie.iaa.examsurvey.controller;

import com.google.common.collect.Lists;
import de.nordakademie.iaa.examsurvey.domain.Option;
import de.nordakademie.iaa.examsurvey.domain.Participation;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.service.AuthenticationService;
import de.nordakademie.iaa.examsurvey.service.OptionService;
import de.nordakademie.iaa.examsurvey.service.ParticipationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SurveyControllerTest {
    private SurveyController controllerUnderTest;
    private SurveyService surveyService;
    private AuthenticationService authenticationService;
    private ParticipationService participationService;
    private OptionService optionService;

    @Before
    public void setUp() {
        surveyService = mock(SurveyService.class);
        authenticationService = mock(AuthenticationService.class);
        participationService = mock(ParticipationService.class);
        optionService = mock(OptionService.class);
        controllerUnderTest = new SurveyController(surveyService, authenticationService, optionService, participationService);
    }

    @Test
    public void createSurvey() {
        // GIVEN
        Survey survey = mock(Survey.class);
        Survey anotherSurvey = mock(Survey.class);
        User user = mock(User.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(surveyService.createSurvey(survey, user)).thenReturn(anotherSurvey);

        // WHEN
        Survey createdSurvey = controllerUnderTest.createSurvey(survey);

        // THEN
        assertThat(createdSurvey, is(anotherSurvey));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();

    }

    @Test
    public void loadSurveys() {
        // GIVEN
        List<Survey> surveys = Lists.newArrayList(mock(Survey.class),
                mock(Survey.class));
        User user = mock(User.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(surveyService.loadAllSurveysWithFilterCriteriaAndUser(any(), eq(user))).thenReturn(surveys);

        // WHEN
        List<Survey> loadedSurveys = controllerUnderTest.loadSurveys(null);

        // THEN
        assertThat(loadedSurveys, is(surveys));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test
    public void loadOptionsForSurvey() {
        // GIVEN
        Long id = -1L;
        List<Option> mockedOptions = Lists.newArrayList(mock(Option.class), mock(Option.class));
        User user = mock(User.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(optionService.loadAllOptionsOfSurveyForUser(id, user)).thenReturn(mockedOptions);

        // WHEN
        List<Option> options = controllerUnderTest.loadOptions(id);

        // THEN
        assertThat(mockedOptions, is(options));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();

    }

    @Test
    public void updateSurvey() {
        //Given
        Long id = -1L;
        Survey mockedSurvey = mock(Survey.class);
        User user = mock(User.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(surveyService.update(mockedSurvey, user)).thenReturn(mockedSurvey);

        //WHEN
        Survey surveyReturn = controllerUnderTest.updateSurvey(id, mockedSurvey);

        //THEN
        assertThat(mockedSurvey, is(surveyReturn));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test
    public void deleteSurvey() {
        //GIVEN
        Long id = -1L;
        User user = mock(User.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);

        //WHEN
        controllerUnderTest.deleteSurvey(id);

        //THEN
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
        verify(surveyService, times(1)).deleteSurvey(id, user);
    }

    @Test
    public void loadSurvey() {
        //GIVEN
        Long id = -1L;
        User user = mock(User.class);
        Survey survey = mock(Survey.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(surveyService.loadSurveyWithUser(id, user)).thenReturn(survey);

        //WHEN
        Survey loadedSurvey = controllerUnderTest.loadSurvey(id);

        //THEN
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
        assertThat(survey, is(loadedSurvey));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void loadSurveyNotVisible() {
        //GIVEN
        Long id = -1L;
        User user = mock(User.class);
        ResourceNotFoundException exception = mock(ResourceNotFoundException.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(surveyService.loadSurveyWithUser(id, user)).thenThrow(exception);

        //WHEN
        controllerUnderTest.loadSurvey(id);

        //THEN
        fail();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void loadOptionsFail() {
        // GIVEN
        Long id = -1L;
        User user = mock(User.class);
        ResourceNotFoundException exception = mock(ResourceNotFoundException.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(optionService.loadAllOptionsOfSurveyForUser(id, user)).thenThrow(exception);

        // WHEN
        controllerUnderTest.loadOptions(id);

        // THEN
        fail();

    }

    @Test
    public void loadParticipations() {
        //WHEN
        Long id = -1L;
        User user = mock(User.class);
        List<Participation> participations = Lists.newArrayList(mock(Participation.class), mock(Participation.class));

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.loadAllParticipationsOfSurveyForUser(id, user)).thenReturn(participations);

        //WHEN
        List<Participation> returned = controllerUnderTest.loadParticipations(id);

        //THEN
        assertThat(participations, is(returned));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void loadParticipationsFail() {
        //WHEN
        Long id = -1L;
        User user = mock(User.class);
        ResourceNotFoundException exception = mock(ResourceNotFoundException.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.loadAllParticipationsOfSurveyForUser(id, user)).thenThrow(exception);

        //WHEN
        controllerUnderTest.loadParticipations(id);

        //THEN
        fail();
    }

    @Test
    public void createParticipationForSurvey() {
        //WHEN
        Long id = -1L;
        User user = mock(User.class);
        Participation participation = mock(Participation.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenReturn(participation);

        //WHEN
        Participation returned = controllerUnderTest.createParticipationForSurvey(participation, id);

        //THEN
        assertThat(participation, is(returned));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void createParticipationForSurveyFail() {
        //WHEN
        Long id = -1L;
        User user = mock(User.class);
        ResourceNotFoundException exception = mock(ResourceNotFoundException.class);
        Participation participation = mock(Participation.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenThrow(exception);

        //WHEN
        controllerUnderTest.createParticipationForSurvey(participation, id);

        //THEN
        fail();
    }

    @Test(expected = PermissionDeniedException.class)
    public void createParticipationForSurveyFailInitiator() {
        //WHEN
        Long id = -1L;
        User user = mock(User.class);
        Exception exception = mock(PermissionDeniedException.class);
        Participation participation = mock(Participation.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenThrow(exception);

        //WHEN
        controllerUnderTest.createParticipationForSurvey(participation, id);

        //THEN
        fail();
    }

    @Test
    public void saveParticipationForSurvey() {
        //WHEN
        Long id = -1L;
        Long idP = -2L;
        User user = mock(User.class);
        Participation participation = mock(Participation.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenReturn(participation);

        //WHEN
        Participation returned = controllerUnderTest.saveParticipationForSurvey(participation, id, idP);

        //THEN
        assertThat(participation, is(returned));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void saveParticipationForSurveyFail() {
        //WHEN
        Long id = -1L;
        Long idP = -2L;
        User user = mock(User.class);
        Exception exception = mock(ResourceNotFoundException.class);
        Participation participation = mock(Participation.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenThrow(exception);

        //WHEN
        controllerUnderTest.saveParticipationForSurvey(participation, id, idP);

        //THEN
        fail();
    }

    @Test(expected = PermissionDeniedException.class)
    public void saveParticipationForSurveyFailInitiator() {
        //WHEN
        Long id = -1L;
        Long idP = -2L;
        User user = mock(User.class);
        Exception exception = mock(PermissionDeniedException.class);
        Participation participation = mock(Participation.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenThrow(exception);

        //WHEN
        controllerUnderTest.saveParticipationForSurvey(participation, id, idP);

        //THEN
        fail();
    }

}