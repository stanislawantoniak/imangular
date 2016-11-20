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
		url: '/itemGRs/add',
		templateUrl : 'js/itemgr/itemGREdit.html',
		controller : 'itemGRedit as itemGRCtrl'
	});

}]);

itemGRApp.controller( 'itemGRslist', ['$q','translator','itemGRService', 'ngTableParams',
                                      function itemGRsController( $q,  translator,  itemGRService, ngTableParams) {

	var self = this;
	self.service = itemGRService;
	self.editGRContext = false;

	self.fetchAllGrs = function(){
		self.service.fetchAll().then(function(response) {
			//console.log(response);
			self.grs = response.collection;
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
					res.resolve(translator.label.usersdeletesuccessinfo);
				},
				function(errResponse){
					console.error('Error while deleting GR');
					res.reject(translator.label.usersdeletefailureinfo);
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

itemGRApp.controller( 'itemGRedit', ['itemGRService', '$state',  function(itemGRService, $state ) {
	var self = this;
	self.service = itemGRService;
	
	self.gr = {};

	self.createOrUpdateGR = function(){
		
		self.gr.id = 0;
		
		self
		.service
		.createOrUpdate(self.gr)
		.then( 
				function(response){
					//console.log("sussecd");
					$state.go('root.itemGRs');
				},	function(errResponse){
					console.error('Error while creating/saving GR');
				});
	}

}]);