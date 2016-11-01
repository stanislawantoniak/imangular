'use strict';

//Register `items` component, along with its associated controller and template
var itemApp = angular.module('items', ['translationService','toolbox', 'checklist-model','xeditable','ui.select']);

itemApp.config(['$stateProvider', function mainController( $stateProvider ) {

	console.log('items config starting');

	$stateProvider

	.state ('root.itemEdit', {
		url: 'items/edit/:id',
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit as itemCtrl'
	})
	.state ('root.itemDetails', {
		url: 'items/itemdetails/:id',
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit as itemCtrl'
	})
	.state ('root.itemAdd', {
		url: 'items/add/:id',
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit as itemCtrl'
	});

	console.log('items config ending');
}]);

itemApp.controller( 'itemslist', ['$scope','$http','translator','itemService','dialogFactory', 
          function itemsController( $scope,   $http,  translator,  itemService,  dialogFactory ) {

	var self = this;
	self.service = itemService;
	self.deleteDialog = dialogFactory.getService();

	console.log('itemslist controller starting');

	self.fetchAllItems = function(){
		//console.log('starting fetching items');
		self.service.fetchAll().then(function(response) {
			console.log('items fetched '+response.length);
			self.items = response;
		}, function(){
			console.log('get items from service - fail');
		})
	};

	self.deleteItem = function(){
		self.service.deleteEntity(self.deleteDialog.object.id).
		then( function(response){
			self.fetchAllItems();
			self.deleteDialog.close();
		},
		function(errResponse){
			console.error('Error while deleting item');
		});
	}

	self.fetchAllItems();

	console.log('itemslist controller - ending');
}]);

itemApp.controller( 'itemEdit', ['$stateParams','$scope', '$http', '$location',  'translator','itemService', 'itemComponentService', 'dialogFactory',
         function itemsController($stateParams,  $scope,  $http,    $location,    translator,  itemService,   itemComponentService,   dialogFactory ) {
	console.log('itemEdit controller starting');

	var self = this;
	self.service = itemService;
	self.componentService = itemComponentService;

	self.deleteDialog = dialogFactory.getService();
	console.log('self.deleteDialog',self.deleteDialog);
	console.log('openedDialog',self.deleteDialog.openedDialog);

	self.addItemCtx = false;

	self.translator = translator;

	self.itemId = $stateParams.id;

	//fetch item - when adding item get empty item but populated predefined fields
	self.fetchItem = function(){
		self.service.fetch(self.itemId).then(function(response) {
			self.item = response;
			console.log('item::',self.item);
		}, function(){
			console.log('get item from service - fail');
		})
	};

	self.fetchItem();

	self.createOrUpdateItem = function(){
		self.service.createOrUpdate(self.item)
		.then( function(response){
			//console.log('create item::',response);
			//self.itemId = response;
			$location.path('/items/edit/'+response);
		},	function(errResponse){
			console.error('Error while creating/saving item');
		});
	}

	//get items for select in item component edit form
	self.service.fetchAnyData('/items/forselect/'+self.itemId).then(function(response){
		var items = response;
		self.itemsForSelect = [];
		angular.forEach(items, function(row) { 
			self.itemsForSelect.push({value: row.id, text: row.name});
		});
		console.log('items for select',self.itemsForSelect);
	}, function(){
		console.log('get items for select - fail');
	})

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

	self.createOrUpdateItemComponent = function(component, source){

		if (source.$$hashKey){ //item is from edit not from add
			component.id = source.id;
			component.$$hashKey = source.$$hashKey;
		} else {
			component.component = component.component.value; //make it flat from ng-selectstructure "component":{"value":4,"text":"bbbbbbbbbbbbbbbbbbbbbbbbbb (#4)"}
		}
		component.parent = this.item.id;
		console.log('component ::',component,source)
		self.componentService.createOrUpdate(component)
		.then( function(response){
			self.fetchItem();
			self.unsetAddComponentCtx();
		},	function(errResponse){
			console.error('Error while creating/saving component');
		});
	}

	self.setAddComponentCtx = function(){
		self.addItemCtx = true;
		self.setEditRowContext();
	}
	self.unsetAddComponentCtx = function(){
		self.addItemCtx = false;
		self.unsetEditRowContext();
	}	
	self.addItemComponent = function() {
		self.inserted = {};
		self.inserted.id = 0;
		self.inserted.component = {value: "", text: "-------"};
		//self.inserted.component.value = 0;
		self.setAddComponentCtx();
	}
	self.cancelAddComponent	= function(){
		self.inserted = {};
		self.unsetAddComponentCtx();
	}

	self.deleteItemComponent = function(){
		self.componentService.deleteEntity(self.deleteDialog.object.id).
		then( function(response){
			self.fetchItem();
			self.deleteDialog.close();
		},
		function(errResponse){
			console.error('Error while deleting component');
		});
	}

	//return value is input for ng-show form button
	self.editNameIfEmpty = function(formScope){
		if (formScope['nameBtnForm'].$visible){
			return false; 
		}

		if ( self.nameEditShow ) { //edit name mode when new item
			formScope['nameBtnForm'].$show();
			self.nameEditShow = false;
			return false;
		}

		return !self.addItemCtx;
	}



	if (self.itemId == 0){ //if new item - set edit name flag 
		// will be used in editNameIfEmpty
		self.nameEditShow = true;
	} else {
		self.nameEditShow = false;
	}

	console.log('itemEdit controller - ending');
}]);


