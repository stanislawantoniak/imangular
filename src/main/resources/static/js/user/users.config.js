'use strict';

//Register `users` component, along with its associated controller and template
angular.
module('users', ['translationService','checklist-model','generic-restservice']).
config(['$routeProvider', function mainController( $routeProvider ) {

	console.log('users config starting');

	$routeProvider.when('/users/edit/:id', {
		templateUrl : 'js/user/userEdit.html',
		controller : 'userEdit'
	}).when('/users/add/:id', {
		templateUrl : 'js/user/userEdit.html',
		controller : 'userEdit'
	});

	console.log('users config ending');
}]).
controller( 'userslist', ['$scope', '$http', 'translator', 'userService',  function usersController($scope, $http, translator, userService ) {

	var self = this;
	self.service = userService;
	
	console.log('userslist controller starting');

	$scope.translator = translator;
	
	$scope.fetchAllUsers = function(){
		console.log('starting fetching users');
		self.service.fetchAll().then(function(response) {
			console.log('users fetched '+response.length);
			$scope.users = response;
		}, function(){
			console.log('get users from service - fail');
		})
	};

	$scope.deleteUser = function(id){
		self.service.deleteEntity(id).
		then( function(response){
			$scope.fetchAllUsers()
		},
		function(errResponse){
			console.error('Error while deleting User');
		}
		);
	}

	$scope.fetchAllUsers();

	console.log('userslist controller - ending');
}]).
controller( 'userEdit', ['$scope', '$http', '$location', '$routeParams', 'translator','userService', function usersController($scope, $http, $location, $routeParams, translator, userService ) {
	var self = this;
	self.service = userService;

	console.log('userEdit controller starting');

	$scope.translator = translator;

	var userId = $routeParams.id;

	//fetch user - when adding user get empty user but populated with all roles
	self.service.fetch(userId).then(function(response) {
		$scope.user = response;
		$scope.user.enabledPreselected = $scope.user.enabled;
		console.log($scope.user);
	}, function(){
		console.log('get user from service - fail');
	});

	$scope.createOrUpdateUser = function(user){
		self.service.createOrUpdate(user)
		.then( function(response){
			$location.path('/users')
		},	function(errResponse){
			console.error('Error while creating/saving User');
		});
	}

	console.log('userEdit controller - ending');
}]);

