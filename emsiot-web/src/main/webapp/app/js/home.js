coldWeb.controller('home', function ($rootScope, $scope, $state, $cookies, $http, $location) {
	 var map = new BMap.Map("allmap",{
   	  minZoom:5,
   	  maxZoom:30
   	 });    // 创建Map实例
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
			       
			        	 map.centerAndZoom($scope.cityName, 10);  // 初始化地图,设置中心点坐标和地图级别
			        	 map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
			        	 map.disableDoubleClickZoom();
			        // 获取基站
			   	    $http.get('/i/station/findAllStationsForMap').success(function (data) {
			   	        $scope.stations = data;
			   	        showStation();
			   	    });
			        	 
			 });
	 });
	 function showStation(){
		 
		 var opts = {
					width : 250,     // 信息窗口宽度
					height: 80,     // 信息窗口高度
					title : "信息窗口" , // 信息窗口标题
					enableMessage:true//设置允许信息窗发送短息
				   };
		 var pt;
		 var marker2;
		 var sContent ="<h4>显示的是一个基站！</h4>";
		 var markers = [];
	     for(var i=0;i<100;i++){
	    	 	pt = new BMap.Point($scope.stations[i].longitude,$scope.stations[i].latitude);
	    	 	marker2 = new BMap.Marker(pt); 
    	   
	    	 	var infoWindow = new BMap.InfoWindow(sContent,opts); 				        	   
	    	 	marker2.addEventListener("click", function(e){  
	    	 		var p = e.target;
	    			var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
	    	 		map.openInfoWindow(infoWindow,point);
    	   });
    	   //map.addOverlay(marker2); 
    	   markers.push(marker2);
    	  }
    	  var markerClusterer = new BMapLib.MarkerClusterer(map, {markers:markers});
	 }
	 
	 function openInfo(){
		 
	 }
	 
	 
	 //
	 $scope.AllKeywordType = [
	    {id:"0",name:"防盗芯片ID"},
	    {id:"1",name:"车牌号"}
	 ];
	 $scope.keywordTypeForLocation = "0";
	 $scope.keywordTypeForTrace = "0";
	 //清除定位
	 var stationIDforDelete;
	 var elecMarker;
	 $scope.clearElectLocation = function() {
		// map.clearOverlays()
		 elecMarker.hide();
		 $("#dingweiModal").modal("hide");

		 
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
				
				stationIDforDelete=data.station_phy_num;
				var elecPt = new BMap.Point($scope.longitude,$scope.latitude);
       		 	var elecIcon = new BMap.Icon("../app/img/eb.jpg", new BMap.Size(67,51));
       		 	elecMarker = new BMap.Marker(elecPt,{icon:elecIcon}); 
       		 	
       		 	map.addOverlay(elecMarker); 
       		 	map.centerAndZoom(elecPt, 17);
       		 	//console.log(elecMarker);
			});
		 $("#dingweiModal").modal("hide");
	 }
	 //根据条件查询车辆轨迹
	 var walking;
	 function drawLine(p1,p2){
		//walking = new BMap.WalkingRoute(map, {renderOptions:{map: map, autoViewport: true}});
		walking.search(p1, p2);
	 }
	 
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
					if(data.length == 1)
						alert("该车辆仅经过一个基站："+data[0].station.station_name);
					else if(data.length == 0)
						alert("该车辆没有经过任何基站！");
					else
						for(var i = 0;i<data.length-1;i++){
							walking = new BMap.WalkingRoute(map, {renderOptions:{map: map, autoViewport: true}});

							var p1 = new BMap.Point(data[i].station.longitude,data[i].station.latitude);
							var p2 = new BMap.Point(data[i+1].station.longitude,data[i+1].station.latitude);
							drawLine(p1,p2);
						}
					
				});
			$("#guijiModal").modal("hide");
	 }
	 $scope.clearElectTrace = function(){
		 //walking.clearResults();
		 $("#guijiModal").modal("hide");
		 map.clearOverlays();
		 showStation();
		 
		 
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
