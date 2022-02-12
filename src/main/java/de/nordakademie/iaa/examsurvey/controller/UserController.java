package de.nordakademie.iaa.examsurvey.controller;

import de.nordakademie.iaa.examsurvey.domain.Event;
import de.nordakademie.iaa.examsurvey.domain.Notification;
import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.UserAlreadyExistsException;
import de.nordakademie.iaa.examsurvey.service.AuthenticationService;
import de.nordakademie.iaa.examsurvey.service.EventService;
import de.nordakademie.iaa.examsurvey.service.NotificationService;
import de.nordakademie.iaa.examsurvey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

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

    @Autowired
    public UserController(UserService userService,
                          AuthenticationService authenticationService,
                          NotificationService notificationService,
                          EventService eventService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.notificationService = notificationService;
        this.eventService = eventService;
    }

    /**
     * returns current Principal. Call only works with authenticated request.
     *
     * @param user current
     * @return current user
     */
    @RequestMapping(value = PATH_USERS + "/me",
            method = RequestMethod.GET)
    public Principal user(Principal user) {
        return user;
    }

    /**
     * creates new user
     *
     * @param user to create
     * @return created user
     */
    @RequestMapping(value = PATH_USERS,
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * returns all notifications for current user
     *
     * @return notifications for authenticated user
     */
    @RequestMapping(value = PATH_USERS_NOTIFICATIONS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Notification> getNotifications() {
        User authenticatedUser = authenticationService.getCurrentAuthenticatedUser();
        return notificationService.getNotificationsForUser(authenticatedUser);
    }

    /**
     * deletes Participatiosn with {@param id}
     *
     * @param id of the notification to delete
     */
    @RequestMapping(value = PATH_USERS_NOTIFICATIONS + "/{id}",
            method = RequestMethod.DELETE)
    public void getNotifications(@PathVariable(name = "id") Long id) {
        notificationService.deleteNotificationWithUser(id, authenticationService.getCurrentAuthenticatedUser());
    }

    /**
     * loads all events for authenticated User
     *
     * @return all events for authenticated User
     */
    @RequestMapping(value = PATH_USERS_EVENTS,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Event> loadEventsForUser() {
        return eventService.loadAllEventsForAuthenticatedUser(authenticationService.getCurrentAuthenticatedUser());
    }

    /**
     * creates Event for Survey.
     *
     * @param event to create
     * @return created event
     */
    @RequestMapping(value = PATH_USERS_EVENTS,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event, authenticationService.getCurrentAuthenticatedUser());
    }
}
