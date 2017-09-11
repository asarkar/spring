(function() {
  var app = angular.module("pinterest", ["kendo.directives"]);
  app.controller("PinterestController", function($scope) {
    $scope.board = undefined;
    $scope.showErrorMsg = false;

    $scope.onChange = function() {
      $scope.showErrorMsg = !$scope.board;
    };
    $scope.onUpload = function(e) {
      $scope.$apply(function() {
        $scope.showErrorMsg = !$scope.board;
      });
      if ($scope.board) {
        var urlTemplate = "pins?boardName={board}"
        e.sender.options.async.saveUrl = urlTemplate.replace("{board}", $scope.board)
      } else {
        e.preventDefault();
      }
    };
  });
})();