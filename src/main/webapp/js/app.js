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
            var eventData = {
                title: 'BOOKING',
                start: start,
                end: end
            };

            // TODO: make rest call to back end /book
            // If 204 then render, else message
            $('#client-calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
            $('#client-calendar').fullCalendar('unselect');
        },
        editable: true,
        eventLimit: true,
        events: function(start, end, timezone, callback) {
            $.ajax({
                url: 'http://localhost:8080/api/office-hours/5c8d95d1-3ef4-4bdd-85ca-267bb6cd380d?startDate=' + start.unix() + '000&endDate=' + end.unix() + '000',
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
                    var eventData = {
                        title: 'AVAILABLE',
                        rendering: 'background',
                        start: start,
                        end: end
                    };

                    // TODO: make rest call to back end set
                    // If 204 then render, else message
                    $('#consultant-calendar').fullCalendar('renderEvent', eventData, true); // stick? = true
                    $('#consultant-calendar').fullCalendar('unselect');
                },
                editable: true,
                eventLimit: true,
                events: function(start, end, timezone, callback) {
                    $.ajax({
                        url: 'http://localhost:8080/api/office-hours/5c8d95d1-3ef4-4bdd-85ca-267bb6cd380d?startDate=' + start.unix() + '000&endDate=' + end.unix() + '000',
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
           } else {
               $scope.currentAction = "consultantCal";
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
            method: 'PUT',
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
            $scope.currentAction = "register";
        });
    };

    $scope.Home = function() {
        $scope.currentAction = "home";
    };

    $scope.Client = function() {
        $scope.currentAction = "clientCal";
    };

    $scope.Consultant = function() {
        $scope.currentAction = "consultantCal";
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