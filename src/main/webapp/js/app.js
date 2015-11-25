var app = angular.module('cal', ['ui.calendar']);

var constants = {
   'host': 'localhost',
   'port': '8080',
   'startDateParam': 'startDate=',
   'endDateParam': 'endDate=',
   'emailParam': 'email='
};

app.controller('calCtrl', ['$scope', '$http', function ($scope, $http) {
    $scope.currentAction = "home";
    $scope.clientId = "";
    $scope.consultantId = "";

    $scope.events = [];
    $scope.eventSources = [$scope.events];

    $scope.clientConfig = {
      calendar:{
        // put your options and callbacks here
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'agendaWeek,agendaDay'
        },
        allDaySlot: false,
        defaultView: 'agendaWeek',
        theme: true,
        selectable: true,
        selectHelper: true,
        select: function(start, end) {
            var durationMillis = end - start;
            $http({
                    method: 'POST',
                    url: 'http://' + constants.host + ':' + constants.port + '/api/book',
                    headers: {'Content-Type':'application/json'},
                    data: { "clientId": $scope.clientId, "consultantId": $scope.consultantId, "startTime": start.valueOf(), "durationMillis": durationMillis }
                }).then(function successCallback(response) {
                       var eventData = {
                           title: 'NEW BOOKING',
                           start: start,
                           end: end
                       };

                       $('#client-calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                       $('#client-calendar').fullCalendar('unselect');
                   }, function errorCallback(response) {
                        alert("This slot is not available to book an appointment");
                        $('#client-calendar').fullCalendar('unselect');
                }, function errorCallback(response) {

                });

        },
        editable: true,
        eventLimit: true,
        timezone: 'UTC',
        events: function(start, end, timezone, callback) {
            $http({
                method: 'GET',
                url: 'http://' + constants.host + ':' + constants.port + '/api/office-hours/' + $scope.consultantId + '?' + constants.startDateParam + start.valueOf() + '&' + constants.endDateParam + end.valueOf(),
            }).then(function successCallback(response) {
                    var events = [];

                    var prevStatus = response.data.startStatus;
                    var prevStart = start;
                    var prevBookedClientId = "";

                    for (var i = 0; i < response.data.deltas.length; i++) {
                        var currEnd = $.fullCalendar.moment(response.data.deltas[i].time);

                        addAvailable(prevStatus, prevStart, currEnd, events);
                        if($scope.clientId == prevBookedClientId) {
                            addBooked(prevStatus, prevBookedClientId, prevStart, currEnd, events);
                        }

                        prevStatus = response.data.deltas[i].toStatus;
                        prevStart = currEnd;

                        if(prevStatus == "BOOKED") {
                            prevBookedClientId = response.data.deltas[i].clientId
                        } else {
                            prevBookedClientId = "";
                        }
                    }

                    addAvailable(prevStatus, prevStart, currEnd, events);
                    if($scope.clientId == prevBookedClientId) {
                        addBooked(prevStatus, prevBookedClientId, prevStart, currEnd, events);
                    }

                    callback(events);
           }, function errorCallback(response) {
                    
           });
        }
      }
    };

    $scope.consultantConfig = {
      calendar:{
            // put your options and callbacks here
            header: {
                left: 'prev,next today',
                center: 'title',
                right: 'agendaWeek,agendaDay'
            },
            allDaySlot: false,
            theme: true,
            defaultView: 'agendaDay',
            selectable: true,
            selectHelper: true,
            select: function(start, end) {
                var durationMillis = end - start;
                $http({
                        method: 'POST',
                        url: 'http://' + constants.host + ':' + constants.port + '/api/office-hours/set',
                        headers: {'Content-Type':'application/json'},
                        data: { "consultantId": $scope.consultantId, "startTime": start.valueOf(), "durationMillis": durationMillis }
                }).then(function successCallback(response) {
                           var eventData = {
                               title: 'AVAILABLE',
                               rendering: 'background',
                               start: start,
                               end: end
                           };

                           $('#consultant-calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                           $('#consultant-calendar').fullCalendar('unselect');
                }, function errorCallback(response) {

                });
            },
            editable: true,
            eventLimit: true,
            timezone: 'UTC',
            events: function(start, end, timezone, callback) {
                $http({
                    method: 'GET',
                    url: 'http://' + constants.host + ':' + constants.port + '/api/office-hours/' + $scope.consultantId + '?' + constants.startDateParam + start.valueOf() + '&' + constants.endDateParam + end.valueOf(),
                }).then(function successCallback(response) {
                        var events = [];

                        var prevStatus = response.data.startStatus;
                        var prevStart = start;
                        var prevBookedClientId = "";

                        for (var i = 0; i < response.data.deltas.length; i++) {
                            var currEnd = $.fullCalendar.moment(response.data.deltas[i].time);

                            addAvailable(prevStatus, prevStart, currEnd, events);
                            addBooked(prevStatus, prevBookedClientId, prevStart, currEnd, events);

                            prevStatus = response.data.deltas[i].toStatus;
                            prevStart = currEnd;

                            if(prevStatus == "BOOKED") {
                                prevBookedClientId = response.data.deltas[i].clientId
                            } else {
                                prevBookedClientId = "";
                            }
                        }

                        addAvailable(prevStatus, prevStart, end, events);
                        addBooked(prevStatus, prevStart, end, events);

                        callback(events);
                }, function errorCallback(response) {
                    
                });
            }
      }
    };

    $scope.init = function() {
        $scope.currentAction = "home";

        // get all consultants to build the consultant select drop down
        $select = $('#consultants');
        $http({
          url: 'http://' + constants.host + ':' + constants.port + '/api/user/consultant/list',
          headers: {'Content-Type':'application/json'},
         }).then(function successCallback(response) {
                $select.html('<option value="-1"></option>');

                $.each(response.data, function(key, val){
                  $select.append('<option value="' + val.uuid + '">' + val.firstName + '</option>');
                })
         }, function errorCallback(response) {
                $select.html('<option value="-1">none available</option>');
         });
    }

     $scope.SelectConsultant = function() {
        $('#client-calendar').fullCalendar('refetchEvents');
     }

    $scope.Login = function(email) {
        $http({
            method: 'GET',
            url: 'http://' + constants.host + ':' + constants.port + '/api/user?' + constants.emailParam + email,
            headers: {'Content-Type':'application/json'},
       }).then(function successCallback(response) {
            if(response.data == "") {
                $scope.currentAction = "register";
            } else if(response.data.client) {
                $scope.currentAction = "clientCal";
                $scope.clientId = response.data.uuid;
           } else {
               $scope.currentAction = "consultantCal";
               $scope.consultantId = response.data.uuid;
               $('#consultant-calendar').fullCalendar('refetchEvents');
           }
       }, function errorCallback(response) {
            $scope.currentAction = "register";
       });
    };

    $scope.Register = function(type, firstName, lastName, email) {
         if(type == "client") {
             isClient = true;
         } else {
             isClient = false;
         }
        $http({
            method: 'POST',
            url: 'http://' + constants.host + ':' + constants.port + '/api/user/register',
            headers: {'Content-Type':'application/json'},
            data: { "firstName": firstName, "lastName": lastName, "email": email, "isClient": isClient }
        }).then(function successCallback(response) {
            if(isClient) {
               $scope.clientId = response.data;
               $scope.currentAction = "clientCal";
           } else {
               $scope.consultantId = response.data;
               $scope.currentAction = "consultantCal";
               $('#consultant-calendar').fullCalendar('refetchEvents');
           }
        }, function errorCallback(response) {
            alert("Could not register with that information. Please try again.")
            $scope.currentAction = "register";
        });
    };
}]);

function addAvailable(status, start, end, events) {
   if (status == 'AVAILABLE') {
        events.push({
            title: status,
            start: start,
            end: end,
            rendering: 'background'
        });
    }
}

function addBooked(status, bookedClientId, start, end, events) {
    if (status == 'BOOKED') {
        events.push({
            title: "Booked by " + bookedClientId,
            start: start,
            end: end
        });
    }
}