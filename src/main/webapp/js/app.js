var app = angular.module('cal', []);

app.controller('calCtrl', ['$scope', '$http', function ($scope, $http) {
    $scope.currentAction = "home";
    $scope.clientId = "";
    $scope.consultantId = "";

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