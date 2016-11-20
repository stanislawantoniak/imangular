'use strict';

var login = angular
.module('imlogin', 
		[ 
'authenticationService',
'translationService'

] );

login
.config(['$httpProvider', 
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

	self.resetPassErrorSet = function(username){
		console.log('resetPassErrorSet');
		self.resetPassError = true;
		self.userSent = username;
	}

	self.resetPassErrorUnset = function(){
		console.log('resetPassErrorUnset');
		self.resetPassError = false;
	}

	self.isResetPassError = function(){
		var result = self.resetPassError  //there was error returned
		&&	self.userSent == authService.credentials.username ; //and username was not changed since then
		return result;
	}

	self.sendHash = function(){
		$http
		.put('/forgotpass',authService.credentials.username)
		.then( 
				function(){
					console.log("hash sent");
					self.forgotActive = 0;
					self.hashSent = true;
				},
				function(){
					self.resetPassErrorSet(authService.credentials.username);

					//console.log("user does not exist");
				})

	}

	self.postUser = function(){
		//unset error in UI
		self.registerErrorUnset();

		//first check for errors
		//console.log('in postUser() credentials:: ',authService.credentials);
		if (typeof(authService.credentials.username) == 'undefined' 
			|| typeof(authService.credentials.password) == 'undefined'){
			self.registerErrorSet( translator.label.loginRegisterErrorMessage );
			return;
		}

		$http
		.put('/userexists/0',authService.credentials.username)
		.then( 
				function(){
					self.registerErrorSet( translator.label.loginRegisterErrorUserExists );	
				},

				function(){

					var theUser = {};
					theUser.username = authService.credentials.username;
					theUser.password = authService.credentials.password;
					//console.log('the user::',theUser);
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
