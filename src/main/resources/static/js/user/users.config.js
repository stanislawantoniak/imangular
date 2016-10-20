'use strict';

//Register `users` component, along with its associated controller and template
angular.
module('users', ['translationService','checklist-model','generic-restservice']).
config(['$routeProvider', function mainController( $routeProvider ) {

	console.log('users config starting');

	$routeProvider.when('/users/edit/:id', {
		templateUrl : 'js/user/userEdit.html',
		controller : 'userEdit as userCtrl'
	}).when('/users/add/:id', {
		templateUrl : 'js/user/userEdit.html',
		controller : 'userEdit as userCtrl'
	});

	console.log('users config ending');
}]).

controller( 'userslistx', ['$http', 'translator', 'userService',  function($http, translator, userService ) {

	var self = this;
	self.service = userService;
	
	console.log('userslist controller starting');

	self.translator = translator;
	
	self.fetchAllUsers = function(){
		console.log('starting fetching users');
		self.service.fetchAll().then(function(response) {
			console.log('users fetched '+response.length);
			self.users = response;
		}, function(){
			console.log('get users from service - fail');
		})
	};

	self.deleteUser = function(id){
		self.service.deleteEntity(id).
		then( function(response){
			self.fetchAllUsers()
		},
		function(errResponse){
			console.error('Error while deleting User');
		}
		);
	}

	self.fetchAllUsers();

	console.log('userslist controller - ending');
}]).

controller( 'userslist', ['$http', 'translator', 'userService',  function($http, translator, userService ) {

	var self = this;
	self.service = userService;
	
	console.log('userslist controller starting');

	self.translator = translator;
	
	self.fetchAllUsers = function(){
		console.log('starting fetching users');
		self.service.fetchAll().then(function(response) {
			console.log('users fetched '+response.length);
			self.users = response;
		}, function(){
			console.log('get users from service - fail');
		})
	};

	self.deleteUser = function(id){
		self.service.deleteEntity(id).
		then( function(response){
			self.fetchAllUsers()
		},
		function(errResponse){
			console.error('Error while deleting User');
		}
		);
	}

	self.fetchAllUsers();

	console.log('userslist controller - ending');
}]).

controller( 'userEdit', ['$http', '$location', '$routeParams', 'translator','userService', function( $http, $location, $routeParams, translator, userService ) {
	var self = this;
	self.service = userService;

	console.log('userEdit controller starting');

	self.translator = translator;

	var userId = $routeParams.id;

	//fetch user - when adding user get empty user but populated with all roles
	self.service.fetch(userId).then(function(response) {
		self.user = response;
		self.user.enabledPreselected = self.user.enabled;
		console.log(self.user);
	}, function(){
		console.log('get user from service - fail');
	});

	self.createOrUpdateUser = function(user){
		self.service.createOrUpdate(user)
		.then( function(response){
			$location.path('/users')
		},	function(errResponse){
			console.error('Error while creating/saving User');
		});
	}

	console.log('userEdit controller - ending');
}]);

