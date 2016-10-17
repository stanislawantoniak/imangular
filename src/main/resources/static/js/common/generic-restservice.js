'use strict';
 
angular.
module('generic-restservice',[]).
factory('restservice', ['$http', '$q', function($http, $q){

    var Service = function(){
        this.REST_SERVICE_ONE = '';
        this.REST_SERVICE_ALL = '';
        this.setRestServiceOne = function(u){ this.REST_SERVICE_ONE = u; };
        this.setRestServiceAll = function(u){ this.REST_SERVICE_ALL = u; };  
    }
    
    Service.prototype.fetch = function(id){ return this.fetchAnyData( this.REST_SERVICE_ONE + id);  }
   
    Service.prototype.fetchAll = function(){ return this.fetchAnyData( this.REST_SERVICE_ALL ); }
    
    Service.prototype.fetchAnyData = function( url ) {
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
    
    Service.prototype.create = function(entity) {
        var deferred = $q.defer();
        $http.post(this.REST_SERVICE_ONE,  entity)
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
  
    Service.prototype.update = function(REST_SERVICE_ONE, entity, id) {
        var deferred = $q.defer();
        $http.put(this.REST_SERVICE_ONE+id, entity)
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while updating '+entity);
                console.error(errResponse);
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
    
    Service.prototype.createOrUpdate = function(entity){
        if(entity.id==0){
            console.log('Saving to '+this.REST_SERVICE_ONE, entity);
            return this.create(this.REST_SERVICE_ONE, entity);
        }else{
            console.log('Updating to '+this.REST_SERVICE_ONE, entity);
            return this.update(this.REST_SERVICE_ONE, entity, entity.id);
        }
    }
 
    Service.prototype.deleteEntity = function(id) {
        var deferred = $q.defer();
        $http.delete(this.REST_SERVICE_ONE+id)
            .then(
            function (response) {
                deferred.resolve(response.data);
            },
            function(errResponse){
                console.error('Error while deleting from '+this.REST_SERVICE_ONE);
                deferred.reject(errResponse);
            }
        );
        return deferred.promise;
    }
   
    var Factory = {
    		getService : function(){ return new Service(); }
    }
    
    return Factory;
 
}]);