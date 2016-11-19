'use strict';

//Register `navigation` component, along with its associated controller and template
angular.
module('dashboard', ['translationService']).
controller( 'dashboard', ['$scope', 'translator', 'newsService', function DashboardController($scope, translator, newsService) {

	var self = this;
	self.service = newsService;

	/*
	self
	.service
	.fetchAll(1,1000000,['-priority'],{category:'home'})
	.then( 
			function(response){
				console.log("news list::",response);
				self.temp = response.collection;
				self.news = [];
				angular.forEach(self.temp, function(news) {
					if ( news.isPublished ){
						//prepare map for ngClass with background color
						var bgmColor = {};
						bgmColor[news.bgmColor] = true;
						news.bgmColor = bgmColor;
						self.news.push(news);
					}
				});
			} );
	 */


	self
	.service
	.fetchByCategoryPublished('home')
	.then(
			function(response){
				/*self.news = {};
				self.news.colA = [];
				self.news.colB = [];
				angular.forEach( 
						response, 
						function(news) {
							if (news.counter % 2 == 0)
								self.news.colA.push(news);
							else
								self.news.colB.push(news);
						})
						*/
				self.news = response;
			});

}]);
