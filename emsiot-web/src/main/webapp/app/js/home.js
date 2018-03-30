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
			        var map = new BMap.Map("allmap",{
			        	  minZoom:5,
			        	  maxZoom:13
			        	 });    // 创建Map实例
			        	 map.centerAndZoom($scope.cityName, 11);  // 初始化地图,设置中心点坐标和地图级别
			        	 //map.setCurrentCity("喀什");          // 设置地图显示的城市 此项是必须设置的
			        	 map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
			        	 //map.setMapStyle({style:'dark'})
			        	 map.disableDoubleClickZoom()
			        // 获取基站
			   	    $http.get('/i/station/findAllStations').success(function (data) {
			   	        $scope.stations = data;
			   	        console.log(data[0]);
			   	        
			   	        showStation();
			   	    });
			        
			        	 function showStation(){
			        	  var latlngs=[
			        	   [75.979,39.475],
			        	   [76.0038210511,39.5189055971],
			        	   [75.8222930000,39.5087320000],
			        	   [75.9844190000,39.3718700000]
			        	  ];
			        	  var markers = [];
			        	  for(var i=0;i<900;i++){
			        	   var pt = new BMap.Point($scope.stations[i].longitude,$scope.stations[i].latitude);
			        	   var myIcon = new BMap.Icon("../app/img/station.jpg", new BMap.Size(43,50));
			        	   var marker2 = new BMap.Marker(pt,{icon:myIcon}); 
			        	   
			        	   var infoWindow = new BMap.InfoWindow(sContent);
			        	   var sContent ="<h4 style='margin:0 0 5px 0;padding:0.2em 0'>显示的是一个基站！</h4>";
			        	   var infoWindow = new BMap.InfoWindow(sContent); 
			        	   
			        	   marker2.addEventListener("click", function(){          
			        	        this.openInfoWindow(infoWindow);
			        	         //图片加载完毕重绘infowindow
			        	         document.getElementById('imgDemo').onload = function (){
			        	          infoWindow.redraw();   //防止在网速较慢，图片未加载时，生成的信息框高度比图片的总高度小，导致图片部分被隐藏
			        	         }
			        	   });
			        	   map.addOverlay(marker2); 
			        	  // markers.push(marker2);
			        	  }
			        	  //var markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});
			        	 }
			        	 map.addEventListener("dblclick",function(e){
			        	  var pt = new BMap.Point(e.point.lng,e.point.lat);
			        	   var myIcon = new BMap.Icon("../app/img/station.jpg", new BMap.Size(43,50));
			        	   var marker = new BMap.Marker(pt,{icon:myIcon}); 
			        	   map.addOverlay(marker); 
			        	   
			        	   alert(e.point.lng + "," + e.point.lat);
			        	 });
			 });
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
