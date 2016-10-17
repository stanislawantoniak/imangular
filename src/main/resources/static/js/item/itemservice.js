'use strict';

angular.
module('items').
factory('itemService', ['$http', '$q', 'restservice', function( $http, $q, restservice){
	
	var ItemService = restservice.getService();
	ItemService.setRestServiceOne('items/item/');
	ItemService.setRestServiceAll('items/');
	console.log(ItemService);
	return ItemService;

}]);