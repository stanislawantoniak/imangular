'use strict';

//Register `users` component, along with its associated controller and template
var userApp = angular.module('users', ['translationService','checklist-model','generic-restservice','xeditable']);
userApp.config(function mainController( ) {

	//console.log('users config starting');

	/*
	$routeProvider.when('/users/add/:id', {
		templateUrl : 'js/user/userEdit.html',
		controller : 'useredit',
		controllerAs : 'userCtrl'
	});
	 */
	//console.log('users config ending');
});

userApp.controller( 'userslist', ['$http', '$scope', 'translator', 'userService', 'dialogFactory', 
                                  function( $http,   $scope,   translator,    userService,  dialogFactory ) {

	var self = this;
	self.service = userService;
	self.editUserContext = false;

	self.deleteDialog = dialogFactory.getService();

	//console.log('userslist controller starting');

	self.fetchAllUsers = function(){
		//console.log('starting fetching users');
		self.service.fetchAll().then(function(response) {
			//console.log('users fetched '+response.length);
			self.users = response;
		}, function(){
			console.log('get users from service - fail');
		})
	};

	self.deleteUser = function(){
		self.service.deleteEntity(self.deleteDialog.object.id).
		then( function(response){
			self.fetchAllUsers();
			self.deleteDialog.close();
		},
		function(errResponse){
			console.error('Error while deleting User');
		}
		);
	}

	self.test = function(){ console.log('obj::'); };

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
		console.log('all roles',self.allRoles);
	});

	self.fetchAllUsers();

	//console.log('userslist controller - ending');
}]);

userApp
.directive('swalExec', function(){
	return {
		restrict: 'A',
		scope: {
			object : '@',
			dialogTitle : '@',
			mainText : '@',
			confirmButton : '@',
			cancelButton : '@',
			execFn : '&'          //will be called with scope.object parameter
		},
		link: function(scope, element, attrs) {

			console.log('obj :: ',scope.object);

			var callbackFn = function(isConfirm){   
				if (isConfirm) {
					console.log('obj :: ',scope.object);
					//scope.execFn(scope.object);
					swal("!!!!Deleted! "+scope.object, "Your imaginary file has been deleted.", "success");   
				} else {     
					swal("Cancelled", "Your imaginary file is safe :)", "error");   
				}
			};

			element.click(function(){
				console.log('obj :: ',scope);
				swal({   
					title: scope.dialogTitle,   
					text: scope.mainText, 
					type: "warning",   
					showCancelButton: true,   
					confirmButtonColor: "#DD6B55",   
					confirmButtonText:  scope.confirmButton,  
					cancelButtonText: scope.cancelButton,    
					closeOnConfirm: false,   
					closeOnCancel: false 
				}, 
				callbackFn
				);
			});
		}
	}
})

userApp.controller( 'useredit', ['$http', '$routeParams', '$location', 'userService',   function($http, $routeParams,$location, userService ) {
	var self = this;
	self.service = userService;

	console.log('userEdit controller starting');

	var userId = $routeParams.id;

	//fetch user - when adding user get empty user but populated with all roles
	self.service.fetch(userId).then(function(response) {
		self.user = response;
		self.user.enabledPreselected = self.user.enabled;
		console.log(self.user);
	}, function(){
		console.log('get user from service - fail');
	});

	self.createOrUpdateUser = function(user){
		self.service.createOrUpdate(user)
		.then( function(response){
			$location.path('/users')
		},	function(errResponse){
			console.error('Error while creating/saving User');
		});
	}

	console.log('userEdit controller - ending');
}]);