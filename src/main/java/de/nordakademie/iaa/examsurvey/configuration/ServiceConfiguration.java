package de.nordakademie.iaa.examsurvey.configuration;

import de.nordakademie.iaa.examsurvey.persistence.*;
import de.nordakademie.iaa.examsurvey.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfiguration {

    @Bean
    @Scope
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Scope
    public UserService userService(final UserRepository userRepository,
                                   final PasswordEncoder passwordEncoder) {
        return new UserService(userRepository, passwordEncoder);
    }

    @Bean
    @Scope
    public SurveyService surveyService(final SurveyRepository surveyRepository,
                                       final NotificationService notificationService,
                                       final OptionService optionService,
                                       final ParticipationService participationService) {
        return new SurveyService(surveyRepository, notificationService, optionService, participationService);
    }

    @Bean
    @Scope
    public AuthenticationService authenticationService(final UserService userService) {
        return new AuthenticationService(userService);
    }

    @Bean
    @Scope
    public NotificationService notificationService(final NotificationRepository notificationRepository,
                                                   final ParticipationRepository participationRespository) {
        return new NotificationService(notificationRepository, participationRespository);
    }

    @Bean
    @Scope
    public OptionService optionService(final OptionRepository optionRepository,
                                       final SurveyRepository surveyRepository) {
        return new OptionService(surveyRepository, optionRepository);
    }

    @Bean
    @Scope
    public ParticipationService participationService(final ParticipationRepository participationRepository,
                                                     final SurveyRepository surveyRepository) {
        return new ParticipationService(surveyRepository, participationRepository);
    }

    @Bean
    @Scope
    public EventService eventService(final SurveyService surveyService,
                                     final EventRepository eventRespository,
                                     final NotificationService notificationService,
                                     final ParticipationRepository participationRepository) {
        return new EventService(surveyService, eventRespository, notificationService, participationRepository);
    }
}
