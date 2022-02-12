package de.nordakademie.iaa.examsurvey.service.impl;

import de.nordakademie.iaa.examsurvey.domain.User;
import de.nordakademie.iaa.examsurvey.exception.MissingDataException;
import de.nordakademie.iaa.examsurvey.exception.UserAlreadyExistsException;
import de.nordakademie.iaa.examsurvey.persistence.UserRepository;
import de.nordakademie.iaa.examsurvey.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static de.nordakademie.iaa.examsurvey.persistence.specification.UserSpecifications.byUsername;

/**
 * UserService implementation.
 *
 * @author Felix Plazek
 */
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(final User user) {
        requireNonExistent(user);
        requireValidData(user);
        final String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No User with given username found"));
    }

    private Optional<User> findUserByUsername(final String userName) {
        return userRepository.findOne(byUsername(userName));
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
