'use strict';

var itemGRApp = angular.module('itemGRs', ['translationService','toolbox', 'checklist-model','xeditable']);

itemGRApp.config(['$stateProvider', function mainController( $stateProvider ) {

	$stateProvider
	.state ('root.itemGRs', {
		url: '/itemGRs',
		templateUrl : 'js/itemgr/itemGRList.html',
		controller : 'itemGRslist as itemGRsCtrl'
	})
	.state ('root.itemGRadd', {
		url: '/itemGRs/add/:id',
		templateUrl : 'js/itemgr/itemGREdit.html',
		controller : 'itemGRedit as itemGRCtrl'
	})
	.state ('root.itemGRedit', {
		url: '/itemGRs/edit/:id',
		templateUrl : 'js/itemgr/itemGREdit.html',
		controller : 'itemGRedit as itemGRCtrl'
	});

}]);

itemGRApp.controller( 'itemGRslist', ['$q','translator','itemGRService',  'growlService',
                                      function itemGRsController( $q,  translator,  itemGRService, growlService) {

	var self = this;
	var labels = translator;
	self.service = itemGRService;
	self.messageService = growlService;
	self.editGRContext = false;
	
	self.fetchAllGrs = function(){
		self.service.fetchAll().then(function(response) {
			if (self.grs != null){ //do not show message at first table data load
				self.messageService.growl( labels.label.ListHasBeenRefreshed, 'info') ;
			}
			self.grs = response.collection;
			console.log('grs::',self.grs);
		}, function(){
			console.log('get game releases from service - fail');
		})
	};
	self.fetchAllGrs();

	self.deleteGRPromise = function(obj){ 

		var res = $q.defer();

		self
		.service
		.deleteEntity(obj.id)
		.then( 
				function(response){
					self.fetchAllGrs();
					res.resolve("Aktualizacja usunięta");
				},
				function(errResponse){
					console.error('Error while deleting GR');
					res.reject("wystąpił problem i nie udało się usunąć aktualizacji");
				}
		);

		return res.promise;

	};

	self.createOrUpdateGR = function(user,id){
		user.id = id;
		self.service.createOrUpdate(user)
		.then( function(response){
			//console.log('GR saved');
			self.fetchAllGrs();
			self.unsetEditRowContext();
		},	function(errResponse){
			console.error('Error while creating/saving GR');
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

}]);

itemGRApp.controller( 'itemGRedit', ['itemGRService', '$state','$stateParams',  
                                     function(itemGRService, $state, $stateParams ) {
	var self = this;
	self.service = itemGRService;
	
	self.itemId = parseInt($stateParams.id);
	console.log('id::',self.itemId);
	
	self.fetchGR = function(){
		self.service
		.fetch(self.itemId)
		.then(
				function(response) {
					self.gr = response;
					if (self.itemID == 0){
						self.gr.id = 0;
					}
					console.log('gr::',self.gr);
				}
				)
	};
	
	self.fetchGR();
	
	self.createOrUpdateGR = function(){

		self
		.service
		.createOrUpdate(self.gr)
		.then( 
				function(response){
					console.log("succeed",response);
					$state.go('^.itemGRedit({id:response})');
				},	function(errResponse){
					console.error('Error while creating/saving GR');
				});
	}
	
	self.setAddStepCtx = function(){
		self.addStepCtx = true;
	}
	self.unsetAddStepCtx = function(){
		self.addStepCtx = false;
	}
	

}]);
