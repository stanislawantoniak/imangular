var toolbox = angular.module('toolbox',[]);

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

toolbox
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
});

toolbox
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
						enter: scope.growlAnimationIn,
						exit: scope.growlAnimationOut
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
