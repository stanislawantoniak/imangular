'use strict';
var transl = angular.module('translationService', []);

transl.factory('translator', ['$q','$http', function($q,$http){

	var Translator = {

			label: {},

			refresh : function(){

				$http.get('/common/labels').then( function(response) {

					Translator.label = response.data;
					
					console.log('translator refresh service');
					console.log(Translator.label);

				} , function(){

					console.log('no data available on service /common/labels');

				} );

				return Translator.label; 
				
			}
	};
	
	Translator.refresh();
	
	return Translator;

}]);