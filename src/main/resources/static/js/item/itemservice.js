'use strict';

angular.
module('items').
factory('itemService', ['$http', '$q', 'restservice', function( $http, $q, restservice){
	
	var ItemService = restservice.getService();
	ItemService.setRestServiceOne('itemrest/');
	ItemService.setRestServiceAll('items/');
	//console.log(ItemService);
	return ItemService;

}]).
factory('itemComponentService', ['$http', '$q', 'restservice', function( $http, $q, restservice){
	
	var ItemComponentService = restservice.getService();
	ItemComponentService.setRestServiceOne('items/component/');
	//console.log(ItemComponentService);
	return ItemComponentService;

}]);