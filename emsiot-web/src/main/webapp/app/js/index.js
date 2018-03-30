coldWeb.controller('index', function ($scope, $state, $cookies, $http, $location) {
	   $scope.goMenu = function (name,url) {
	   		window.location.href=url;
	   	};
});
