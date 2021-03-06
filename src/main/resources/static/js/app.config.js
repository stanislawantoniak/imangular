'use strict';

materialAdmin
.config(  				['$stateProvider', '$httpProvider', '$urlRouterProvider', '$locationProvider', 
          				 function mainController( $stateProvider,   $httpProvider,   $urlRouterProvider,   $locationProvider ) {

	//console.log('imangular starting');

	//$locationProvider.hashPrefix('!');
	//$locationProvider.html5Mode(true);

	$urlRouterProvider.when('','/');

	$stateProvider

	.state('parent', {url: '/home', abstract: true, template: '<ui-view/>'} )
	// ALSO url '/home', overriding its parent's activation
	.state('parent.index', {url: '/index'} )

	.state ('root', { 							//contains navigation
		url: '',
		templateUrl: 'js/common/common.html'
	})
	.state ('login', {
		url: '/login',
		templateUrl : 'js/common/login.html',
		controller : 'login as login'
	})


	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

	//console.log('imangular ending');
}])

.run(['$window','$rootScope', '$location', 'editableOptions', 'editableThemes', 'authService', 
      function ($window, $rootScope, $location, editableOptions, editableThemes, authService ) {

	self.authService = authService;

	$rootScope.redirect = $location.path(); 

	if (false)
		$rootScope.$on('$routeChangeStart', function (event, next, current) {
			//just for checking if route change is fired
			//console.log('$rootScope.$on::$routeChangeStart start');
			if (!$rootScope.authenticated) {

				//basically you will never be authenticated when ths runs first time on page reload
				//because authentication service runs asynchroniously 
				//so only save required path for future redirect
				//redirect will be performed on authentication success

				if ($location.path() != '/login') //if required path is login do not save it 
					$rootScope.redirect = $location.path(); //get required path for redirect - when authenticated will be redirected to this path
				if ($location.path().startsWith('/users' ) ){ //users must be authorized
					console.log('Authentication:: Deny - not authenticated');
					event.preventDefault();
					$location.path('/login');
				}
			}else { 
				if ($location.path() == '/login'){
					console.log('Authentication:: already authenticated');
					event.preventDefault();
					$location.path('/');
				}
				$rootScope.redirect = $location.path();
				//console.log("redirect saved::",$rootScope.redirect);
				authService.manageRedirectsOnAuthentication();
			} 
			//console.log('$rootScope.$on::$routeChangeStart ending');
		});


	//xeditable settings (inline edit)
	editableOptions.theme = 'bs3';
	editableThemes.bs3.buttonsClass = 'btn btn-default waves-effect';
	editableThemes['bs3'].submitTpl = '<button type="submit"> <i class="zmdi zmdi-check zmdi-hc-fw"> </i> </button>';

}]);
