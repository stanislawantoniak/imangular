'use strict';

//Register `items` component, along with its associated controller and template
var itemApp = angular.module('items', ['translationService','toolbox', 'itemGRs', 'checklist-model','xeditable','ui.select', 'ngTable']);

itemApp.config(['$stateProvider', function( $stateProvider ) {

	$stateProvider
	.state ('root.items', {
		url: '/items',
		templateUrl : 'js/item/itemList.html',
		controller : 'itemslist as itemsCtrl'
	})
	.state ('root.itemDetails', {
		url: '/items/itemdetails/:id',
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit as itemCtrl'
	})
	.state ('root.itemAdd', {
		url: '/items/additem/:id',
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit as itemCtrl'
	})

}]);

itemApp.controller( 'itemslist', ['$q','translator','itemService',  'ngTableParams', 'growlService',
                                  function itemsController( $q, translator,  itemService, ngTableParams, growlService ) {

	var self = this;
	self.service = itemService;
	self.messageService = growlService;

	//table for items
	self.itemTable = new ngTableParams({
		page: 1,            // show first page
		count: 25,
		sorting: {
			name: 'asc'     // initial sorting
		}

	}, {
		total: 0, 
		getData: function($defer, params) {

			//console.log('page::',params.page());
			//console.log('count::',params.count());
			//console.log('orderBy::',params.orderBy());
			//console.log('filter::',params.filter());

			self
			.service
			.fetchAll(params.page(), params.count(), params.orderBy(), params.filter())
			.then( function(response){
				console.log("response::",response);
				if (params.total() != 0){ //do not show message at first table data load
					self.messageService.growl(translator.label.ListHasBeenRefreshed, 'info') ;
				}
				params.total(response.totalRows);
				$defer.resolve(response.collection);
			} );
		}
	})

	self.deleteItemPromise = function(item){
		var res = $q.defer();

		self
		.service
		.deleteEntity(item.id)
		.then( 
				function(response){
					self.itemTable.reload(); //reload grid on succesful delete
					res.resolve(translator.label.itemsdeletesuccessinfo);
				},
				function(errResponse){
					console.error('Error while deleting item');
					res.reject(translator.label.itemsdeletefailureinfo);
				});

		return res.promise;
	}
}]);

itemApp.controller( 'itemEdit', ['$q','$state', '$stateParams', '$http', 'itemGRService', 'translator','itemService', 'itemComponentService', 'authService',
                                 function itemsController(  $q, $state,  $stateParams,  $http,  itemGRService, translator, itemService, itemComponentService, authService ) {
	//console.log('itemEdit controller starting');

	var self = this;
	self.service = itemService;
	self.componentService = itemComponentService;
	self.serviceGR = itemGRService;

	self.addItemCtx = false;

	self.translator = translator;

	self.itemId = parseInt($stateParams.id);
	//console.log('itemId param::',self.itemId);

	self.setEditItemContext = function(){
		self.editItemContext = true;
	}

	self.unsetEditItemContext = function(){
		self.editItemContext = false;
	}

	//resolve if it is add item or browse details 
	if (self.itemId == 0){
		self.setEditItemContext();
	} else {
		self.unsetEditItemContext();
	}

	//fetch item - when adding item get empty item but populated predefined fields
	self.fetchItem = function(){
		self.service
		.fetch(self.itemId)
		.then(
				function(response) {
					self.item = response;
					if (self.itemId != 0 && ( self.item.isUsed || self.item.isComposed) ){
						var bgmClass = {};
						bgmClass[self.item.bgmColor] = true;;
						self.item.bgmClass = bgmClass;

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
					//console.log('item::',self.item);
				}
		)
	};

	self.fetchItem();

	self.itemExists = false;

	self.createOrUpdateItem = function(){
		$http
		.put('/itemexists/'+self.item.id, self.item.name)
		.then( 
				function(){
					self.itemExists = true, 	
					self.itemChecked = self.item.name;
				},
				function(){
					self.service.createOrUpdate(self.item)
					.then( 
							function(response){
								self.unsetEditItemContext();
								if (self.itemId == 0){
									//console.log('create item::',response);
									//we are in add item context
									//need to route to item details page after succesful adding
									//restservice returns item id !
									$state.go('^.itemDetails',{id:response});
								}
								self.fetchItem();
							},	
							function(errResponse){
								console.error('Error while creating/saving item');
							});
				})
	}

	//	get stuff needed for select in edit forms
	if (authService.session.isSupervisor){
		self
		.service
		.fetchAnyData('/items/forselect/'+self.itemId)
		.then(
				function(response){
					self.itemsForSelect = response;
				}, function(){
					console.log('get items for select - fail');
				});
		
		self
		.serviceGR
		.fetchAll()
		.then(
				function(response){
					self.gameReleases = response.collection; 
				},
				function(errResponse){
					console.error('Error while fetching game releases');
				});
	};

	self.setEditRowContext = function(){
		self.editRowContext = true;
	};

	self.cancelEditRow = function(formScope){
		self.unsetEditRowContext();
		formScope['rowform'].$cancel();
	};

	self.unsetEditRowContext = function(){
		self.editRowContext = false;
	};

	self.unsetEditRowContext();

	self.createOrUpdateItemComponent = function(component){

		//update parent for new components
		component.parent = self.itemId;
		//convert quantity to int type
		component.quantity = parseInt(component.quantity);

		console.log('component ::',component);
		self.componentService.createOrUpdate(component)
		.then( function(response){
			self.fetchItem();
			self.unsetAddComponentCtx();
		},	function(errResponse){
			console.error('Error while creating/saving component');
		});
	};

	self.setAddComponentCtx = function(){
		self.addItemCtx = true;
		self.setEditRowContext();
	};
	self.unsetAddComponentCtx = function(){
		self.addItemCtx = false;
		self.unsetEditRowContext();
	}	;
	self.addItemComponent = function() {
		self.inserted = {};
		self.inserted.id = 0;
		self.inserted.component = {value: "", text: "-------"};
		//self.inserted.component.value = 0;
		self.setAddComponentCtx();
	};
	self.cancelAddComponent	= function(){
		self.inserted = {};
		self.unsetAddComponentCtx();
	};

	self.deleteItemComponentPromise = function(ic){
		var res = $q.defer();

		self
		.componentService
		.deleteEntity(ic.id).
		then( function(response){
			self.fetchItem();
			res.resolve(translator.label.itemsdeletecomponentsuccessinfo);
		},
		function(errResponse){
			console.error('Error while deleting component');
			res.reject(translator.label.itemsdeletecomponentfailureinfo);
		});

		return res.promise;
	};

	//console.log('itemEdit controller - ending');
}]);


