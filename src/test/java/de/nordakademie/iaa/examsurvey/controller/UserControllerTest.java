package de.nordakademie.iaa.examsurvey.controller;

import com.google.common.collect.Lists;
import de.nordakademie.iaa.examsurvey.controller.dto.EventDTO;
import de.nordakademie.iaa.examsurvey.controller.dto.NotificationDTO;
import de.nordakademie.iaa.examsurvey.controller.dto.UserCreationDTO;
import de.nordakademie.iaa.examsurvey.controller.dto.UserDTO;
import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.UserAlreadyExistsException;
import de.nordakademie.iaa.examsurvey.service.AuthenticationService;
import de.nordakademie.iaa.examsurvey.service.EventService;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EventService eventService;
    @Mock
    private ModelMapper modelMapper;

    private UserController controllerUnderTest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controllerUnderTest = new UserController(
                userService,
                authenticationService,
                notificationService,
                eventService,
                modelMapper
        );
    }

    @Test
    public void user() {
        // GIVEN
        final Authentication auth = mock(Authentication.class);
        final User user = mock(User.class);
        final UserDTO dto = mock(UserDTO.class);

        when(auth.getPrincipal()).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(dto);

        // WHEN
        ResponseEntity<UserDTO> principal = controllerUnderTest.user(auth);

        // THEN
        assertThat(principal.getBody(), is(dto));
    }

    @Test
    public void createUser() {
        // GIVEN
        final User user = mock(User.class);
        final UserCreationDTO userDTO = mock(UserCreationDTO.class);
        final UserDTO createdUserDTO = mock(UserDTO.class);

        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(createdUserDTO);
        when(userService.createUser(user)).thenReturn(user);

        // WHEN
        ResponseEntity<UserDTO> createdUser = controllerUnderTest.createUser(userDTO);

        // THEN
        assertThat(createdUser.getBody(), is(createdUserDTO));

    }

    @Test(expected = UserAlreadyExistsException.class)
    public void createUserAlreadyExists() {
        // GIVEN
        final User user = mock(User.class);
        final UserCreationDTO userDTO = mock(UserCreationDTO.class);
        final UserDTO createdUserDTO = mock(UserDTO.class);

        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(modelMapper.map(user, UserDTO.class)).thenReturn(createdUserDTO);

        when(userService.createUser(user)).thenThrow(new UserAlreadyExistsException());

        // WHEN
        controllerUnderTest.createUser(userDTO);

        // THEN
        fail();

    }

    @Test
    public void getNotifications() {
        // GIVEN
        User user = mock(User.class);
        List<Notification> returnNotifications = Lists.newArrayList(mock(Notification.class), mock(Notification.class));

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(notificationService.getNotificationsForUser(user)).thenReturn(returnNotifications);

        // WHEN
        ResponseEntity<List<NotificationDTO>> notifications = controllerUnderTest.getNotifications();

        // THEN
        assertThat(notifications.getBody(), hasSize(2));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test
    public void createEvent() {
        //GIVEN
        final User user = mock(User.class);
        final Event event = mock(Event.class);
        final EventDTO eventDTO = mock(EventDTO.class);

        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);
        when(modelMapper.map(eventDTO, Event.class)).thenReturn(event);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(eventService.createEvent(event, user)).thenReturn(event);

        //WHEN
        ResponseEntity<EventDTO> returned = controllerUnderTest.createEvent(eventDTO);

        //THEN
        assertThat(returned.getBody(), is(eventDTO));
        verify(authenticationService, times(1)).getCurrentAuthenticatedUser();
    }

    @Test(expected = de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException.class)
    public void createEventPermissionDenied() {
        //GIVEN
        User user = mock(User.class);
        Exception exception = mock(de.nordakademie.iaa.examsurvey.exception.PermissionDeniedException.class);
        final Event event = mock(Event.class);
        final EventDTO eventDTO = mock(EventDTO.class);

        when(modelMapper.map(event, EventDTO.class)).thenReturn(eventDTO);
        when(modelMapper.map(eventDTO, Event.class)).thenReturn(event);

        when(authenticationService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(eventService.createEvent(event, user)).thenThrow(exception);


        //WHEN
        controllerUnderTest.createEvent(eventDTO);

        //THEN
        fail();
    }
}