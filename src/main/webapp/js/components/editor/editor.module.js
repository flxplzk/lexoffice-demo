(function () {
    /**
     * @name "de.nordakademie.iaa.survey.editor"
     *
     * EditorModule
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var editor = angular.module("de.nordakademie.iaa.survey.editor", [
        "de.nordakademie.iaa.survey.core.domain",
        "de.nordakademie.iaa.survey.routes",
        "de.nordakademie.iaa.survey.core",
        "de.nordakademie.iaa.i18n",
        "md.time.picker",
        "ngMaterial"
    ]);

    editor.directive("surveyEditorActionButton", FloatingActionButtonDirective);
    editor.directive("deleteSurveyActionButton", DeleteButtonDirective);
    editor.directive("closeSurveyActionButton", CloseButtonDirective);
    editor.controller("floatingEditorController", ["$mdDialog", "$scope", FloatingActionButtonController]);
    editor.controller("closeActionButtonController", ["$mdDialog", "$scope", "EventResource", CloseActionButtonController]);
    editor.controller("deleteSurveyController", ["SurveyResource", "$mdDialog", "$translate",
        "$scope", "$state", "notificationService", "ROUTE_STATES", DeleteSurveyController]);

    function FloatingActionButtonDirective() {
        return {
            restrict: "E",
            transclude: false,
            controller: "floatingEditorController",
            controllerAs: "dialogManager",
            scope: {
                surveyId: "=",
                icon: "@"
            },
            template: "<div>" +
                "<md-button class=\"md-primary md-raised md-accent md-icon-button\"" +
                " ng-click=\"dialogManager.showEditorDialog($event)\">" +
                "<i class=\"material-icons\"> {{icon}} </i></md-button>" +
                "</div>"
        }
    }

    function CloseButtonDirective() {
        return {
            restrict: "E",
            transclude: false,
            controller: "closeActionButtonController",
            controllerAs: "closeCrtl",
            scope: {
                surveyId: "=",
                icon: "@"
            },
            template: "<div>" +
                "<md-button class=\"md-primary md-raised md-accent md-icon-button\"" +
                " ng-click=\"closeCrtl.close($event)\">" +
                "<i class=\"material-icons\"> {{icon}} </i></md-button>" +
                "</div>"
        }
    }

    function CloseActionButtonController($mdDialog, $scope) {
        this.close = function (ev) {
            $mdDialog.show({
                locals: {surveyId: $scope.surveyId},
                templateUrl: "/js/components/editor/close.dialog.template.html",
                controller: ["$mdDialog", "$scope", "EventResource", "SurveyResource",
                    "OptionResource", "$state", "surveyId", SurveyCloseController],
                controllerAs: "surveyCloseCrtl",
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: false,
                fullscreen: true
            })
        };
    }

    function SurveyCloseController($mdDialog, $scope, EventResource, SurveyResource,
                                   OptionResource, $state, surveyId) {
        $scope.caption = "EDITOR_CLOSE_TITLE";
        $scope.survey = SurveyResource.get({survey: surveyId});
        $scope.options = OptionResource.query({survey: surveyId});
        $scope.selected = null;

        this.cancel = function () {
            $mdDialog.cancel();
        };


        this.save = function () {
            var selectedDate = new Date(angular.fromJson($scope.selected).dateTime);
            var saveRequest = EventResource.save({title: $scope.survey.title, survey: $scope.survey, time:selectedDate});
            saveRequest.$promise.then(success, reject);
            function success(success) {
                $state.go($state.current, {}, {reload: true});
                $mdDialog.cancel();
            }

            function reject(error) {
                // TODO
            }
        };

        this.valid = function () {
            return $scope.selected != null;
        }
    }

    function DeleteButtonDirective() {
        return {
            restrict: "E",
            transclude: false,
            controller: "deleteSurveyController",
            controllerAs: "deleteCrtl",
            scope: {
                surveyId: "=",
                icon: "@"
            },
            template: "<div>" +
                "<md-button class=\"md-primary md-raised md-accent md-icon-button\"" +
                " ng-click=\"deleteCrtl.delete($event)\">" +
                "<i class=\"material-icons\"> {{icon}} </i></md-button>" +
                "</div>"
        }
    }

    function DeleteSurveyController(SurveyResource, $mdDialog, $translate, $scope, $state, notificationService, ROUTE_STATES) {
        this.delete = function (event) {
            var confirm = $mdDialog.confirm()
                .title($translate.instant("EDITOR_CONFIRM_DELETE_TITLE"))
                .textContent($translate.instant("EDITOR_CONFIRM_DELETE_CONTENT"))
                .targetEvent(event)
                .ok($translate.instant("EDITOR_CONFIRM_DELETE_OK"))
                .cancel($translate.instant("EDITOR_CONFIRM_DELETE_CANCEL"));

            $mdDialog.show(confirm).then(function () {
                SurveyResource.delete({survey: $scope.surveyId}, success, reject);

                function success(value) {
                    $state.go(ROUTE_STATES.DASHBOARD_STATE);
                    $mdDialog.cancel();
                }

                function reject(error) {
                    notificationService.showNotification(error.status === 409
                        ? "EDITOR_ERROR_DELETE_PERMISSION"
                        : "EDITOR_ERROR_DELETE_UNKNOWN");
                    $mdDialog.cancel();
                }
            }, function () {
                $mdDialog.cancel();
            });
        }
    }

    function FloatingActionButtonController($mdDialog, $scope) {
        this.showEditorDialog = function (ev) {
            $mdDialog.show({
                locals: {surveyId: $scope.surveyId},
                templateUrl: "/js/components/editor/editor.dialog.template.html",
                controller: ["$scope", "$mdDialog", "SurveyResource", "OptionResource", "$state",
                    "notificationService", "ROUTE_STATES", "surveyId", EditorController],
                controllerAs: "surveyEditorController",
                parent: angular.element(document.body),
                targetEvent: ev,
                clickOutsideToClose: false,
                fullscreen: true
            })
        };

        function EditorController($scope, $mdDialog, SurveyResource, OptionResource, $state,
                                  notificationService, ROUTE_STATES, surveyId) {
            var vm = this;
            $scope.createNew = angular.isUndefined(surveyId);
            $scope.caption = $scope.createNew ? "EDITOR_TITLE_NEW" : "EDITOR_TITLE_UPDATE";



            this.addEmptyOption = function () {
                var date = new Date();
                date.setMilliseconds(0);
                date.setSeconds(0);
                $scope.options.push(new OptionResource({dateTime: date}));
            };
            initialize();
            this.removeOption = function (optionToRemove) {
                $scope.options = $scope.options.filter(function (value) {
                    return !value.equals(optionToRemove);
                });
                if ($scope.options.length === 0) {
                    this.addEmptyOption();
                }
            };

            this.saveAsDraft = function () {
                saveWithStatus("PRIVATE");
            };

            this.saveAndPublish = function () {
                saveWithStatus("OPEN");
            };

            function saveWithStatus(status) {
                var survey = $scope.survey;
                survey.surveyStatus = status;
                survey.options = $scope.options;
                if (!validOptions()) {
                    notificationService.showNotification("EDITOR_OPTIONS_HINT");
                    return;
                }
                if ($scope.createNew) {
                    survey.$save(successHandler, createErrorHandler);
                } else {
                    survey.$update({survey: survey.getId()}, successHandler, createErrorHandler)
                }
            }

            function validOptions() {
                return optionsDefined() && optionsValid() && optionsUnique();
            }

            function optionsDefined() {
                return angular.isDefined($scope.options) && $scope.options.length > 0
            }

            function optionsValid() {
                for (var i = 0; i < $scope.options.length; i++) {
                    if (!$scope.options[i].isValid()) {
                        return false;
                    }
                }
                return true;
            }

            function optionsUnique() {
                var valueSoFar = Object.create(null);
                for (var i = 0; i < $scope.options.length; i++) {
                    var value = $scope.options[i];
                    if (value.dateTime in valueSoFar) {
                        return false;
                    }
                    valueSoFar[value.dateTime] = true;
                }
                return true;
            }

            this.valid = function () {
                return angular.isDefined($scope.survey)
                    && $scope.survey.isValid();
            };
            this.cancel = function () {
                $mdDialog.cancel();
            };

            function initialize() {
                if ($scope.createNew) {
                    $scope.survey = new SurveyResource({title: "", description: ""});
                    $scope.options = [];
                    vm.addEmptyOption();
                    $scope.busy = false;
                    return;
                }
                var requestParam = {survey: surveyId};
                $scope.survey = SurveyResource.get(requestParam);
                $scope.options = OptionResource.query(requestParam);
            }

            function successHandler(survey) {
                if ($scope.createNew) {
                    $state.go(ROUTE_STATES.DETAIL_STATE, {surveyId: survey.getId()});
                } else {
                    $state.go($state.current, {}, {reload: true});
                }
                vm.cancel()
            }

            function createErrorHandler(error) {
                if (error.status === 409) {
                    notificationService.showNotification("EDITOR_CONFLICT")
                } else if (error.status === 404) {
                    notificationService.showNotification("EDITOR_NOT_FOUND");
                    vm.cancel()
                } else {
                    notificationService.showNotification("EDITOR_NETWORK")
                }
            }

            function updateErrorHandler(error) {
                if (error.status === 409) {
                    notificationService.showNotification("EDITOR_CONFLICT")
                } else if (error.status === 404) {
                    notificationService.showNotification("EDITOR_NOT_FOUND");
                    vm.cancel()
                } else {
                    notificationService.showNotification("EDITOR_NETWORK")
                }
            }
        }
    }
}());