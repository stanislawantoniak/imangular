'use strict';

var auth = angular.module('authenticationService',[]);

auth.factory('authService', ['$http', '$rootScope', '$location', function($http, $rootScope, $location){

	var AuthService = {

			error : '',
			session : {},
			credentials : {},

			authenticate : function(callback) {

				//console.log('authenticate credentials: ', AuthService.credentials);

				var headers = AuthService.credentials.username ? {authorization : "Basic "
					+ btoa(AuthService.credentials.username + ":" + AuthService.credentials.password)
				} : {};

				//console.log('authenticate headers: ', headers);

				$http.get('userDetails', {headers : headers}).then( function(response) {
					if (response.data.name) {
						$rootScope.authenticated = true;
						AuthService.setSession(response);
						AuthService.manageRedirectsOnAuthentication();
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
						//AuthService.manageRedirectsOnAuthentication();
					} else {
						AuthService.error = true;
					}
				});
			},

			manageRedirectsOnAuthentication : function(){
				//authenticated - check rights and manage redirects if not authorized
				//only users and item paths are restricted
				//console.log("session::",AuthService.session);
				if ($rootScope.redirect){ //redirect to target page if there was a redirect to login  
					if ($rootScope.redirect.startsWith('/users' )){
						if (AuthService.session.isAdmin)
							$location.path($rootScope.redirect);
						else {
							$location.path('/');
							console.log("/users** not authorized");
						}
					} else {
						$location.path($rootScope.redirect);
					}
					$rootScope.redirect = null;
				} else {
					$location.path('/');
				}
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
			}
	}

//	console.log("auth service in factory: ", AuthService);

	$rootScope.authenticated = false;
	AuthService.authenticate();
	AuthService.getLanguages();

	return AuthService;
}]);