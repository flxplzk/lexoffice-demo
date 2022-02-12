(function () {
    /**
     * @name "de.nordakademie.iaa.survey.dashboard"
     *
     * ToolBarModule
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var dashboard = angular.module("de.nordakademie.iaa.survey.dashboard", [
        "de.nordakademie.iaa.survey.core.domain",
        "de.nordakademie.iaa.survey.core",
        "de.nordakademie.iaa.survey.routes",
        "de.nordakademie.iaa.i18n",
        "ngMaterial"
    ]);

    dashboard.controller("dashboardController", ["$scope", "SurveyResource", "$state", "$mdDialog", "ROUTE_STATES",
        DashboardController]);
    dashboard.controller("dashboardSideNavController", ["$scope", "NotificationResource", "EventResource",
        "appService", "$timeout", "$state", "notificationService", "ROUTE_STATES", DashboardSideNavController]);
    dashboard.directive("dashboardSideNav", DashboardSideNavDirective);
    dashboard.directive("dashboardSurveyListing", SurveyListingDirective);

    function DashboardSideNavController($scope, NotificationResource, EventResource, appService,
                                        $timeout, $state, notificationService, ROUTE_STATES) {
        $scope.loggedIn = appService.isAuthenticated();
        $scope.events = [];
        $scope.notifications = [];
        var timer;
        init();

        function init() {
            if (appService.isAuthenticated()) {
                EventResource.query(function (success) {
                    if ($scope.events.length < success.length) {
                        notificationService.showNotification("DASHBOARD_SIDE_NAV_NEW_EVENT");
                    }
                    $scope.events = success;
                });
                NotificationResource.query(function (success) {
                    if ($scope.notifications.length < success.length) {
                        notificationService.showNotification("DASHBOARD_SIDE_NAV_NEW_NOTIFICATION");
                    }
                    $scope.notifications = success;
                });
                timer = $timeout(init, 10000);
            }
        }

        this.viewDetails = function (survey) {
            $state.go(ROUTE_STATES.DETAIL_STATE, {surveyId: survey._id})
        };

        this.delete = function (notification) {
            $timeout.cancel(timer);
            notification.$delete({notification: notification.getId()});
            init();
        };
        appService.$authenticated.subscribeOnNext(function (authenticationStatus) {
            $scope.loggedIn = authenticationStatus;
            if (!authenticationStatus) {
                $scope.events = [];
                $scope.notifications = [];
            } else {
                init();
            }
        })
    }

    function DashboardSideNavDirective() {
        return {
            restrict: "E",
            templateUrl: "/js/components/dashboard/dashboard.side.nav.template.html",
            controller: "dashboardSideNavController",
            controllerAs: "sideNavCrtl"
        }
    }

    function SurveyListingDirective() {
        return {
            restrict: "E",
            template: " <md-list>\n" +
                "         <md-list-item class=\"md-2-line\" ng-repeat=\"survey in model.surveys\"\n" +
                "           ng-click=\"dashboardCrtl.viewDetails(survey)\">\n" +
                "             <div class=\"md-list-item-text\">\n" +
                "                <h3>{{survey.title}}</h3>\n" +
                "                <h4>{{survey.description}}</h4>\n" +
                "                 </div>\n" +
                "                 <md-button class=\"md-icon-button md-primary\"\n" +
                "                      ng-click=\"dashboardCrtl.viewDetails(survey)\">\n" +
                "                      <i class=\"material-icons\">{{ survey.isOpen() ? \"lock_open\" : \"lock_closed\"}}</i>\n" +
                "                 </md-button>\n" +
                "            <md-divider ng-if=\"!$last\"></md-divider>\n" +
                "         </md-list-item>\n" +
                "        </md-list>",
            scope: {
                filter: "@"
            },
            controller: "dashboardController",
            controllerAs: "dashboardCrtl"
        }
    }

    function DashboardController($scope, SurveyResource, $state, $mdDialog, ROUTE_STATES) {
        $scope.model = {
            surveys: [],
            loading: true
        };
        var filter = $scope.filter;
        // On init load all surveys from backend.
        var query = SurveyResource.query({filter: filter});
        query.$promise.then(function (surveys) {
            $scope.model.surveys = surveys;
            $scope.model.loading = false;
        });

        this.viewDetails = function (survey) {
            $state.go(ROUTE_STATES.DETAIL_STATE, {surveyId: survey.getId()})
        };

        $scope.initiatorOfSurveyShort = function (survey) {
            return survey.initiator.firstName.substring(0, 1)
                + survey.initiator.lastName.substring(0, 1)
        };

        $scope.showAdvanced = function (ev) {
            $mdDialog.show({
                templateUrl: "/js/components/editor/editor.dialog.template.html",
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: true,
                fullscreen: false
            })
        };
    }
}());