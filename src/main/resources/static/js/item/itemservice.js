'use strict';

angular.
module('items').
factory('itemService', ['restservice', function( restservice){
	
	var ItemService = restservice.getService();
	ItemService.setRestServiceOne('itemrest/');
	ItemService.setRestServiceAll('items');
	//console.log(ItemService);
	return ItemService;

}]).
factory('itemComponentService', ['restservice', function(  restservice){
	
	var ItemComponentService = restservice.getService();
	ItemComponentService.setRestServiceOne('componentrest/');
	//console.log(ItemComponentService);
	return ItemComponentService;

}]);