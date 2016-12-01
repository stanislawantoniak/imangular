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
			self.grs = [];
			angular.forEach( 
					response.collection, 
					function(row){
						if (row.releaseDate != null)
							row.releaseDate = new Date(row.releaseDate);
						this.push(row)
					}, 
					self.grs
			);
			//console.log('grs::',self.grs);
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
					res.resolve("Aktualizacja usunięta");
				},
				function(errResponse){
					console.error('Error while deleting GR');
					res.reject("wystąpił problem i nie udało się usunąć aktualizacji");
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

itemGRApp.controller( 'itemGRedit', ['itemGRService', '$scope', '$state','$stateParams', '$filter', '$http', 'growlService', 'translator', 'fileReader',
                                     function(itemGRService, $scope, $state, $stateParams, $filter, $http, growlService, translator,fileReader ) {
	var self = this;
	self.service = itemGRService;
	self.messageService = growlService;

	self.itemId = parseInt($stateParams.id);
	self.editCtx = false;
	self.editStepCtx = false;
	self.addStepCtx = false;
	self.addImageCtx = false;

	$scope.getFile = function (file) {
		fileReader.readAsDataUrl(file, $scope)
		.then(
				function(result) {
					self.imageFileName = file;
					self.imageSrc = result;
				}
		);
	};

	//console.log('id::',self.itemId);

	self.fetchGR = function(){
		self.gr = {};
		self.service
		.fetch(self.itemId)
		.then(
				function(response) {
					self.gr = response;
					if (self.itemID == 0){
						self.gr.id = 0;
					} else {
						if (self.gr.releaseDate !=  null)
							self.gr.releaseDate = new Date(self.gr.releaseDate);

						//deserialize json in steps lines
						angular.forEach( 
								self.gr.steps, 
								function(step){
									step.linesArray = step.lines ? angular.fromJson(step.lines) : [{id: 1, text: ''}]; 
								} )

					}
					//console.log('gr fetch::',self.gr);
				}
		)
	};

	self.fetchGR();

	self.createOrUpdateGR = function(){

		//serialize json in steps lines
		angular.forEach( 
				self.gr.steps, 
				function(step){
					step.lines = step.linesArray ? angular.toJson(step.linesArray) : null; 
					delete step.linesArray;
				} 
		);

		//console.log('gr update::',self.gr);

		self
		.service
		.createOrUpdate(self.gr)
		.then( 
				function(response){
					//console.log('succeed',response);
					if ( self.itemId == 0){
						self.itemId = parseInt(response);
						$state.go('^.itemGRedit',{id: self.itemId});
					} else {
						self.fetchGR();
					}
				},	function(errResponse){
					console.error('Error while creating/saving GR');
				});
	}

	self.setAddStepCtx = function(){

		self.inserted = { };

		var len = self.gr.steps.length;
		if (len > 0){
			var stepsSorted = $filter('orderBy')(self.gr.steps, 'seq');
			self.inserted.seq = stepsSorted[len-1].seq + 10;
		}
		else 
			self.inserted.seq = 10;

		self.inserted.linesArray = [{id: 1, text: ''}];

		self.addStepCtx = true;
	}
	self.unsetAddStepCtx = function(){
		self.addStepCtx = false;
	}
	self.setEditCtx = function(){
		self.editCtx = true;
	}
	if (self.itemId == 0)
		self.setEditCtx();

	self.unsetEditCtx = function(){
		self.editCtx = false;
	}

	self.cancelEdit = function(){
		if (self.itemId == 0)
			$state.go('^.itemGRs');
		else 
			self.unsetEditCtx();
	}

	self.addStep = function(){
		self.gr.steps.push(self.inserted);
		//console.log('add step gr::',self.gr);
		self.createOrUpdateGR();
		self.unsetAddStepCtx();
	}

	self.deleteStep = function( step ){
		$http
		.delete('/gamereleasesteprest/'+step.id)
		.then( 
				function(response){
					self.fetchGR();
					self.messageService.growl(translator.label.itemGRStepDeletedInfo, 'info');
				}
		)
	}

	self.setEditStepCtx = function(step){
		step.editCtx = true;
		self.editStepCtx = true;
	}

	self.unsetEditStepCtx = function(step){
		delete step.editCtx;
		self.editStepCtx = false;
		self.fetchGR(); //refresh model in case it was edited
	}

	self.saveStep = function(step){
		delete step.editCtx;
		self.createOrUpdateGR();
		self.editStepCtx = false;
	}

	self.setAddImageCtx = function(step){
		step.addImageCtx = true;
		self.editStepCtx = true;
	}

	self.unsetAddImageCtx = function(step){
		delete step.addImageCtx;
		delete self.imageSrc;
		self.editStepCtx = false;
	}

	var b64EncodeUnicode = function(str) {
	    return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
	        return String.fromCharCode('0x' + p1);
	    }));
	}
	self.saveImage = function(step){
		//console.log('file ::',self.imageFileName);
		
		fileReader
		.readAsDataUrl(self.imageFileName, $scope)
		.then(

				function(result) {
					var res = result.substr(result.indexOf(',')+1);
					//console.log('res::',res);
					$http
					.post('/gamereleasesteprest/fileupload/'+step.id, res)
					.then( 
							function(response){
								delete step.addImageCtx;
								delete self.imageSrc;
								self.editStepCtx = false;
								self.fetchGR();
							}
					)
				}
		)
	}

	self.isEditCtx = function(){
		return self.editCtx || self.editStepCtx || self.addStepCtx || self.addImageCtx;
	}

	self.addLine = function(step){
		step.linesArray.push({id: step.linesArray.length + 1, text: ''});
	}

}]);
