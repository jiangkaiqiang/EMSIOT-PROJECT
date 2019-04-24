coldWeb.controller('peopleManageMap', function ($rootScope, $scope, $state, $cookies, $http, $location) {
    var map = new BMap.Map("allmap", {
        minZoom: 5,
        maxZoom: 30
    });    // 创建Map实例
    map.addControl(new BMap.MapTypeControl(
        {
            mapTypes:[
                BMAP_NORMAL_MAP,
                BMAP_HYBRID_MAP
            ]}
    ));
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
                    //map.centerAndZoom(new BMap.Point($scope.point.lng, $scope.point.lat), 15); // 初始化地图,设置中心点坐标和地图级别
                    map.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
                }else{
                    map.centerAndZoom(new BMap.Point($scope.user.fixed_lon, $scope.user.fixed_lat), $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                    //map.centerAndZoom($scope.cityName, $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                }
            })
            map.setCurrentCity($scope.cityName); // 初始化地图,设置中心点坐标和地图级别
            map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
            map.disableDoubleClickZoom();//禁止地图双击放大功能
            //showCssFlag('#xsjizhan');
            var top_left_control = new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT});
            var top_left_navigation = new BMap.NavigationControl();  //左上角，添加默认缩放平移控件
            map.addControl(top_left_control);
            map.addControl(top_left_navigation);
            
            $http.get('/i/station/findAllStationsForMap', {
                params: {
                    "proPower": $scope.user.pro_power,
                    "cityPower": $scope.user.city_power,
                    "areaPower": $scope.user.area_power
                }
            }).success(function (data) {
                $scope.stations = data;
                showStation();
            });
            
         // 获取在线人数
            /*$http.get('/i/people/findInlinePeoplesNum', {
                params: {
                    "areaPower": $scope.user.area_power,
                    "cityPower": $scope.user.city_power,
                    "proPower": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.inlinePeoples = data;
            });*/
            $scope.inlinePeoplesNum();
        });
    });
    
    $scope.inlinePeoplesNum = function(){
	    $http.get('/i/people/findInlinePeoplesNum', {
	        params: {
	            "areaPower": $scope.user.area_power,
	            "cityPower": $scope.user.city_power,
	            "proPower": $scope.user.pro_power
	        }
	    }).success(function (data) {
	        $scope.inlinePeoples = data;
	    });
    }
    
  //定时刷新页面基站经过的车辆
    var timeelect = function(){
    	console.log("人员定时执行")
    	$scope.inlinePeoplesNum();
        var time = new Date().getTime();//当前时间
        var start;
        if($scope.user.fixed_query_time == null || $scope.user.fixed_query_time == undefined ){
            start = new Date(time - 60 * 1000 * 60);//一小时
        }else{
            start = new Date(time - 60 * 1000 * $scope.user.fixed_query_time );//一小时
        }
        var end = new Date(time);
//      for (var i = 0; i < $scope.stations.length; i++) {
//            tmpStation = $scope.stations[i];
//            var carNum = showElectsCountInStation(FormatDate(start), FormatDate(end), tmpStation.station_phy_num);
//            markers[i].setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address + '\t' + carNum);
//      }	

        var stationPhyRefrehNums = '';
        for(var i = 0; i < $scope.stations.length; i++){
        	if(stationPhyRefrehNums=="")
        		stationPhyRefrehNums=$scope.stations[i].station_phy_num;
        	else
        		stationPhyRefrehNums+=","+$scope.stations[i].station_phy_num
        }
        var carRefrehNums = showPeoplesCountInStations(FormatDate(start), FormatDate(end), stationPhyRefrehNums);
        for(var i = 0; i < $scope.stations.length; i++){
        	tmpStation = $scope.stations[i];
        	markers[i].setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address + '\t' + (carRefrehNums[tmpStation.station_phy_num]==undefined?0:carRefrehNums[tmpStation.station_phy_num]));
        }
        if(markerClusterer!=null){
            markerClusterer._redraw();
        }
    };
    //setInterval(console.log(1),3000);
    $scope.$on("$destroy", function() {
        //清除配置,不然scroll会重复请求
    	console.log("清除人员定时器")
    	clearInterval($scope.timingCount)
    })
    $scope.timingCount = setInterval(function(){
        timeelect();
     //$scope.allStationAndElectNums = a
    }
   ,1000 * 60);
    
    
    
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

  //显示基站和聚合
    var markerClusterer = null;
    var marker2;
    var markers = [];

    function showStation() {
    	
        var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><img src='app/img/station.png'/><h4>";
        var sHtml2 = "</h4></li><li>";
        var sHtml3 = "</li></ul><p class='flex-items'><i class='glyphicon glyphicon-map-marker'></i><span>";
        var sHtml4 = "</p><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>姓名</th><th>经过时间</th></tr></thead><tbody>";
        //var sHtml4 = "</p><ul class='flex flex-time'><li class='active searchTime'>1分钟</li><li class='searchTime'>5分钟</li><li class='searchTime'>1小时</li></ul><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>车辆编号</th><th>经过时间</th></tr></thead><tbody>";
        var endHtml = "</tbody></table></div></div>";
        var pt;
        /*   var bluemarkers = []; //存放聚合的基站
         var grymarkers = [];*/
        var tmpStation;
        //给每一个基站添加监听事件 和窗口信息
        var stationPhyInitNums = '';
        for(var i = 0; i < $scope.stations.length; i++){
        	if(stationPhyInitNums=="")
        		stationPhyInitNums=$scope.stations[i].station_phy_num;
        	else
        		stationPhyInitNums+=","+$scope.stations[i].station_phy_num
        }
        /*var time = new Date().getTime();//当前时间
        var start;
        if($scope.user.fixed_query_time == null || $scope.user.fixed_query_time == undefined ){
            start = new Date(time - 60 * 1000 * 60);//一小时
        }else{
            start = new Date(time - 60 * 1000 * $scope.user.fixed_query_time );//自定义查询时间;
        }
        var end = new Date(time);
        console.log(FormatDate(start))
        console.log(FormatDate(end))*/
        var carInitNums = showPeoplesCountInStations($scope.peopleStartTime, $scope.peopleEndTime, stationPhyInitNums);
        for (var i = 0; i < $scope.stations.length; i++) {
            tmpStation = $scope.stations[i];
            pt = new BMap.Point(tmpStation.longitude, tmpStation.latitude);
            if (tmpStation.station_status == 1) {
            	//基站异常图标
                var myIcon = new BMap.Icon("/app/img/marker_gray.png", new BMap.Size(19, 25));
                marker2 = new BMap.Marker(pt, {icon: myIcon});
            } else {
            	//正常基站（默认）
                marker2 = new BMap.Marker(pt);
            }
            //统计时间内经过改基站的车辆的数
            //var num = tmpStation.station_phy_num;
            //console.log(FormatDate(start) ,FormatDate(end) );
            //showPeopleInStation(FormatDate(start), FormatDate(end), num);
            //var carNum = showElectsCountInStation(FormatDate(start), FormatDate(end), num);
            //$scope.labelSet(carNum, marker2);
            //marker2.setTitle(carNum);
            marker2.setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address + '\t' + (carInitNums[tmpStation.station_phy_num]==undefined?0:carInitNums[tmpStation.station_phy_num]));
            marker2.addEventListener("click", function (e) {
                var title_add = new Array();
                title_add = this.getTitle().split('\t');
                //title_add = tmpStation.station_phy_num;
                //showPeopleInStation(FormatDate(start), FormatDate(end), title_add[0]);  //根据物理编号查找
                $scope.peopleInStation = $scope.peoplesInStationMap[title_add[0]]==undefined?[]:$scope.peoplesInStationMap[title_add[0]];
                var peopleInfo = '';
                for (var k = 0; k < $scope.peopleInStation.length; k++) {
                    //2018-10-15 修改
                    peopleInfo += "<tr><td title='" + (k + 1) + "'>" + (k + 1) + "</td>" + "<td title='" + $scope.peopleInStation[k].people_name + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.peopleInStation[k].people_name + "</td>" + "<td title='" + $scope.peopleInStation[k].corssTime + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.peopleInStation[k].corssTime + "</td></tr>";
                }
                var infoWindow = new BMap.InfoWindow(sHtml + title_add[0] + sHtml2 + $scope.peopleInStation.length + "人" + sHtml3 + title_add[1] + sHtml4 + peopleInfo + endHtml);
                var p = e.target;
                var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
                map.openInfoWindow(infoWindow, point);
            });
            markers.push(marker2);
        }
        //最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
        //console.log(new BMapLib.MarkerClusterer(map,{markers: markers}))
        markerClusterer = new BMapLib.MarkerClusterer(map,
	        {
	            markers: markers,
	            girdSize: 100,
	            styles: [{
	            	url: '../app/img/blue3.png',
                    size: new BMap.Size(50, 50),
                    backgroundColor: 'rgba(102,179,255,0.1)',
                    textColor: '#ffffff',
                    textSize: 14,
                    lineHeight: 15
	             }]
	         });
	        markerClusterer.setMaxZoom(15);
	        markerClusterer.setGridSize(40);
        

        //用于基站跳动
	    $scope.jizhanBounce = markers;
    }

    

  //用于基站跳动的参数
    $scope.jizhanBounce=null;
    $scope.tiao=null;
    //表格选中行，对应标注体现出来
    $('#tableArea tbody').on('click','tr',function(e){
    	if($scope.tiao!=null){
    		$scope.tiao.setAnimation(null);
    	}
        var tablePoint =  $(this).context.cells[2].innerHTML;//获取单击表格时的地址
        //console.log(tablePoint)
        console.log($scope.jizhanBounce)
        for(var g =0;g < $scope.stations.length; g++){
        	
            if(tablePoint==$scope.stations[g].station_name){//表格获取到的地址等于循环基站时的基站地址

                var allOverlay = $scope.jizhanBounce;

                var Oe=null
                for (var i = 0; i < allOverlay.length; i++) {
					Oe = allOverlay[i].point;
					if(Oe==null)continue;
	                if(Oe.lng==$scope.peopleData[g].station.longitude && Oe.lat==$scope.peopleData[g].station.latitude){
	                	$scope.tiao = allOverlay[i];//保存上一次跳动的基站
	                	allOverlay[i].setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
	                    return;
	                }
                }
                return;
            }
        }
    });
/*
    //根据时间和基站id获取基站下面的当前所有车辆
    function showPeopleInStation(startTime, endTime, peopleGuaCardNum) {
        $.ajax({
            method: 'GET',
            url :'/i/people/findPeoplesByStationIdAndTime',
            async:false,
            data : {
                "startTime": startTime,
                "endTime": endTime,
                "peopleGuaCardNum": peopleGuaCardNum
            }
        }).success(function(data){
            $scope.peopleInStation = data;
        })
    }

  */
    function showPeopleInStation(startTime, endTime, stationPhyNum) {
        $.ajax({
            method: 'GET',
            url: '/i/people/findPeoplesByStationIdAndTime1',
            async: false,
            data: {
//                "startTime": startTime,
//                "endTime": endTime,
                "stationPhyNum": stationPhyNum
            }
        }).success(function (data) {
            $scope.peopleInStation = data;

        })
    }
    //根据时间和基站的ids批量获取基站下面的经过车辆数量
    function showPeoplesCountInStations(startTime, endTime, stationPhyNumStr) {
    	var counts;
    	$.ajax({
    		method: 'GET',
    		url: '/i/people/findPeoplesCountByStationsIdAndTime',
    		async: false,
    		data: {
//    			"startTime": startTime,
//    			"endTime": endTime,
    			"stationPhyNumStr": stationPhyNumStr,
    			"proPower": $scope.user.pro_power,
                "cityPower": $scope.user.city_power,
                "areaPower": $scope.user.area_power
    		}
    	}).success(function (data) {
    		console.log(data)
    		counts=data.peopleCountByStation;
    		console.log(counts);
    		$scope.peoplesInStationMap = data.stationPeopleList;
    	})
    	return counts;
    }
    //定义轨迹及定位查询条件的类型
    $scope.AllKeywordType = [
        {id: "0", name: "防盗芯片ID"},
        {id: "1", name: "身份证"}
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
    		$scope.peopleLocation()
    	}

        $("#dingweiModal").modal("hide");
    };
    //根据条件定位人员
    $scope.findElectLocation = function () {
        $scope.showOperation = false;
        $scope.showTable = false;
       if ($scope.keywordTypeForLocation == "1") {
        	if($scope.keywordForLocation==null || $scope.keywordForLocation==""){
        		alert("请输入身份证！");
        		return;
            }
            $scope.peopleIdCards = $scope.keywordForLocation;
            $scope.peopleGuaCardNum = null;
        }
        else if ($scope.keywordTypeForLocation == "0") {
        	if($scope.keywordForLocation==null || $scope.keywordForLocation==""){
        		alert("请输入防盗芯片号！");
        		return;
            }
            $scope.peopleGuaCardNum = $scope.keywordForLocation;
            $scope.peopleIdCards = null;
        }
        else {

        }

        $http.get('/i/people/findPeopleLocation', {
            params: {
            	"proPower": $scope.user.pro_power,
                "cityPower": $scope.user.city_power,
                "areaPower": $scope.user.area_power,
                "peopleIdCards": $scope.peopleIdCards,
                "peopleGuaCardNum": $scope.peopleGuaCardNum
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
            $scope.elecIcon = new BMap.Icon("../app/img/people-icon.png", new BMap.Size(67, 51));
            $scope.elecMarker = new BMap.Marker($scope.elecPt, {icon: $scope.elecIcon});
            $scope.labelSet($scope.keywordForLocation+":"+data.station_name,$scope.elecMarker);
            map.addOverlay($scope.elecMarker);
            $scope.elecMarker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
            map.centerAndZoom($scope.elecPt, 17);


          //用于基站跳动
           // $scope.jizhanBounce=map.getOverlays();
        }).error(function(){
        	alert("搜索条件有误！");
        });
        $("#dingweiModal").modal("hide");
    };

    $scope.findElectTrace = function () {            //显示车辆轨迹
    	//$scope.jizhanjuheFlag = 0;
    	//$scope.jizhankongzhi();
        if (lushu != null) {
            lushu.stop();
            lushu = null;
        }
        map.clearOverlays();

        //showStation();
        if ($scope.keywordTypeForTrace == "1") {
        	if($scope.keywordForTrace == null || $scope.keywordForTrace == ""){
        		alert("请输入身份证！")
        		return;
        	}
            $scope.peopleIdCards = $scope.keywordForTrace;
            $scope.peopleGuaCardNum = null;
            $scope.electNumForTraceTable = $scope.peopleIdCards;

        }
        else if ($scope.keywordTypeForTrace == "0") {
        	if($scope.keywordForTrace == null || $scope.keywordForTrace == ""){
        		alert("请输入防盗芯片号！")
        		return;
        	}
            $scope.peopleGuaCardNum = $scope.keywordForTrace;
            $scope.peopleIdCards = null;
            $scope.electNumForTraceTable = $scope.peopleGuaCardNum;

        }
        if($scope.startTimeForTrace==undefined || $scope.startTimeForTrace == ''){
            alert("请选择开始时间！")
            return;
        }
        if($scope.endTimeForTrace==undefined || $scope.endTimeForTrace == ''){
            alert("请选择结束时间！")
            return;
        }
        /*var endTimeForTrace = "";
        if($scope.endTimeForTrace != null && $scope.endTimeForTrace != undefined && $scope.endTimeForTrace != ""){
        	endTimeForTrace = $scope.endTimeForTrace + " 23:59:59";
        }else{
        	endTimeForTrace = $scope.endTimeForTrace ;
        }*/
        $http.get('/i/people/findPeopleTrace', {
            params: {
            	"proPower": $scope.user.pro_power,
                "cityPower": $scope.user.city_power,
                "areaPower": $scope.user.area_power,
                "peopleIdCards": $scope.peopleIdCards,
                "peopleGuaCardNum": $scope.peopleGuaCardNum,
                "startTimeForTrace": $scope.startTimeForTrace,
                "endTimeForTrace": $scope.endTimeForTrace
            }
        }).success(function (data) {

        	//$scope.bigTotalItems = data.total;
            $scope.AllPeoplelarms = data;
            console.log($scope.AllPeoplelarms)
            $scope.traceStationsLength = data.length;
            $scope.pointsArr = [];
            for(var j=0;j<data.length;j++){
                var pointsJ = data[j].longitude;
                var pointsW = data[j].latitude;
                $scope.pointsArr.push(new BMap.Point(pointsJ,pointsW));
            }

            if (data.length == 1)
                alert("该人员仅经过一个基站：" + data[0].station_name);
            else if (data.length == 0)
                alert("该车辆没有经过任何基站！");
            else{
            	var marker;
                 var arrPoisRes=$scope.pointsArr;
                 //map.setViewport(arrPois);
                var arrPoisall = [];
                var drv = new BMap.WalkingRoute(map, {
                    onSearchComplete: function(res) {
                        console.log(res)
                        //console.log("b")
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
                            map.addOverlay(new BMap.Polyline(arrPois, {strokeColor: '#111'}));
                            //map.setViewport(arrPoisall);
                            if(f_index < arrPoisRes.length - 1)
                                workCallback()
                            else if(f_index == arrPoisRes.length - 1){
                                lushu = new BMapLib.LuShu(map, arrPoisall, {
                                    defaultContent: $scope.electNumForTraceTable,//"从天安门到百度大厦"
                                    autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                                    icon: new BMap.Icon('../app/img/people-icon.png', new BMap.Size(50, 30), {anchor: new BMap.Size(27, 23)}),
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
                var f_index = 0;
                workCallback()
                console.log(arrPoisRes)
                function workCallback() {
                    console.log("f_index",f_index)
                    drv.search(new BMap.Point(arrPoisRes[f_index].lng,arrPoisRes[f_index].lat),new BMap.Point(arrPoisRes[f_index+1].lng,arrPoisRes[f_index+1].lat))
                    f_index++;
                }
                //绑定事件
                $("run").onclick = function(){
                    lushu.start();
                }
                $("pause").onclick = function(){
                    lushu.pause();
                }
                $("hide").onclick = function(){
                    lushu.hideInfoWindow();
                }
                $("show").onclick = function(){
                    lushu.showInfoWindow();
                }
                function $(element){
                    return document.getElementById(element);
                }
                
            }
            $scope.showOperation = true;
            $scope.showTable = true;
        }).error(function(){
            $("#positionTable").removeClass("rightToggle");
            $scope.showOperation = false;
            $scope.showTable = false;
        	alert("搜索条件有误！");
        	return ;
        });


        $("#guijiModal").modal("hide");
        //用于基站跳动
        //$scope.jizhanBounce=map.getOverlays();
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
        $scope.peopleLocation()
        //showStation();
        if($scope.elecMarker!=null){
            //map.addOverlay($scope.elecMarker);
        	$scope.elecMarker.hide();
    		$scope.elecMarker = null;
        }
        //$scope.getElectalarmsByOptions();

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
    //$scope.startTimeForTrace=$scope.doDateStr(new Date(),1)
    $('#homeDateEnd').datetimepicker({
        format: 'yyyy-mm-dd hh:ii:00',
        minView: "hour",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    //$scope.endTimeForTrace=$scope.doDateStr(new Date(),1)
    //报警车辆日期
    $('#peopleStartTime').datetimepicker({
    	format: 'yyyy-mm-dd',
    	minView: "month",
    	autoclose: true,
    	maxDate: new Date(),

    	pickerPosition: "bottom-left"
    });

    $('#peopleEndTime').datetimepicker({
    	format: 'yyyy-mm-dd',
    	minView: "month",
    	autoclose: true,
    	maxDate: new Date(),
    	pickerPosition: "bottom-left"
    });
    $(function(){
    	var sysTime = new Date().getTime();//当天
    	var month = new Date(sysTime - 60*1000*60*24*30)
    	$scope.peopleStartTime=FormatDate(month,1);
    	$scope.peopleEndTime=FormatDate(sysTime,1);

    })

    //function showCssFlag(param) {
    //    $(param).toggleClass("active");
    //    var left = $(param).css('left');
    //    left = parseInt(left);
    //    if (left == 0) {
    //        $(param).css('background-color', '#66b3ff');
    //            $(param).parent().css('background-color', '#66b3ff');
    //        $(param).parent().parent().addClass("active");
    //    } else {
    //        $(param).css('background-color', '#fff');
    //            $(param).parent().css('background-color', '#ccc');
    //        $(param).parent().parent().removeClass("active");
    //    }
    //}
    //
    //$(".dismis").click(function () {
    //    $(this).parents('#positionTable').toggleClass("rightToggle");
    //    if ($(this).hasClass("fa-angle-left")) {
    //        $(this).removeClass("fa-angle-left").addClass("fa-angle-right")
    //    } else {
    //        $(this).removeClass("fa-angle-right").addClass("fa-angle-left")
    //    }
    //});





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

	$scope.peopleLocation = function() {
		$scope.showOperation = false;
        $scope.showTable = false;
        $.ajax({
            method: 'GET',
            url :'/i/people/selectPeopleStationPeopleLocationByTime',
            data : {
                "startTime": $scope.peopleStartTime,
                "endTime": $scope.peopleEndTime + " 23:59:59",
                "proPower": $scope.user.pro_power,
                "cityPower": $scope.user.city_power,
                "areaPower": $scope.user.area_power

            }
        }).success(function(data){
            $scope.peopleData = data;
            map.clearOverlays();
            //$scope.clearElectTrace();
            //showStation();
            var stationPhyInitNums = '';
            for(var i = 0; i < $scope.stations.length; i++){
            	if(stationPhyInitNums=="")
            		stationPhyInitNums=$scope.stations[i].station_phy_num;
            	else
            		stationPhyInitNums+=","+$scope.stations[i].station_phy_num
            }
            
            var carInitNums = showPeoplesCountInStations($scope.peopleStartTime, $scope.peopleEndTime, stationPhyInitNums);
            for (var i = 0; i < $scope.stations.length; i++) {
            	tmpStation = $scope.stations[i];
            	markers[i].setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address + '\t' + (carInitNums[tmpStation.station_phy_num]==undefined?0:carInitNums[tmpStation.station_phy_num]));
            	
            }
            /*if(markerClusterer!=null){
                markerClusterer._redraw();
            }*/
        
        })
    }

	//统计时间内经过改基站的车辆的数
    var time = new Date().getTime();//当前时间
    var start = new Date(time - 60*1000*60);//一小时
    var end = new Date(time);
    function FormatDate (strTime,people) {
        var date = new Date(strTime);
        //2018-10-15 修改
        var year = date.getFullYear();
        var month = (date.getMonth()+1) < 10?"0"+(date.getMonth()+1):(date.getMonth()+1);
        var day = date.getDate() < 10?"0"+date.getDate():date.getDate();
        var hours = date.getHours() < 10?"0"+date.getHours():date.getHours();
        var min = date.getMinutes() < 10?"0"+date.getMinutes():date.getMinutes();
        var seconds = date.getSeconds() < 10?"0"+date.getSeconds():date.getSeconds();
        if(people==0){
        	return year + "-" + month + "-" + day + " " + hours + ":" + min + ":" +seconds
        }else{
        	return year + "-" + month + "-" + day
        }
        //return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+ date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

});
