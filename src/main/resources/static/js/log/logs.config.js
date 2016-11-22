'use strict';

var logApp = angular.module('logs', ['translationService','toolbox', 'checklist-model','xeditable']);

logApp.config(['$stateProvider', function mainController( $stateProvider ) {

	$stateProvider
	.state ('root.logs', {
		url: '/logs',
		templateUrl : 'js/log/logsList.html',
		controller : 'logslist as logsCtrl'
	});

}]);

logApp.controller( 'logslist', ['$q','translator','logsService', 'ngTableParams', 'growlService',
                                      function logsController( $q,  translator,  logsService, ngTableParams, growlService) {

	var self = this;
	var labels = translator;
	self.service = logsService;
	self.messageService = growlService;
	
	self.logsTable = new ngTableParams({
		page: 1,            // show first page
		count: 50,
		sorting: {
			dateCreated: 'desc'     // initial sorting
		}

	}, {
		total: 0, 
		getData: function($defer, params) {

			//console.log('page::',params.page());
			//console.log('count::',params.count());
			//console.log('orderBy::',params.orderBy());
			console.log('filter::',params.filter());

			self
			.service
			.fetchAll( params.page(), params.count(), params.orderBy(), params.filter() ) 
			.then( function(response){
				console.log("response::",response);
				if (params.total() != 0){ //do not show message at first table data load
					self.messageService.growl(translator.label.ListHasBeenRefreshed, 'info') ;
				}
				params.total(response.totalRows);
				$defer.resolve(response.collection);
			} );
		}
	});
	
	self.setFilter = function(propertyName, propertyValue){
		 var filter = {};
	      filter[propertyName] = propertyValue;
	      angular.extend(self.logsTable.filter(), filter);
	};

}]);
