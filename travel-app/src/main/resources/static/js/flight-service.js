(function() {
  var app = angular.module('flight-service', []);

  app.factory('flightService', function() {
    return {
      srcAirport: '',
      destAirport: '',
      departureDt: '',
      arrivalDt: ''
    }
  })
})();