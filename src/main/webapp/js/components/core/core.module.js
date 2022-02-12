(function () {
    // ########################## MODULE DECLARATION #####################################

    /**
     * @name "de.nordakademie.iaa.survey.core"
     *
     * Core functionalities of the frontend
     *
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var core = angular.module("de.nordakademie.iaa.survey.core", [
        "de.nordakademie.iaa.i18n",
        "rx",
        "base64",
        "ui.router",
        "ngMaterial"
    ]);

    core.service("notificationService", ["$mdToast", "$translate", NotificationService]);
    core.service("authService", ["$http", "$base64", AuthenticationService]);
    core.service("appService", ["$state", "authService", "notificationService", "rx", AppService]);

    /**
     * Central service for login and logout + retrieving info concerning logged in user
     *
     * @param $state from ui.router
     * @param authService from core module
     * @param errorService from core module
     * @param rx from rx-angular
     * @constructor default constructor
     */
    function AppService($state, authService, errorService, rx) {
        var isAuthenticated = false;
        var vm = this;
        this.authenticatedUser = null;
        this.$authenticated = new rx.BehaviorSubject(isAuthenticated);

        /**
         * Using {@link BroadcastChannel} events can be broadcasted accres different application threads in multiple
         * browser tabs Doing so, the app can respond to logout events.
         * @type {BroadcastChannel}
         */
        var channel = new BroadcastChannel("de.nordakademie.iaa.survey.logout");
        channel.onmessage = function (ev) {
            if (!isAuthenticated || "user-logout-requested" !== ev.data) return;
            vm.logout();
            errorService.showNotification("LOGGED_OUT");
        };

        this.$authenticated.subscribeOnNext(function (authenticationStatus) {
            isAuthenticated = authenticationStatus;
            if (!isAuthenticated) {
                vm.authenticatedUser = null;
                $state.go("login");
            }
        });

        this.login = function (username, password) {
            authService.authenticate(username, password).then(
                function (success) {
                    $state.go("dashboard");
                    vm.authenticatedUser = success.data;
                    vm.$authenticated.onNext(true);
                },
                function (error) {
                    vm.$authenticated
                        .onNext(authService.removeAuthorization());
                    errorService.showNotification("AUTH_LOGIN_ERROR_CREDENTIALS");
                }
            );
        };

        this.logout = function () {
            channel.postMessage("user-logout-requested");
            authService.logout();
            this.$authenticated
                .onNext(authService.removeAuthorization());
        };
        this.isAuthenticated = function () {
            return isAuthenticated;
        };

        this.testAuthentication = function (successCallback, reject) {
            if (this.isAuthenticated()) {
                successCallback();
                return;
            }
            authService.testAuth(function (success) {
                vm.authenticatedUser = success.data;
                vm.$authenticated.onNext(true);
                successCallback(success);
            }, reject)
        };

        this.getAuthenticatedUser = function () {
            return this.authenticatedUser;
        }
    }

    /**
     * NotificationService; informs user via toast on events.
     *
     * @param $mdToast ngMaterial ToastService
     * @param $translate ngTranslate translationService
     * @constructor
     */
    function NotificationService($mdToast, $translate) {
        this.showNotification = function (errorMessageKey) {
            var toast = $mdToast.simple()
                .textContent($translate.instant(errorMessageKey))
                .position('bottom right');
            $mdToast.show(toast);
        }
    }

    /**
     * Service for ensuring httpbasic authentication, by setting http authorization header information.
     *
     * @param $http http service
     * @param encoder base 64 encoder
     * @constructor
     */
    function AuthenticationService($http, encoder) {
        this.authenticate = function (username, password) {
            // Try to log in
            var encodedCredentials = encoder.encode(username + ":" + password);
            setAuthorizationHeaders("Basic " + encodedCredentials);
            return $http.get("./api/users/me");
        };
        this.removeAuthorization = function () {
            setAuthorizationHeaders(null);
            return false;
        };

        this.logout = function () {
            this.removeAuthorization();
            /**
             * with posting to ./logout jsSession cookies will be removed.
             */
            $http.post("logout");
        };

        this.testAuth = function (success, reject) {
            $http.get("./api/users/me").then(success, reject);
        };

        function setAuthorizationHeaders(header) {
            $http.defaults.headers.common["authorization"] = header;
        }
    }
}());