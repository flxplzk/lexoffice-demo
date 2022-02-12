(function () {

    // ########################## MODULE DECLARATION #####################################

    /**
     * @name "de.nordakademie.iaa.survey.routes"
     *
     * Routes Configuration Module.
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var routing = angular.module("de.nordakademie.iaa.survey.routes", [
        "de.nordakademie.iaa.survey.core",
        "ui.router"
    ]);

    var ROUTE_STATES = {
        LOGIN_STATE: "login",
        REGISTER_STATE: "register",
        DASHBOARD_STATE: "dashboard",
        DETAIL_STATE: "detail",
        DEFAULT_STATE: "home"
    };

    /**
     * wrapping routes as constant for better maintainability
     */
    routing.constant("ROUTE_STATES", ROUTE_STATES);

    routing.config(function ($stateProvider) {
        var enteringGuard = function ($transition$, $state$) {
            var appService = $transition$.injector().get("appService");
            var stateService = $transition$.injector().get("$state");
            appService.testAuthentication(function (success) {
                console.log("user authenticated; access granted!");
            }, function (reject) {
                $transition$.abort();
                stateService.go(ROUTE_STATES.LOGIN_STATE);
                console.log("user not authenticated; redirected to login!");
            });
        };

        var loginState = {
            name: ROUTE_STATES.LOGIN_STATE,
            url: '/login',
            templateUrl: "js/components/authentication/login.template.html"
        };
        var registerState = {
            name: ROUTE_STATES.REGISTER_STATE,
            url: '/register',
            templateUrl: "js/components/authentication/register.template.html"
        };
        var dashboardState = {
            name: ROUTE_STATES.DASHBOARD_STATE,
            url: '/dashboard',
            templateUrl: "js/components/dashboard/dashboard.template.html",
            onEnter: enteringGuard
        };
        var detailState = {
            name: ROUTE_STATES.DETAIL_STATE,
            url: "/detail/{surveyId}",
            templateUrl: "js/components/detail/detail.template.html",
            onEnter: enteringGuard
        };
        var defaultState = {
            name: ROUTE_STATES.DEFAULT_STATE,
            url: '',
            templateUrl: "js/components/dashboard/dashboard.template.html",
            onEnter: enteringGuard
        };

        $stateProvider.state(loginState);
        $stateProvider.state(registerState);
        $stateProvider.state(dashboardState);
        $stateProvider.state(defaultState);
        $stateProvider.state(detailState);
    });
})();