var toolbox = angular.module('toolbox',[]);

toolbox.factory('dialogFactory', [function(){

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


toolbox.directive('onlyDigits', function () {
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
