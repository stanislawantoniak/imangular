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
		 'ngTable',
		 
		 'ui.select',

		 'navigation', 
		 'dashboard', 
		 'users', 
		 'items',
		 'itemGRs',
		 'boms',
		 'news',
		 'logs',
		 'toolbox',
		 'authenticationService',
		 'analytics'] );

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

})

//=========================================================================
//LAYOUT
//=========================================================================

.directive('changeLayout', function(){

	return {
		restrict: 'A',
		scope: {
			changeLayout: '='
		},

		link: function(scope, element, attr) {

			//Default State
			if(scope.changeLayout === '1') {
				element.prop('checked', true);
			}

			//Change State
			element.on('change', function(){
				if(element.is(':checked')) {
					localStorage.setItem('ma-layout-status', 1);
					scope.$apply(function(){
						scope.changeLayout = '1';
					})
				}
				else {
					localStorage.setItem('ma-layout-status', 0);
					scope.$apply(function(){
						scope.changeLayout = '0';
					})
				}
			})
		}
	}
})

//=========================================================================
//MAINMENU COLLAPSE
//=========================================================================

.directive('toggleSidebar', function(){

	return {
		restrict: 'A',
		scope: {
			modelLeft: '=',
			modelRight: '='
		},

		link: function(scope, element, attr) {
			element.on('click', function(){

				if (element.data('target') === 'mainmenu') {
					if (scope.modelLeft === false) {
						scope.$apply(function(){
							scope.modelLeft = true;
						})
					}
					else {
						scope.$apply(function(){
							scope.modelLeft = false;
						})
					}
				}

				if (element.data('target') === 'chat') {
					if (scope.modelRight === false) {
						scope.$apply(function(){
							scope.modelRight = true;
						})
					}
					else {
						scope.$apply(function(){
							scope.modelRight = false;
						})
					}

				}
			})
		}
	}

})



//=========================================================================
//SUBMENU TOGGLE
//=========================================================================

.directive('toggleSubmenu', function(){

	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			element.click(function(){
				element.next().slideToggle(200);
				element.parent().toggleClass('toggled');
			});
		}
	}
})


//=========================================================================
//STOP PROPAGATION
//=========================================================================

.directive('stopPropagate', function(){
	return {
		restrict: 'C',
		link: function(scope, element) {
			element.on('click', function(event){
				event.stopPropagation();
			});
		}
	}
})

.directive('aPrevent', function(){
	return {
		restrict: 'C',
		link: function(scope, element) {
			element.on('click', function(event){
				event.preventDefault();
			});
		}
	}
})


//=========================================================================
//PRINT
//=========================================================================

.directive('print', function(){
	return {
		restrict: 'A',
		link: function(scope, element){
			element.click(function(){
				window.print();
			})   
		}
	}
})

// =========================================================================
// MALIHU SCROLL
// =========================================================================

//On Custom Class
.directive('cOverflow', ['scrollService', function(scrollService){
	return {
		restrict: 'C',
		link: function(scope, element) {

			if (!$('html').hasClass('ismobile')) {
				scrollService.malihuScroll(element, 'minimal-dark', 'y');
			}
		}
	}
}])

// =========================================================================
// WAVES
// =========================================================================

// For .btn classes
.directive('btn', function(){
	return {
		restrict: 'C',
		link: function(scope, element) {
			if(element.hasClass('btn-icon') || element.hasClass('btn-float')) {
				Waves.attach(element, ['waves-circle']);
			}

			else if(element.hasClass('btn-light')) {
				Waves.attach(element, ['waves-light']);
			}

			else {
				Waves.attach(element);
			}

			Waves.init();
		}
	}
});



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

'use strict';

//Register `navigation` component, along with its associated controller and template
var navi = angular

.module('navigation', ['translationService','authenticationService'])

.controller( 'navigation', ['$rootScope', '$http', '$location', '$scope', 'translator', 'authService', function($rootScope, $http, $location, $scope,  translator, authService) {

	var self = this;
	
	//console.log('navigation controller starting');
	
	self.authService = authService;
	//console.log('navigation::authservice: ',self.authService);
	self.authService.getSession();

	self.translatorHandler = translator;

	self.route = $location;
	
	self.selectLanguage = function(language) {
		self.authService.selectLanguage(language, self.translatorHandler.refresh);//on callback refresh translations
	}

	//console.log('navigation controller - ending');

}])
;

'use strict';

var transl = angular.module('translationService', []);

transl.controller( 'translation',['translator', '$scope', function(translator, $scope) {

	//console.log('translation controller starting');

	$scope.translator = translator;

	//console.log('translation controller ending');

}]);

transl.factory('translator', ['$q','$http', function($q,$http){

	var Translator = {

			label: {},

			yesno : [],

			refresh : function(){

				$http
				.get('/common/labels')
				.then( 
						function(response) {

							Translator.label = response.data;

							Translator.yesno = [
							                    {value: 'true', text: Translator.label.booleanyes},
							                    {value: 'false', text: Translator.label.booleanno}
							                    ];

							//console.log('translator refresh service', Translator.label);

						} , function(){

							console.error('no data available on service /common/labels');

						} );

				return Translator.label; 

			}
	};

	Translator.refresh();

	return Translator;

}]);
'use strict';

var changepass = angular
.module('changepass', 
		[ 
		 'translationService'
		 ] );

changepass
.config(						['$httpProvider', 
        						 function mainController(  $httpProvider ) {

	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

}]);

changepass

.controller( 'changepass', [ '$location', '$http', 'translator', '$window', 
                             function( $location, $http, translator, $window ) {

	var self = this;

	self.translator = translator;
	
	self.hashNotFound = true;
	self.username = '';

	var fullPath = $location.absUrl();
	
	self.hash = fullPath.substring(fullPath.lastIndexOf('/')+1);
	//console.log('hash extracted:: ', self.hash);

	self.getUsernameForHash = function(){
		$http
		.get('/getusername/'+self.hash)
		.then( 
				function(response){
					//console.log(response);
					self.username = response.data;
					self.password = '' ;
					self.hashNotFound = false;
					//console.log("username", self.username);
				},
				function(){
					self.hashNotFound = true;
					console.log("user does not exist");
				})

	}

	self.getUsernameForHash();

	self.changePass = function(){
		$http
		.put('/changepass/'+self.hash, self.password)
		.then( 
				function(){
					//on succesful password change redirect to main page
					$window.location.href = '/'
				},
				function(){
					//to fix
					//if anything happens 
					console.log("user does not exist");
				})
	}

}]);
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
'use strict';

var auth = angular.module('authenticationService',[]);

auth.factory('authService', ['$http', '$rootScope', '$location', '$window', function($http, $rootScope, $location, $window){

	var AuthService = {

			error : '',
			session : {},
			credentials : {},

			authenticate : function(callback) {

				//console.log('authenticate credentials: ', AuthService.credentials);

				var headers = AuthService.credentials.username ? {authorization : "Basic "
					+ btoa(AuthService.credentials.username + ":" + AuthService.credentials.password)
				} : {};

				//console.log('authenticate headers: ', headers);

				$http.get('/userDetails', {headers : headers}).then( function(response) {
					if (response.data.name) {
						$rootScope.authenticated = true;
						AuthService.setSession(response);
					} else {
						$rootScope.authenticated = false;
					}
					callback && callback();
				}, function() {
					$rootScope.authenticated = false;
					callback && callback();
				});
			},

			login : function() {
				AuthService.authenticate( function() {
					if ($rootScope.authenticated) {
						AuthService.error = false;
						$window.location.href = '/'
					} else {
						AuthService.error = true;
					}
				});
			},

			logout : function() {
				$http.post('logout', {}).finally(function() {
					$rootScope.authenticated = false;
					$location.path('/'); 
					AuthService.getSession();
				});
			},

			getSession : function(){
				$http.get('/common/userSession').then( 
						AuthService.setSession, 
						function(){
							console.log('userSession from service - fail');
						});
			},

			setSession : function(response){ AuthService.session = response.data;},

			languages : {},

			getLanguages : function(){ 
				$http.get('/common/languages').then(function(response) {
					AuthService.languages = response.data;
				}, function(){
					console.log('languages from service - fail');
				});
			},

			selectLanguage : function(language, callback) {
				$http.get('/common/selectLanguage/'+language).
				then( function(response) {
					AuthService.getSession();
					callback && callback(); //callback for refreshing translator
				});
			},
			
			resetError : function(){
				AuthService.error = false;
			}
	}

//	console.log("auth service in factory: ", AuthService);

	$rootScope.authenticated = false;
	
	var host = $location.host();
	var port = $location.port();
	
	$rootScope.domainSSL = 
		( host == 'localhost' ? 'http://' : 'https://' )
		+ host
		+ ( host == 'localhost' ? ':'+port : '');
	/*
	console.log('location host',host);
	console.log('location port',port);
	console.log('domainSSL',$rootScope.domainSSL);
	*/
	
	AuthService.authenticate();
	AuthService.getLanguages();

	return AuthService;
}]);

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
factory('restservice', ['$http', '$q', function($http, $q){

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
'use strict';

var login = angular
.module('imlogin', 
		[ 
'authenticationService',
'translationService'

] );

login
.config(['$httpProvider', 
         function mainController(  $httpProvider ) {

	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';

}]);

login
.controller( 'login', [ 'authService', '$http', 'translator', '$window', function( authService, $http, translator, $window ) {

	var self = this;

	self.authService = authService;
	self.translator = translator;

	self.loginActive = 1;
	self.registerActive = 0;
	self.forgotActive = 0;

	self.registerErrorSet = function(error){
		console.log('error set');
		self.registerErrorTxt = error;
		self.registerError = true;
	}

	self.registerErrorUnset = function(){
		console.log('error unset');
		self.registerErrorTxt = '';
		self.registerError = false;
	}

	self.resetPassErrorSet = function(username){
		console.log('resetPassErrorSet');
		self.resetPassError = true;
		self.userSent = username;
	}

	self.resetPassErrorUnset = function(){
		console.log('resetPassErrorUnset');
		self.resetPassError = false;
	}

	self.isResetPassError = function(){
		var result = self.resetPassError  //there was error returned
		&&	self.userSent == authService.credentials.username ; //and username was not changed since then
		return result;
	}

	self.sendHash = function(){
		$http
		.put('/forgotpass',authService.credentials.username)
		.then( 
				function(){
					console.log("hash sent");
					self.forgotActive = 0;
					self.hashSent = true;
				},
				function(){
					self.resetPassErrorSet(authService.credentials.username);

					//console.log("user does not exist");
				})

	}

	self.postUser = function(){
		//unset error in UI
		self.registerErrorUnset();

		//first check for errors
		//console.log('in postUser() credentials:: ',authService.credentials);
		if (typeof(authService.credentials.username) == 'undefined' 
			|| typeof(authService.credentials.password) == 'undefined'){
			self.registerErrorSet( translator.label.loginRegisterErrorMessage );
			return;
		}

		$http
		.put('/userexists/0',authService.credentials.username)
		.then( 
				function(){
					self.registerErrorSet( translator.label.loginRegisterErrorUserExists );	
				},

				function(){

					var theUser = {};
					theUser.username = authService.credentials.username;
					theUser.password = authService.credentials.password;
					//console.log('the user::',theUser);
					$http
					.post('/register',theUser)
					.then( 
							function(response){
								$window.location.href = '/'
							},	
							function(errResponse){
								self.registerErrorSet( translator.label.loginRegisterErrorMessage);
								console.error('Error while creating/saving User');
							});
				}
		)
	}
}]);

var toolbox = angular.module('toolbox',[]);

toolbox
.service('growlService', function(){
    var gs = {};
    gs.growl = function(message, type) {
     
        $.growl({
            message: message
        },{
            type: type,
            allow_dismiss: false,
            label: 'Cancel',
            className: 'btn-xs btn-inverse',
            placement: {
                from: 'top',
                align: 'right'
            },
            delay: 1200,
            animate: {
                    enter: 'animated rotateIn',
                    exit: 'animated fadeOutUp'
            },
            offset: {
                x: 20,
                y: 85
            }
        });
    }
    return gs;
});

/*
 * filter for ng-repeat checking if boolean value in row is equal to required
 * 
 * can be used for simple properties like isActive
 * or nested properties like parent.isActive
 * 
 * nesting can be 1 level deep, should be enough for most cases
 * 
 */
toolbox
.filter('booleanValueFilter', [function () {
	return function (input, column, trueOrFalse) {

		//console.log('input::',input);
		//console.log('column::',column);
		//console.log('trueOrFalse::',trueOrFalse);

		var ret = [];

		//if required value is not passed assume true
		if (!angular.isDefined(trueOrFalse)) {
			trueOrFalse = true;
		}

		//check is property name is a simple or nested field
		var columns = column.split('.');
		//console.log('columns::',columns);
		angular.forEach(input, function (v) {
			if (columns.length == 1){
				//if simple field just check if exists and check id equal to required
				//console.log('1 v-column::',v[column]);
				if (angular.isDefined(v[column]) && v[column] === trueOrFalse) {
					ret.push(v);
				}
			} else
				if (columns.length == 2){
					//if nested - check if exists using parts of name as columns
					//and check if value equal to required
					//console.log('2 v-column::',v[columns[0]][columns[1]]);
					if (angular.isDefined(v[columns[0]][columns[1]]) && v[columns[0]][columns[1]] === trueOrFalse) {
						ret.push(v);
					}
				}
		});

		//console.log('ret::',ret);
		return ret;
	};
}])
.directive('onlyDigits', function () {
	return {
		restrict: 'A',
		require: '?ngModel',
		link: function (scope, element, attrs, modelCtrl) {
			modelCtrl.$parsers.push(function (inputValue) {
				if (inputValue == undefined) return '';
				var transformedInput = inputValue.replace(/[^0-9]/g, '');
				if (transformedInput !== inputValue) {
					modelCtrl.$setViewValue(transformedInput);
					modelCtrl.$render();
				}
				return transformedInput;
			});
		}
	};
})
/*
 * from stackoverflow
 * 
 * compiles element content 
 * (ie when you want to show nice html view from editor or database)
 * 
 */
.directive('dynamic', function ($compile) {
  return {
    restrict: 'A',
    replace: true,
    link: function (scope, ele, attrs) {
      scope.$watch(attrs.dynamic, function(html) {
        ele.html(html);
        $compile(ele.contents())(scope);
      });
    }
  };
})

.directive('swalExec', function(){
	return {
		restrict: 'A',
		scope: {

			//parameters for swal dialog
			swalDialogTitle : '@',
			swalMainText : '@',
			swalConfirmButton : '@',
			swalCancelButton : '@',
			swalExecFnOnConfirm : '&',    //will be called with scope.object parameter
			swalObject : '=', //important pass bidirectional - it must be passed back and resolved as an object not string!

			//parameters for growl notification after dialog interaction
			growlFrom : '@',
			growlAlign : '@',
			growlIcon : '@',
			growlAnimationIn : '@',
			growlAnimationOut : '@',
			growlOnConfirmSuccessTitle : '@',
			growlOnConfirmFailureTitle : '@',
			growlOnCancelText : '@',
		},
		link: function(scope, element, attrs) {

			function notify(typeParam, titleParam, msgParam){
				//console.log('in notify',typeParam,titleParam, msgParam);

				$.growl({
					icon: scope.growlIcon,
					title: titleParam,
					message: msgParam,
					url: ''
				},{
					element: 'body',
					type: typeParam,
					allow_dismiss: true,
					placement: {
						from: scope.growlFrom,
						align: scope.growlAlign
					},
					offset: {
						x: 20,
						y: 85
					},
					spacing: 10,
					z_index: 1031,
					delay: 5500,
					timer: 1000,
					url_target: '_blank',
					mouse_over: false,
					animate: {
						enter: typeof scope.growlAnimationIn === 'undefined' ? 'animated rotateIn' : scope.growlAnimationIn,
								exit: typeof scope.growlAnimationOut === 'undefined' ? 'animated fadeOutUp' : scope.growlAnimationOut,
					},
					icon_type: 'class',
					template: '<div data-growl="container" class="alert" role="alert">' +
					'<button type="button" class="close" data-growl="dismiss">' +
					'<span aria-hidden="true">&times;</span>' +
					'<span class="sr-only">Close</span>' +
					'</button>' +
					'<span data-growl="icon"></span>' +
					'<span data-growl="title"></span>' +
					'<span data-growl="message"></span>' +
					'<a href="#" data-growl="url"></a>' +
					'</div>'
				});
			};

			//console.log('obj :: ',scope.execFn);

			element.click('click', function(e) {

				e.preventDefault();

				//console.log('obj :: ',scope);

				swal(
						{ 	title: scope.swalDialogTitle,   
							text: scope.swalMainText, 
							type: "warning",   
							showCancelButton: true,   
							confirmButtonColor: "#DD6B55",   
							confirmButtonText:  scope.swalConfirmButton,  
							cancelButtonText: scope.swalCancelButton,    
							closeOnConfirm: true,   
							closeOnCancel: true 
						}, 
						function(isConfirm){   
							if (isConfirm) {
								scope
								.swalExecFnOnConfirm()(scope.swalObject)
								.then(
										function(result){ 
											//console.log(scope.growlOnConfirmSuccessTitle);
											notify('success',scope.growlOnConfirmSuccessTitle, result); 
										},	
										function(errresult){ 
											//console.log(scope.growlOnConfirmFailureTitle);
											notify('danger',scope.growlOnConfirmFailureTitle, errresult);
										}
								);

							} else {     
								//console.log('in callback fn - cancel :: ',scope.growlOnCancelText); 
								notify('info',scope.growlOnCancelText, "..."); 
							}
						}
				);
			});
		}
	}
});

/*
 * obsolete, replaced by swalExec in the project
 */
toolbox
.factory('dialogFactory', [function(){

	var Service = function(){
		this.dialogOpened = false;
		this.object = {};
		this.classes = 'open';

		this.open = function(object){
			this.object = object;
			this.dialogOpened = true;
		};

		this.close = function(){
			this.object = {};
			this.dialogOpened = false;
		};

		this.getClass = function(){
			if (this.dialogOpened)
				return this.classes;
		};

		this.setClasses = function(c){
			this.classes = c;
		};

	};

	var Factory = {
			getService : function(){ return new Service(); }
	};

	return Factory;

}]);
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

'use strict';

//Register `users` component, along with its associated controller and template
var userApp = angular.module('users', ['translationService','checklist-model','generic-restservice','xeditable']);
userApp.config(['$stateProvider', function( $stateProvider ) {

	$stateProvider
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
}]);

userApp.controller( 'userslist', ['$q', 'translator', 'userService',  'ngTableParams',
                                  function( $q,   translator,   userService,    ngTableParams ) {

	var self = this;
	self.service = userService;
	self.editUserContext = false;

	//console.log('userslist controller starting');

	self.fetchAllUsers = function(){
		//console.log('starting fetching users');
		self.service.fetchAll().then(function(response) {
			//console.log('users fetched '+response.length);
			//console.log(response);
			self.users = response.collection;
		}, function(){
			console.log('get users from service - fail');
		})
	};

	self.fetchAllUsers();

	self.deleteUserPromise = function(obj){ 

		var res = $q.defer();

		self
		.service
		.deleteEntity(obj.id)
		.then( 
				function(response){
					self.fetchAllUsers();
					res.resolve(translator.label.usersdeletesuccessinfo);
				},
				function(errResponse){
					console.error('Error while deleting User');
					res.reject(translator.label.usersdeletefailureinfo);
				}
		);

		return res.promise;

	};

	self.createOrUpdateUser = function(user,id){
		user.id = id;
		self.service.createOrUpdate(user)
		.then( function(response){
			//console.log('User saved');
			self.fetchAllUsers();
			self.unsetEditRowContext();
		},	function(errResponse){
			console.error('Error while creating/saving User');
		});
	}

	self.setEditRowContext = function(){
		self.editContext = true;
	}

	self.cancelEditRow = function(formScope){
		self.unsetEditRowContext();
		formScope['rowform'].$cancel();
	}

	self.unsetEditRowContext = function(){
		self.editContext = false;
	}

	self.checkEmail = function(data) {
		//console.log('email check fn on: ',data);
		if ( !data ) {
			return self.translator.label.edituserhelpname;
		}
		var EMAIL_REGEXP = /^[_a-z0-9]+(\.[_a-z0-9]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$/;
		//console.log(EMAIL_REGEXP.test(data));

		if(! EMAIL_REGEXP.test(data)){
			return self.translator.label.edituserhelpname;
		} 
	};

	//get all roles checkboxes edit
	self.service.fetchAnyData('/allRoles').then(function(response) {
		var roles = response;
		self.allRoles = [];
		angular.forEach(roles, function(value, key) { 
			self.allRoles.push({value: value, text: key});
		});
		//console.log('all roles',self.allRoles);
	});


	//console.log('userslist controller - ending');
}]);

userApp.controller( 'useredit', ['$http', '$stateParams', '$state', 'userService',   function($http, $stateParams, $state, userService ) {
	var self = this;
	self.service = userService;

	var userId = $stateParams.id;
	//fetch user - when adding user get empty user but populated with all roles

	self
	.service
	.fetch(userId)
	.then(
			function(response) {
				self.user = response;
				self.user.enabledPreselected = self.user.enabled;
				self.rolesForToggle = [];
				angular.forEach(self.user.allRoles, function(row) { 
					self.rolesForToggle.push({role: row, enabled : self.user.rolesSelected[row] != null});
				});
				//console.log("toggle",self.rolesForToggle);
				//console.log(self.user);
			}, function(){
				console.log('get user from service - fail');
			});

	self.userExists = false;
	self.userChecked = "";

	self.createOrUpdateUser = function(user){

		var theUser = user;

		$http
		.put('/userexists/'+theUser.id, theUser.username)
		.then( 
				function(){
					self.userExists = true, 	
					self.userChecked = theUser.username;
				},

				function(){
					theUser.rolesSelected = [];
					angular.forEach(self.rolesForToggle, function(row) { 
						if (row.enabled) theUser.rolesSelected.push(row.role);
					});
					//console.log(theUser);
					self.service.createOrUpdate(theUser)
					.then( function(response){
						$state.go('root.users');
					},	function(errResponse){
						console.error('Error while creating/saving User');
					});
				}
		)
	}

}]);

'use strict';
 
angular.
module('users').
factory('userService', ['$http', '$q', 'restservice', function( $http, $q, restservice){
 
    var UserService = restservice.getService();
    UserService.setRestServiceOne('userrest/');
    UserService.setRestServiceAll('userslistrest/');
    return UserService;
 
}]);
'use strict';

//Register `items` component, along with its associated controller and template
var itemApp = angular.module('items', ['translationService','toolbox', 'itemGRs', 'checklist-model','xeditable','ui.select', 'ngTable']);

itemApp.config(['$stateProvider', function( $stateProvider ) {

	$stateProvider
	.state ('root.items', {
		url: '/items',
		templateUrl : 'js/item/itemList.html',
		controller : 'itemslist as itemsCtrl'
	})
	$stateProvider
	.state ('root.itemsByGR', {
		url: '/items/byGameRelease/:gameRelease',
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

}]);

itemApp.controller( 'itemslist', ['$q','$stateParams', '$state', 'translator','itemService',  'ngTableParams', 'growlService', 'itemGRService',
                                  function itemsController( $q, $stateParams, $state, translator,  itemService, ngTableParams, growlService, itemGRService ) {

	var self = this;
	self.service = itemService;
	self.GRservice = itemGRService;
	self.messageService = growlService;
	
	//console.log('$stateParams::',$stateParams);

	self.gameReleaseForFilter = $stateParams.gameRelease ? $stateParams.gameRelease : '';

	//table for items
	self.itemTable = new ngTableParams({
		page: 1,            // show first page
		count: 25,
		sorting: {
			name: 'asc'     // initial sorting
		},
		filter: {
			'gameRelease.name' : self.gameReleaseForFilter // initial filtering
		}

	}, {
		total: 0, 
		getData: function($defer, params) {

			//console.log('page::',params.page());
			//console.log('count::',params.count());
			//console.log('orderBy::',params.orderBy());
			//console.log('filter::',params.filter());

			self
			.service
			.fetchAll(params.page(), params.count(), params.orderBy(), params.filter())
			.then( 
					function(response){
						//console.log("response::",response);
						if (params.total() != 0){ //do not show message at first table data load
							self.messageService.growl(translator.label.ListHasBeenRefreshed, 'info') ;
						}
						params.total(response.totalRows);
						$defer.resolve(response.collection);
					} );
		}
	})
	
	self.resetFilters = function(){
		if (self.gameReleaseForFilter) //we are in itemsByGR context - just go to items
			$state.go('^.items',{});
		else                           //in items context reset filter
			self.itemTable.filter({});
	}

	self.deleteItemPromise = function(item){
		var res = $q.defer();

		self
		.service
		.deleteEntity(item.id)
		.then( 
				function(response){
					self.itemTable.reload(); //reload grid on succesful delete
					res.resolve(translator.label.itemsdeletesuccessinfo);
				},
				function(errResponse){
					console.error('Error while deleting item');
					res.reject(translator.label.itemsdeletefailureinfo);
				});

		return res.promise;
	}

	self.getGRsPromise = getGRs;

	function getGRs(){
		var grs = [];

		var res = $q.defer();

		self
		.GRservice
		.fetchAll()
		.then( 
				function(response){
					angular
					.forEach( 
							response.collection,
							function(gr) {
								grs.push(
										{ 
											id: gr.name,
											title: gr.name
										}
								);								
							}
					);
					res.resolve(grs);
				},
				function(errResponse){
					console.error('Error while getching game releases');
					res.reject('Error while getching game releases');
				});

		return res.promise;
	}


}]);

itemApp.controller( 'itemEdit', ['$q','$state', '$stateParams', '$http', 'itemGRService', 'translator','itemService', 'itemComponentService', 'authService',
                                 function itemsController(  $q, $state,  $stateParams,  $http,  itemGRService, translator, itemService, itemComponentService, authService ) {
	//console.log('itemEdit controller starting');

	var self = this;
	self.service = itemService;
	self.componentService = itemComponentService;
	self.serviceGR = itemGRService;

	self.addItemCtx = false;

	self.translator = translator;

	self.itemId = parseInt($stateParams.id);
	//console.log('itemId param::',self.itemId);

	self.setEditItemContext = function(){
		self.editItemContext = true;
	}

	self.unsetEditItemContext = function(){
		self.editItemContext = false;
	}

	//resolve if it is add item or browse details 
	if (self.itemId == 0){
		self.setEditItemContext();
	} else {
		self.unsetEditItemContext();
	}

	//fetch item - when adding item get empty item but populated predefined fields
	self.fetchItem = function(){
		self.service
		.fetch(self.itemId)
		.then(
				function(response) {
					self.item = response;

					if (self.itemId != 0 && ( self.item.isUsed || self.item.isComposed) ){
						var bgmClass = {};
						bgmClass[self.item.bgmColor] = true;;
						self.item.bgmClass = bgmClass;

						self.service
						.fetchAnyData('/itemrest/associations/'+self.itemId)
						.then( 
								function(response){
									//console.log('item 1::',self.item);
									self.item.components = response.components;
									self.item.usedIn = response.usedIn;
									//console.log('item 2::',self.item);
								},
								function(){
									console.log('get item from service - fail');
								}
						)
					}
				}
		)
	};

	self.fetchItem();

	self.itemExists = false;

	self.createOrUpdateItem = function(){
		$http
		.put('/itemexists/'+self.item.id, self.item.name)
		.then( 
				function(){
					self.itemExists = true, 	
					self.itemChecked = self.item.name;
				},
				function(){
					self.service.createOrUpdate(self.item)
					.then( 
							function(response){
								self.unsetEditItemContext();
								if (self.itemId == 0){
									//console.log('create item::',response);
									//we are in add item context
									//need to route to item details page after succesful adding
									//restservice returns item id !
									$state.go('^.itemDetails',{id:response});
								}
								self.fetchItem();
							},	
							function(errResponse){
								console.error('Error while creating/saving item');
							});
				})
	}

	//	get stuff needed for select in edit forms
	if (authService.session.isSupervisor){
		self
		.service
		.fetchAnyData('/items/forselect/'+self.itemId)
		.then(
				function(response){
					self.itemsForSelect = response;
					//console.log(self.itemsForSelect);
				}, function(){
					console.log('get items for select - fail');
				});

		self
		.serviceGR
		.fetchAll()
		.then(
				function(response){
					self.gameReleases = response.collection; 
				},
				function(errResponse){
					console.error('Error while fetching game releases');
				});
	};

	self.setEditRowContext = function(){
		self.editRowContext = true;
	};

	self.cancelEditRow = function(formScope){
		self.unsetEditRowContext();
		formScope['rowform'].$cancel();
	};

	self.unsetEditRowContext = function(){
		self.editRowContext = false;
	};

	self.unsetEditRowContext();

	self.createOrUpdateItemComponent = function(component){

		//update parent for new components
		component.parent = self.itemId;
		//convert quantity to int type
		component.quantity = parseInt(component.quantity);

		console.log('component ::',component);
		self.componentService.createOrUpdate(component)
		.then( function(response){
			self.fetchItem();
			self.unsetAddComponentCtx();
		},	function(errResponse){
			console.error('Error while creating/saving component');
		});
	};

	self.setAddComponentCtx = function(){
		self.addItemCtx = true;
		self.setEditRowContext();
	};
	self.unsetAddComponentCtx = function(){
		self.addItemCtx = false;
		self.unsetEditRowContext();
	}	;
	self.addItemComponent = function() {
		self.inserted = {};
		self.inserted.id = 0;
		self.inserted.component = {value: "", text: "-------"};
		//self.inserted.component.value = 0;
		self.setAddComponentCtx();
	};
	self.cancelAddComponent	= function(){
		self.inserted = {};
		self.unsetAddComponentCtx();
	};

	self.deleteItemComponentPromise = function(ic){
		var res = $q.defer();

		self
		.componentService
		.deleteEntity(ic.id).
		then( function(response){
			self.fetchItem();
			res.resolve(translator.label.itemsdeletecomponentsuccessinfo);
		},
		function(errResponse){
			console.error('Error while deleting component');
			res.reject(translator.label.itemsdeletecomponentfailureinfo);
		});

		return res.promise;
	};

	//console.log('itemEdit controller - ending');
}]);



'use strict';

angular.
module('items').
factory('itemService', ['restservice', function( restservice){
	
	var ItemService = restservice.getService();
	ItemService.setRestServiceOne('itemrest/');
	ItemService.setRestServiceAll('items');
	//console.log(ItemService);
	return ItemService;

}]).
factory('itemComponentService', ['restservice', function(  restservice){
	
	var ItemComponentService = restservice.getService();
	ItemComponentService.setRestServiceOne('componentrest/');
	//console.log(ItemComponentService);
	return ItemComponentService;

}]);
'use strict';

var itemGRApp = angular.module('itemGRs', ['translationService','toolbox', 'checklist-model','xeditable']);

itemGRApp.config(['$stateProvider', function mainController( $stateProvider ) {

	$stateProvider
	.state ('root.itemGRs', {
		url: '/itemGRs',
		templateUrl : 'js/itemgr/itemGRList.html',
		controller : 'itemGRslist as itemGRsCtrl'
	})
	.state ('root.itemGRadd', {
		url: '/itemGRs/add/:id',
		templateUrl : 'js/itemgr/itemGREdit.html',
		controller : 'itemGRedit as itemGRCtrl'
	})
	.state ('root.itemGRedit', {
		url: '/itemGRs/edit/:id',
		templateUrl : 'js/itemgr/itemGREdit.html',
		controller : 'itemGRedit as itemGRCtrl'
	});

}]);

itemGRApp.controller( 'itemGRslist', ['$q','translator','itemGRService',  'growlService',
                                      function itemGRsController( $q,  translator,  itemGRService, growlService) {

	var self = this;
	var labels = translator;
	self.service = itemGRService;
	self.messageService = growlService;
	self.editGRContext = false;
	
	self.fetchAllGrs = function(){
		self.service.fetchAll().then(function(response) {
			if (self.grs != null){ //do not show message at first table data load
				self.messageService.growl( labels.label.ListHasBeenRefreshed, 'info') ;
			}
			self.grs = response.collection;
			console.log('grs::',self.grs);
		}, function(){
			console.log('get game releases from service - fail');
		})
	};
	self.fetchAllGrs();

	self.deleteGRPromise = function(obj){ 

		var res = $q.defer();

		self
		.service
		.deleteEntity(obj.id)
		.then( 
				function(response){
					self.fetchAllGrs();
					res.resolve("Aktualizacja usuniÄta");
				},
				function(errResponse){
					console.error('Error while deleting GR');
					res.reject("wystÄpiĹ problem i nie udaĹo siÄ usunÄÄ aktualizacji");
				}
		);

		return res.promise;

	};

	self.createOrUpdateGR = function(user,id){
		user.id = id;
		self.service.createOrUpdate(user)
		.then( function(response){
			//console.log('GR saved');
			self.fetchAllGrs();
			self.unsetEditRowContext();
		},	function(errResponse){
			console.error('Error while creating/saving GR');
		});
	}

	self.setEditRowContext = function(){
		self.editContext = true;
	}

	self.cancelEditRow = function(formScope){
		self.unsetEditRowContext();
		formScope['rowform'].$cancel();
	}

	self.unsetEditRowContext = function(){
		self.editContext = false;
	}

}]);

itemGRApp.controller( 'itemGRedit', ['itemGRService', '$state','$stateParams',  
                                     function(itemGRService, $state, $stateParams ) {
	var self = this;
	self.service = itemGRService;
	
	self.itemId = parseInt($stateParams.id);
	console.log('id::',self.itemId);
	
	self.fetchGR = function(){
		self.service
		.fetch(self.itemId)
		.then(
				function(response) {
					self.gr = response;
					if (self.itemID == 0){
						self.gr.id = 0;
					}
					console.log('gr::',self.gr);
				}
				)
	};
	
	self.fetchGR();
	
	self.createOrUpdateGR = function(){

		self
		.service
		.createOrUpdate(self.gr)
		.then( 
				function(response){
					console.log("succeed",response);
					$state.go('^.itemGRedit({id:response})');
				},	function(errResponse){
					console.error('Error while creating/saving GR');
				});
	}
	
	self.setAddStepCtx = function(){
		self.addStepCtx = true;
	}
	self.unsetAddStepCtx = function(){
		self.addStepCtx = false;
	}
	

}]);

'use strict';

angular.
module('itemGRs').
factory('itemGRService', ['restservice', function( restservice){
	
	var ItemGRService = restservice.getService();
	ItemGRService.setRestServiceOne('itemgamereleaserest/');
	ItemGRService.setRestServiceAll('itemgamereleases');
	return ItemGRService;

}]);
'use strict';

//Register `boms` component, along with its associated controller and template
var bomApp = angular.module('boms', ['translationService','toolbox', 'checklist-model','xeditable','ui.select']);

itemApp
.config(['$stateProvider', function mainController( $stateProvider ) {

	//console.log('boms config starting');

	$stateProvider
	.state ('root.boms', {
		url: '/boms',
		templateUrl : 'js/bom/bomList.html',
		controller : 'bomslist as bomsCtrl'
	})
	.state ('root.bomDetails', {
		url: '/boms/details/:id',
		templateUrl : 'js/bom/bomEdit.html',
		controller : 'bomEdit as bomCtrl'
	})
	.state('root.bomWizard', {
		url: '/boms/wizard',
		templateUrl : 'js/bom/bomWizzard.html',
		controller : 'bomWizard as bomCtrl'
	});

	//console.log('boms config ending');
}]);

itemApp
.controller( 'bomslist', ['translator', 'bomService',  'newsService', 'growlService', 
                          function bomsController( translator, bomService, newsService, growlService ) {

	var self = this;
	self.bomSrv = bomService;
	self.newsSrv = newsService;
	self.messageService = growlService;

	self
	.newsSrv
	.fetchByCategoryPublished('bom')
	.then(
			function(response){
				self.news = response;
			});

	self.fetchAllBoms = function(){
		//console.log('starting fetching boms');
		self
		.bomSrv
		.fetchAll()
		.then(
				function(response) {
					self.boms = response.collection;
					//console.log('boms from backend::',self.boms);
					angular.forEach( 
							self.boms, 
							function(bom) {
								//prepare map for ngClass with background color
								var bgmClass = {};
								if (bom.forItem.bgmColor != null){
									bgmClass[bom.forItem.bgmColor] = true;
								} else

									if (bom.forItem.canBeSplit)
										bgmClass['bgm-lime'] = true;
									else
										bgmClass['bgm-gray'] = true;

								bom.bgmClass = bgmClass;

							});
					//console.log('boms processed::',self.boms);
				}, function(){
					console.log('get boms from service - fail');
				})
	};

	self.fetchAllBoms();

	self.deleteBom = function(){
		self.bomSrv.deleteEntity(self.deleteDialog.object.id).
		then( 
				function(response){
					self.fetchAllBoms();
				},
				function(errResponse){
					console.error('Error while deleting item');
				});
	}

}]);

itemApp.controller( 'bomWizard', ['$state',  'translator', 'bomService', 'newsService',
                                  function bomsController( $state,  translator, bomService, newsService ) {

	var self = this;
	self.bomSrv = bomService;
	self.translator = translator;

	self.newsSrv = newsService;

	self
	.newsSrv
	.fetchByCategoryPublished('bomwizard')
	.then(
			function(response){
				self.news = response;
			});

	self.bom = {
			id : 0,
			requiredQuantity : 1
	};

	//get items for select in add bom
	self.bomSrv.fetchAnyData('/items/forselect/0').then(function(response){

		self.itemsForSelect = response;

		//console.log('items for select',self.itemsForSelect);
	}, function(){
		console.log('get items for select - fail');
	})

	self.createOrUpdateBom = function(){

		//console.log('bom::',self.bom);

		self.bomSrv.createOrUpdate(self.bom)
		.then( 
				function(response){
					//console.log('create item::',response);
					$state.go('^.bomDetails',{id:response});
				},	function(errResponse){
					console.error('Error while creating/saving bom');
				});
	}

}]);

itemApp.controller( 'bomEdit', ['$state', '$stateParams','$q', 'translator', 'bomStockService', 'bomService', 'growlService', 
                                function bomsController( $state, $stateParams,  $q, translator, bomStockService, bomService, growlService ) {

	//console.log('bomEdit ctrl starting');

	var self = this;
	self.bomSrv = bomService;
	self.translator = translator;
	self.msgService = growlService;

	self.bomId = parseInt($stateParams.id);

	self.createOrUpdateBomQuantity = function(){

		var bomRequest = {};
		bomRequest.id = self.bom.id;
		bomRequest.requiredQuantity = self.bom.requiredQuantity;

		//console.log('bomRequest::',bomRequest);
		self.bomSrv.createOrUpdate(bomRequest)
		.then( function(response){
			self.fetchBom();
		},	function(errResponse){
			console.error('Error while creating/saving bom');
		});
	};

	self.fetchBom = function(){
		self
		.bomSrv
		.fetch(self.bomId)
		.then(
				function(response) {
					if (self.bom != null) //show growl only when this is not first data load
						self.msgService.growl(translator.label.BomSaved,'success');
					self.bom = response;
					//console.log('fetch bom::',self.bom);
				}
		)
	};

	self.fetchRequirements = function(){
		self
		.bomSrv
		.fetchAnyData('/bomrest/requirements/'+self.bomId)
		.then( 
				function(response){
					self.requirements = response;
					//console.log('requirements::', self.requirements);
				}
		)
	};

	self.saveStock = function(req){
		//console.log("req, line::",req);
		var stock = {
				//id : typeof( req.stockId ) == "undefined" ? 0 : req.stockId,
				bom : { id : self.bomId },
				forItem : { id : req.forItem.id },
				inStockQuantity : req.stock.inStockQuantity,
				remarks : req.stock.remarks
		}
		//console.log('stock :: ',stock);

		self.msgService.growl(translator.label.SavingAndRecalculatingBom,'info');

		bomStockService
		.update(stock,0)
		.then( 
				function(response){
					self.fetchBom();
					//console.log('requirements::', self.requirements);
				}
		)
	};

	self.deleteBomPromise = function(){
		var res = $q.defer();

		self
		.bomSrv
		.deleteEntity(self.bom.id).
		then( function(response){
			$state.go('root.boms');
			res.resolve(': '+self.bom.forItem.name);
		},
		function(errResponse){
			console.error('Error while deleting item');
			res.reject(translator.label.bomEditDeleteFailureHeading);
		});

		return res.promise;

	}


	self.fetchBom();
	//self.fetchRequirements();

	//console.log('bomEdit ctrl ending');

}]);


'use strict';

angular
.module('boms')
.factory('bomService', ['restservice', function( restservice){
	
	var BomService = restservice.getService();
	BomService.setRestServiceOne('bomrest/');
	BomService.setRestServiceAll('boms');
	//console.log(BomService);
	return BomService;

}])
.factory('bomStockService', ['restservice', function( restservice){
	
	var BomStockService = restservice.getService();
	BomStockService.setRestServiceOne('bomstockrest/');
	BomStockService.setRestServiceAll('bomsx'); //notrelevant
	//console.log(BomStockService);
	return BomStockService;

}]);
'use strict';

//Register `newss` component, along with its associated controller 
var newsApp = angular.module('news', ['translationService','toolbox', 'checklist-model','xeditable','ui.select', 'ngTable', 'textAngular']);

newsApp
.config(['$stateProvider', function( $stateProvider ) {

	$stateProvider
	.state ('root.news', {
		url: '/news',
		templateUrl : 'js/news/newsList.html',
		controller : 'newsList as newsCtrl'
	})
	.state ('root.newsDetails', {
		url: '/news/details/:id',
		templateUrl : 'js/news/newsEdit.html',
		controller : 'newsEdit as newsCtrl'
	})
	.state ('root.newsAdd', {
		url: '/news/add/:id',
		templateUrl : 'js/news/newsEdit.html',
		controller : 'newsEdit as newsCtrl'
	});
}]);

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
					var counter = 0;
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
							news.counter = counter;
							counter ++;
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
'use strict';

var logApp = angular.module('logs', ['translationService','toolbox', 'checklist-model','xeditable']);

logApp.config(['$stateProvider', function mainController( $stateProvider ) {

	$stateProvider
	.state ('root.logs', {
		url: '/logs',
		templateUrl : 'js/log/logsList.html',
		controller : 'logslist as logsCtrl'
	});

}]);

logApp.controller( 'logslist', ['$q','translator','logsService', 'ngTableParams', 'growlService',
                                      function logsController( $q,  translator,  logsService, ngTableParams, growlService) {

	var self = this;
	var labels = translator;
	self.service = logsService;
	self.messageService = growlService;
	
	self.logsTable = new ngTableParams({
		page: 1,            // show first page
		count: 50,
		sorting: {
			dateCreated: 'desc'     // initial sorting
		}

	}, {
		total: 0, 
		getData: function($defer, params) {

			//console.log('page::',params.page());
			//console.log('count::',params.count());
			//console.log('orderBy::',params.orderBy());
			console.log('filter::',params.filter());

			self
			.service
			.fetchAll( params.page(), params.count(), params.orderBy(), params.filter() ) 
			.then( function(response){
				console.log("response::",response);
				if (params.total() != 0){ //do not show message at first table data load
					self.messageService.growl(translator.label.ListHasBeenRefreshed, 'info') ;
				}
				params.total(response.totalRows);
				$defer.resolve(response.collection);
			} );
		}
	});
	
	self.setFilter = function(propertyName, propertyValue){
		 var filter = {};
	      filter[propertyName] = propertyValue;
	      angular.extend(self.logsTable.filter(), filter);
	};

}]);

'use strict';

angular.
module('logs').
factory('logsService', ['restservice', function( restservice){
	
	var Logservice = restservice.getService();
	Logservice.setRestServiceAll('sessionlogs');
	return Logservice;

}]);
