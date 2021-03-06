package de.nordakademie.iaa.examsurvey.controller;

import com.google.common.collect.Lists;
import de.nordakademie.iaa.examsurvey.controller.dto.OptionDTO;
import de.nordakademie.iaa.examsurvey.controller.dto.ParticipationDTO;
import de.nordakademie.iaa.examsurvey.controller.dto.SurveyDTO;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SurveyControllerTest {
    @Mock
    private SurveyController controllerUnderTest;
    @Mock
    private SurveyService surveyService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private ParticipationService participationService;
    @Mock
    private OptionService optionService;
    @Mock
    private ModelMapper modelMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.controllerUnderTest = new SurveyController(
                surveyService,
                authenticationService,
                optionService,
                participationService,
                modelMapper
        );
    }

    @Test
    public void createSurvey() {
        // GIVEN
        SurveyDTO survey = mock(SurveyDTO.class);
        Survey anotherSurvey = mock(Survey.class);
        Survey mappedSurvey = mock(Survey.class);
        User user = mock(User.class);

        when(modelMapper.map(survey, Survey.class)).thenReturn(mappedSurvey);
        when(modelMapper.map(anotherSurvey, SurveyDTO.class)).thenReturn(survey);
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(surveyService.createSurvey(mappedSurvey, user)).thenReturn(anotherSurvey);

        // WHEN
        ResponseEntity<SurveyDTO> createdSurvey = controllerUnderTest.createSurvey(survey);

        // THEN
        assertThat(createdSurvey.getBody(), is(survey));
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
        ResponseEntity<List<SurveyDTO>> loadedSurveys = controllerUnderTest.loadSurveys(null);

        // THEN
        assertThat(loadedSurveys.getBody(), is(notNullValue()));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test
    public void loadOptionsForSurvey() {
        // GIVEN
        final Long id = -1L;
        final Option mock = mock(Option.class);
        final Option mock1 = mock(Option.class);
        final List<Option> mockedOptions = Lists.newArrayList(mock, mock1);
        final User user = mock(User.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(optionService.loadAllOptionsOfSurveyForUser(id, user)).thenReturn(mockedOptions);
        // WHEN
        ResponseEntity<List<OptionDTO>> options = controllerUnderTest.loadOptions(id);

        // THEN
        assertThat(mockedOptions, hasSize(2));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();

    }

    @Test
    public void updateSurvey() {
        //Given
        final Long id = -1L;
        final SurveyDTO surveyDTO = mock(SurveyDTO.class);
        final Survey mappedSurvey = mock(Survey.class);
        final User user = mock(User.class);

        when(modelMapper.map(surveyDTO, Survey.class)).thenReturn(mappedSurvey);
        when(modelMapper.map(mappedSurvey, SurveyDTO.class)).thenReturn(surveyDTO);
        when(mappedSurvey.getInitiator()).thenReturn(user);
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(surveyService.update(mappedSurvey, user)).thenReturn(mappedSurvey);

        //WHEN
        ResponseEntity<SurveyDTO> surveyReturn = controllerUnderTest.updateSurvey(id, surveyDTO);

        //THEN
        assertThat(surveyReturn.getBody(), is(notNullValue()));
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
        ResponseEntity<SurveyDTO> loadedSurvey = controllerUnderTest.loadSurvey(id);

        //THEN
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
        assertThat(survey, is(notNullValue()));
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
        ResponseEntity<List<ParticipationDTO>> returned = controllerUnderTest.loadParticipations(id);

        //THEN
        assertThat(returned.getBody(), is(notNullValue()));
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

        when(participation.getUser()).thenReturn(user);
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenReturn(participation);

        //WHEN
        ResponseEntity<ParticipationDTO> returned = controllerUnderTest.createParticipationForSurvey(participation, id);

        //THEN
        assertThat(returned, is(notNullValue()));
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
        final Long id = -1L;
        final Long idP = -2L;
        final User user = mock(User.class);
        final ParticipationDTO participationDTO = mock(ParticipationDTO.class);
        final Participation participation = mock(Participation.class);

        when(modelMapper.map(participationDTO, Participation.class)).thenReturn(participation);
        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenReturn(participation);

        //WHEN
        ResponseEntity<ParticipationDTO> returned = controllerUnderTest.saveParticipationForSurvey(participationDTO, id, idP);

        //THEN
        assertThat(returned, is(notNullValue()));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void saveParticipationForSurveyFail() {
        //WHEN
        final Long id = -1L;
        final Long idP = -2L;
        final User user = mock(User.class);
        final Exception exception = mock(ResourceNotFoundException.class);
        final Participation participation = mock(Participation.class);
        final ParticipationDTO participationDTO = mock(ParticipationDTO.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenThrow(exception);
        when(modelMapper.map(participationDTO, Participation.class)).thenReturn(participation);
        when(modelMapper.map(participation, ParticipationDTO.class)).thenReturn(participationDTO);

        //WHEN
        controllerUnderTest.saveParticipationForSurvey(participationDTO, id, idP);

        //THEN
        fail();
    }

    @Test(expected = PermissionDeniedException.class)
    public void saveParticipationForSurveyFailInitiator() {
        //WHEN
        final Long id = -1L;
        final Long idP = -2L;
        final User user = mock(User.class);
        final Exception exception = mock(PermissionDeniedException.class);
        final Participation participation = mock(Participation.class);
        final ParticipationDTO participationDto = mock(ParticipationDTO.class);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(participationService.saveParticipationForSurveyWithAuthenticatedUser(participation, id, user)).thenThrow(exception);
        when(modelMapper.map(participationDto, Participation.class)).thenReturn(participation);
        //WHEN
        controllerUnderTest.saveParticipationForSurvey(participationDto, id, idP);

        //THEN
        fail();
    }

}