(function(i, s, o, g, r, a, m) {
	i['GoogleAnalyticsObject'] = r;
	i[r] = i[r] || function() {
		(i[r].q = i[r].q || []).push(arguments)
	}, i[r].l = 1 * new Date();
	a = s.createElement(o), m = s.getElementsByTagName(o)[0];
	a.async = 1;
	a.src = g;
	m.parentNode.insertBefore(a, m)
})(window, document, 'script',
		'https://www.google-analytics.com/analytics.js', 'ga');

ga('create', 'UA-87629565-1', 'auto');

var analitics = angular.module('analytics', []);

analitics 
.run(['$window','$rootScope', '$location',  
      function ($window, $rootScope, $location  ) {

	/*
	 * google analytics
	 */
	$rootScope
	.$on('$stateChangeSuccess',
			function(event){

		if (!$window.ga)
			return;

		$window.ga('send', 'pageview', { page: $location.path() });
	});
}]);