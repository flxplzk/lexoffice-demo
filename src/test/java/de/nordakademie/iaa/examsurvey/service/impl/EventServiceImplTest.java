package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.persistence.EventRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.service.EventService;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class EventServiceImplTest {
    @Mock
    private SurveyService surveyService;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private ParticipationRepository participationRepository;

    private EventService serviceUnderTest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        this.serviceUnderTest = new EventService(
                surveyService,
                eventRepository,
                notificationService,
                participationRepository
        );
    }

    @Test
    public void createEvent() {
        //GIVEN
        Event event = mock(Event.class);
        User user = mock(User.class);
        Survey survey = mock(Survey.class);

        when(event.getSurvey()).thenReturn(survey);
        when(eventRepository.save(event)).thenReturn(event);


        //WHEN
        Event returned = serviceUnderTest.createEvent(event, user);

        //THEN
        assertEquals(returned, event);
        verify(participationRepository, times(1)).findAllBySurvey(survey);
        verify(surveyService, times(1)).closeSurvey(survey, user);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void createEventNoSurvey() {
        //GIVEN
        Event event = mock(Event.class);
        User user = mock(User.class);
        Survey survey = mock(Survey.class);
        Exception exception = mock(ResourceNotFoundException.class);

        when(event.getSurvey()).thenReturn(survey);
        surveyService.closeSurvey(survey, user);
        doThrow(exception).when(surveyService).closeSurvey(survey, user);
        when(eventRepository.save(event)).thenReturn(event);


        //WHEN
        Event returned = serviceUnderTest.createEvent(event, user);

        //THEN
        fail();
    }
}