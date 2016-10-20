'use strict';

var app = angular.module('imangular', [ 'ngRoute', 'navigation', 'dashboard', 'users', 'items' ] );

app.config(['$routeProvider', '$httpProvider', '$locationProvider',  function mainController( $routeProvider, $httpProvider,$locationProvider ) {

	console.log('imangular starting');

	$locationProvider.hashPrefix('!');

	$routeProvider.when('/', {
		templateUrl : 'js/dashboard/dashboard.html',
		controller : 'dashboard'
	}).when('/users', {
		templateUrl : 'js/user/userList.html',
		controller : 'userslistx as usersCtrl'
	}).when('/items', {
		templateUrl : 'js/item/itemList.html',
		controller : 'itemlist as itemsCtrl'
	}).when('/login', {
		templateUrl : 'js/common/login.html',
		controller : 'login as login'
	}).otherwise({
		redirectTo: '/'
	});

	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

	console.log('imangular ending');
}]);

app.run(['$rootScope', '$location', function ($rootScope, $location) {

	console.log('app.run start');
/*	$rootScope.$on('$routeChangeStart', function (event, next, current) {
		//console.log('$rootScope.$on::$routeChangeStart start');
		//just for checking if route change is fired
		if (!$rootScope.authenticated) {
			if ($location.path() !== '/login'){
				console.log('Authentication:: Deny - not authenticated');
				event.preventDefault();
				$location.path('/login');
			} else {
				if ($location.path() == '/login'){
					console.log('Authentication:: already authenticated');
					event.preventDefault();
					$location.path('#!/');
				}
			}

		} 
		//console.log('$rootScope.$on::$routeChangeStart ending');
	});
*/
	console.log('app.run end');

}]);

