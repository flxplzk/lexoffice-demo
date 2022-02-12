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

    toolbar.controller("mainController", ["appService", ToolBarController]);
    toolbar.directive("surveyToolBar", ["ROUTE_STATES", ToolBarDirective]);

    function ToolBarController(appService) {
        this.authenticated = false;
        var vm = this;
        appService.$authenticated.subscribeOnNext(function (authenticationStatus) {
            vm.authenticated = authenticationStatus;
        });
        this.logout = function () {
            appService.logout();
        }
    }

    function ToolBarDirective(ROUTE_STATES) {
        return {
            restrict: "E",
            template: "<md-toolbar ng-controller=\"mainController as toolBarCrtl\" layout=\"row\" class=\"md-toolbar-tools\">\n" +
                "        <a ui-sref="+ ROUTE_STATES.DASHBOARD_STATE +" ><h2 md-truncate>{{\"APP_HEADLINE\"|translate}}</h2></a>\n" +
                "        <span flex></span>" +
                "        <language-selector></language-selector>" +
                "        <survey-editor-action-button icon='add' ng-show=\"toolBarCrtl.authenticated\">" +
                "        </survey-editor-action-button>" +
                "        <md-button ng-show=\"toolBarCrtl.authenticated\" ng-click=\"toolBarCrtl.logout()\"\n" +
                "                   class=\"md-icon-button\">\n" +
                "            <i class=\"material-icons\">power_settings_new</i>" +
                "           <md-tooltip >{{\"APP_LOGOUT\"|translate}}</md-tooltip>\n" +
                "        </md-button>\n" +
                "</md-toolbar>"
        }
    }

}());