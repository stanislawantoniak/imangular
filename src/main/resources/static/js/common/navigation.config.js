'use strict';

//Register `navigation` component, along with its associated controller and template
var navi = angular.module('navigation', ['translationService']);

navi.controller( 'navigation', ['$rootScope', '$http', '$location', '$scope', 'translator', 'authService', function NavigationController($rootScope, $http, $location, $scope,  translator, authService) {
	var self = this;

	console.log('navigation controller starting');

	self.authService = authService;
	self.authService.authenticate();
	self.authService.getSession();

	self.translator = translator;

	self.route = $location;

	self.languages = {};

	self.credentials = {}; 

	$http.get('common/languages').then(function(response) {
		self.languages = response.data;
		console.log(self.languages);
	}, function(){
		console.log('languages from service - fail');
	});

	self.selectLanguage = function(language) {
		$http.get('common/selectLanguage/'+language).
		then( function(response) {
			self.authService.getSession();
			self.translator.refresh();
			console.log('from navi refresh');
			console.log(self.translator);
		});
	};

	console.log('navigation controller - ending');

}]);

navi.factory('authService', ['$http', '$q', '$rootScope', '$location', '$route' , function($http, $q, $rootScope, $location, $route){
	var GET_ALL_USERS_URL = 'userslistrest/12qs';
	var USER_SERVICE_URL = 'userrest/';

	var factory = {
			error : '',
			session : {},
			authenticate : authenticate,
			login : login,
			logout : logout,
			getSession : getSession
	};

	return factory;

	function authenticate(credentials, callback) {

		//console.log('authenticate credentials');
		//console.log(credentials);

		var headers = credentials ? {authorization : "Basic "
			+ btoa(credentials.username + ":" + credentials.password)
		} : {};

		//console.log('headers');
		//console.log(headers);

		$http.get('userDetails', {headers : headers}).then(function(response) {
			if (response.data.name) {
				console.log('authenticated from user details')
				$rootScope.authenticated = true;
				$route.reload();//very important - makes route refresh and show right template after authentication (for instance when page is reloaded)
				factory.session = response.data;
			} else {
				$rootScope.authenticated = false;
			}
			callback && callback();
		}, function() {
			$rootScope.authenticated = false;
			callback && callback();
		});
	}

	function login(credentials) {
		authenticate( credentials, function() {
			if ($rootScope.authenticated) {
				factory.error = false;
			} else {
				factory.error = true;
			}
		});
	}

	function logout() {
		$http.post('logout', {}).finally(function() {
			$rootScope.authenticated = false;
			getSession();
		});
	}

	function getSession(){
		$http.get('common/userSession').then(function(response) {
			factory.session = response.data;
			console.log(factory.session);
		}, function(){
			console.log('userSession from service - fail');
		});
	}
	
}]);