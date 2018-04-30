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
			        	 //map.setMapStyle({style:'light'})
			        	 map.disableDoubleClickZoom();
			        // 获取基站
			   	    $http.get('/i/station/findAllStationsForMap').success(function (data) {
			   	        $scope.stations = data;
			   	        showStation();
			   	    });
			        	 
			 });
	 });
	 function G(id) {
		    return document.getElementById(id);
	 }
	 var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
			    {
			        "input": "suggestId"
			        , "location": map
			    });

			ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
			    var str = "";
			    var _value = e.fromitem.value;
			    var value = "";
			    if (e.fromitem.index > -1) {
			        value = _value.province + _value.city + _value.district + _value.street + _value.business;
			    }
			    str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

			    value = "";
			    if (e.toitem.index > -1) {
			        _value = e.toitem.value;
			        value = _value.province + _value.city + _value.district + _value.street + _value.business;
			    }
			    str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
			    G("searchResultPanel").innerHTML = str;
			});

			var myValue;
			ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
			    var _value = e.item.value;
			    myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
			    G("searchResultPanel").innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
			    setPlace();
			});

			function setPlace() {
			    map.clearOverlays();    //清除地图上
			    function myFun() {
			        var pp = local.getResults().getPoi(0).point;//获取第一个智能搜索信息
			        map.centerAndZoom(pp, 18);
			        map.addOverlay(new BMap.Marker(pp));

			    }

			    var local = new BMap.LocalSearch(map, {
			        onSearchComplete: myFun
			    });
			    local.search(myValue);
			}

	 function showStation(){
		 var sHtml=`
			   <div id="positionTable" class="shadow">
			   <ul class="flex-between">
			      <li class="flex-items">
			         <img src="app/img/station.png"/>
			         <h4>基站SHKF124589</h4>
			      </li>
			      <li>21辆</li>
			   </ul>
			   <p class="flex-items">
			      <i class="glyphicon glyphicon-map-marker"></i>
			      <span>喀什地区金地路128号喀什市农经局2号门</span>
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
			            <tbody>
			               <tr ng-repeat="electDto in AllElectDtos">
			                  <td></td>
			                  <td></td>
			                  <td></td>
			               </tr>
			            </tbody>
			         </table>
			      </div>
			   </div>
			`;
		 var pt;
		 var marker2;
		 var sContent = sHtml;
		 var markers = [];
	     for(var i=0;i<100;i++){
	    	 	pt = new BMap.Point($scope.stations[i].longitude,$scope.stations[i].latitude);
	    	 	marker2 = new BMap.Marker(pt); 
    	   
	    	 	var infoWindow = new BMap.InfoWindow(sContent); 				        	   
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
	 //根据时间和基站id获取基站下面的当前所有车辆
     function showElectsInStation(startTime,endTime,stationPhyNum){
    	 $http.get('/i/elect/findElectsByStationIdAndTime', {
 			params : {
 				"startTime" : startTime,
 				"endTime" : endTime,
 				"stationPhyNum" : stationPhyNum
 			}
 		}).success(function(data) {
 			$scope.electsInStation = data;
 		});
	 }
	 
	 function openInfo(){
		 
	 }
	 
	 //定义轨迹及定位查询条件的类型
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
				$scope.electNumForTraceTable = $scope.plateNum;
			}
			else if($scope.keywordTypeForTrace == "0"){
				$scope.guaCardNum = $scope.keywordForTrace;
				$scope.electNumForTraceTable = $scope.keywordForTrace;
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
					$scope.traceStationsLength = data.length;
					$("#positionTable").addClass("rightToggle");
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
	//轨迹选择日期
	 $('#homeDateStart').datetimepicker({
	     format: 'yyyy-mm-dd',
	     minView: "month",
	     autoclose: true,
	     maxDate: new Date(),
	     pickerPosition: "bottom-left"
	 });

	 $('#homeDateEnd').datetimepicker({
	     format: 'yyyy-mm-dd',
	     minView: "month",
	     autoclose: true,
	     maxDate: new Date(),
	     pickerPosition: "bottom-left"
	 });


	 $scope.showReLiTu = function(){
		 $http.get('/i/elect/findElectsNumByStations').success(function (data) {
	   	        $scope.thermodynamics = data;
	   	        alert($scope.thermodynamics);
	   	        //reLituShow();
	   	    });
	 }
	 //热力图开关选项控制
	 $scope.relituFlag = 0;
	 $('#relitu').click(function () {
	     if($scope.relituFlag==0){
	    	 $scope.showReLiTu();
	    	 $scope.relituFlag=1;
	     }
	     else if($scope.relituFlag==1){
	    	 alert("隐藏热力图");
	    	 $scope.relituFlag=0;
	     }
	     showCssFlag('#relitu');
	 });
	 //显示基站开关选项控制
	 $scope.jizhanFlag = 1;
	 $('#xsjizhan').click(function () {
	     if($scope.jizhanFlag==0){
	    	 alert("显示基站");
	    	 $scope.jizhanFlag=1;
	     }
	     else if($scope.jizhanFlag==1){
	    	 alert("隐藏基站");
	    	 $scope.jizhanFlag=0;
	     }
	     showCssFlag('#xsjizhan');
	 });
	 //基站聚合开关选项控制
	 $scope.jizhanjuheFlag = 1;
	 $('#jizhanjuhe').click(function () {
	     if($scope.jizhanjuheFlag==0){
	    	 alert("显示基站聚合");
	    	 $scope.jizhanjuheFlag=1;
	     }
	     else if($scope.jizhanjuheFlag==1){
	    	 alert("隐藏基站集聚合");
	    	 $scope.jizhanjuheFlag=0;
	     }
	     showCssFlag('#jizhanjuhe');
	 });
	 function showCssFlag(param){
		  $(param).toggleClass("active");
		     var left = $(param).css('left');
		     left = parseInt(left);
		     if (left == 0) {
		         $(param).css('background-color', '#66b3ff'),
		             $(param).parent().css('background-color', '#66b3ff');
		         $(param).parent().parent().addClass("active");
		     } else {
		         $(param).css('background-color', '#fff'),
		             $(param).parent().css('background-color', '#ccc');
		         $(param).parent().parent().removeClass("active");
		     }
	 }
	 $(".dismis").click(function () {
	     $(this).parents('#positionTable').toggleClass("rightToggle");
	     if ($(this).hasClass("glyphicon-chevron-left")) {
	         $(this).removeClass("glyphicon-chevron-left").addClass("glyphicon-chevron-right")
	     } else {
	         $(this).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-left")
	     }
	 });
});
