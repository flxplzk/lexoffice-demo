package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Felix Plazek
 */
@Transactional(propagation = Propagation.REQUIRED)
public class AuthenticationService {
    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;
    }

    /**
     * determines the current authenticated {@link User}
     *
     * @return current user if one exists {@code null} otherwise
     */
    public User getCurrentAuthenticatedUser() {
        final Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return authentication == null
                ? null
                : (User) userService.loadUserByUsername(authentication.getName());
    }
}
