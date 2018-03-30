coldWeb.controller('home', function ($rootScope, $scope, $state, $cookies, $http, $location) {
	 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
		    $scope.user = data;
		     // 根据用户的区域权限定位城市，如果为超级管理员暂时定位喀什
			 $http.get('/i/city/findCityNameByAreaID', {
			            params: {
			                "AreaID": $scope.user.area_power
			            }
			  }).success(function (data) {
			        $scope.cityName = data.name;
			        alert($scope.cityName);
			 });
	 });	 
	 // 获取基站
	 $http.get('/i/station/findAllStations').success(function (data) {
	        $scope.stations = data;
	 });
	 //根据车牌号定位车辆
	 $scope.findElecLocation = function(keyword, type) {
		if (type == 1) {
			$http.get('/i/electrombile/findElecLocationByPlateNum', {
				params : {
					"PlateNum" : PlateNum
				}
			}).success(function(data) {
				$scope.longitude = data.longitude;
				$scope.latitude = data.latitude;
				alert($scope.longitude);
			});
		}
	 }
	 $scope.goHome=function(){
		 $state.reload();
	 }
	 
});
