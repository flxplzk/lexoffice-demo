package de.nordakademie.iaa.examsurvey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ExamSurveyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamSurveyApplication.class, args);
    }
}
