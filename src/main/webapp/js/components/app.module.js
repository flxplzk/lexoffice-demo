(function () {
    // ########################## MODULE DECLARATION #####################################

    /**
     * @name "de.nordakademie.iaa.survey"
     *
     * this is the app; The application structure is highly modularized in order to separate concerns.
     * Each module defines its own dependencies an tries to hide technological decisions. each module can be used
     * as standalone app or plugn with very less effort.
     *
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    angular.module("de.nordakademie.iaa.survey", [
        "de.nordakademie.iaa.survey.authentication",
        "de.nordakademie.iaa.survey.toolbar",
        "de.nordakademie.iaa.survey.dashboard",
        "de.nordakademie.iaa.survey.detail",
        "de.nordakademie.iaa.survey.editor",
        "de.nordakademie.iaa.survey.error",
        "de.nordakademie.iaa.survey.routes"
    ]);
}());