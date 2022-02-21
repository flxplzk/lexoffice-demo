(function () {
    // ########################## MODULE DECLARATION #####################################

    /**
     * @name "de.nordakademie.iaa.survey.toolbar"
     *
     * ToolBarModule
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var toolbar = angular.module("de.nordakademie.iaa.survey.toolbar", [
        "de.nordakademie.iaa.survey.routes",
        "de.nordakademie.iaa.survey.editor",
        "de.nordakademie.iaa.survey.core",
        "de.nordakademie.iaa.i18n",
        "ngMaterial"
    ]);

    toolbar.controller("mainController", ["$scope", "$state", "ROUTE_STATES", "appService", ToolBarController]);
    toolbar.directive("surveyToolbar", ToolBarDirective);

    function ToolBarController($scope, $state, ROUTE_STATES, appService) {
        $scope.authenticated = false;

        appService.$authenticated.subscribeOnNext(function (authenticationStatus) {
            $scope.authenticated = authenticationStatus;
        });

        this.logout = function () {
            appService.logout();
        }

        $scope.homeRef = ROUTE_STATES.DASHBOARD_STATE;
    }

    function ToolBarDirective() {
        return {
            restrict: "E",
            templateUrl: "/js/components/toolbar/survey-toolbar.template.html",
            controller: "mainController",
            controllerAs: "toolBarCrtl"
        }
    }

}());