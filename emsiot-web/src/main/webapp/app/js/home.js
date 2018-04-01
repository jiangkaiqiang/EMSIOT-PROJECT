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
			        	  maxZoom:15
			        	 });    // 创建Map实例
			        	 map.centerAndZoom($scope.cityName, 10);  // 初始化地图,设置中心点坐标和地图级别
			        	 map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
			        	 //map.setMapStyle({style:'dark'})
			        	 map.disableDoubleClickZoom();
			        // 获取基站
			   	    $http.get('/i/station/findAllStations').success(function (data) {
			   	        $scope.stations = data;
			   	        
			   	        showStation();
			   	    });
			        	 function showStation(){
			        		 var pt;
			        		 var marker2;
			        		 var stationIcon = new BMap.Icon("../app/img/station.jpg", new BMap.Size(43,50));
			        		 var sContent ="<h4 style='margin:0 0 5px 0;padding:0.2em 0'>显示的是一个基站！</h4>";

			        		 var markers = [];
			        	  for(var i=0;i<100;i++){
			        		  console.log($scope.stations[i].longitude+","+$scope.stations[i].latitude);
				        	   pt = new BMap.Point($scope.stations[i].longitude,$scope.stations[i].latitude);
				        	   marker2 = new BMap.Marker(pt,{icon:stationIcon}); 
				        	   var infoWindow = new BMap.InfoWindow(sContent); 
				        	   
				        	   marker2.addEventListener("click", function(){          
				        	        this.openInfoWindow(infoWindow);
				        	         //图片加载完毕重绘infowindow
				        	        // document.getElementById('imgDemo').onload = function (){
				        	        //  infoWindow.redraw();   //防止在网速较慢，图片未加载时，生成的信息框高度比图片的总高度小，导致图片部分被隐藏
				        	        // }
				        	   });
				        	   //map.addOverlay(marker2); 
				        	   markers.push(marker2);
				        	  }
				        	  var markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});
			        	 }
			        	 // 添加两个基站之间的连线，两个基站之间的流量越多，则对应的连线越粗
			        	 
			        	 var p1 = new BMap.Point(75.96629999999999,39.4505);
			        	 var p2 = new BMap.Point(75.97529999999999,39.4645);
			        	 var p3 = new BMap.Point(76.0153,39.5005);
			        	 var p4 = new BMap.Point( 75.9863,39.4435);
			        	
			        	 
			        	 
			        	 var line1 = [p1,p2];
			        	 var line2 = [p2,p3];
			        	 var line3 = [p3,p4];
			        	 
			        	 var beijingPosition=new BMap.Point(116.432045,39.910683),
		        			hangzhouPosition=new BMap.Point(120.129721,30.314429),
		        			taiwanPosition=new BMap.Point(121.491121,25.127053);
			        	 var points = [beijingPosition,hangzhouPosition, taiwanPosition];
			        	 drawLine(line1,10);
			        	 drawLine(line2,3);
			        	 drawLine(line3,7);
			        	 
			        	 
			        	 function drawLine(points, weight){
			        		 console.log(points);

			        		var curve = new BMapLib.CurveLine(points, {strokeColor:"blue", strokeWeight:weight, strokeOpacity:0.5}); //创建弧线对象
			        		map.addOverlay(curve); //添加到地图中
			        		curve.enableEditing(); //开启编辑功能
			        	 }
			        	 
			        	 
			        	 
			        	 
			        	 //通过双击地图添加基站，弹出窗口
			        	 var stationInfo = "<div style=\"width:300px;height:400px;\">请输入要添加的基站信息：</div>";
			        	 var stationWin = new BMap.InfoWindow(stationInfo); 
			        	 map.addEventListener("dblclick",function(e){
			        	 var currentpt = new BMap.Point(e.point.lng,e.point.lat);
			        	 var myIcon = new BMap.Icon("../app/img/station.jpg", new BMap.Size(43,50));
			        	 var marker = new BMap.Marker(currentpt,{icon:myIcon}); 
			        	 marker.addEventListener("click", function(){          
			        	        this.openInfoWindow(stationWin);
			        	  });
			        	  map.addOverlay(marker); 			        	   
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
