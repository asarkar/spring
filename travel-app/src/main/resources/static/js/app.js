(function() {
  var app = angular.module('travelApp', ['airport-search', 'travel-dt', 'flight-service',
    'ui.bootstrap', 'ui.grid', 'ui.grid.pagination'
  ]);

  app.controller('FlightSearchCtrl', [
    '$scope', '$http', '$filter', '$interval', 'flightService',
    function($scope, $http, $filter, $interval, flightService) {
      $scope.flight = flightService;

      var paginationOptions = {
        pageNumber: 1,
        pageSize: 25,
        sort: null
      };

      $scope.gridOptions = {
        paginationPageSizes: [25, 50, 75],
        paginationPageSize: 25,
        useExternalPagination: true,
        useExternalSorting: false,
        minRowsToShow: 10,
        columnDefs: [{
          name: 'airline',
          field: 'airline'
        }, {
          name: 'flightNumber',
          field: 'flightNum'
        }, {
          name: 'departure',
          field: 'departureTime'
        }, {
          name: 'from',
          field: 'srcAirportName'
        }, {
          name: 'to',
          field: 'destAirportName'
        }, {
          name: 'aircraft',
          field: 'aircraft'
        }],
        onRegisterApi: function(gridApi) {
          $scope.gridApi = gridApi;

          gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
            paginationOptions.pageNumber = newPage;
            paginationOptions.pageSize = pageSize;

            searchFlights();
          });
        }
      };

      $scope.searchFlights = function() {
        $http.get('flights', {
          params: {
            src: $scope.flight.srcAirport.faaCode,
            dest: $scope.flight.destAirport.faaCode,
            date: $filter('date')($scope.flight.departureDt, 'yyyy-MM-dd'),
            pageSize: paginationOptions.pageSize,
            pageNum: paginationOptions.pageNumber
          }
        }).success(function(results) {
          $scope.gridOptions.totalItems = results.pageSize * results.numPages;
          $scope.gridOptions.data = results.data;
          $interval(function() {
            $scope.gridApi.core.handleWindowResize();
          }, 10, 500);
        });
      };
    }
  ]);
})();