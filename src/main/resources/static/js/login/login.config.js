'use strict';

var login = angular
.module('imlogin', 
		[ 
'authenticationService',
'translationService'

] );

login
.config(						['$httpProvider', 
        						 function mainController(  $httpProvider ) {

	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

}]);

login
.controller( 'login', [ 'authService', '$http', 'translator', '$window', function( authService, $http, translator, $window ) {

	var self = this;

	self.authService = authService;
	self.translator = translator;

	self.loginActive = 1;
	self.registerActive = 0;
	self.forgotActive = 0;

	self.registerErrorSet = function(error){
		console.log('error set');
		self.registerErrorTxt = error;
		self.registerError = true;
	}

	self.registerErrorUnset = function(){
		console.log('error unset');
		self.registerErrorTxt = '';
		self.registerError = false;
	}

	self.sendHash = function(){
		$http
		.put('/forgotpass',authService.credentials.username)
		.then( 
				function(){
					console.log("hash sent");
				},
				function(){
					console.error("user does not exist");
				})

	}

	self.postUser = function(){
		//unset error in UI
		self.registerErrorUnset();

		//first check for errors
		console.log('in postUser() credentials:: ',authService.credentials);
		if (typeof(authService.credentials.username) == 'undefined' 
			|| typeof(authService.credentials.password) == 'undefined'){
			self.registerErrorSet( translator.label.loginRegisterErrorMessage);
			return;
		}

		$http
		.put('/userexists',authService.credentials.username)
		.then( 
				function(){
					self.registerErrorSet( translator.label.loginRegisterErrorUserExists );	
				},

				function(){

					var theUser = {};
					theUser.username = authService.credentials.username;
					theUser.password = authService.credentials.password;
					console.log('the user::',theUser);
					$http
					.post('/register',theUser)
					.then( 
							function(response){
								$window.location.href = '/'
							},	
							function(errResponse){
								self.registerErrorSet( translator.label.loginRegisterErrorMessage);
								console.error('Error while creating/saving User');
							});
				}
		)
	}
}]);
