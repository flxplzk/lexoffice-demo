(function () {
    // ########################## MODULE DECLARATION #####################################

    /**
     * @name "de.nordakademie.iaa.i18n"
     *
     * This module provides i18n messages for the frontend application
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var i18n = angular.module("de.nordakademie.iaa.i18n", [
        "pascalprecht.translate",
        "ngMaterial"
    ]);
    i18n.directive("languageSelector", LanguageButtonDirective);
    i18n.controller("languageController", ["$scope", "$translate", LanguageSelectorController]);

    function LanguageButtonDirective() {
        return {
            restrict: "E",
            controller: "languageController",
            controllerAs: "languageCrtl",
            template: "<div>" +
                "<md-select style='padding-top: 7px' name=\"favoriteColor\" ng-model=\"preferredLanguage\" " +
                "aria-label='language-selector' ng-change='languageCrtl.updateLanguage()' required>\n" +
                "          <md-option ng-repeat='language in supportedLanguages' " +
                "           ng-selected='language.isDefault' value=\"{{language}}\">" +
                "               {{language.value}}" +
                "           </md-option>\n" +
                "        </md-select>" +
                "</div>"
        }
    }

    function LanguageSelectorController($scope, $translate) {
        $scope.supportedLanguages = [{key: "en_US", value: "EN", isDefault: true}, {key: "de_DE", value:"DE", isDefault: false}];
        $scope.preferredLanguage = $scope.supportedLanguages[0];
        this.updateLanguage = function () {
            var languageSelected = angular.fromJson($scope.preferredLanguage);
            $translate.use(languageSelected.key);
        }
    }

    i18n.config(function ($translateProvider) {
        // german
        $translateProvider.translations("de_DE", {

            APP_HEADLINE: "Noodle",
            APP_LOGOUT: "Abmelden",

            AUTH_LOGIN_HEADLINE: "Bitte melde Dich an",
            AUTH_LOGIN_EMAIL_LABEL: "E-Mail Adresse",
            AUTH_LOGIN_PASSWORD_LABEL: "Kennwort",
            AUTH_LOGIN_REGISTER: "Nocht kein Konto? Hier registrieren!",
            AUTH_LOGIN_SIGN_IN: "Anmelden",
            AUTH_LOGIN_ERROR_CREDENTIALS: "Die eingegebenen Anmeldedaten sind leider falsch!",
            AUTH_REGISTER_HEADLINE: "Registrieren",
            AUTH_REGISTER_FIRST_NAME_LABEL: "Vorname",
            AUTH_REGISTER_LAST_NAME_LABEL: "Nachname",
            AUTH_REGISTER_REPEAT_PASSWORD_LABEL: "Kennwort wiederholen",
            AUTH_REGISTER_BUTTON_REGISTER: "Konto erstellen",
            AUTH_REGISTER_ERROR_EMAIL: "Deine E-Mail Adresse muss aussehen wie eine E-Mail Adresse und zwischen 10 und 100 Zeichen enthalten.",
            AUTH_REGISTER_ERROR_PASSWORD_STRENGTH: "Das Kennwort darf nicht leer sein, muss mindestens eine Zahl, ein Sonderzeichen und jeweils einen Groß-und einen Kleinbuchstaben enthalten.",
            AUTH_REGISTER_USER_ALREADY_EXISTS: "Es existiert bereits ein Benutzer mit dieser E-Mail Adresse. Bitte wähle eine Andere<.",
            AUTH_REGISTER_PASSWORD_NON_MATCH: "Die Kennwörter müssen übereinstimmen.",
            AUTH_REGISTER_USER_CREATED: "Benutzer wurde erfolgreich erstellt.",
            LOGGED_OUT: "Dein Logout war erfolgreich.",
            DASHBOARD_OPEN_DETAILS: "Öffnen",
            DASHBOARD_TITLE_ALL: "Alle Umfragen",
            DASHBOARD_TITLE_OPEN: "Offen",
            DASHBOARD_TITLE_OWN: "Deine Umfragen",
            DASHBOARD_TITLE_PARTICIPATED: "Teilgenommen",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TAB: "Benachrichtigungen",
            DASHBOARD_SIDE_NAV_EVENT_TAB: "Events",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_SURVEY_CHANGE: "Umfrage verändert",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_EVENT_PLANNED: "Umfrage geschlossen!",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_DESC_SURVEY_CHANGE: "Der Ersteller hat die Umfrage geändert. Daher wurde deine Teilnahme gelöscht. Bitte stimme erneut ab!",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_DESC_EVENT_PLANNED: "Der Ersteller hat die Umfrage geschlossen und das Event geplant. Falls Du für die ausgewählte Option abgestimmt hast, guck in Deine anstehenden Events!",
            DASHBOARD_SIDE_NAV_SHOW: "Details ansehen",
            DASHBOARD_SIDE_NAV_NEW_EVENT: "Neue Events für Dich!",
            DASHBOARD_SIDE_NAV_NEW_NOTIFICATION: "Neue Benachrichtigung! :)",

            EDITOR_TITLE: "Neue Umfrage erstellen",
            EDITOR_TITLE_NEW: "Neue Umfrage erstellen",
            EDITOR_TITLE_UPDATE: "Umfrage aktualisieren",
            EDITOR_NOT_FOUND: "Die angefragte Entität existiert nicht mehr.",
            EDITOR_SAVE: "Veröffentlichen",
            EDITOR_CANCEL: "Abbrechen",
            EDITOR_FORM_TITLE: "Titel",
            EDITOR_FORM_ERROR_REQUIRED: "Dieses Feld muss gefüllt sein",
            EDITOR_FORM_ERROR_MAX_LENGTH: "Der Titel einer Umfrage darf nicht länger als 50 Zeichen sein!",
            EDITOR_FORM_DESCRIPTION: "Beschreibung",
            EDITOR_FORM_OPTION_HEADING: "Bitte füge ein paar Optionen hinzu.",
            EDITOR_FORM_OPTION_FROM: "Datum auswählen",
            EDITOR_SAVE_AS_DRAFT: "Entwurf speichern",
            EDITOR_CONFLICT: "Bitte wähle einen anderen Titel für Deine Umfrage!",
            EDITOR_NETWORK: "Bitte versuche es später erneut!",
            EDITOR_CONFIRM_DELETE_TITLE: "Bestätigen...",
            EDITOR_CONFIRM_DELETE_CONTENT: "Du bist dabei, diese Umfrage zu löschen. Sie wird nach dem Löschen nicht wieder verfügbar sein!",
            EDITOR_CONFIRM_DELETE_OK: "Einverstanden",
            EDITOR_CONFIRM_DELETE_CANCEL: "Abbrechen!!!",
            EDITOR_ERROR_DELETE_PERMISSION: "Du kannst diese Umfrage nicht löschen, da Du sie nicht erstellt hast",
            EDITOR_ERROR_DELETE_UNKNOWN: "Die Umfrage wurde erfolgreich gelöscht",
            EDITOR_CLOSE_TITLE: "Umfrage schließen...",
            EDITOR_SURVEY_CLOSE: "Bitte wähle eine der untenstehenden Optionen für Dein Event. Alle Benutzer werden benachrichtigt. Diese Aktion kann nicht rückgängig gemacht werden!",
            EDITOR_OPTIONS_HINT: "Optionen müssen einmalig sein und in der Zukunft liegen.",
            DETAIL_SEND: "Teilnahme speichern",
            DETAIL_YOU: "Du",
            DETAIL_TERMINATE_SURVEY: "Umfrage schließen",
            DETAIL_TOOLTIP_OPEN: "OFFEN",
            DETAIL_TOOLTIP_CLOSED: "GESCHLOSSEN",
            DETAIL_TOOLTIP_PRIVATE: "PRIVAT",
            DETAIL_INITIATOR: "Ersteller",
            DETAIL_SAVE_SUCCESS: "Änderungen wurden gespeichert!",
            DETAIL_SUM: "Summe",
            ERROR_HEADING: "Ups, das hätte nicht passieren sollen. Es sei denn, Du wolltest hierhin. In diesem Fall: Herzlichen Glückwunsch!",
            ERROR_HEADING_404: "Ups, da warst Du zu gierig: Die gewünschte Entität konnte nicht geladen werden, da sie nicht (mehr) existiert!",
            ERROR_HEADING_NETWORK: "Ups, es sieht so aus, als bestünde keine Verbindung zum Server.",
            ERROR_SUB_HEADING: "Um Dich etwas aufzumuntern, haben wir unseren guten Freund Nicolas gefragt, was er davon hält: "
        });

        // english
        $translateProvider.translations("en_US", {
            APP_HEADLINE: "Noodle",
            APP_LOGOUT: "Logout",

            AUTH_LOGIN_HEADLINE: "Please Sign in",
            AUTH_LOGIN_EMAIL_LABEL: "E-mail address",
            AUTH_LOGIN_PASSWORD_LABEL: "Password",
            AUTH_LOGIN_REGISTER: "No Account yet? Register!",
            AUTH_LOGIN_SIGN_IN: "Sign in",
            AUTH_LOGIN_ERROR_CREDENTIALS: "Your login data is wrong",
            AUTH_REGISTER_HEADLINE: "Register",
            AUTH_REGISTER_FIRST_NAME_LABEL: "First name",
            AUTH_REGISTER_LAST_NAME_LABEL: "Last name",
            AUTH_REGISTER_REPEAT_PASSWORD_LABEL: "Repeat password",
            AUTH_REGISTER_BUTTON_REGISTER: "Register",
            AUTH_REGISTER_ERROR_EMAIL: "Your email must be between 10 and 100 characters long and look like an e-mail address.",
            AUTH_REGISTER_ERROR_PASSWORD_STRENGTH: "Password is required and must be secure! with at least 8 characters.",
            AUTH_REGISTER_USER_ALREADY_EXISTS: "Username (e-mail) is already in use. please select a new one",
            AUTH_REGISTER_PASSWORD_NON_MATCH: "The passwords must match.",
            AUTH_REGISTER_USER_CREATED: "User created successfully.",
            LOGGED_OUT: "Your logout was successful.",

            DASHBOARD_OPEN_DETAILS: "Open",
            DASHBOARD_TITLE_ALL: "All surveys",
            DASHBOARD_TITLE_OPEN: "Open",
            DASHBOARD_TITLE_OWN: "Your surveys",
            DASHBOARD_TITLE_PARTICIPATED: "Participated",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TAB: "Notifications",
            DASHBOARD_SIDE_NAV_EVENT_TAB: "Events",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_SURVEY_CHANGE: "Survey changed",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_EVENT_PLANNED: "Survey closed!",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_DESC_SURVEY_CHANGE: "The initiator has changed the survey and therefore your particiation has been deleted. Please participate again!",
            DASHBOARD_SIDE_NAV_NOTIFICATION_TYPE_DESC_EVENT_PLANNED: "The initiator has closed the survey and planned the event. If you participated on the chosen options, check your upcoming events",
            DASHBOARD_SIDE_NAV_SHOW: "View details",
            DASHBOARD_SIDE_NAV_NEW_EVENT: "New events for you!",
            DASHBOARD_SIDE_NAV_NEW_NOTIFICATION: "New notification! :)",
            EDITOR_TITLE: "Create new survey",
            EDITOR_TITLE_NEW: "Create new survey",
            EDITOR_TITLE_UPDATE: "Update survey",
            EDITOR_NOT_FOUND: "The requested entity does not exist anymore :( ",

            EDITOR_SAVE: "Save and publish",
            EDITOR_CANCEL: "Cancel",
            EDITOR_FORM_TITLE: "Title",
            EDITOR_FORM_ERROR_REQUIRED: "This field is required.",
            EDITOR_FORM_ERROR_MAX_LENGTH: "The title must be less than 50 characters long.",
            EDITOR_FORM_DESCRIPTION: "Description",
            EDITOR_FORM_TYPE_DATE: "Date",
            EDITOR_FORM_TYPE_TIME: "Time",
            EDITOR_FORM_OPTION_FROM: "Select date",
            EDITOR_FORM_OPTION_HEADING: "Please add options",
            EDITOR_SAVE_AS_DRAFT: "Save draft",
            EDITOR_CONFLICT: "Please choose a new title for your survey, yours is already in use",
            EDITOR_NETWORK: "Please try again later!",
            EDITOR_CONFIRM_DELETE_TITLE: "Confirm ...",
            EDITOR_CONFIRM_DELETE_CONTENT: "You are deleting your survey. With doing so, all associated notifications, participations and the event will be deleted for ever and ever and ever.",
            EDITOR_CONFIRM_DELETE_OK: "Do it!",
            EDITOR_CONFIRM_DELETE_CANCEL: "Cancel",
            EDITOR_ERROR_DELETE_PERMISSION: "You can not delete a survey when you are not the initiator.",
            EDITOR_ERROR_DELETE_UNKNOWN: "Survey has been deleted",
            EDITOR_CLOSE_TITLE: "Close survey ...",
            EDITOR_SURVEY_CLOSE: "Please select one of the below options for your event. All users will be notified. This action can not be changed",
            EDITOR_OPTIONS_HINT: "Options must be unique and in the future.",

            DETAIL_SEND: "Save",
            DETAIL_YOU: "You",
            DETAIL_TERMINATE_SURVEY: "Close survey",
            DETAIL_TOOLTIP_OPEN: "OPEN",
            DETAIL_TOOLTIP_CLOSED: "CLOSED",
            DETAIL_TOOLTIP_PRIVATE: "PRIVATE",
            DETAIL_INITIATOR: "Initiator",
            DETAIL_SAVE_SUCCESS: "Changes has been saved!",
            DETAIL_SUM: "Sum",

            ERROR_HEADING: "Oops, this error should not occur. Unless you wanted to see this. In this case: Congratulations!",
            ERROR_HEADING_404: "Oops, you naughty thing tried to load a resource which does not exist.",
            ERROR_HEADING_NETWORK: "Oops, seems like you have no server connection.",
            ERROR_SUB_HEADING: "In order too cheer you up a bit, we asked our beloved Nicolas what he would say:"

        });

        $translateProvider.preferredLanguage("en_US");
        $translateProvider.useSanitizeValueStrategy('escape');
    })
}());