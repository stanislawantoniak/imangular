'use strict';
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * author St.Antoniak
 * 
 * this service is designed for one entity (like Products)
 * we must provide one url for collection (via setRestServiceOne method) 
 * and one for single item operations (via setRestServiceAll method)
 * 
 * 
 * Collection rest must accept extra parameters for 
 *  pagination /{pageNumber}/{pageSize}
 *  sorting ....
 *  filtering ...
 * 
 * Also url for collection count must be supported.
 * Count requests are created by appending 'count' after base url.
 * Extra parameters for filtering must be supported accordingly.
 *  
 * Base single item operations are executed using relevant HTTP requests:
 *  get for getting data (with parameter entity id) - with appended {id} after base url
 *  delete for deleting (with parameter entity id) - with appended {id} after base url
 *  post for creating (with parameter entity object) - with entity object in request body
 *  put for updating (with parameter entity object) - with appended {id} after base url and entity object in request body
 * 
 * keep in mind that object must have id property for entity id
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

angular.
module('generic-restservice',[]).
factory('restservice', ['$http', '$q', '$window', '$location', 
                        function($http, $q, $window, $location){

	var Service = function(){
		this.REST_SERVICE_ONE = '';
		this.REST_SERVICE_ALL = '';
		this.setRestServiceOne = function(u){ this.REST_SERVICE_ONE = u; };
		this.setRestServiceAll = function(u){ this.REST_SERVICE_ALL = u; };  
	}

	Service.prototype.count = function(x){ return this.fetchAnyData( this.REST_SERVICE_ALL + 'count');  }

	Service.prototype.fetch = function(id){ return this.fetchAnyData( this.REST_SERVICE_ONE + id);  }

	/*
	 * pagination, filter and sort params to send in post request
	 * {"pageNo":1,"pageSize":25,"sortOrderFields":{"name":"asc","age":"asc"},"filterFields":{"key1":"value1","key2":"value2"}}
	 * 
	 * if no pageNo/pageSize is suppliedit falls back to http.get with no params
	*/

	Service.prototype.fetchAll = function(pageNo, pageSize, sortBy, filter){ 

		var params = {};
		if (pageNo != null && pageSize != null){
			//params += '/'+pageNo + '/' + pageSize;
			
			params.pageNo = pageNo;
			params.pageSize = pageSize;

			if (sortBy != null){

				var dir = sortBy[0].substring(0,1) == '+' ? 'asc' : 'desc';
				var field = sortBy[0].substring(1);
				var sortOrderFields = {};
				sortOrderFields[ field ] = dir;
				params.sortOrderFields = sortOrderFields;

				if (filter !=  null){
					params.filterFields =  filter;
				}
			}
			
			/*
			 * 
			 $window.ga('send', 'pageview', 
					{ page: $location.path()
							+'/filter/'+filter
							+'/page/'+pageNo
							+'/perPage/'+pageSize
							+'/orderBy/'+sortBy
					});
			*/
			return this.postAnyData( this.REST_SERVICE_ALL, params); 
		
		} else {
			
			return this.fetchAnyData( this.REST_SERVICE_ALL );
			
			}			
		console.log("params::",params);
	
	}
	
	Service.prototype.postAnyData = function( url, params ) {
		var deferred = $q.defer();
		$http.post( url, params )
		.then(
				function (response) {
					//console.log(response.data);
					deferred.resolve(response.data);
				},
				function(errResponse){
					//console.error('Error while fetching from url '+url);
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}

	Service.prototype.fetchAnyData = function( url ) {
		var deferred = $q.defer();
		$http.get( url )
		.then(
				function (response) {
					//console.log(response.data);
					deferred.resolve(response.data);
				},
				function(errResponse){
					//console.error('Error while fetching from url '+url);
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
					//in create rest service we get entity id in response
					//it is important to get it back
					deferred.resolve(response.data);
				},
				function(errResponse){
					//console.error('Error while creating '+entity);
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}

	Service.prototype.update = function( entity, id) {
		var deferred = $q.defer();
		$http.put(this.REST_SERVICE_ONE+id, entity)
		.then(
				function (response) {
					deferred.resolve(response.data);
				},
				function(errResponse){
					//console.error('Error while updating '+entity);
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}

	Service.prototype.createOrUpdate = function(entity){
		if(entity.id==0){
			//console.log('Saving to '+this.REST_SERVICE_ONE, entity);
			return this.create(entity);
		}else{
			//console.log('Updating to '+this.REST_SERVICE_ONE, entity);
			return this.update(entity, entity.id);
		}
	}

	Service.prototype.deleteEntity = function(id) {
		var deferred = $q.defer();
		var rest = this.REST_SERVICE_ONE;
		$http.delete(this.REST_SERVICE_ONE+id)
		.then(
				function (response) {
					deferred.resolve(response.data);
				},
				function(errResponse){
					//console.error('Error while deleting from '+rest);
					deferred.reject(errResponse);
				}
		);
		return deferred.promise;
	}

	var Factory = {
			getService : function(){ return new Service(); }
	}

	//console.log("rest service in factory : ",Factory.getService());

	return Factory;

}]);