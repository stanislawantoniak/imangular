'use strict';

//Register `users` component, along with its associated controller and template
var userApp = angular.module('users', ['translationService','checklist-model','generic-restservice','xeditable']);
userApp.config(['$routeProvider', function mainController( $routeProvider ) {

	//console.log('users config starting');
	$routeProvider.when('/users/add/:id', {
		templateUrl : 'js/user/userEdit.html',
		controller : 'useredit',
		controllerAs : 'userCtrl'
	});
	//console.log('users config ending');
}]);

userApp.controller( 'userslist', ['$http', '$scope', 'translator', 'userService',  function($http, $scope, translator, userService ) {

	var self = this;
	self.service = userService;
	self.editUserContext = false;

	//console.log('userslist controller starting');

	self.fetchAllUsers = function(){
		//console.log('starting fetching users');
		self.service.fetchAll().then(function(response) {
			//console.log('users fetched '+response.length);
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

	self.createOrUpdateUser = function(user,id){
		user.id = id;
		self.service.createOrUpdate(user)
		.then( function(response){
			//console.log('User saved');
			self.fetchAllUsers();
			self.unsetEditRowContext();
		},	function(errResponse){
			console.error('Error while creating/saving User');
		});
	}
	
	self.setEditRowContext = function(){
		self.editContext = true;
	}
	
	self.cancelEditRow = function(formScope){
		self.unsetEditRowContext();
		formScope['rowform'].$cancel();
	}
	
	self.unsetEditRowContext = function(){
		self.editContext = false;
	}

	self.checkEmail = function(data) {
		//console.log('email check fn on: ',data);
		if ( !data ) {
			return self.translator.label.edituserhelpname;
		}
		var EMAIL_REGEXP = /^[_a-z0-9]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/;
		//console.log(EMAIL_REGEXP.test(data));

		if(! EMAIL_REGEXP.test(data)){
			return self.translator.label.edituserhelpname;
		} 
	};

	//get all roles checkboxes edit
	self.service.fetchAnyData('/allRoles').then(function(response) {
		var roles = response;
		self.allRoles = [];
		angular.forEach(roles, function(value, key) { 
			self.allRoles.push({value: value, text: key});
		});
		console.log('all roles',self.allRoles);
	});

	self.fetchAllUsers();

	//console.log('userslist controller - ending');
}]);

userApp.controller( 'useredit', ['$http', '$routeParams', '$location', 'userService',   function($http, $routeParams,$location, userService ) {
	var self = this;
	self.service = userService;

	console.log('userEdit controller starting');

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