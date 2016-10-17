'use strict';

//Register `items` component, along with its associated controller and template
angular.
module('items', ['translationService','checklist-model']).
config(['$routeProvider', function mainController( $routeProvider ) {

	console.log('items config starting');

	$routeProvider.when('/items/edit/:id', {
		templateUrl : 'js/user/itemEdit.html',
		controller : 'userEdit'
	}).when('/items/add/:id', {
		templateUrl : 'js/user/itemEdit.html',
		controller : 'userEdit'
	});

	console.log('items config ending');
}]).
controller( 'itemlist', ['$scope', '$http', 'translator', 'itemService',  function itemsController($scope, $http, translator, itemService ) {

	var self = this;
	self.service = itemService;
	
	console.log('itemslist controller starting');

	$scope.translator = translator;
	
	$scope.fetchAllItems = function(){
		console.log('starting fetching items');
		self.service.fetchAll().then(function(response) {
			console.log('items fetched '+response.length);
			$scope.items = response;
		}, function(){
			console.log('get items from service - fail');
		})
	};

	$scope.deleteItem = function(id){
		self.service.deleteEntity(id).
		then( function(response){
			$scope.fetchAllItems()
		},
		function(errResponse){
			console.error('Error while deleting item');
		}
		);
	}

	$scope.fetchAllItems();

	console.log('itemslist controller - ending');
}]).
controller( 'itemEdit', ['$scope', '$http', '$location', '$routeParams', 'translator','itemService', function itemsController($scope, $http, $location, $routeParams, translator, itemService ) {
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
			$location.path('/items')
		},	function(errResponse){
			console.error('Error while creating/saving User');
		});
	}

	console.log('userEdit controller - ending');
}]);

