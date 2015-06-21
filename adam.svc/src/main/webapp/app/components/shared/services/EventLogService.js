/**
 * Copyright (c) 2015 GATBLAU - www.gatblau.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
angular.module('adam').service("EventLogService", function($http, $q, $filter){
    return ({
        getEvents: getEvents,
        getEventsByProcess: getEventsByProcess,
        getEvent: getEvent,
        deleteEvent: deleteEvent,
        deleteEvents: deleteEvents,
        clearLog: clearLog
    });

    function getEvents(fromDate, toDate){
        var request = $http({
            method: "GET",
            url: "/adam/wapi/event/query",
            params: {
                from: $filter('date')(fromDate, "dd-MM-yyyy-HH:mm"),
                to: $filter('date')(toDate, "dd-MM-yyyy-HH:mm")
            },
            data: '',
            headers: {
                "Content-Type": "application/json"
            }
        });
        return request.then(handleSuccess, handleError);
    }

    function getEventsByProcess(processId){
        var request = $http({
            method: "GET",
            url: "/adam/wapi/event/process",
            params: {
                processId: processId
            },
            data: '',
            headers: {
                "Content-Type": "application/json"
            }
        });
        return request.then(handleSuccess, handleError);
    }

    function getEvent(eventId){
        var request = $http({
            method: "GET",
            url: "/adam/wapi/event/" + eventId,
            data: '',
            headers: {
                "Content-Type": "application/json"
            }
        });
        return request.then(handleSuccess, handleError);
    }

    function deleteEvent(eventId){
        var request = $http({
            method: "DELETE",
            url: "/adam/wapi/event/" + eventId
        });
        return request.then(handleSuccess, handleError);
    }

    function deleteEvents(fromDate, toDate){
        var request = $http({
            method: "DELETE",
            url: "/adam/wapi/event",
            params: {
                from: $filter('date')(fromDate, "dd-MM-yyyy-HH:mm"),
                to: $filter('date')(toDate, "dd-MM-yyyy-HH:mm")
            },
            data: '',
            headers: {
                "Content-Type": "application/json"
            }
        });
        return request.then(handleSuccess, handleError);
    }

    function clearLog(){
        var request = $http({
            method: "DELETE",
            url: "/adam/wapi/event"
        });
        return request.then(handleSuccess, handleError);
    }

    function handleSuccess(response){
        return response.data;
    }

    function handleError(response){
        if (!angular.isObject(response.data) || !response.data.message){
            return $q.reject("An error occurred");
        }
        return $q.reject(response.error.message);
    }
})