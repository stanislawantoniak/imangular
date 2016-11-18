'use strict';

angular.
module('news').
factory('newsService', ['$http', '$q', 'restservice', function( $http, $q, restservice){
	
	var newservice = restservice.getService();
	newservice.setRestServiceOne('newsitemrest/');
	newservice.setRestServiceAll('newsitems');
	return newservice;

}]);