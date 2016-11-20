'use strict';

angular.
module('itemGRs').
factory('itemGRService', ['restservice', function( restservice){
	
	var ItemGRService = restservice.getService();
	ItemGRService.setRestServiceOne('itemgamereleaserest/');
	ItemGRService.setRestServiceAll('itemgamereleases');
	return ItemGRService;

}]);