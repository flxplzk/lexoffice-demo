package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.Survey;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.ResourceNotFoundException;
import de.nordakademie.iaa.examsurvey.persistence.EventRepository;
import de.nordakademie.iaa.examsurvey.persistence.ParticipationRepository;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.SurveyService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventServiceImplTest {

    private SurveyService surveyService;
    private EventRepository eventRepository;
    private NotificationService notificationService;
    private ParticipationRepository participationRepository;
    private de.nordakademie.iaa.examsurvey.service.EventService serviceUnderTest;

    @Before
    public void setUp() {
        surveyService = mock(SurveyService.class);
        eventRepository = mock(EventRepository.class);
        notificationService = mock(NotificationService.class);
        participationRepository = mock(ParticipationRepository.class);
        serviceUnderTest = new EventServiceImpl(surveyService, eventRepository, notificationService, participationRepository);


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
        verify(participationRepository, times(1)).findAll(any(Specification.class));
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