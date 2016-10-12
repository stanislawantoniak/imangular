'use strict';

//Register `navigation` component, along with its associated controller and template
angular.
module('dashboard', []).
controller( 'dashboard', function DashboardController($rootScope, $http, $location) {
	var self = this;
	if (!$rootScope.authenticated){
		$location.path("/login");
		self.error = true;
	}
	console.log('from dashboard controller');
});
