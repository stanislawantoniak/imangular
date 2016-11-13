'use strict';

var auth = angular.module('authenticationService',[]);

auth.factory('authService', ['$http', '$rootScope', '$location', '$window', function($http, $rootScope, $location, $window){

	var AuthService = {

			error : '',
			session : {},
			credentials : {},

			authenticate : function(callback) {

				console.log('authenticate credentials: ', AuthService.credentials);

				var headers = AuthService.credentials.username ? {authorization : "Basic "
					+ btoa(AuthService.credentials.username + ":" + AuthService.credentials.password)
				} : {};

				console.log('authenticate headers: ', headers);

				$http.get('userDetails', {headers : headers}).then( function(response) {
					if (response.data.name) {
						$rootScope.authenticated = true;
						AuthService.setSession(response);
					} else {
						$rootScope.authenticated = false;
					}
					callback && callback();
				}, function() {
					$rootScope.authenticated = false;
					callback && callback();
				});
			},

			login : function() {
				AuthService.authenticate( function() {
					if ($rootScope.authenticated) {
						AuthService.error = false;
						$window.location.href = '/'
					} else {
						AuthService.error = true;
					}
				});
			},

			logout : function() {
				$http.post('logout', {}).finally(function() {
					$rootScope.authenticated = false;
					$location.path('/'); 
					AuthService.getSession();
				});
			},

			getSession : function(){
				$http.get('common/userSession').then( 
						AuthService.setSession, 
						function(){
							console.log('userSession from service - fail');
						});
			},

			setSession : function(response){ AuthService.session = response.data;},

			languages : {},

			getLanguages : function(){ 
				$http.get('common/languages').then(function(response) {
					AuthService.languages = response.data;
				}, function(){
					console.log('languages from service - fail');
				});
			},

			selectLanguage : function(language, callback) {
				$http.get('common/selectLanguage/'+language).
				then( function(response) {
					AuthService.getSession();
					callback && callback(); //callback for refreshing translator
				});
			},
			
			resetError : function(){
				AuthService.error = false;
			}
	}

//	console.log("auth service in factory: ", AuthService);

	$rootScope.authenticated = false;
	AuthService.authenticate();
	AuthService.getLanguages();

	return AuthService;
}]);
