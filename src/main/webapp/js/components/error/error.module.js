(function () {
    // ########################## MODULE DECLARATION #####################################

    /**
     * @name "de.nordakademie.iaa.survey.error"
     *
     * Module for reacting to unexpected server errors.
     *
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var error = angular.module("de.nordakademie.iaa.survey.error", [
        "de.nordakademie.iaa.survey.routes",
        "de.nordakademie.iaa.survey.cage",
        "de.nordakademie.iaa.i18n",
        "ngMaterial"
    ]);

    var ERROR_STATE = 'error';
    error.directive("error", ["ROUTE_STATES", ErrorDirective]);
    error.controller("errorPageController", ["$scope", "$stateParams", ErrorPageController]);

    function ErrorDirective(ROUTE_STATES) {
        return {
            restrict: "E",
            controller: "errorPageController",
            controllerAs: "errorCrtl",
            template:
               "<div layout='column' layout-align='center center' flex>\n" +
                "    <h1>{{heading|translate}}</h1>\n" +
                "    <h4>{{'ERROR_SUB_HEADING'|translate}}</h4>\n" +
                "    <what-would-nicolas-say></what-would-nicolas-say>\n" +
                "<md-button class='md-icon-button md-raised' ui-sref=" + ROUTE_STATES.DASHBOARD_STATE +">" +
                "<i class='material-icons'>home</i> "+
                "</md-button> " +
                "</div>"
        }
    }

    function ErrorPageController($scope, $stateParams){
        $scope.heading = $stateParams.reason === "500" ? "ERROR_HEADING" :
            $stateParams.reason === "404" ? "ERROR_HEADING_404" : "ERROR_HEADING_NETWORK";
    }
    /**
     * show error page in case of 500
     */
    error.factory('errorInterceptor', function ($q, $state, ROUTE_STATES) {
        return {
            request: function (config) {
                return config || $q.when(config);
            },
            requestError: function (request) {
                return $q.reject(request);
            },
            response: function (response) {
                return response || $q.when(response);
            },
            responseError: function (response) {
                if (response && response.status === 500 || response && response.status < 0) {
                    $state.go(ERROR_STATE, {reason: response.status});
                }
                if (response && response.status === 404) {
                    $state.go(ERROR_STATE, {reason: 404})
                }
                if (response && response.status === 401) {
                    $state.go(ROUTE_STATES.LOGIN_STATE)
                }
                return $q.reject(response);
            }
        };
    });

    /**
     * config is done here for easy use of error module.
     */
    error.config(function ($httpProvider, $stateProvider) {
        $httpProvider.interceptors.push('errorInterceptor');
        var errorState = {
            name: ERROR_STATE,
            url: "/oops/:reason",
            template: "<error></error>"
        };
        $stateProvider.state(errorState);
    });

}());