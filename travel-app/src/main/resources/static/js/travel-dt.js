(function() {
  var app = angular.module('travel-dt', ['ui.bootstrap', 'flight-service']);

  app.directive('travelDt', function() {
    var travelDtCtrl = ['$scope', 'flightService',
      function($scope, flightService) {
        $scope.flight = flightService;

        $scope.clear = function() {
          $scope.dt = null;
        };

        $scope.today = function() {
          $scope.dt = new Date();
        };
        $scope.today();

        $scope.minDate = $scope.dt;
        var maxYear = $scope.minDate.getFullYear() + 1;
        $scope.maxDate = new Date(maxYear, $scope.dt.getMonth(), $scope.dt.getDate())

        $scope.open = function($event) {
          $scope.arrivalDtStatus.opened = ($event.currentTarget.id == 'arrivalDt');
          $scope.departureDtStatus.opened = ($event.currentTarget.id == 'departureDt');
        };

        $scope.dateOptions = {
          formatYear: 'yy',
          startingDay: 1
        };

        $scope.departureDtStatus = {
          opened: false
        };

        $scope.arrivalDtStatus = {
          opened: false
        };
      }
    ];

    return {
      restrict: 'E',
      templateUrl: 'pages/travel-dt.html',
      scope: {},
      controller: travelDtCtrl
    };
  });
})();