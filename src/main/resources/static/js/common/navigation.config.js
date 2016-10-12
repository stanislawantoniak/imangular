'use strict';

//Register `navigation` component, along with its associated controller and template
angular.
module('navigation', ['imangular']).
controller( 'navigation', ['$rootScope', '$http', '$location', 'labels', function NavigationController($rootScope, $http, $location, labels) {
	var self = this;
	
	console.log('navigation controller starting');
	
	var label = labels();
	
	console.log(label);
	
	var authenticate = function(credentials, callback) {

		console.log('authenticate credentials');
		console.log(credentials);
		
		var headers = credentials ? {authorization : "Basic "
			+ btoa(credentials.username + ":" + credentials.password)
		} : {};
		
		console.log('headers');
		console.log(headers);

		$http.get('userDetails', {headers : headers}).then(function(response) {
			if (response.data.name) {
				$rootScope.authenticated = true;
			} else {
				$rootScope.authenticated = false;
			}
			callback && callback();
		}, function() {
			$rootScope.authenticated = false;
			callback && callback();
		});

	}

	authenticate();
	self.credentials = {}; //{username:"stan@wp.pl", password:""}; //{username:"stan@wp.pl", password:"123456"};
	
	self.login = function() {
		authenticate(self.credentials, function() {
			if ($rootScope.authenticated) {
				$location.path("/");
				self.error = false;
			} else {
				$location.path("/login");
				self.error = true;
			}
		});
	}

	self.logout = function() {
		$http.post('logout', {}).finally(function() {
			$rootScope.authenticated = false;
			$location.path("/login");
		});
	}
	
	console.log('navigation controller - ending');
}]);

