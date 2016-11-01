'use strict';

angular.
module('boms').
factory('bomService', ['$http', '$q', 'restservice', function( $http, $q, restservice){
	
	var BomService = restservice.getService();
	BomService.setRestServiceOne('bomrest/');
	BomService.setRestServiceAll('boms/');
	//console.log(BomService);
	return BomService;

}]);