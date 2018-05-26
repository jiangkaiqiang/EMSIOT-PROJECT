/**
 * Created by SAIHE01 on 2018/4/8.
 */
/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('limitArea', function ($rootScope, $scope, $state, $cookies, $http, $location) {
    //$.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
    //    var user = data;
    //
    //});
	 var limitAreaMap = new BMap.Map("limitAreaMap",{
	   	  minZoom:5,
	   	  maxZoom:30
	   	 });    
		$.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
		    $scope.user = data;
		    if($scope.user == null || $scope.user.user_id == 0 || $scope.user.user_id==undefined){
				url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
				window.location.href = url;
			}
		     // 根据用户的区域权限定位城市，如果为超级管理员暂时定位喀什
			 $http.get('/i/city/findCityNameByUserPower', {
			            params: {
			                "areaID": $scope.user.area_power,
			                "cityID": $scope.user.city_power,
			                "proID": $scope.user.pro_power
			            }
			  }).success(function (data) {
				$scope.cityName = data.name;
				limitAreaMap.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
				limitAreaMap.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
				// 获取基站
				$http.get('/i/station/findAllStationsForMap', {
	                params: {
	                     "proPower" : $scope.user.pro_power,
	        			 "cityPower" : $scope.user.city_power,
	        			 "areaPower" : $scope.user.area_power
	                }
	            }).success(function (data) { 
					$scope.stations = data;
					showStationForAera(limitAreaMap);
				});

			});
	 });
		
		 function showStationForAera(map){
			 var sHtml=`<div id="positionTable" class="shadow">
				   <ul class="flex-between">
				      <li class="flex-items">
				         <img src="app/img/station.png"/>
				         <h4>`;
		    var sHtml2 = `</h4>
				      </li>
				      <li>`;
		    var sHtml3 = `</li>
				   </ul>
				   <p class="flex-items">
				      <i class="glyphicon glyphicon-map-marker"></i>
				      <span>`;
			 
			 var sHtml4= `
				   </p >
				   <ul class="flex flex-time">
				      <li class="active">1分钟</li>
				      <li>5分钟</li>
				      <li>1小时</li>
				   </ul>
				   <hr/>
				   <div class="tableArea margin-top2">
				      <table class="table table-striped " id="tableArea" ng-model="AllElects">
				         <thead>
				            <tr>
				                  <th>序号</th>
				                  <th>车辆编号</th>
				                  <th>经过时间</th>
				               </tr>
				            </thead>
				            <tbody>`;

			 var endHtml = `
				            </tbody>
				         </table>
				      </div>
				   </div>
				`;
			 var pt;
			 var marker2;
			 var sContent = sHtml;
			 var markers = []; //存放聚合的基站
			 //var infoWindow;
			 var tmpStation;
			for (var i = 0; i < $scope.stations.length; i++) {
				tmpStation=$scope.stations[i];
				pt = new BMap.Point(tmpStation.longitude, tmpStation.latitude);
				marker2 = new BMap.Marker(pt);
				marker2.setTitle(tmpStation.station_phy_num+'\t'+tmpStation.station_address);
				console.log($scope.stations.length);

				marker2.addEventListener("click", function(e) {
					var title_add = new Array();
					title_add = this.getTitle().split('\t');
					showElectsInStation(null,null,title_add[0]);  //根据物理编号查找
					var electInfo='';
					for(var k=0; k<$scope.electsInStation.length;k++){
						electInfo += `<tr>
							<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`+(k+1)+`</td>`
							+`<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`+$scope.electsInStation[k].plate_num+`</td>`
							+`<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`+$scope.electsInStation[k].corssTime+`</td></tr>`;
					}
					
					var infoWindow = new BMap.InfoWindow(sHtml+title_add[0]+sHtml2+$scope.electsInStation.length+sHtml3+title_add[1]+sHtml4+electInfo+endHtml);

					var p = e.target;
					var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
					map.openInfoWindow(infoWindow, point);
				});
    	 			map.addOverlay(marker2); 

	    	  }		     
		 }
		 
		 //根据时间和基站id获取基站下面的当前所有车辆
	     function showElectsInStation(startTime,endTime,stationPhyNum){
	    	 $http.get('/i/elect/findElectsByStationIdAndTime', {
	 			params : {
	 				"startTime" : startTime,
	 				"endTime" : endTime,
	 				"stationPhyNum" : stationPhyNum
	 			}
	 		}).success(function(data) {
	 			$scope.electsInStation = data.slice(2,8);
	 			//console.log($scope.electsInStation);
	 		});
		 }
	     
		 //鼠标绘制多边形，选择区域并弹出信息框，展示显示的基站
		    var overlaysDraw = [];
			var overlaycomplete = function(e){
		      		console.log(e.overlay.getPath());  //多边形轨迹
		      		
		      		overlaysDraw.push(e.overlay);
			};
		    var styleOptions = {
		            strokeColor:"blue",    //边线颜色。
		            fillColor:"red",      //填充颜色。当参数为空时，圆形将没有填充效果。
		            strokeWeight: 3,       //边线的宽度，以像素为单位。
		            strokeOpacity: 0.8,	   //边线透明度，取值范围0 - 1。
		            fillOpacity: 0.6,      //填充的透明度，取值范围0 - 1。
		            strokeStyle: 'solid' //边线的样式，solid或dashed。
		        }
		    //实例化鼠标绘制工具
		    var drawingManager = new BMapLib.DrawingManager(limitAreaMap, {
		        isOpen: false, //是否开启绘制模式
		        enableDrawingTool: true, //是否显示工具栏
		        drawingToolOptions: {
		            anchor: BMAP_ANCHOR_TOP_RIGHT, //位置
		            offset: new BMap.Size(5, 5), //偏离值
		          drawingModes : [
		            BMAP_DRAWING_POLYGON,
		         ]
		        },
		        polygonOptions: styleOptions //多边形的样式
		    }); 
		    drawingManager.addEventListener('overlaycomplete', overlaycomplete);
		    function clearAll() {
				for(var i = 0; i < overlaysDraw.length; i++){
					limitAreaMap.removeOverlay(overlaysDraw[i]);
		        }
				overlaysDraw.length = 0   
		    }
		  




});
