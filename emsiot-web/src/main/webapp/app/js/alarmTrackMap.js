coldWeb.controller('alarmTrackMap', function ($rootScope, $scope, $state, $cookies, $http, $location) {
    var map = new BMap.Map("allmap", {
        minZoom: 5,
        maxZoom: 30
    });    // 创建Map实例
    $.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
        $scope.user = data;
        
        if ($scope.user == null || $scope.user.user_id == 0 || $scope.user.user_id == undefined) {
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
            //$scope.cityName = "芒市";
            var myGeo = new BMap.Geocoder();
            myGeo.getPoint($scope.cityName, function (point) {
                $scope.point = point;
                //console.log($scope.user.fixed_lon);
                if($scope.user.fixed_lat == null || $scope.user.fixed_lat == undefined || $scope.user.fixed_lon == null || $scope.user.fixed_lon == undefined){
                    map.centerAndZoom(new BMap.Point($scope.point.lng, $scope.point.lat), 15); // 初始化地图,设置中心点坐标和地图级别
                    map.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
                }else{
                    map.centerAndZoom(new BMap.Point($scope.user.fixed_lon, $scope.user.fixed_lat), $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                    //map.centerAndZoom($scope.cityName, $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                }
            })
           // map.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
            map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
            //map.setMapStyle({style:'light'})
            //map.disableDoubleClickZoom();
            showCssFlag('#xsjizhan');
            //报警的车辆
            $scope.alarmVehicleLocation();
            /*// 获取基站
            $http.get('/i/station/findAllStationsForMap', {
                params: {
                    "proPower": $scope.user.pro_power,
                    "cityPower": $scope.user.city_power,
                    "areaPower": $scope.user.area_power
                }
            }).success(function (data) {
                $scope.stations = data;
                
                showStation();
            });*/
            
            // 获取在线车辆数量
            $http.get('/i/electalarm/findInlineElectsNum', {
                params: {
                    "areaPower": $scope.user.area_power,
                    "cityPower": $scope.user.city_power,
                    "proPower": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.inlineElects = data;
            });
            // 获取报警数量
            $http.get('/i/electalarm/findElectAlarmsListCount', {
                params: {
                    "areaPower": $scope.user.area_power,
                    "cityPower": $scope.user.city_power,
                    "proPower": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.electAlarmsCount = data;
            });
        });
    });
    var lushu=null;
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


    $scope.labelSet=function(value1,value2){
        var label = new BMap.Label(value1,{offset:new BMap.Size(0,-30)});
            label.setStyle({
                color: "#fff",
                fontSize: "14px",
                backgroundColor: "#66B3FF",
                border: "1px solid rgb(102, 179, 255)",
                fontWeight: "normal",
                display: "block",
                borderShadow: "0 5px 15px rgba(0, 0, 0, 0.6)",
                minHeight: "20px",
                minWidth: "40px",
                lineHeight: "20px",
                borderRadius: "5px",
                textAlign: "center",
                padding:"1px 8px"
            });
        //}
        value2.setLabel(label);
    }
    var isAddMarker=true
    function markerClickListener(marker2){
    	
    	 var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><i class='iconfont icon-cheliangguanli' style='color: #2C7DFA; font-size: 25px;'></i><h4>";
         var sHtml2 = "</h4></li><li>";
         var sHtml3 = "</li></ul><p class='flex-items'><i class='glyphicon glyphicon-map-marker'></i><span>";
         var sHtml4 = "</p><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>基站名称</th><th>经过时间</th></tr></thead><tbody>";
         //var sHtml4 = "</p><ul class='flex flex-time'><li class='active searchTime'>1分钟</li><li class='searchTime'>5分钟</li><li class='searchTime'>1小时</li></ul><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>车辆编号</th><th>经过时间</th></tr></thead><tbody>";
         var endHtml = "</tbody></table></div></div>";
         var showCount=""
        	 
        	 
             var selectCardNum = "";
             var option="";
             var overlay = map.getOverlays();
             //console.log(overlay)
             var marker = null;
             var markerTitle
             isAddMarker=true;
             var list = [];
             var firstObj = {};
             var obj = {};
             for (var i = 0; i < overlay.length; i++) {
            	 marker = overlay[i]
            	 if(marker.point.lat == marker2.point.lat && marker.point.lng == marker2.point.lng){
            		 
                     if(marker != null){
                    	 isAddMarker=false
                    	 if(marker.test != null && marker.test != undefined){
                    		 list = marker.test
                    	 }else{
                    		 var obj = {};
                    		 marker2Title = marker.getTitle().split('\t');
                    		 firstObj["card_num"]=marker2Title[0]
                    		 firstObj["station_name"]=marker2Title[1]
                    		 firstObj["name"]=marker2Title[2]
                	         list.push(firstObj)
                    	 }

                    	 marker2Title = marker2.getTitle().split('\t');
                    	 obj["card_num"]=marker2Title[0]
    			         obj["station_name"]=marker2Title[1]
                    	 obj["name"]=marker2Title[2]
    			         list.push(obj)
                    	 marker2 = marker;
                    	 marker2["test"]=list
                    	 $scope.labelSet(list[0].name+"：+"+list.length,marker2);
                     }
            		 break;
            	 }
    		 }
    	
    	marker2.addEventListener("click", function (e) {
            var title_add = new Array();
            title_add = this.getTitle().split('\t');
            showElectsInStation($scope.alarmStartTime, $scope.alarmEndTime+" 23:59:59",title_add[0]);  //根据物理编号查找
            
            var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><i class='iconfont icon-yonghuguanli' style='color: #707070; font-size: 20px;'></i><h4>";
            if(selectCardNum==""){
	            if(list.length>1){
		            for (var i = 0; i < list.length; i++) {
		            		option += "<option value='"+list[i].card_num+"'>"+list[i].name+"</option>" 
					}
		            selectCardNum = "<select  id='"+title_add[0]+"' class='form-control'>"+option+"</select>";
		            showCount = title_add[0] + ",经过"+ $scope.electsInStation.length+"个 基站";
	            }else{
	            	selectCardNum = title_add[0];
	            	showCount = "经过"+ $scope.electsInStation.length+"个 基站";
	            }
            }
            
            var electInfo='';
            for (var k = 0; k < $scope.electsInStation.length; k++) {
                //electInfo += "<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (k + 1) + "</td>" + "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].plate_num + "</td>" + "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].corssTime + "</td></tr>";
                //2018-10-15 修改
            	electInfo += "<tr><td title='" + (k + 1) + "'>" + (k + 1) + "</td>" + "<td title='" + $scope.electsInStation[k].station_name + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].station_name + "</td>" + "<td title='" + $scope.electsInStation[k].corssTime + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].corssTime + "</td></tr>";
            }
            var infoWindow = new BMap.InfoWindow(sHtml + selectCardNum + sHtml2 + showCount + sHtml3 + title_add[1] + sHtml4 + electInfo + endHtml);
            var p = e.target;
            var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
            map.openInfoWindow(infoWindow, point);

            
            setTimeout(() => {
        		const device = $("#"+title_add[0])
        		device.off("change")
        		device.on("change", function(){
        			console.log("change")
        			showElectsInStation($scope.peopleStartTime, $scope.peopleEndTime+" 23:59:59",$(this).val());  //根据物理编号查找
        			electInfo="";
        			for (var k = 0; k < $scope.electsInStation.length; k++) {
                    	electInfo += "<tr><td title='" + (k + 1) + "'>" + (k + 1) + "</td>" + "<td title='" + $scope.electsInStation[k].station_name + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].station_name + "</td>" + "<td title='" + $scope.electsInStation[k].corssTime + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].corssTime + "</td></tr>";
                    }
                    $(this).parent().parent().next().html($(this).val()+",经过"+ $scope.electsInStation.length+"个 基站")
                    
        			$("#tableArea tbody").html(electInfo)
        			
        		})
        	}, 0);
        });
    }

    //显示基站和聚合
    var markerClusterer=null;
    var marker2;
    //var markers = []
    function showStation() {
    	if(lushu!=null){
    		lushu.stop();
    		lushu = null;
    	}
        var pt;
        //var marker2;
        var markers = []; //存放聚合的基站
        //var infoWindow;
        var tmpStation;
        
        //给每一个基站添加监听事件 和窗口信息
        for (var i = 0; i < $scope.alarmVehicle.length; i++) {
            /*tmpStation = $scope.alarmVehicle[i].station;
            tmpElectAlarm = $scope.alarmVehicle[i].electAlarm;*/
            //if(tmpStation==null || tmpElectAlarm==null || $scope.alarmVehicle[i].ownerPlateNum==null)continue;
            
            pt = new BMap.Point($scope.alarmVehicle[i].longitude, $scope.alarmVehicle[i].latitude);
            //报警车辆图标
            var myIcon = new BMap.Icon("../app/img/eb-1.jpg", new BMap.Size(67, 51));
            marker2 = new BMap.Marker(pt,{icon:myIcon});
            $scope.labelSet($scope.alarmVehicle[i].plate_num,marker2);
            marker2.setTitle($scope.alarmVehicle[i].alarm_gua_card_num + '\t' + $scope.alarmVehicle[i].station_name + "\t" + $scope.alarmVehicle[i].plate_num);

            markerClickListener(marker2);
            
            if(isAddMarker){
            	map.addOverlay(marker2);
            }
        }
        console.log(map)
      //用于基站跳动
        $scope.jizhanBounce=map.getOverlays();
    }

  //用于基站跳动的参数
    $scope.jizhanBounce=null;
    $scope.tiao=null;
    //表格选中行，对应标注体现出来
    $('#tableArea tbody').on('click','tr',function(e){
    	if($scope.tiao!=null){
    		$scope.tiao.setAnimation(null);
    	}
        var tablePoint =  $(this).context.cells[1].innerHTML;//获取单击表格时的地址
        for(var g =0;g < $scope.alarmVehicle.length; g++){
        	if($scope.alarmVehicle[g].station==null)continue;
            if(tablePoint==$scope.alarmVehicle[g].ownerPlateNum){//表格获取到的地址等于循环基站时的基站地址
               
                var allOverlay = $scope.jizhanBounce;
                
                var Oe=null
                for (var i = 0; i < allOverlay.length; i++) {
					Oe = allOverlay[i].point;
					if(Oe==null)continue;
	                if(Oe.lng==$scope.alarmVehicle[g].station.longitude && Oe.lat==$scope.alarmVehicle[g].station.latitude){
	                	$scope.tiao = allOverlay[i];//保存上一次跳动的基站
	                	//allOverlay[i].setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
	                    return;
	                }
                }
                return;
            }
        }
    });

    //根据时间和基站id获取基站下面的当前所有车辆
    function showElectsInStation(startTime, endTime, guaCardNum) {
    	$scope.loading = true;
        $.ajax({
            method: 'GET',
            url :'/i/electalarm/findElectsByStationIdAndTime',
            async:false,
            data : {
                "startTime": startTime,
                "endTime": endTime,
                "guaCardNum": guaCardNum
            }
        }).success(function(data){
            $scope.electsInStation = data;
        }).error(function(){
        	
        })
    }



    //定义轨迹及定位查询条件的类型
    $scope.AllKeywordType = [
        {id: "0", name: "防盗芯片ID"},
        {id: "1", name: "车牌号"}
    ];
    $scope.keywordTypeForLocation = "0";
    $scope.keywordTypeForTrace = "0";
    //清除定位
    var stationIDforDelete;
    $scope.clearElectLocation = function() {
    	if($scope.elecMarker!=null){
    		$scope.elecMarker.hide();
    		$scope.elecMarker = null;
    		
    		//map.clearOverlays();
    		//showStation();
    		$scope.alarmVehicleLocation()
    	}

        $("#dingweiModal").modal("hide");
    };
    //根据条件定位车辆
    $scope.findElectLocation = function () {
        $scope.showOperation = false;
        $scope.showTable = false;
       if ($scope.keywordTypeForLocation == "1") {
        	if($scope.keywordForLocation==null || $scope.keywordForLocation==""){
        		alert("请输入车牌号！");
        		return;
            }
            $scope.plateNum = $scope.keywordForLocation;
            $scope.guaCardNum = null;
        }
        else if ($scope.keywordTypeForLocation == "0") {
        	if($scope.keywordForLocation==null || $scope.keywordForLocation==""){
        		alert("请输入防盗芯片号！");
        		return;
            }
            $scope.guaCardNum = $scope.keywordForLocation;
            $scope.plateNum = null;
        }
        else {
        	
        }

        $http.get('/i/elect/findElectLocation', {
            params: {
                "plateNum": $scope.plateNum,
                "guaCardNum": $scope.guaCardNum
            }
        }).success(function (data) {
        	
            $scope.longitude = data.longitude;
            $scope.latitude = data.latitude;
            
            if($scope.longitude == undefined || $scope.longitude == null){
            	return alert("未查到相应数据！");
            }
            if(lushu!=null){
        		lushu.stop();
        		lushu=null;
        	}
            map.clearOverlays();
            $scope.showOperation = false;
            stationIDforDelete = data.station_phy_num;
            $scope.elecPt = new BMap.Point($scope.longitude, $scope.latitude);
            $scope.elecIcon = new BMap.Icon("../app/img/eb-1.jpg", new BMap.Size(67, 51));
            $scope.elecMarker = new BMap.Marker($scope.elecPt, {icon: $scope.elecIcon});
            $scope.labelSet($scope.keywordForLocation,$scope.elecMarker);
            map.addOverlay($scope.elecMarker);
            $scope.elecMarker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
            map.centerAndZoom($scope.elecPt, 17);
            
            
          //用于基站跳动
            $scope.jizhanBounce=map.getOverlays();
        }).error(function(){
        	alert("搜索条件有误！");
        });
        $("#dingweiModal").modal("hide");
    };

    $scope.findElectTrace = function () {            //显示车辆轨迹
    	//$scope.jizhanjuheFlag = 0;
    	//$scope.jizhankongzhi();
    	
        
        //showStation();
        if ($scope.keywordTypeForTrace == "1") {
        	if($scope.keywordForTrace == null || $scope.keywordForTrace == ""){
        		alert("请输入车牌号！")
        		return;
        	}
            $scope.plateNum = $scope.keywordForTrace;
            $scope.guaCardNum = null
            
        }
        else if ($scope.keywordTypeForTrace == "0") {
        	if($scope.keywordForTrace == null || $scope.keywordForTrace == ""){
        		alert("请输入防盗芯片号！")
        		return;
        	}
            $scope.guaCardNum = $scope.keywordForTrace;
            $scope.plateNum = null;
            
        }
        else {

        }
        var endTimeForTrace = "";
        if($scope.endTimeForTrace != null && $scope.endTimeForTrace != undefined && $scope.endTimeForTrace != ""){
        	endTimeForTrace = $scope.endTimeForTrace + " 23:59:59";
        }else{
        	endTimeForTrace = $scope.endTimeForTrace ;
        }
        $http.get('/i/electalarm/findElectAlarmTrace', {
            params: {
                "plateNum": $scope.plateNum,
                "guaCardNum": $scope.guaCardNum,
                "startTimeForTrace": $scope.startTimeForTrace,
                "endTimeForTrace": endTimeForTrace
            }
        }).success(function (data) {
        	
        	$scope.bigTotalItems = data.total;
            $scope.AllElectalarms = data;
            $scope.traceStationsLength = data.length;
            $scope.pointsArr = [];
            for(var j=0;j<data.length;j++){
                var pointsJ = data[j].longitude;
                var pointsW = data[j].latitude;
                $scope.pointsArr.push(new BMap.Point(pointsJ,pointsW));
            }
            
            if (data.length == 1)
                alert("该车辆仅经过一个基站：" + data[0].station_name);
            else if (data.length == 0)
                alert("该车辆没有经过任何基站！");
            else{
            var marker;
            // var lushu;
            var arrPoisRes = $scope.pointsArr;
            console.log(arrPoisRes)
            var arrPoisall = [];
            //map.setViewport(arrPois);
            // 实例化一个驾车导航用来生成路线
            var drv = new BMap.WalkingRoute(map, {
                //renderOptions:{
                //    map:map,
                //    autoViewport:true
                //},
                onSearchComplete: function(res) {
                    console.log(res)
                    console.log("b")
                    if (drv.getStatus() == BMAP_STATUS_SUCCESS) {
                        var plan = res.getPlan(0);
                        console.log(plan.getNumRoutes())
                        var arrPois = [];
                        for (var j = 0; j < plan.getNumRoutes(); j++) {
                            var route = plan.getRoute(j);
                            arrPois = arrPois.concat(route.getPath());
                        }
                        console.log(arrPois,arrPois.length)
                        arrPoisall = arrPoisall.concat(arrPois);
                        console.log(arrPoisall,arrPoisall.length)
                        //console.log(arrPois);
                        map.addOverlay(new BMap.Polyline(arrPois, {strokeColor: '#111'}));
                        //map.setViewport(arrPoisall);
                        if(f_index < arrPoisRes.length - 1)
                            workCallback()
                        else if(f_index == arrPoisRes.length - 1){
                            lushu = new BMapLib.LuShu(map, arrPoisall, {
                                defaultContent: $scope.electNumForTraceTable,//"从天安门到百度大厦"
                                autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                                icon: new BMap.Icon('../app/img/eb.png', new BMap.Size(50, 30), {anchor: new BMap.Size(27, 23)}),
                                speed: 300,
                                enableRotation: true,//是否设置marker随着道路的走向进行旋转
                                landmarkPois: [
                                    {lng: arrPoisall[0].lng, lat: arrPoisall[0].lat, html: '起点', pauseTime: 0}
                                ]
                            })
                            drv.search(new BMap.Point(arrPoisRes[0].lng,arrPoisRes[0].lat),new BMap.Point(arrPoisRes[arrPoisRes.length-1].lng,arrPoisRes[arrPoisRes.length-1].lat))
                            f_index++;
                        }
                    }
                }
            });
            // arrPois 四个点位
            // arrPois

            var f_index = 0;
            workCallback()
            console.log(arrPoisRes)
            function workCallback() {
                console.log("f_index",f_index)
                drv.search(new BMap.Point(arrPoisRes[f_index].lng,arrPoisRes[f_index].lat),new BMap.Point(arrPoisRes[f_index+1].lng,arrPoisRes[f_index+1].lat))
                f_index++;
            }

                //绑定事件
                $("run").onclick = function () {
                    //marker.enableMassClear(); //设置后可以隐藏改点的覆盖物
                   // marker.hide();
                    lushu.start();
                }

                $("pause").onclick = function () {
                    lushu.pause();
                }
                $("hide").onclick = function () {
                    lushu.hideInfoWindow();
                }
                $("show").onclick = function () {
                    lushu.showInfoWindow();
                }
                function $(element) {
                    return document.getElementById(element);
                }
            }
            $scope.showOperation = true;
            $scope.showTable = true;
            $scope.electNumForTraceTable = $scope.keywordForTrace;
        }).error(function(){
            $("#positionTable").removeClass("rightToggle");
            $scope.showOperation = false;
            $scope.showTable = false;
        	alert("搜索条件有误！");
        	return ;
        });
        
        
        $("#guijiModal").modal("hide");
        //用于基站跳动
        $scope.jizhanBounce=map.getOverlays();
    };
    $scope.showOperation = false;
    $scope.showTable = false;

    $scope.showDetail = function () {
    	$scope.showTable = !($scope.showTable);
        $("#detail i").toggleClass("down-up");
    }
    $scope.clearElectTrace = function () {
    	
    	
        $("#guijiModal").modal("hide");
        $scope.showOperation = false;
        $scope.showTable = false;
        $scope.traceStationsLength=null
        //map.clearOverlays();
        $scope.alarmVehicleLocation()
        //showStation();
        if($scope.elecMarker!=null){
            //map.addOverlay($scope.elecMarker);
        	$scope.elecMarker.hide();
    		$scope.elecMarker = null;
        }
        $scope.getElectalarmsByOptions();
        
    };


    var navBtn = $(".home-title a");
    navBtn.click(function () {
        navBtn.removeClass("active");
        $(this).addClass("active");

    });
    //轨迹选择日期
    $('#homeDateStart').datetimepicker({
        format: 'yyyy-mm-dd hh:ii:00',
        minView: "hour",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $scope.startTimeForTrace=$scope.doDateStr(new Date(),1)
    $('#homeDateEnd').datetimepicker({
        format: 'yyyy-mm-dd hh:ii:00',
        minView: "hour",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $scope.endTimeForTrace=$scope.doDateStr(new Date(),1)
    //报警车辆日期
    $('#alarmStartTime').datetimepicker({
    	format: 'yyyy-mm-dd',
    	minView: "month",
    	autoclose: true,
    	maxDate: new Date(),
    	
    	pickerPosition: "bottom-left"
    });
    
    $('#alarmEndTime').datetimepicker({
    	format: 'yyyy-mm-dd',
    	minView: "month",
    	autoclose: true,
    	maxDate: new Date(),
    	pickerPosition: "bottom-left"
    });
    $(function(){
    	var sysTime = new Date().getTime();//当天
    	var month = new Date(sysTime - 60*1000*60*24*30)
    	$scope.alarmStartTime=FormatDate(month,1);
    	$scope.alarmEndTime=FormatDate(sysTime,1);
        
    })

    function showCssFlag(param) {
        $(param).toggleClass("active");
        var left = $(param).css('left');
        left = parseInt(left);
        if (left == 0) {
            $(param).css('background-color', '#66b3ff');
                $(param).parent().css('background-color', '#66b3ff');
            $(param).parent().parent().addClass("active");
        } else {
            $(param).css('background-color', '#fff');
                $(param).parent().css('background-color', '#ccc');
            $(param).parent().parent().removeClass("active");
        }
    }

    $(".dismis").click(function () {
        $(this).parents('#positionTable').toggleClass("rightToggle");
        if ($(this).hasClass("fa-angle-left")) {
            $(this).removeClass("fa-angle-left").addClass("fa-angle-right")
        } else {
            $(this).removeClass("fa-angle-right").addClass("fa-angle-left")
        }
    });
    
    
    ////////////////////////////////////////////////////////////////////////////
	/*// 显示最大页数
	$scope.maxSize = 10;
	// 总条目数(默认每页十条)
	$scope.bigTotalItems = 10;
	// 当前页
	$scope.bigCurrentPage = 1;
	$scope.AllElectalarms = [];
	//根据条件加载用户所有的黑名单
	$scope.getElectalarmsByOptions = function() {
		$http({
			method : 'POST',
			url : '/i/electalarm/findAllElectAlarmByOptions',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				plateNum : $scope.plateNum,
				alarmDateStart : $scope.alarmDateStart,
				alarmDateEnd : $scope.alarmDateEnd,
				proPower : $scope.admin.pro_power,
				cityPower : $scope.admin.city_power,
				areaPower : $scope.admin.area_power
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.AllElectalarms = data.list;
		});
	}
	$scope.getElectalarmsByOptions();
	$scope.selected = [];
	$scope.exists = function(electAlarmDto, list) {
		return list.indexOf(electAlarmDto) > -1;
	};
	$scope.toggle = function(electAlarmDto, list) {
		var idx = list.indexOf(electAlarmDto);
		if (idx > -1) {
			list.splice(idx, 1);
		} else {
			list.push(electAlarmDto);
		}
	};
	$scope.isChecked = function() {
	      return $scope.selected.length === $scope.AllElectalarms.length;
	  };
	  $scope.toggleAll = function() {
	      if ($scope.selected.length === $scope.AllElectalarms.length) {
	      	$scope.selected = [];
	      } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
	      	$scope.selected = $scope.AllElectalarms.slice(0);
	      }
	  };
	$scope.pageChanged = function() {
		$scope.getElectalarmsByOptions();
	}
	$scope.goSearch = function() {
		$scope.getElectalarmsByOptions();
	}

	function delcfm() {
		if (!confirm("确认要删除？")) {
			return false;
		}
		return true;
	}
	$scope.goDeleteElectAlarm = function(elect_alarm_id) {
		if (delcfm()) {
			$http.get('/i/electalarm/deleteElectAlarmByID', {
				params : {
					"elect_alarm_id" : elect_alarm_id
				}
			}).success(function(data) {
				$scope.getElectalarmsByOptions();
			});
		}
	}
	//批量删除
	$scope.goDeleteElectAlarms = function(){
	  	if(delcfm()){
	  	var ElectAlarmIDs = [];
	  	for(var i=0 ;i< $scope.selected.length;i++){
	  		ElectAlarmIDs.push($scope.selected[i].electAlarm.elect_alarm_id);
	  	}
	  	if(ElectAlarmIDs.length >0 ){
	  		$http({
	  			method:'DELETE',
	  			url:'/i/electalarm/deleteElectAlarmByIDs',
	  			params:{
	  				"ElectAlarmIDs": ElectAlarmIDs
	  			}
	  		}).success(function (data) {
	  			 $scope.getElectalarmsByOptions();
	          });
	  	}
	  	}
	  }*/
	
	
	/*JS.Engine.on(
			{ 
				limitData : function(msgData){
					
					var message = msgData.split(";");
					if($rootScope.admin.pro_power==-1){
						alert(message[3]); 
					}
					else if($rootScope.admin.pro_power==message[0]) {
						if($rootScope.admin.city_power==-1){
							alert(message[3]);
						}
						else if($rootScope.admin.city_power==message[1]){
							if($rootScope.admin.area_power==-1||$rootScope.admin.area_power==message[2]){
								alert(message[3]);
							}
						}
					}
				}
			}
	);*/
	
	$scope.alarmVehicleLocation = function() {
		$scope.showOperation = false;
        $.ajax({
            method: 'GET',
            url :'/i/electalarm/selectElectAlarmVehicleLocationByTime',
            data : {
                "startTime": $scope.alarmStartTime,
                "endTime": $scope.alarmEndTime + " 23:59:59",
                "proPower": $scope.user.pro_power,
                "cityPower": $scope.user.city_power,
                "areaPower": $scope.user.area_power
                
            }
        }).success(function(data){
            $scope.alarmVehicle = data;
            map.clearOverlays();
            //$scope.clearElectTrace();
            showStation();
        
        })
    }
	
	//统计时间内经过改基站的车辆的数
    var time = new Date().getTime();//当前时间
    var start = new Date(time - 60*1000*60);//一小时
    var end = new Date(time);
    function FormatDate (strTime,elect) {
        var date = new Date(strTime);
        //2018-10-15 修改
        var year = date.getFullYear();
        var month = (date.getMonth()+1) < 10?"0"+(date.getMonth()+1):(date.getMonth()+1);
        var day = date.getDate() < 10?"0"+date.getDate():date.getDate();
        var hours = date.getHours() < 10?"0"+date.getHours():date.getHours();
        var min = date.getMinutes() < 10?"0"+date.getMinutes():date.getMinutes();
        var seconds = date.getSeconds() < 10?"0"+date.getSeconds():date.getSeconds();
        if(elect==0){
        	return year + "-" + month + "-" + day + " " + hours + ":" + min + ":" +seconds
        }else{
        	return year + "-" + month + "-" + day
        }
        //return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+ date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }
    
});
