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
