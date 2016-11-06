'use strict';

//Register `items` component, along with its associated controller and template
var itemApp = angular.module('items', ['translationService','toolbox', 'checklist-model','xeditable','ui.select', 'ngTable']);

itemApp.config(['$stateProvider', function mainController( $stateProvider ) {

	//console.log('items config starting');

	//console.log('items config ending');

}]);

itemApp.controller( 'itemslist', ['$q','$scope','$http','translator','itemService','dialogFactory', 'ngTableParams',
                                  function itemsController( $q,  $scope,   $http,  translator,  itemService,  dialogFactory,  ngTableParams ) {

	var self = this;
	self.service = itemService;
	self.deleteDialog = dialogFactory.getService();

	console.log('itemslist controller starting');

	//table for items
	this.itemTable = new ngTableParams({
		page: 1,            // show first page
		count: 25,
		sorting: {
			name: 'asc'     // initial sorting
		}

	}, {
		total: 0, 
		getData: function($defer, params) {

			//console.log('get data 1');
			console.log(params.orderBy());
			console.log(params);
			console.log('fiter::',params.filter());

			self
			.service
			.fetchAll(params.page(), params.count(), params.orderBy(), params.filter())
			.then( function(response){
				console.log("response::",response);
				params.total(response.totalRows);
				$defer.resolve(response.collection);
			} );
		}
	})

	self.deleteItemPromise = function(item){
		var res = $q.defer();

		self
		.service
		.deleteEntity(item.id).
		then( function(response){
			self.fetchAllItems();
			res.resolve(translator.label.itemsdeletesuccessinfo);
		},
		function(errResponse){
			console.error('Error while deleting item');
			res.reject(translator.label.itemsdeletefailureinfo);
		});

		return res.promise;

	}

	console.log('itemslist controller - ending');
}]);

itemApp.controller( 'itemEdit', ['$q','$stateParams','$scope', '$http', '$location',  'translator','itemService', 'itemComponentService', 'dialogFactory', 'authService',
                                 function itemsController(  $q,  $stateParams,  $scope,  $http,    $location,    translator,  itemService,   itemComponentService,   dialogFactory, authService ) {
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
		self.service
		.fetch(self.itemId)
		.then(
				function(response) {
					self.item = response;
					if (self.itemId != 0 && ( self.item.isUsed || self.item.isComposed) ){
						self.service
						.fetchAnyData('/itemrest/associations/'+self.itemId)
						.then( 
								function(response){
									self.item.components = response.components;
									self.item.usedIn = response.usedIn;
								},
								function(){
									console.log('get item from service - fail');
								}
						)
					}
					console.log('item::',self.item);
				}
		)
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

//	get items for select in item component edit form
	if (authService.session.isSupervisor){
		self.service.fetchAnyData('/items/forselect/'+self.itemId).then(function(response){
			var items = response;
			self.itemsForSelect = [];
			angular.forEach(items, function(row) { 
				self.itemsForSelect.push({value: row.id, text: row.name});
			});
			//console.log('items for select',self.itemsForSelect);
		}, function(){
			console.log('get items for select - fail');
		})
	}

	self.setEditRowContext = function(){
		self.editRowContext = true;
	}

	self.cancelEditRow = function(formScope){
		self.unsetEditRowContext();
		formScope['rowform'].$cancel();
	}

	self.unsetEditRowContext = function(){
		self.editRowContext = false;
	}
	
	self.unsetEditRowContext();
	
	self.setEditItemContext = function(){
		self.editItemContext = true;
	}

	self.unsetEditItemContext = function(){
		self.editItemContext = false;
	}
	
	self.unsetEditItemContext();

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

	self.deleteItemComponent = function(ic){

		var res = $q.defer();

		self
		.componentService
		.deleteEntity(ic.id).
		then( function(response){
			self.fetchItem();
			res.resolve(translator.label.itemsdeletesuccessinfo);
		},
		function(errResponse){
			console.error('Error while deleting component');
			res.reject(translator.label.itemsdeletefailureinfo);
		});
		return res.promise;
	}

	self.deleteItemPromise = function(item){
		var res = $q.defer();

		self
		.service
		.deleteEntity(item.id).
		then( function(response){
			self.fetchAllItems();
			res.resolve(translator.label.itemsdeletesuccessinfo);
		},
		function(errResponse){
			console.error('Error while deleting item');
			res.reject(translator.label.itemsdeletefailureinfo);
		});

	}


//	return value is input for ng-show form button
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


