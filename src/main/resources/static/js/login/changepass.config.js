'use strict';

var changepass = angular
.module('changepass', 
		[ 
		 'translationService'
		 ] );

changepass
.config(						['$httpProvider', 
        						 function mainController(  $httpProvider ) {

	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

}]);

changepass

.controller( 'changepass', [ '$location', '$http', 'translator', '$window', 
                             function( $location, $http, translator, $window ) {

	var self = this;

	self.translator = translator;
	
	self.hashNotFound = true;
	self.username = '';

	var fullPath = $location.absUrl();
	
	self.hash = fullPath.substring(fullPath.lastIndexOf('/')+1);
	console.log('hash extracted:: ', self.hash);

	self.getUsernameForHash = function(){
		$http
		.get('/getusername/'+self.hash)
		.then( 
				function(response){
					//console.log(response);
					self.username = response.data;
					self.password = '' ;
					self.hashNotFound = false;
					//console.log("username", self.username);
				},
				function(){
					self.hashNotFound = true;
					console.log("user does not exist");
				})

	}

	self.getUsernameForHash();

	self.changePass = function(){
		$http
		.put('/changepass/'+self.hash, self.password)
		.then( 
				function(){
					//on succesful password change redirect to main page
					$window.location.href = '/'
				},
				function(){
					//to fix
					//if anything happens 
					console.log("user does not exist");
				})
	}

}]);