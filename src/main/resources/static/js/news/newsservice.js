'use strict';

angular.
module('news').
factory('newsService', [ '$q', 'restservice', function( $q, restservice){

	var newservice = restservice.getService();
	newservice.setRestServiceOne('newsitemrest/');
	newservice.setRestServiceAll('newsitems');

	newservice.fetchByCategoryPublished = function(cat){
		//console.log('invoked fetchByCategoryPublished with cat::', cat);
		
		var deferred = $q.defer();
		
		this
		.fetchAll(1,1000000,['-priority'],{category:cat})
		.then( 
				function(response){
					var temp = response.collection;
					var newsList = [];
					//filter published only - filters on server side work on string fields only
					angular.forEach( temp, function(news) {
						if ( news.isPublished ){
							//prepare map for ngClass with background color
							var bgmColor = {};
							bgmColor[news.bgmColor] = true;
							var cColor = {};
							cColor[news.bgmColor.replace('bgm-','c-')] = true;
							news.bgmColor = bgmColor;
							news.cColor = cColor;
							newsList.push(news);
						}
					});
					//console.log("news list::",newsList);
					deferred.resolve(newsList);
				} );
	
		return deferred.promise;
	}

	return newservice;

}]);