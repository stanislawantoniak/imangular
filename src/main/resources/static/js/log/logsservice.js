'use strict';

angular.
module('logs').
factory('logsService', ['restservice', function( restservice){
	
	var Logservice = restservice.getService();
	Logservice.setRestServiceAll('sessionlogs');
	return Logservice;

}]);