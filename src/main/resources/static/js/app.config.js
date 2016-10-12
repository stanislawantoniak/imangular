'use strict';

angular.
module('imangular', [ 'ngRoute', 'navigation', 'dashboard'  ] ).
config(['$routeProvider', '$httpProvider', function( $routeProvider, $httpProvider) {

	console.log('imangular starting');

	$routeProvider.when('/', {
		templateUrl : 'js/dashboard/dashboard.html',
		controller : 'dashboard'
	}).when('/login', {
		templateUrl : 'js/common/login.html',
		controller : 'navigation',
		controllerAs : 'controller'
	}).otherwise('/');

	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

	console.log('imangular ending');
}]).
factory('labels',['$http', function($http){
	var self = this;
	if (!self.label){
		$http.get('/common/labels').then( function(response) {
			self.label = response.data;
		})
	};
	
	return function(){
		console.log('labels service')
		return self.label;
	}
}]);
