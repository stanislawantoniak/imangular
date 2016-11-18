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
		 'boms',
		 'news',
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


