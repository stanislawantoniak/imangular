'use strict';

//Register `navigation` component, along with its associated controller and template
var navi = angular.module('navigation', ['translationService','authenticationService']);

navi.controller( 'navigation', ['$rootScope', '$http', '$location', '$scope', 'translator', 'authService', function($rootScope, $http, $location, $scope,  translator, authService) {

	var self = this;
	
	//console.log('navigation controller starting');
	
	self.authService = authService;
	//console.log('navigation::authservice: ',self.authService);
	self.authService.getSession();

	self.translatorHandler = translator;

	self.route = $location;
	
	self.selectLanguage = function(language) {
		self.authService.selectLanguage(language, self.translatorHandler.refresh);//on callback refresh translations
	}

	//console.log('navigation controller - ending');

}]);

navi.controller( 'login', [ 'authService', function( authService) {

	var self = this;
	
	self.loginActive = 1;
	self.registerActive = 0;
	self.forgotActive = 0;
	
	console.log('login controller starting');
	
	self.authService = authService;
	//console.log('login::authservice: ',self.authService);

	console.log('login controller - ending');

}]);
