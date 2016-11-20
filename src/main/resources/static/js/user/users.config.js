'use strict';

//Register `users` component, along with its associated controller and template
var userApp = angular.module('users', ['translationService','checklist-model','generic-restservice','xeditable']);
userApp.config(['$stateProvider', function( $stateProvider ) {

	$stateProvider
	.state ('root.users', {
		url: '/users',
		templateUrl : 'js/user/userList.html',
		controller : 'userslist as usersCtrl'
	})
	.state('root.useradd', {
		url: '/users/add/:id',
		templateUrl : 'js/user/userEdit.html',
		controller : 'useredit',
		controllerAs : 'userCtrl'
	})
}]);

userApp.controller( 'userslist', ['$q', 'translator', 'userService',  'ngTableParams',
                                  function( $q,   translator,   userService,    ngTableParams ) {

	var self = this;
	self.service = userService;
	self.editUserContext = false;

	//console.log('userslist controller starting');

	self.fetchAllUsers = function(){
		//console.log('starting fetching users');
		self.service.fetchAll().then(function(response) {
			//console.log('users fetched '+response.length);
			//console.log(response);
			self.users = response.collection;
		}, function(){
			console.log('get users from service - fail');
		})
	};

	self.fetchAllUsers();

	self.deleteUserPromise = function(obj){ 

		var res = $q.defer();

		self
		.service
		.deleteEntity(obj.id)
		.then( 
				function(response){
					self.fetchAllUsers();
					res.resolve(translator.label.usersdeletesuccessinfo);
				},
				function(errResponse){
					console.error('Error while deleting User');
					res.reject(translator.label.usersdeletefailureinfo);
				}
		);

		return res.promise;

	};

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
		//console.log('all roles',self.allRoles);
	});


	//console.log('userslist controller - ending');
}]);

userApp.controller( 'useredit', ['$http', '$stateParams', '$state', 'userService',   function($http, $stateParams, $state, userService ) {
	var self = this;
	self.service = userService;

	var userId = $stateParams.id;
	//fetch user - when adding user get empty user but populated with all roles

	self
	.service
	.fetch(userId)
	.then(
			function(response) {
				self.user = response;
				self.user.enabledPreselected = self.user.enabled;
				self.rolesForToggle = [];
				angular.forEach(self.user.allRoles, function(row) { 
					self.rolesForToggle.push({role: row, enabled : self.user.rolesSelected[row] != null});
				});
				//console.log("toggle",self.rolesForToggle);
				//console.log(self.user);
			}, function(){
				console.log('get user from service - fail');
			});

	self.userExists = false;
	self.userChecked = "";

	self.createOrUpdateUser = function(user){

		var theUser = user;

		$http
		.put('/userexists/'+theUser.id, theUser.username)
		.then( 
				function(){
					self.userExists = true, 	
					self.userChecked = theUser.username;
				},

				function(){
					theUser.rolesSelected = [];
					angular.forEach(self.rolesForToggle, function(row) { 
						if (row.enabled) theUser.rolesSelected.push(row.role);
					});
					//console.log(theUser);
					self.service.createOrUpdate(theUser)
					.then( function(response){
						$state.go('root.users');
					},	function(errResponse){
						console.error('Error while creating/saving User');
					});
				}
		)
	}

}]);
