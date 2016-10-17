'use strict';
 
angular.
module('users').
factory('userService', ['$http', '$q', function($http, $q){
	var GET_ALL_USERS_URL = 'userslistrest/12qs';
    var REST_SERVICE_URI = 'userrest/';
 
    var factory = {
        fetchAllUsers: fetchAllUsers,
        
        fetchUser: fetchUser,
        createUser: createUser,
        updateUser:updateUser,
        createOrUpdateUser : createOrUpdateUser,
        deleteUser:deleteUser,
        
        fetchAnyData: fetchAnyData
    };
 
    return factory;
 
    function fetchAllUsers() {
        var deferred = $q.defer();
        $http.get( GET_ALL_USERS_URL )
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while fetching Users');
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
 
    function fetchUser(id) {
    	return this.fetchAnyData(REST_SERVICE_URI + id);
    }
    
    function fetchAnyData(url) {
        var deferred = $q.defer();
        $http.get( url )
            .then(
            function (response) {
            	console.log(response.data);
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while fetching from url '+url);
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
    
    function createUser(user) {
        var deferred = $q.defer();
        $http.post(REST_SERVICE_URI, user)
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while creating User');
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
  
    function updateUser(user, id) {
        var deferred = $q.defer();
        $http.put(REST_SERVICE_URI+id, user)
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while updating User');
                console.error(errResponse);
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
    
    function createOrUpdateUser(user){
        if(user.id==0){
            console.log('Saving New User', user);
            return createUser(user);
        }else{
            console.log('Updating user ', user);
            return updateUser(user, user.id);
        }
    }
 
    function deleteUser(id) {
        var deferred = $q.defer();
        $http.delete(REST_SERVICE_URI+id)
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while deleting User');
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
 
}]);