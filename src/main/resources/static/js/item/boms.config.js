'use strict';

//Register `boms` component, along with its associated controller and template
var bomApp = angular.module('boms', ['translationService','toolbox', 'checklist-model','xeditable','ui.select']);

itemApp.config(['$stateProvider', function mainController( $stateProvider ) {

	//console.log('boms config starting');

	$stateProvider

	.state ('root.bomDetails', {
		url: '/boms/details/:id',
		templateUrl : 'js/item/bomEdit.html',
		controller : 'bomEdit as bomCtrl'
	})
	.state('root.bomWizard', {
		url: '/boms/wizard',
		templateUrl : 'js/item/bomWizzard.html',
		controller : 'bomWizard as bomCtrl'
	});

	//console.log('boms config ending');
}]);

itemApp.controller( 'bomslist', ['$scope', '$http', 'translator', 'bomService',  'dialogFactory', function bomsController($scope, $http, translator, bomService, dialogFactory ) {

	var self = this;
	self.service = bomService;
	self.deleteDialog = dialogFactory.getService();

	self.fetchAllBoms = function(){
		//console.log('starting fetching boms');
		self.service.fetchAll().then(function(response) {
			self.boms = response.collection;
			//console.log('boms::',self.boms);
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
		},
		function(errResponse){
			console.error('Error while deleting item');
		});
	}

}]);

itemApp.controller( 'bomWizard', ['$scope', '$state', '$http', 'translator', 'bomService',  'dialogFactory', 
                                  function bomsController($scope, $state, $http, translator, bomService, dialogFactory ) {

	var self = this;
	self.service = bomService;
	self.translator = translator;

	self.bom = {
			item : null,
			quantity : 1
	};

	self.selectItem = function(i){
		//console.log('sel');
		self.bom.item = i;
	}

	self.itemSelected = function(){
		return self.bom.item != null;
	}

	//get items for select in add bom
	self.service.fetchAnyData('/items/forselect/0').then(function(response){

		self.itemsForSelect = response;

		//console.log('items for select',self.itemsForSelect);
	}, function(){
		console.log('get items for select - fail');
	})

	self.createOrUpdateBom = function(){

		var b = {
				id : 0,
				forItem : self.bom.item.id,
				requiredQuantity : self.bom.quantity
		}
		//console.log('bom::',b);

		self.service.createOrUpdate(b)
		.then( function(response){
			//console.log('create item::',response);
			$state.go('^.bomDetails',{id:response});
		},	function(errResponse){
			console.error('Error while creating/saving bom');
		});
	}

}]);

itemApp.controller( 'bomEdit', ['$scope', '$state', '$stateParams', '$http', '$q', 'translator', 'bomStockService', 'bomService', 'dialogFactory', 
                                function bomsController($scope, $state, $stateParams, $http, $q, translator, bomStockService, bomService, dialogFactory ) {

	//console.log('bomEdit ctrl starting');

	var self = this;
	self.service = bomService;
	self.translator = translator;

	self.bomId = parseInt($stateParams.id);

	self.createOrUpdateBomQuantity = function(){

		var bomRequest = {};
		bomRequest.id = self.bom.id;
		bomRequest.requiredQuantity = self.bom.requiredQuantity;

		//console.log('bomRequest::',bomRequest);
		self.service.createOrUpdate(bomRequest)
		.then( function(response){
			self.fetchBom();
		},	function(errResponse){
			console.error('Error while creating/saving bom');
		});
	};

	self.fetchBom = function(){
		bomService
		.fetch(self.bomId)
		.then(
				function(response) {
					self.bom = response;
					//console.log('bom::',self.bom);
				}
		)
	};

	self.fetchRequirements = function(){
		bomService
		.fetchAnyData('/bomrest/requirements/'+self.bomId)
		.then( 
				function(response){
					self.requirements = response;
					//console.log('requirements::', self.requirements);
				}
		)
	};

	self.saveStock = function(req){
		//console.log("req, line::",req);
		var stock = {
				//id : typeof( req.stockId ) == "undefined" ? 0 : req.stockId,
				bom : { id : self.bomId },
				forItem : { id : req.forItem.id },
				inStockQuantity : req.stock.inStockQuantity,
				remarks : req.stock.remarks
		}
		//console.log('stock :: ',stock);
		
		bomStockService
		.update(stock,0)
		.then( 
				function(response){
					self.fetchBom();
					//console.log('requirements::', self.requirements);
				}
		)
	};
	
	self.deleteBomPromise = function(){
		var res = $q.defer();

		self
		.service
		.deleteEntity(self.bom.id).
		then( function(response){
			$state.go('root.boms');
			res.resolve(': '+self.bom.forItem.name);
		},
		function(errResponse){
			console.error('Error while deleting item');
			res.reject(translator.label.bomEditDeleteFailureHeading);
		});

		return res.promise;

	}


	self.fetchBom();
	//self.fetchRequirements();

	//console.log('bomEdit ctrl ending');

}]);

