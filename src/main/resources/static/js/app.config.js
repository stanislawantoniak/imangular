'use strict';

var materialAdmin = angular
.module('imangular', 
		[ 
		 'ngAnimate',
		 'ngResource',
		 'ui.router',
		 'ui.bootstrap',
		 //'angular-loading-bar',
		 //'oc.lazyLoad',
		 //'nouislider',
		 //'ngTable',
		 
		 'navigation', 
		 'dashboard', 
		 'users', 
		 'items',
		 'boms',
		 'toolbox',
		 'authenticationService' ] );

materialAdmin
.config(['$stateProvider', '$httpProvider', '$locationProvider', function mainController( $stateProvider, $httpProvider, $locationProvider ) {

	console.log('imangular starting');

	//$locationProvider.hashPrefix('!');
	//$locationProvider.html5Mode(true);

	//$routeProvider.when('','/');
	
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
	.state('root.users.add', {
		url: '/add/:id',
		templateUrl : 'js/user/userEdit.html',
		controller : 'useredit',
		controllerAs : 'userCtrl'
	})
	.state ('root.items', {
		url: '/items',
		templateUrl : 'js/item/itemList.html',
		controller : 'itemslist as itemsCtrl'
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


	/*
	$routeProvider.when('/', {
		templateUrl : 'js/dashboard/dashboard.html',
		controller : 'dashboard'
	}).when('/users', {
		templateUrl : 'js/user/userList.html',
		controller : 'userslist as usersCtrl'
	}).when('/items', {
		templateUrl : 'js/item/itemList.html',
		controller : 'itemslist as itemsCtrl'
	}).when('/boms', {
		templateUrl : 'js/item/bomList.html',
		controller : 'bomslist as bomsCtrl'
	}).when('/login', {
		templateUrl : 'js/common/login.html',
		controller : 'login as login'
	}).otherwise({
		redirectTo: '/'
	});

	 */

	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

	console.log('imangular ending');
}]);

materialAdmin

.run(['$rootScope', '$location', 'editableOptions', 'authService', function ($rootScope, $location, editableOptions, authService ) {

	editableOptions.theme = 'bs3';//xeditable config - use bootstrap 3

	self.authService = authService;

	console.log('materialAdmin.run start');
	
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
	console.log('materialAdmin run end');

}]);

materialAdmin
//=========================================================================
//Base controller for common functions
//=========================================================================

.controller('imangularCtrl', function($timeout, $state, $scope){
	//Welcome Message
	//growlService.growl('Welcome back Mallinda!', 'inverse')


	// Detact Mobile Browser
	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
		angular.element('html').addClass('ismobile');
	}

	// By default Sidbars are hidden in boxed layout and in wide layout only the right sidebar is hidden.
	this.sidebarToggle = {
			left: false,
			right: false
	}

	// By default template has a boxed layout
	this.layoutType = localStorage.getItem('ma-layout-status');

	// For Mainmenu Active Class
	this.$state = $state;    

	//Close sidebar on click
	this.sidebarStat = function(event) {
		if (!angular.element(event.target).parent().hasClass('active')) {
			this.sidebarToggle.left = false;
		}
	}

	//Listview Search (Check listview pages)
	this.listviewSearchStat = false;

	this.lvSearch = function() {
		this.listviewSearchStat = true; 
	}

	//Listview menu toggle in small screens
	this.lvMenuStat = false;

	//Blog
	this.wallCommenting = [];

	this.wallImage = false;
	this.wallVideo = false;
	this.wallLink = false;

	//Skin Switch
	this.currentSkin = 'blue';

	this.skinList = [
	                 'lightblue',
	                 'bluegray',
	                 'cyan',
	                 'teal',
	                 'green',
	                 'orange',
	                 'blue',
	                 'purple'
	                 ]

	this.skinSwitch = function (color) {
		this.currentSkin = color;
	}

})


//=========================================================================
//Header
//=========================================================================
.controller('headerCtrl', function($timeout){


	// Top Search
	this.openSearch = function(){
		angular.element('#header').addClass('search-toggled');
		angular.element('#top-search-wrap').find('input').focus();
	}

	this.closeSearch = function(){
		angular.element('#header').removeClass('search-toggled');
	}

	// Get messages and notification for header
	//this.img = messageService.img;
	//this.user = messageService.user;
	//this.user = messageService.text;

	//this.messageResult = messageService.getMessage(this.img, this.user, this.text);


	//Clear Notification
	this.clearNotification = function($event) {
		$event.preventDefault();

		var x = angular.element($event.target).closest('.listview');
		var y = x.find('.lv-item');
		var z = y.size();

		angular.element($event.target).parent().fadeOut();

		x.find('.list-group').prepend('<i class="grid-loading hide-it"></i>');
		x.find('.grid-loading').fadeIn(1500);
		var w = 0;

		y.each(function(){
			var z = $(this);
			$timeout(function(){
				z.addClass('animated fadeOutRightBig').delay(1000).queue(function(){
					z.remove();
				});
			}, w+=150);
		})

		$timeout(function(){
			angular.element('#notifications').addClass('empty');
		}, (z*150)+200);
	}

	// Clear Local Storage
	this.clearLocalStorage = function() {

		//Get confirmation, if confirmed clear the localStorage
		swal({   
			title: "Are you sure?",   
			text: "All your saved localStorage values will be removed",   
			type: "warning",   
			showCancelButton: true,   
			confirmButtonColor: "#F44336",   
			confirmButtonText: "Yes, delete it!",   
			closeOnConfirm: false 
		}, function(){
			localStorage.clear();
			swal("Done!", "localStorage is cleared", "success"); 
		});

	}

	//Fullscreen View
	this.fullScreen = function() {
		//Launch
		function launchIntoFullscreen(element) {
			if(element.requestFullscreen) {
				element.requestFullscreen();
			} else if(element.mozRequestFullScreen) {
				element.mozRequestFullScreen();
			} else if(element.webkitRequestFullscreen) {
				element.webkitRequestFullscreen();
			} else if(element.msRequestFullscreen) {
				element.msRequestFullscreen();
			}
		}

		//Exit
		function exitFullscreen() {
			if(document.exitFullscreen) {
				document.exitFullscreen();
			} else if(document.mozCancelFullScreen) {
				document.mozCancelFullScreen();
			} else if(document.webkitExitFullscreen) {
				document.webkitExitFullscreen();
			}
		}

		if (exitFullscreen()) {
			launchIntoFullscreen(document.documentElement);
		}
		else {
			launchIntoFullscreen(document.documentElement);
		}
	}

});


