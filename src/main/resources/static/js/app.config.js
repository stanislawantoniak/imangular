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

	.state ('root', { //contains navigation
		url: '',
		templateUrl: 'js/common/common.html'
	})

	.state ('root.dashboard', {
		url: '/',
		templateUrl : 'js/dashboard/dashboard.html',
		controller : 'dashboard'
	})
	.state ('root.users', {
		url: '/users',
		templateUrl : 'js/user/userList.html',
		controller : 'userslist as usersCtrl'
	})
	.state('root.useradd', {
		url: '/users/add/:id',
		templateUrl : 'js/user/userEdit.html',
		controller : 'useredit',
		controllerAs : 'userCtrl'
	})

	.state ('root.items', {
		url: '/items',
		templateUrl : 'js/item/itemList.html',
		controller : 'itemslist as itemsCtrl'
	})
	.state ('root.itemDetails', {
		url: '/items/itemdetails/:id',
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit as itemCtrl'
	})
	.state ('root.itemAdd', {
		url: '/items/additem/:id',
		templateUrl : 'js/item/itemEdit.html',
		controller : 'itemEdit as itemCtrl'
	})

	.state ('root.boms', {
		url: '/boms',
		templateUrl : 'js/item/bomList.html',
		controller : 'bomslist as bomsCtrl'
	})
	.state ('login', {
		url: '/login',
		templateUrl : 'js/common/login.html',
		controller : 'login as login'
	});



	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

	//console.log('imangular ending');
}]);

materialAdmin

.run(['$rootScope', '$location', 'editableOptions', 'authService', function ($rootScope, $location, editableOptions, authService ) {

	editableOptions.theme = 'bs3';//xeditable config - use bootstrap 3

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


}]);
