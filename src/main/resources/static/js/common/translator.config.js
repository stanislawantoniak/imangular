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

				$http.get('/common/labels').then( function(response) {

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