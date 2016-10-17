'use strict';
 
angular.
module('users').
factory('userService', ['$http', '$q', 'restservice', function( $http, $q, restservice){
 
    var UserService = restservice.getService();
    UserService.setRestServiceOne('userrest/');
    UserService.setRestServiceAll('userslistrest/12qs');
    return UserService;
 
}]);