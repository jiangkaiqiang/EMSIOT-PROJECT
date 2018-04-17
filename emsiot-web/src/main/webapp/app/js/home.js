coldWeb.controller('home', function ($rootScope, $scope, $state, $cookies, $http, $location) {
	 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
		    $scope.user = data;
		    if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
				url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
				window.location.href = url;
			}
		     // 根据用户的区域权限定位城市，如果为超级管理员暂时定位喀什
			 $http.get('/i/city/findCityNameByAreaID', {
			            params: {
			                "AreaID": $scope.user.area_power
			            }
			  }).success(function (data) {
			        $scope.cityName = data.name;
			        var map = new BMap.Map("allmap",{
			        	  minZoom:5,
			        	  maxZoom:30
			        	 });    // 创建Map实例
			        	 map.centerAndZoom($scope.cityName, 10);  // 初始化地图,设置中心点坐标和地图级别
			        	 map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
			        	 //map.setMapStyle({style:'dark'})
			        	 map.disableDoubleClickZoom();
			        // 获取基站
			   	    $http.get('/i/station/findAllStationsForMap').success(function (data) {
			   	        $scope.stations = data;
			   	        showStation();
			   	    });
			        	 function showStation(){
			        		 var pt;
			        		 var marker2;
			        		 //var stationIcon = new BMap.Icon("../app/img/station.jpg", new BMap.Size(43,50));
			        		 var sContent ="<h4 style='margin:0 0 5px 0;padding:0.2em 0'>显示的是一个基站！</h4>";
			        		 var markers = [];
			        	     for(var i=0;i<100;i++){
			        		  //console.log($scope.stations[i].longitude+","+$scope.stations[i].latitude);
				        	   pt = new BMap.Point($scope.stations[i].longitude,$scope.stations[i].latitude);
				        	  // marker2 = new BMap.Marker(pt,{icon:stationIcon}); 
				        	   marker2 = new BMap.Marker(pt); 
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
			        	 
			        	 //var points = [beijingPosition,hangzhouPosition, taiwanPosition];
			        	 drawLine(p1,p2);
			        	 drawLine(p2,p3);
			        	 //drawLine(line3);
			        	 
			        	 function drawLine(p1,p2){
			        		 //var driving = new BMap.DrivingRoute(map, {renderOptions:{map: map, autoViewport: true}});
			        		// driving.search(p1, p2);
			        		 var walking = new BMap.WalkingRoute(map, {renderOptions:{map: map, autoViewport: true}});
			        			walking.search(p1, p2);
			        		//var curve = new BMapLib.CurveLine(points, {strokeColor:"blue", strokeWeight:weight, strokeOpacity:0.5}); //创建弧线对象
			        		//map.addOverlay(curve); //添加到地图中
			        		//curve.enableEditing(); //开启编辑功能
			        	 }
			 			        	 
			 });
	 });	
	 //
	 $scope.AllKeywordType = [
	    {id:"0",name:"防盗芯片ID"},
	    {id:"1",name:"车牌号"}
	 ];
	 $scope.keywordTypeForLocation = "0";
	 $scope.keywordTypeForTrace = "0";
	 //清除定位
	 $scope.clearElectLocation = function() {
		 
	 }
	 //根据条件定位车辆
	 $scope.findElectLocation = function() {
		if ($scope.keywordTypeForLocation == "1") {	
			$scope.plateNum = $scope.keywordForLocation;
		}
		else if($scope.keywordTypeForLocation == "0"){
			$scope.guaCardNum = $scope.keywordForLocation;
		}
		else{
			
		}
		$http.get('/i/elect/findElectLocation', {
				params : {
					"plateNum" : $scope.plateNum,
					"guaCardNum" : $scope.guaCardNum
				}
			}).success(function(data) {
				$scope.longitude = data.longitude;
				$scope.latitude = data.latitude;	
				alert($scope.longitude);
			});
	 }
	 //根据条件查询车辆轨迹
	 $scope.findElectTrace = function() {
		 if ($scope.keywordTypeForTrace == "1") {	
				$scope.plateNum = $scope.keywordForTrace;
			}
			else if($scope.keywordTypeForTrace == "0"){
				$scope.guaCardNum = $scope.keywordForTrace;
			}
			else{
				
			}
			$http.get('/i/elect/findElectTrace', {
					params : {
						"plateNum" : $scope.plateNum,
						"guaCardNum" : $scope.guaCardNum,
						"startTimeForTrace" : $scope.startTimeForTrace,
						"endTimeForTrace" : $scope.endTimeForTrace
					}
				}).success(function(data) {
					$scope.traceStations = data;
					alert($scope.traceStations);
				});
	 }
	 $scope.goHome=function(){
		 $state.reload();
	 }
	 var navBtn=$(".home-title a")
		//console.log(navBtn);
		navBtn.click(function(){
		   navBtn.removeClass("active");
		   $(this).addClass("active");
		   console.log("点击了一次");

		});
});
