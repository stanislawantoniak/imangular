'use strict';

angular
.module('boms')
.factory('bomService', ['restservice', function( restservice){
	
	var BomService = restservice.getService();
	BomService.setRestServiceOne('bomrest/');
	BomService.setRestServiceAll('boms');
	//console.log(BomService);
	return BomService;

}])
.factory('bomStockService', ['restservice', function( restservice){
	
	var BomStockService = restservice.getService();
	BomStockService.setRestServiceOne('bomstockrest/');
	BomStockService.setRestServiceAll('bomsx'); //notrelevant
	//console.log(BomStockService);
	return BomStockService;

}]);