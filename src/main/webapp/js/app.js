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
                    alert("This slot is not available to book an appointment")
                    ('#client-calendar').fullCalendar('unselect');
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

    $scope.SelectConsultant = function(uuid) {
        $scope.consultantId = uuid;
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
                isClient = true;
           } else {
                isClient = false;
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

     $scope.ListConsultants = function() {
        //get a reference to the select element
        $select = $('#consultants');
        //request the JSON data and parse into the select element
        $http({
          url: 'http://localhost:8080/api/user/consultant/list',
          headers: {'Content-Type':'application/json'},
          }).success(function(data){
            //clear the current content of the select
            $select.html('');
            //iterate over the data and append a select option
            $.each(data, function(key, val){
              $select.append('<option id="' + val.uuid + '">' + val.firstName + '</option>');
            })

          }).error(function(){
            //if there is an error append a 'none available' option
            $select.html('<option id="-1">none available</option>');
          });
    }
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