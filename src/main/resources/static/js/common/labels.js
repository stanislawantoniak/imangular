angular.
module('labels', {}).
controller('labels', function($http) {
	var self = this;
	$http.get('/common/labels').then(function(response) {
		self.index = response.data;
	})
})