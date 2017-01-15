
(function() {
  var app = angular.module('helloWorld', ['ui.bootstrap', 'ui.grid', 'ui.grid.pagination']);

  app.controller('HelloWorldCtrl', ['$http', '$scope', '$interval', '$log',
    function($http, $scope, $interval, $log) {
      $scope.$log = $log;

      $scope.tabs = [{
        title: 'Countries',
        gridOptions: {
          columnDefs: [{
            name: 'code'
          }, {
            name: 'name'
          }, {
            name: 'continent'
          }, {
            name: 'region'
          }, {
            name: 'population'
          }]
        }
      }, {
        title: 'Cities',
        gridOptions: {
          columnDefs: [{
            name: 'id'
          }, {
            name: 'name'
          }, {
            name: 'country'
          }, {
            name: 'population'
          }]
        }
      }, {
        title: 'Languages',
        gridOptions: {
          columnDefs: [{
            name: 'country'
          }, {
            name: 'language'
          }]
        }
      }];

      var initPaginationOptions = {
        pageNumber: 1,
        pageSize: 25
      };

      var tabCommonGridOptions = {
        paginationPageSizes: [25, 50, 75],
        paginationPageSize: 25,
        useExternalPagination: true,
        useExternalSorting: false,
        enableFiltering: true,
        minRowsToShow: 20
      };

      /* Must use forEach, not index loop.
       * http://stackoverflow.com/questions/750486/javascript-closure-inside-loops-simple-practical-example
       */
      $scope.tabs.forEach(function(tab) {
        tab.paginationOptions = angular.copy(initPaginationOptions);
        angular.extend(tab.gridOptions, tabCommonGridOptions);

        tab.gridOptions.onRegisterApi = function(gridApi) {
          tab.gridApi = gridApi;
          gridApi.pagination.on.paginationChanged($scope, function(newPageNum, newPageSize) {
            tab.paginationOptions.pageNumber = newPageNum;
            tab.paginationOptions.pageSize = newPageSize;
            $scope.update(tab.title);
          });
        };
      });

      $scope.update = function(title) {
        $scope.tabs.forEach(function(tab) {
          if (tab.title === title) {
            $http.get(tab.title.toLowerCase(), {
              params: {
                pageSize: tab.paginationOptions.pageSize,
                pageNum: tab.paginationOptions.pageNumber
              }
            }).success(function(data) {
              tab.gridOptions.totalItems = data.totalElements;
              tab.gridOptions.data = data.content;
              $interval(function() {
                tab.gridApi.core.handleWindowResize();
              }, 10, 500);
            });
          }
        });
      };
    }
  ]);
})();