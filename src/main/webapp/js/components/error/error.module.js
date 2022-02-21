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
    error.directive("error", ErrorDirective);
    error.controller("errorPageController", ["$scope", "$stateParams", "$state", "ROUTE_STATES", ErrorPageController]);

    function ErrorDirective() {
        return {
            restrict: "E",
            controller: "errorPageController",
            controllerAs: "errorCrtl",
            templateUrl: "/js/components/error/error.template.html"
        }
    }

    function ErrorPageController($scope, $stateParams, $state, ROUTE_STATES){
        this.navigateToDashboard = function() {
            $state.go(ROUTE_STATES.DASHBOARD_STATE);
        }
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