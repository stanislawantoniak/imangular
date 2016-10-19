'use strict';

//Register `items` component, along with its associated controller and template
angular.
module('items', ['translationService','checklist-model']).
config(['$routeProvider', function mainController( $routeProvider ) {

	console.log('items config starting');

	$routeProvider.when('/items/edit/:id', {
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit'
	}).when('/items/add/:id', {
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit'
	});

	console.log('items config ending');
}]).
controller( 'itemlist', ['$scope', '$http', 'translator', 'itemService',  function itemsController($scope, $http, translator, itemService ) {

	var self = this;
	self.service = itemService;
	
	console.log('itemslist controller starting');

	$scope.translator = translator;
	
	$scope.fetchAllItems = function(){
		//console.log('starting fetching items');
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
	self.service = itemService;

	console.log('itemEdit controller starting');

	$scope.translator = translator;

	var itemId = $routeParams.id;

	//fetch item - when adding item get empty item but populated predefined fields
	self.service.fetch(itemId).then(function(response) {
		$scope.item = response;

		console.log($scope.item);
	}, function(){
		console.log('get item from service - fail');
	});

	$scope.createOrUpdateItem = function(item){
		self.service.createOrUpdate(item)
		.then( function(response){
			$location.path('/items')
		},	function(errResponse){
			console.error('Error while creating/saving item');
		});
	}

	console.log('itemEdit controller - ending');
}]);

