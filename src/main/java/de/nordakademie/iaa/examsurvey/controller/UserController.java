package de.nordakademie.iaa.examsurvey.controller;

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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Felix Plazek
 */
@RestController
@RequestMapping(value = "/api")
public class UserController {
    public static final String PATH_USERS = "/users";
    private static final String PATH_USERS_NOTIFICATIONS = "/users/me/notifications";
    private static final String PATH_USERS_EVENTS = "/users/me/events";

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final NotificationService notificationService;
    private final EventService eventService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(final UserService userService,
                          final AuthenticationService authenticationService,
                          final NotificationService notificationService,
                          final EventService eventService,
                          final ModelMapper modelMapper) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.notificationService = notificationService;
        this.eventService = eventService;
        this.modelMapper = modelMapper;
    }

    /**
     * returns current Principal. Call only works with authenticated request.
     *
     * @param user current
     * @return current user
     */
    @GetMapping(value = PATH_USERS + "/me")
    public ResponseEntity<UserDTO> user(final Authentication authentication) {
        final User details = (User) authentication.getPrincipal();
        final UserDTO userDTO = this.modelMapper.map(details, UserDTO.class);

        return ResponseEntity.ok(userDTO);
    }

    /**
     * creates new user
     *
     * @param user to create
     * @return created user
     */
    @PostMapping(value = PATH_USERS,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreationDTO user) {
        final User createdUser = userService.createUser(asUser(user));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(asUserDto(createdUser));
    }

    private User asUser(final UserCreationDTO user) {
        return modelMapper.map(user, User.class);
    }

    private UserDTO asUserDto(final User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    /**
     * returns all notifications for current user
     *
     * @return notifications for authenticated user
     */
    @GetMapping(
            value = PATH_USERS_NOTIFICATIONS,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        final User authenticatedUser = authenticationService.getCurrentAuthenticatedUser();
        final List<NotificationDTO> notificationsForUser = notificationService.getNotificationsForUser(authenticatedUser)
                .stream()
                .map(this::asNotificationDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(notificationsForUser);
    }

    private NotificationDTO asNotificationDto(final Notification notification) {
        return modelMapper.map(notification, NotificationDTO.class);
    }

    /**
     * deletes Participatiosn with {@param id}
     *
     * @param id of the notification to delete
     */
    @DeleteMapping(value = PATH_USERS_NOTIFICATIONS + "/{id}")
    public void getNotifications(@PathVariable(name = "id") Long id) {
        notificationService.deleteNotificationWithUser(id, authenticationService.getCurrentAuthenticatedUser());
    }

    /**
     * loads all events for authenticated User
     *
     * @return all events for authenticated User
     */
    @GetMapping(
            value = PATH_USERS_EVENTS,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<List<EventDTO>> loadEventsForUser() {
        final User currentAuthenticatedUser = authenticationService.getCurrentAuthenticatedUser();
        final List<EventDTO> events = eventService.loadAllEventsForAuthenticatedUser(currentAuthenticatedUser).stream()
                .map(this::asEventDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(events);
    }

    private EventDTO asEventDto(final Event event) {
        return modelMapper.map(event, EventDTO.class);
    }

    /**
     * creates Event for Survey.
     *
     * @param event to create
     * @return created event
     */
    @PostMapping(value = PATH_USERS_EVENTS,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO event) {
        final Event createdEvent = eventService.createEvent(
                asEvent(event),
                authenticationService.getCurrentAuthenticatedUser()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(asEventDto(createdEvent));
    }

    private Event asEvent(EventDTO event) {
        return modelMapper.map(event, Event.class);
    }
}
