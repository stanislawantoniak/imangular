'use strict';

//Register `navigation` component, along with its associated controller and template
angular.
module('dashboard', ['translationService'])
.config(['$stateProvider', function( $stateProvider ) {

	$stateProvider
	.state ('root.dashboard', {
		url: '/',
		templateUrl : 'js/dashboard/dashboard.html',
		controller : 'dashboard as dashCtrl'
	})

}])
.controller( 'dashboard', ['$scope', 'translator', 'newsService', function DashboardController($scope, translator, newsService) {

	var self = this;
	self.service = newsService;

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
