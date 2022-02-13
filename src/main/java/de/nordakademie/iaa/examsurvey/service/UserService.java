package de.nordakademie.iaa.examsurvey.service;

import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.MissingDataException;
import de.nordakademie.iaa.examsurvey.exception.UserAlreadyExistsException;
import de.nordakademie.iaa.examsurvey.persistence.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.REQUIRED)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates new User. username must be unused.
     *
     * @param user to safe
     * @return created user
     * @throws UserAlreadyExistsException if a User with the username already exists
     */
    public User createUser(final User user) {
        requireNonExistent(user);
        requireValidData(user);
        final String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }
    
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No User with given username found"));
    }

    private Optional<User> findUserByUsername(final String userName) {
        return userRepository.findOneByUsername(userName);
    }

    private void requireNonExistent(final User user) {
        if (findUserByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
    }

    private void requireValidData(final User user) {
        if (user.getUsername() == null || user.getFirstName() == null
                || user.getLastName() == null || user.getPassword() == null) {
            throw new MissingDataException("Fields: \"firstName\", \"lastName\", " +
                    "\"userName\" and \"password\" are required");
        }
    }
}
