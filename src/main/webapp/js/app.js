var app = angular.module('cal', ['ui.calendar']);

app.controller('calCtrl', ['$scope', '$http', function ($scope, $http) {
    $scope.currentAction = "home";
    $scope.clientId = "";
    $scope.consultantId = "";

    $scope.clientConfig = {
      calendar:{
        // put your options and callbacks here
        header: {
            left: 'prev,next',
            center: 'title',
            right: 'agendaWeek,agendaDay'
        },
        selectable: true,
        selectHelper: true,
        select: function(start, end) {
            var durationMillis = end - start;
            $http({
                    method: 'POST',
                    url: 'http://localhost:8080/api/book',
                    headers: {'Content-Type':'application/json'},
                    data: { "clientId": $scope.clientId, "consultantId": $scope.consultantId, "startTime": start.unix() * 1000, "durationMillis": durationMillis }
                }).success(function (data) {
                   var eventData = {
                       title: 'BOOKING',
                       start: start,
                       end: end
                   };

                   $('#client-calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                   $('#client-calendar').fullCalendar('unselect');
                }).error(function (data, status, headers, config) {

                });

        },
        editable: true,
        eventLimit: true,
        events: function(start, end, timezone, callback) {
            $.ajax({
                url: 'http://localhost:8080/api/office-hours/' + $scope.consultantId + '?startDate=' + start.unix() + '000&endDate=' + end.unix() + '000',
                type: 'GET',
                success: function(data) {
                    var events = [];

                    var prevStatus = data.startStatus;
                    var prevStart = start;

                    for (var i = 0; i < data.deltas.length; i++) {
                        var currEnd = $.fullCalendar.moment(data.deltas[i].time.millis);

                        addAvailable(prevStatus, prevStart, currEnd, events);

                        prevStatus = data.deltas[i].toStatus;
                        prevStart = currEnd;
                    }

                    addAvailable(prevStatus, prevStart, currEnd, events);

                    callback(events);
                }
            });
        }
      }
    };

    $scope.consultantConfig = {
      calendar:{
                // put your options and callbacks here
                header: {
                    left: 'prev,next',
                    center: 'title',
                    right: 'agendaWeek,agendaDay'
                },
                selectable: true,
                selectHelper: true,
                select: function(start, end) {
                    var durationMillis = end - start;
                    $http({
                            method: 'POST',
                            url: 'http://localhost:8080/api/office-hours/set',
                            headers: {'Content-Type':'application/json'},
                            data: { "consultantId": $scope.consultantId, "startTime": start.unix() * 1000, "durationMillis": durationMillis }
                        }).success(function (data) {
                           var eventData = {
                               title: 'AVAILABLE',
                               rendering: 'background',
                               start: start,
                               end: end
                           };

                           $('#consultant-calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                           $('#consultant-calendar').fullCalendar('unselect');
                        }).error(function (data, status, headers, config) {

                        });
                },
                editable: true,
                eventLimit: true,
                events: function(start, end, timezone, callback) {
                    $.ajax({
                        url: 'http://localhost:8080/api/office-hours/' + $scope.consultantId + '?startDate=' + start.unix() + '000&endDate=' + end.unix() + '000',
                        type: 'GET',
                        success: function(data) {
                            var events = [];

                            var prevStatus = data.startStatus;
                            var prevStart = start;

                            for (var i = 0; i < data.deltas.length; i++) {
                                var currEnd = $.fullCalendar.moment(data.deltas[i].time.millis);

                                addAvailable(prevStatus, prevStart, currEnd, events);
                                addBooked(prevStatus, prevStart, currEnd, events);

                                prevStatus = data.deltas[i].toStatus;
                                prevStart = currEnd;
                            }

                            addAvailable(prevStatus, prevStart, end, events);
                            addBooked(prevStatus, prevStart, end, events);

                            callback(events);
                        }
                    });
                }
      }
    };

    $scope.init = function() {
        $scope.currentAction = "home";
    }

    $scope.Login = function(email) {
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/user?email=' + email,
            headers: {'Content-Type':'application/json'},
        }).success(function (data) {
            if(data == "") {
                $scope.currentAction = "register";
            } else if(data.client) {
                $scope.currentAction = "clientCal";
                $scope.clientId = data.uuid;
                $scope.consultantId = "5c8d95d1-3ef4-4bdd-85ca-267bb6cd380d";
           } else {
               $scope.currentAction = "consultantCal";
               $scope.consultantId = data.uuid;
           }
        }).error(function (data, status, headers, config) {
            $scope.currentAction = "register";
        });
    };

    $scope.Register = function(type, firstName, lastName, email) {
           if($scope.type == "client") {
                isClient = "true"
           } else {
                isClient = "false";
           }
        $http({
            method: 'POST',
            url: 'http://localhost:8080/api/user/register',
            headers: {'Content-Type':'application/json'},
            data: { "firstName": firstName, "lastName": lastName, "email": email, "isClient": isClient }
        }).success(function (data) {
            if(isClient) {
                $scope.currentAction = "clientCal";
           } else {
               $scope.currentAction = "consultantCal";
           }
        }).error(function (data, status, headers, config) {
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

function addBooked(status, start, end, events) {
    if (status == 'BOOKED') {
        events.push({
            title: "Booked by client",
            start: start,
            end: end
        });
    }
}