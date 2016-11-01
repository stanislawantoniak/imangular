'use strict';

//Register `boms` component, along with its associated controller and template
var bomApp = angular.module('boms', ['translationService','toolbox', 'checklist-model','xeditable','ui.select','mgo-angular-wizard']);

itemApp.config(['$stateProvider', function mainController( $stateProvider ) {

	console.log('boms config starting');

	$stateProvider

	.state ('root.bomEdit', {
		url: 'boms/edit/:id',
		templateUrl : 'js/item/bomEdit.html',
		controller : 'bomedit as bomCtrl'
	})
	.state('root.bomWizard', {
		url: 'boms/wizard',
		templateUrl : 'js/item/bomWizzard.html',
		controller : 'bomedit as bomCtrl'
	});

	/*
	$routeProvider.when('/boms/edit/:id', {
		templateUrl : 'js/item/bomEdit.html',
		controller : 'bomedit as bomCtrl'
	}).when('/boms/wizard', {
		templateUrl : 'js/item/bomWizzard.html',
		controller : 'bomedit as bomCtrl'
	});*/

	console.log('boms config ending');
}]);

itemApp.controller( 'bomslist', ['$scope', '$http', 'translator', 'bomService',  'dialogFactory', function bomsController($scope, $http, translator, bomService, dialogFactory ) {

	var self = this;
	self.service = bomService;
	self.deleteDialog = dialogFactory.getService();

	self.fetchAllBoms = function(){
		//console.log('starting fetching boms');
		self.service.fetchAll().then(function(response) {
			self.boms = response;
			console.log('boms fetched '+response.length);
			console.log('boms::',self.boms);
		}, function(){
			console.log('get boms from service - fail');
		})
	};

	self.fetchAllBoms();

	self.kick = function(){
		self.service.fetchAnyData('/kick/').then(function(response) {
			self.fetchAllBoms();
			//self.boms = response;
		}, function(){
			console.log('get boms from service - fail');
		})
	}

	self.deleteBom = function(){
		self.service.deleteEntity(self.deleteDialog.object.id).
		then( function(response){
			self.fetchAllBoms();
			self.deleteDialog.close();
		},
		function(errResponse){
			console.error('Error while deleting item');
		});
	}

}]);

itemApp.controller( 'bomedit', ['$scope', '$http', 'translator', 'bomService',  'dialogFactory', function bomsController($scope, $http, translator, bomService, dialogFactory ) {


	console.log('bomedit ctrl starting');

	var self = this;
	self.service = bomService;
	self.translator = translator;
	self.deleteDialog = dialogFactory.getService();

	self.bom = {
			item : null,
			quantity : 1
	};

	self.selectItem = function(i){
		console.log('sel');
		self.bom.item = i;
	}

	self.itemSelected = function(){
		return self.bom.item != null;
	}

	//get items for select in add bom
	self.service.fetchAnyData('/items/forselect/0').then(function(response){
		var items = response;
		self.itemsForSelect = [];
		angular.forEach(items, function(row) { 
			if (row.isComposed){
				self.itemsForSelect.push(row);
			}
		});
		console.log('items for select',self.itemsForSelect);
	}, function(){
		console.log('get items for select - fail');
	})


	console.log('bomedit ctrl ending');

}]);
