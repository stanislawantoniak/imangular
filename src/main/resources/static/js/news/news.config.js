'use strict';

//Register `newss` component, along with its associated controller 
var newsApp = angular.module('news', ['translationService','toolbox', 'checklist-model','xeditable','ui.select', 'ngTable', 'textAngular']);

newsApp
.controller( 'newsList', ['$q','$scope','$http','translator','newsService', 'ngTableParams',
                          function newsController( $q,  $scope,   $http,  translator,  newsService,  ngTableParams ) {

	var self = this;
	self.service = newsService;

	//table for news
	self.newsTable = new ngTableParams({
		page: 1,            // show first page
		count: 25,
		sorting: {
			category: 'asc'     // initial sorting
		}

	}, {
		total: 0, 
		getData: function($defer, params) {

			self
			.service
			.fetchAll(params.page(), params.count(), params.orderBy(), params.filter())
			.then( 
					function(response){
						//console.log("response::",response);
						params.total(response.totalRows);
						$defer.resolve(response.collection);
					} );
		}
	})

	self.deleteNewsPromise = function(news){
		var res = $q.defer();

		self
		.service
		.deleteEntity(news.id)
		.then( 
				function(response){
					self.newsTable.reload(); //reload grid on succesful delete
					res.resolve(translator.label.newssdeletesuccessinfo);
				},
				function(errResponse){
					console.error('Error while deleting news');
					res.reject(translator.label.newssdeletefailureinfo);
				});

		return res.promise;
	}
}]);

newsApp.controller( 'newsEdit', ['$q','$state', '$stateParams','$scope', '$http', '$location',  'translator','newsService', 'authService',
                                 function newssController(  $q, $state,  $stateParams,  $scope,  $http,    $location,    translator,  newsService,  authService ) {
	//console.log('newsEdit controller starting');

	var self = this;
	self.service = newsService;

	self.translator = translator;

	self.newsId = parseInt($stateParams.id);
	//console.log('newsId param::',self.newsId);

	self.bgmClass = {};
	self.cClass = {};
	self.applyBgmColor = function(){
		if (self.news.id != 0){
			self.bgmClass[self.news.bgmColor] = true ;
			self.cClass[self.news.bgmColor.replace('bgm-','c-')] = true ;
		}
	}

	//	fetch news - when adding news get empty news but populated predefined fields id any
	self.fetchnews = function(){
		self.service
		.fetch(self.newsId)
		.then(
				function(response) {
					self.news = response;
					self.applyBgmColor();
					//console.log('news fetched::',self.news);
				}
		)
	};

	self.fetchnews();

	self.createOrUpdateNews = function(x){

		self.service.createOrUpdate(self.news)
		.then( 
				function(response){
					$state.go('root.news');
				},	function(errResponse){
					console.error('Error while creating/saving news');
				});
	}

//	console.log('newsEdit controller - ending');
}]);


