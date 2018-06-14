/**
 * Created by SAIHE01 on 2018/4/8.
 */
/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('limitArea', function ($rootScope, $scope, $state, $cookies, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
		   });	 
		 
	};
	$scope.load();
	// 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
    $scope.AllLimitAreas=[];
    $scope.getLimitAreaByOptions = function() {
		$http({
			method : 'POST',
			url : '/i/specialArea/findLimitAreaByOptions',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				limitAreaName: $scope.LimitAreaName,
				limitAreaID:$scope.limitAreaID
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.AllLimitAreas = data.list;
		});
	}
    $scope.getLimitAreaByOptions();
    $scope.goSearch = function () {
  		$scope.getLimitAreaByOptions();
    }
    $scope.pageChanged = function() {
  	  $scope.getLimitAreaByOptions();
   }
    function delcfm() {
        if (!confirm("确认要删除？")) {
            return false;
        }
        return true;
  }
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
					$scope.addStationNames = data;
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
		    //var borderPoints;
			var overlaycomplete = function(e){
				 $scope.borderPoints=e.overlay.getPath();//多边形轨迹数据点
	      		console.log(e.overlay.getPath());  //多边形轨迹数据点
//	      		$http.post('/path',borderPoints).success(function(data){
//	      			
//	      		});
	      		
	      		
	            e.overlay.addEventListener("click", function(){ 
	            	 $http({
				  			method : 'POST',
				  			url : '/i/specialArea/findStations',
				  			params : {
				  				area_power:$scope.user.area_power,
				  				city_power:$scope.user.city_power,
				  				pro_power: $scope.user.pro_power,
				  				borderPoints:$scope.borderPoints
				  			}}).success(function (data) {
				  				$scope.addStationNames=data;
				  				 $("#addLimitArea").modal("show"); 
				  			})
	            		
	            				        	  });		      		
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
		  //添加限制区域
		    function checkInputInfo(){
		        var flag = true;
		        // 检查必须填写项
		        if ($scope.addLimitAreaName == undefined || $scope.addLimitAreaName == '') {
		            flag = false;
		        }
		        if ($scope.addStationNames == undefined || $scope.addStationNames == '') {
		            flag = false;
		        }
		        return flag;
		    }
		   $scope.addLimitArea = function(){
			   console.log($scope.borderPoints);
			   if(checkInputInfo()){
				   $http({
			  			method : 'POST',
			  			url : '/i/specialArea/addSpecialArea',
			  			params : {
			  				addLimitAreaName: $scope.addLimitAreaName,
			  				addStationNames: $scope.addStationNames,
			  				addBlackelectPlatenum: $scope.addBlackelectPlatenum,
			  			}}).success(function (data) {
			  				alert(data.message);
			  				$scope.getLimitAreaByOptions();
			  				 $("#addLimitArea").modal("hide"); 
			  			})
			   }else{
				   alert("限制区域名和基站ID不能为空！")
			   }
		   }
		   $scope.selected = [];
		   $scope.exists = function (LimitArea, list) {
		    	return list.indexOf(LimitArea) > -1;
		    };
		    $scope.toggle = function (LimitArea, list) {
				  var idx = list.indexOf(LimitArea);
				  if (idx > -1) {
				    list.splice(idx, 1);
				  }
				  else {
				    list.push(LimitArea);
				  }
		  };
		   $scope.isChecked = function() {
			      return $scope.selected.length === $scope.AllLimitAreas.length;
			  };
		  $scope.toggleAll = function() {
		      if ($scope.selected.length === $scope.AllLimitAreas.length) {
		      	$scope.selected = [];
		      } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
		      	$scope.selected = $scope.AllLimitAreas.slice(0);
		      }
		  };
		  $scope.goDeleteLimitArea=function(limitAreaID){
			  if(delcfm()){
				  	$http.get('/i/specialArea/deleteLimitAreaByID', {
				          params: {
				              "limitAreaID": limitAreaID,
				          }
				      }).success(function (data) {
				    	  $scope.getLimitAreaByOptions();
				      });
				  	}
		  }
		  $scope.goDeleteLimitAreas = function(){
			  	if(delcfm()){
			  	var limitAreaIDs = [];
			  	for(i in $scope.selected){
			  		limitAreaIDs.push($scope.selected[i].limit_area_id);
			  	}
			  	if(limitAreaIDs.length >0 ){
			  		$http({
			  			method:'DELETE',
			  			url:'/i/specialArea/deleteLimitAreaByIDs',
			  			params:{
			  				"limitAreaIDs": limitAreaIDs,
			  			}
			  		}).success(function (data) {
			  			 $scope.getLimitAreaByOptions();
			          });
			  	}
			  	}
			  }

	//----------------表格收缩功能-----------------

	$(".closeStationPosition").click(function(){
		$(this).parents('.limitArea-content').toggleClass("leftToggle");
		if($(this).hasClass("glyphicon-chevron-left")){
			$(this).removeClass("glyphicon-chevron-left").addClass("glyphicon-chevron-right")
		}else{
			$(this).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-left");
		}
	});

});
