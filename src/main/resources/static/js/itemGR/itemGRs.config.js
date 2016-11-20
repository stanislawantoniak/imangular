'use strict';

var itemGRApp = angular.module('itemGRs', ['translationService','toolbox', 'checklist-model','xeditable']);

itemGRApp.config(['$stateProvider', function mainController( $stateProvider ) {

	$stateProvider
	.state ('root.itemGRs', {
		url: '/itemGRs',
		templateUrl : 'js/itemGR/itemGRList.html',
		controller : 'itemGRslist as itemGRsCtrl'
	})
	.state ('root.itemGRadd', {
		url: '/itemGRs/add',
		templateUrl : 'js/itemGR/itemGREdit.html',
		controller : 'itemGRedit as itemGRCtrl'
	});

}]);

itemGRApp.controller( 'itemGRslist', ['$q','$scope','$http','translator','itemGRService', 'ngTableParams',
                                      function itemGRsController( $q,  $scope,   $http,  translator,  itemGRService, ngTableParams) {

	var self = this;
	self.service = itemGRService;
	self.editGRContext = false;

	self.fetchAllGrs = function(){
		self.service.fetchAll().then(function(response) {

			console.log(response);
			self.grs = response.collection;
		}, function(){
			console.log('get game releases from service - fail');
		})
	};

	self.fetchAllGrs();

	this.userTable = new ngTableParams({
		page: 1,            // show first page
		count: 25,
		sorting: {
			username: 'asc'     // initial sorting
		}

	}, {
		total: 0, 
		getData: function($defer, params) {

			//console.log('get data 1');
			console.log(params.orderBy());
			console.log(params);
			self
			.service
			.fetchAll(params.page(), params.count(), params.orderBy())
			.then( function(response){
				//console.log("response::",response);
				params.total(response.totalRows);
				$defer.resolve(response.collection);
			} );
		}
	})

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

itemGRApp.controller( 'itemGRedit', ['$http', 'itemGRService', '$state',  function($http, itemGRService, $state ) {
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
					console.log("sussecd");
					$state.go('root.itemGRs');
				},	function(errResponse){
					console.error('Error while creating/saving GR');
				});
	}

}]);
