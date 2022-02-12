(function () {

    // ########################## MODULE DECLARATION #####################################

    /**
     * @name "de.nordakademie.iaa.survey.core.domain"
     *
     * This module provides domain Objects for this application.
     * Domain Objects are:
     *      {@link UserResource}
     *      {@link SurveyResource}
     *      {@link ParticipationResource}
     *      {@link OptionResource}
     *      {@link NotificationResource}
     *      {@link EventResource}
     *
     * @author Felix Plazek
     *
     * @type {angular.Module}
     */
    var domain = angular.module("de.nordakademie.iaa.survey.core.domain", [
        "ngResource"
    ]);

    domain.factory("UserResource", ["$resource", UserServiceFactory]);
    domain.factory("SurveyResource", ["$resource", SurveyResourceFactory]);
    domain.factory("ParticipationResource", ["$resource", ParticipationResourceFactory]);
    domain.factory("OptionResource", ["$resource", OptionResourceFactory]);
    domain.factory("NotificationResource", ["$resource", NotificationResourceFactory]);
    domain.factory("EventResource", ["$resource", EventResourceFactory]);

    // ########################## FACTORY FUNCTIONS #####################################

    function UserServiceFactory($resource) {
        var resource = $resource("./api/users");
        angular.extend(resource.prototype, user);
        return resource;
    }

    function SurveyResourceFactory($resource) {
        var resource = $resource(
            "./api/surveys/:survey",
            {survey: "@survey"},
            {
                query: requestWithResponseTransformation("GET", true, transformSurveys, $resource),
                update: requestWithResponseTransformation("PUT", false, transformSurveys, $resource),
                get: requestWithResponseTransformation("GET", false, transformSurveys, $resource)
            }
        );
        angular.extend(resource.prototype, survey);
        return resource
    }

    function ParticipationResourceFactory($resource) {
        var resource = $resource(
            "./api/surveys/:survey/participations/:participation",
            {survey: "@survey", participation: "@participation"},
            {
                query: requestWithResponseTransformation("GET", true, transformParticipations, $resource),
                save: requestWithResponseTransformation("POST", false, transformParticipations, $resource),
                update: requestWithResponseTransformation("PUT", false, transformParticipations, $resource),
                get: requestWithResponseTransformation("GET", false, transformParticipations, $resource)
            }
        );
        angular.extend(resource.prototype, participation);
        return resource
    }

    function OptionResourceFactory($resource) {
        var resource = $resource(
            "./api/surveys/:survey/options",
            {survey: "@survey"},
            {
                query: requestWithResponseTransformation("GET", true, transformOptions, $resource),
                get: requestWithResponseTransformation("GET", false, transformOptions, $resource)
            }
        );
        angular.extend(resource.prototype, option);
        return resource
    }

    function NotificationResourceFactory($resource) {
        var resource = $resource("./api/users/me/notifications/:notification", {notification: "@notification"});
        angular.extend(resource.prototype, notification);
        return resource
    }

    function EventResourceFactory($resource) {
        var resource = $resource("./api/users/me/events/:event", {event: "@event"});
        angular.extend(resource.prototype, event);
        return resource
    }

    // ########################## DOMAIN OBJECTS #####################################

    var survey = {
        getId: _getId,
        isOpen: function () {
            return this.surveyStatus && this.surveyStatus === "OPEN"
        },
        isClosed: function () {
            return this.surveyStatus && this.surveyStatus === "CLOSED"
        },
        isValid: function () {
            return this.title !== ""
                && this.description !== ""
        }
    };

    var option = {
        getId: _getId,
        isValid: function () {
            return this.dateTime > new Date()
        },
        equals: function (other) {
            return other !== null &&
                (angular.isDefined(this._id) && angular.isDefined(other._id) && this._id === other._id
                    || angular.isDefined(this.dateTime) && angular.isDefined(other.dateTime)
                    && this.dateTime === other.dateTime)
        }
    };

    var participation = {
        getId: _getId,
        $persist: function (surveyId, success, error) {
            if (angular.isDefined(this.getId())) {
                return this.$update({survey: surveyId, participation: this.getId()}, success, error)
            } else {
                return this.$save({survey: surveyId}, success, error)
            }
        }
    };

    var user = {
        getId: _getId,
        isValid: function () {
            return this.firstName && this.lastName && this.username && this.password
                && this.firstName !== "" && this.lastName !== ""
                && this.username !== "" && this.password !== ""
        },
        equals: function (other) {
            return this.username && other
                && this.username === other.username;
        }
    };

    var notification = {
        getId: _getId
    };

    var event = {
        getId: _getId
    };

    // ########################## HELPER FUNCTIONS #####################################

    function requestWithResponseTransformation(method, isResponseArray, transformer, $resource) {
        return {
            method: method,
            isArray: isResponseArray,
            transformResponse: function (data, header) {
                return transform($resource, data, isResponseArray, transformer)
            }
        }
    }

    function transform(resource, data, isArray, transformer) {
        var transformedJson = angular.fromJson(data);
        if (transformedJson.status && transformedJson.status !== 200) {
            return transformedJson;
        }
        return transformer(transformedJson, resource, isArray);
    }

    function _getId() {
        return this._id;
    }

    function transformOptions(options, $resource, isArray) {
        if (isArray) {
            options.forEach(transformDateTime);
        } else {
            transformDateTime(options);
        }
        return options;
    }

    function transformDateTime(option) {
        option.dateTime = new Date(option.dateTime)
    }

    function transformParticipations(participations, resource, isArray) {
        if (isArray) {
            participations.forEach(participationTransformer(resource))
        } else {
            participationTransformer(resource)
        }
        return participations;
    }

    function participationTransformer(resource) {
        var optionResource = OptionResourceFactory(resource);
        return function (participation) {
            participation.options.forEach(function (option, index) {
                transformDateTime(option);
                participation.options[index] = new optionResource({
                    _id: option._id,
                    dateTime: option.dateTime
                })
            })
        }
    }

    function transformSurveys(surveys, resource, isArray) {
        if (isArray) {
            surveys.forEach(surveyTransformer(resource));
        } else {
            surveyTransformer(resource)(surveys);
        }
        return surveys;
    }

    function surveyTransformer(resource) {
        var userResource = UserServiceFactory(resource);
        return function transformSurvey(surveysOrSurvey) {
            var initiator = surveysOrSurvey.initiator;
            surveysOrSurvey.initiator = new userResource({
                _id: initiator._id,
                firstName: initiator.firstName,
                lastName: initiator.lastName,
                username: initiator.username
            })
        }
    }
}());