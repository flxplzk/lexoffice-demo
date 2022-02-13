package de.nordakademie.iaa.examsurvey.domain;

public enum NotificationType {
    SURVEY_CHANGE("Survey Changed"),
    EVENT_PLANNED("Event Planned"),
    NEW_SURVEY("New Survey");

    private String name;

    NotificationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
