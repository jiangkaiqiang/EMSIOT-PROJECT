coldWeb.controller('home', function ($rootScope, $scope, $state, $cookies, $http, $location) {
    var map = new BMap.Map("allmap", {
        minZoom: 5,
        maxZoom: 30,

        //mapType:BMAP_HYBRID_MAP
    });    // 创建Map实例

    map.addControl(new BMap.MapTypeControl(
        {
           mapTypes:[
               BMAP_NORMAL_MAP,
               BMAP_HYBRID_MAP
        ]}
    ));
    $scope.load = function(){
        $.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
            $scope.user = data;
            if ($scope.user == null || $scope.user.user_id == 0 || $scope.user.user_id == undefined) {
                url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
                window.location.href = url;
            }
            $scope.configTim=$scope.user.fixed_query_time ;
            // 根据用户的区域权限定位城市，如果为超级管理员暂时定位喀什
            $http.get('/i/city/findCityNameByUserPower', {
                params: {
                    "areaID": $scope.user.area_power,
                    "cityID": $scope.user.city_power,
                    "proID": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.cityName = data.name;
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

                    map.setCurrentCity($scope.cityName);
                    map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
                    map.disableDoubleClickZoom();//禁止地图双击放大功能
                    // 添加比例尺
                    var top_left_control = new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT});
                    var top_left_navigation = new BMap.NavigationControl();  //左上角，添加默认缩放平移控件
                    map.addControl(top_left_control);
                    map.addControl(top_left_navigation);
                    showCssFlag('#xsjizhan');
                    // 获取基站
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
                    // 获取登记车辆数量
                    $http.get('/i/elect/findElectsListCount', {
                        params: {
                            "areaPower": $scope.user.area_power,
                            "cityPower": $scope.user.city_power,
                            "proPower": $scope.user.pro_power
                        }
                    }).success(function (data) {
                        $scope.electsCount = data;
                    });
                });
                //});
                // 获取黑名单车辆数量
                $http.get('/i/blackelect/findBlackelectsListCount', {
                    params: {
                        "areaPower": $scope.user.area_power,
                        "cityPower": $scope.user.city_power,
                        "proPower": $scope.user.pro_power
                    }
                }).success(function (data) {
                    $scope.blackElectsCount = data;
                });
                // 获取在线车辆数量
                $http.get('/i/elect/findInlineElectsNum', {
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
    };
    $scope.load();

    //定时刷新页面基站经过的车辆
    var timeelect = function(){
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
        var carRefrehNums = showElectsCountInStations(FormatDate(start), FormatDate(end), stationPhyRefrehNums);
        for(var i = 0; i < $scope.stations.length; i++){
        	tmpStation = $scope.stations[i];
        	markers[i].setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address + '\t' + carRefrehNums[tmpStation.station_phy_num]==undefined?0:carRefrehNums[tmpStation.station_phy_num]);
        }
        if(markerClusterer!=null){
            markerClusterer._redraw();
        }
    };
  /*  setInterval(console.log(1),3000);
    setInterval(function(){
        timeelect();
     $scope.allStationAndElectNums = a

   ,10000);*/

    var lushu = null;

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


    $scope.labelSet = function (value1, value2) {
        var label = new BMap.Label(value1, {offset: new BMap.Size(0, -30)});
        label.setStyle({
            color: "rgb(102, 179, 255)",
            fontSize: "16px",
            backgroundColor: "#fff",
            border: "1px solid rgb(102, 179, 255)",
            fontWeight: "bold",
            display: "block",
            borderShadow: "0 5px 15px rgba(0, 0, 0, .5)",
            minHeight: "20px",
            minWidth: "25px",
            lineHeight: "20px",
            borderRadius: "5px",
            textAlign: "center"
        });
        value2.setLabel(label);
    };
    function FormatDate(strTime) {
        var date = new Date(strTime);
        //2018-10-15 修改
        var year = date.getFullYear();
        var month = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1);
        var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
        var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        var min = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
        return year + "-" + month + "-" + day + " " + hours + ":" + min + ":" + seconds;
    }

    //显示基站和聚合
    var markerClusterer = null;
    var marker2;
    var markers = [];

    function showStation() {
        var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><img src='app/img/station.png'/><h4>";
        var sHtml2 = "</h4></li><li>";
        var sHtml3 = "</li></ul><p class='flex-items'><i class='glyphicon glyphicon-map-marker'></i><span>";
        var sHtml4 = "</p><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>车辆编号</th><th>经过时间</th></tr></thead><tbody>";
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
        var time = new Date().getTime();//当前时间
        var start;
        if($scope.user.fixed_query_time == null || $scope.user.fixed_query_time == undefined ){
            start = new Date(time - 60 * 1000 * 60);//一小时
        }else{
            start = new Date(time - 60 * 1000 * $scope.user.fixed_query_time );/*自定义查询时间;*/
        }
        var end = new Date(time);
       
        var carInitNums = showElectsCountInStations(FormatDate(start), FormatDate(end), stationPhyInitNums);
        for (var i = 0; i < $scope.stations.length; i++) {
            tmpStation = $scope.stations[i];
            pt = new BMap.Point(tmpStation.longitude, tmpStation.latitude);
            //基站异常图标
            if (tmpStation.station_status == 1) {
                var myIcon = new BMap.Icon("/app/img/marker_gray.png", new BMap.Size(19, 25));
                marker2 = new BMap.Marker(pt, {icon: myIcon});
            } else {
                marker2 = new BMap.Marker(pt);
            }

            //统计时间内经过改基站的车辆的数

            //var num = tmpStation.station_phy_num;
            //console.log(FormatDate(start) ,FormatDate(end) );
            //showElectsInStation(FormatDate(start), FormatDate(end), num);
            //var carNum = showElectsCountInStation(FormatDate(start), FormatDate(end), num);
            //$scope.labelSet(carNum, marker2);
            //marker2.setTitle(carNum);
            marker2.setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address + '\t' + (carInitNums[tmpStation.station_phy_num]==undefined?0:carInitNums[tmpStation.station_phy_num]));

            marker2.addEventListener("click", function (e) {
                var title_add = new Array();
                title_add = this.getTitle().split('\t');
                //title_add = tmpStation.station_phy_num;
                showElectsInStation(FormatDate(start), FormatDate(end), title_add[0],title_add[2]);  //根据物理编号查找
                var electInfo = '';
                for (var k = 0; k < $scope.electsInStation.length; k++) {
                    //2018-10-15 修改
                    electInfo += "<tr><td title='" + (k + 1) + "'>" + (k + 1) + "</td>" + "<td title='" + $scope.electsInStation[k].plate_num + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].plate_num + "</td>" + "<td title='" + $scope.electsInStation[k].corssTime + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].corssTime + "</td></tr>";
                }
                var infoWindow = new BMap.InfoWindow(sHtml + title_add[0] + sHtml2 + $scope.electsInStation.length + "辆" + sHtml3 + title_add[1] + sHtml4 + electInfo + endHtml);
                var p = e.target;
                var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
                map.openInfoWindow(infoWindow, point);

            });
            markers.push(marker2);
        }
        //最简单的用法，生成一个marker数组，然后调用markerClusterer类即可。
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
    $scope.jizhanBounce = null;
    $scope.tiao = null;
    //表格选中行，对应标注体现出来
    $('#tableArea tbody').on('click', 'tr', function (e) {
        if ($scope.tiao != null) {
            $scope.tiao.setAnimation(null);
        }
        var tablePoint = $(this).context.cells[1].innerHTML;//获取单击表格时的地址
        //console.log(tablePoint);
        for (var g = 0; g < $scope.stations.length; g++) {
            if (tablePoint == $scope.stations[g].station_address) {//表格获取到的地址等于循环基站时的基站地址
               // console.log(tablePoint == $scope.stations[g].station_address)
                var allOverlay = $scope.jizhanBounce;
               // console.log($scope.jizhanBounce)
                var Oe = null;
                for (var i = 0; i < allOverlay.length; i++) {
                    Oe = allOverlay[i].point;
                    if (Oe.lng == $scope.stations[g].longitude && Oe.lat == $scope.stations[g].latitude) {
                        $scope.tiao = allOverlay[i];//保存上一次跳动的基站
                        allOverlay[i].setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
                        return;
                    } else {
                    }
                }
                return;
            }
        }
    });

    //根据时间和基站id获取基站下面的当前所有车辆
    function showElectsCountInStation(startTime, endTime, stationPhyNum) {
    	var count = 0;
    	$.ajax({
    		method: 'GET',
    		url: '/i/elect/findElectsCountByStationIdAndTime',
    		async: false,
    		data: {
    			"startTime": startTime,
    			"endTime": endTime,
    			"stationPhyNum": stationPhyNum
    		}
    	}).success(function (data) {
    		count=data;
    	})
    	return count;
    }
    //根据时间和基站的ids批量获取基站下面的经过车辆数量
    function showElectsCountInStations(startTime, endTime, stationPhyNumStr) {
    	var counts;
    	$.ajax({
    		method: 'GET',
    		url: '/i/elect/findElectsCountByStationsIdAndTime',
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
    		counts=data;
    	})
    	return counts;
    }
    
    //根据时间和基站id获取基站下面的当前所有车辆
    function showElectsInStation(startTime, endTime, stationPhyNum,limit) {
        $.ajax({
            method: 'GET',
            url: '/i/elect/findElectsByStationIdAndTime',
            async: false,
            data: {
//                "startTime": startTime,
//                "endTime": endTime,
                "limit": limit,
                "stationPhyNum": stationPhyNum
            }
        }).success(function (data) {
            $scope.electsInStation = data;
            $rootScope.electLength = data.length;
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
    $scope.clearElectLocation = function () {
        $scope.elecMarker.hide();
        if(markerClusterer!=null){
            markerClusterer._redraw();
        }
        $("#dingweiModal").modal("hide");
    };
    //根据条件定位车辆
    $scope.findElectLocation = function () {
        $scope.clearElectTrace();
        map.clearOverlays();
        if ($scope.keywordTypeForLocation == "1") {
            $scope.plateNum = $scope.keywordForLocation;
            $scope.guaCardNum = null;
        }
        else if ($scope.keywordTypeForLocation == "0") {
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
            //console.log(data);
            $scope.longitude = data.longitude;
            $scope.latitude = data.latitude;
            if ($scope.longitude == undefined || $scope.longitude == null) {
                return alert("未查到相应数据！");
            }

            stationIDforDelete = data.station_phy_num;
            $scope.elecPt = new BMap.Point($scope.longitude, $scope.latitude);
            $scope.elecIcon = new BMap.Icon("../app/img/eb-1.jpg", new BMap.Size(67, 51));
            $scope.elecMarker = new BMap.Marker($scope.elecPt, {icon: $scope.elecIcon});
            $scope.elecMarker.setZIndex(10);//设置单个marker的index


            map.addOverlay($scope.elecMarker);
            $scope.elecMarker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
            map.centerAndZoom($scope.elecPt, 17);
            //用于基站跳动
            $scope.jizhanBounce = map.getOverlays();
            $("#dingweiModal").modal("hide");
        }).error(function () {
            alert("请输入正确的车牌号或者芯片号！")
        });
    };
    $scope.findElectTrace = function () {            //显示车辆轨迹
        if (lushu != null) {
            lushu.stop();
            lushu = null;
        }
        map.clearOverlays();
        //showStation();
        if ($scope.keywordTypeForTrace == "1") {
            $scope.plateNum = $scope.keywordForTrace;
            $scope.guaCardNum = null;
            $scope.electNumForTraceTable = $scope.plateNum;
        }
        else if ($scope.keywordTypeForTrace == "0") {
            $scope.guaCardNum = $scope.keywordForTrace;
            $scope.plateNum = null;
            $scope.electNumForTraceTable = $scope.keywordForTrace;
        }
        else {

        }
        var endTimeForTrace = "";
        if($scope.endTimeForTrace != null && $scope.endTimeForTrace != undefined && $scope.endTimeForTrace != ""){
        	endTimeForTrace = $scope.endTimeForTrace + " 23:59:59";
        }else{
        	endTimeForTrace = $scope.endTimeForTrace ;
        }
        var guijiModal = $("#guijiModal");
        $http.get('/i/elect/findElectTrace', {
            params: {
                "plateNum": $scope.plateNum,
                "guaCardNum": $scope.guaCardNum,
                "startTimeForTrace": $scope.startTimeForTrace,
                "endTimeForTrace": endTimeForTrace
            }
        }).success(function (data) {
            $scope.traceStations = data;
            $scope.traceStationsLength = data.length;
            $scope.pointsArr = [];
            $scope.showOperation = true;
            $scope.showTable = true;
            for (var j = 0; j < data.length; j++) {
                var pointsJ = data[j].longitude;
                var pointsW = data[j].latitude;
                $scope.pointsArr.push(new BMap.Point(pointsJ, pointsW));
            }

            if (data.length == 1)
                alert("该车辆仅经过一个基站：" + data[0].station_name);
            else if (data.length == 0)
                alert("该车辆没有经过任何基站！");
            else{
            	var marker;
                // var lushu;
                var arrPois = $scope.pointsArr;
                console.log(arrPois)
                map.setViewport(arrPois);
                //if (lushu == null) {
                //    //marker = new BMap.Marker(arrPois[0], {
                //    //    icon: new BMap.Icon('../app/img/eb.png',
                //    //        new BMap.Size(50, 30),
                //    //        {anchor: new BMap.Size(27, 23)})
                //    //});
                //    //marker.setZIndex(10);//设置单个marker的index
                //    var label = new BMap.Label($scope.electNumForTraceTable, {offset: new BMap.Size(0, -30)});
                //    label.setStyle({
                //        border: "1px solid rgb(204, 204, 204)",
                //        color: "rgb(0, 0, 0)",
                //        borderRadius: "10px",
                //        padding: "5px 10px",
                //        background: "rgb(255, 255, 255)"
                //    });
                //    //marker.setLabel(label);
                //    //map.addOverlay(marker);
                //    lushu = 1
                //}


                // 实例化一个驾车导航用来生成路线
                var drv = new BMap.WalkingRoute(map, {
                    renderOptions:{
                        map:map,
                        autoViewport:true
                    },
                    onSearchComplete: function(res) {
                        if (drv.getStatus() == BMAP_STATUS_SUCCESS) {
                            var plan = res.getPlan(0);
                            var arrPois = [];
                            for (var j = 0; j < plan.getNumRoutes(); j++) {
                                var route = plan.getRoute(j);
                                arrPois = arrPois.concat(route.getPath());
                            }
                            map.addOverlay(new BMap.Polyline(arrPois, {strokeColor: '#111'}));
                            map.setViewport(arrPois);

                            lushu = new BMapLib.LuShu(map, arrPois, {
                                defaultContent: $scope.electNumForTraceTable,//"从天安门到百度大厦"
                                autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                                icon: new BMap.Icon('../app/img/eb.png', new BMap.Size(50, 30), {anchor: new BMap.Size(27, 23)}),
                                speed: 300,
                                enableRotation: true,//是否设置marker随着道路的走向进行旋转
                                landmarkPois: [
                                    {lng: $scope.pointsArr[0], lat: $scope.pointsArr[1], html: '起点', pauseTime: 2}
                                    ]
                                })
                            }
                        }
                    });
                    var start=new BMap.Point(arrPois[0].lng,arrPois[0].lat);
                    var end=new BMap.Point(arrPois[arrPois.length-1].lng,arrPois[arrPois.length-1].lat);
                    //var otherArr =arrPois.slice(1,arrPois.length-1);
                    //console.log(otherArr);
                    //var wayArr = [];
                    //for(var i = 0;i<otherArr.length;i++){
                    // wayArr.push(new BMap.Point(otherArr[i].lng,otherArr[i].lat))
                    //
                    //
                    // }
                    for(var j=0;j<arrPois.length;j++){
                        drv.search(new BMap.Point(arrPois[j].lng,arrPois[j].lat), new BMap.Point(arrPois[j+1].lng,arrPois[j+1].lat));
                    }
                    //console.log(wayArr)
                    //drv.search(start, end,{waypoints:wayArr});






                                    //for (var i = 0; i < data.length - 1; i++) {
                    
                //    BMapLib.LuShu.prototype._move = function (initPos, targetPos, effect) {
                //        var pointsArr = [initPos, targetPos];  //点数组
                //        var me = this,
                //        //当前的帧数
                //            currentCount = 0,
                //        //步长，米/秒
                //            timer = 10,
                //            step = this._opts.speed / (1000 / timer),
                //        //初始坐标
                //            init_pos = this._projection.lngLatToPoint(initPos),
                //        //获取结束点的(x,y)坐标
                //            target_pos = this._projection.lngLatToPoint(targetPos),
                //        //总的步长
                //            count = Math.round(me._getDistance(init_pos, target_pos) / step);
                //        //显示折线 syj201607191107
                //        this._map.addOverlay(new BMap.Polyline(pointsArr, {
                //            strokeColor: "rgb(102, 179, 255)",
                //            strokeWeight: 2,
                //            strokeOpacity: 1
                //        })); // 画线
                //        //如果小于1直接移动到下一点
                //        if (count < 1) {
                //            me._moveNext(++me.i);
                //            return;
                //        }
                //        me._intervalFlag = setInterval(function () {
                //            //两点之间当前帧数大于总帧数的时候，则说明已经完成移动
                //            if (currentCount >= count) {
                //                clearInterval(me._intervalFlag);
                //                //移动的点已经超过总的长度
                //                if (me.i > me._path.length) {
                //                    return;
                //                }
                //                //运行下一个点
                //                me._moveNext(++me.i);
                //            } else {
                //                currentCount++;
                //                var x = effect(init_pos.x, target_pos.x, currentCount, count),
                //                    y = effect(init_pos.y, target_pos.y, currentCount, count),
                //                    pos = me._projection.pointToLngLat(new BMap.Pixel(x, y));
                //                //设置marker
                //                if (currentCount == 1) {
                //                    var proPos = null;
                //                    if (me.i - 1 >= 0) {
                //                        proPos = me._path[me.i - 1];
                //                    }
                //                    if (me._opts.enableRotation == true) {
                //                        me.setRotation(proPos, initPos, targetPos);
                //                    }
                //                    if (me._opts.autoView) {
                //                        if (!me._map.getBounds().containsPoint(pos)) {
                //                            me._map.setCenter(pos);
                //                        }
                //                    }
                //                }
                //                //正在移动
                //                me._marker.setPosition(pos);
                //                //设置自定义overlay的位置
                //                me._setInfoWin(pos);
                //            }
                //        }, timer);
                //    };
                //}
            

                    //lushu = new BMapLib.LuShu(map, arrPois, {
                    //    defaultContent: $scope.electNumForTraceTable,//"从天安门到百度大厦"
                    //    autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                    //    icon: new BMap.Icon('../app/img/eb.png', new BMap.Size(50, 30), {anchor: new BMap.Size(27, 23)}),
                    //    speed: 350,
                    //    enableRotation: true,//是否设置marker随着道路的走向进行旋转
                    //    landmarkPois: [
                    //        {lng: $scope.pointsArr[0], lat: $scope.pointsArr[1], html: '加油站', pauseTime: 2}
                    //    ]
                    //
                    //});
                    //marker.addEventListener("click", function () {
                    //    marker.enableMassClear();   //设置后可以隐藏改点的覆盖物
                    //    marker.hide();
                    //    lushu.start();
                    //});



                    //绑定事件
                    $("run").onclick = function () {
                        marker.enableMassClear(); //设置后可以隐藏改点的覆盖物
                        marker.hide();
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

            guijiModal.modal("hide");

        }).error(function () {
            $("#positionTable").removeClass("rightToggle");
            $scope.showOperation = false;
            $scope.showTable = false;
            alert("请填写正确的车牌号或者防盗芯片ID!");
        });
    };
    $scope.showOperation = false;
    $scope.showTable = false;

    $scope.showDetail = function () {
        $scope.showTable = !($scope.showTable);
        $("#detail i").toggleClass("down-up");
    };
    $scope.clearElectTrace = function () {
        if (lushu != null) {
            lushu.stop();
        }
        $scope.showOperation = false;
        $scope.showTable = false;
        map.clearOverlays();
        if(markerClusterer!=null){
            markerClusterer._redraw();
        }
        /*if ($scope.jizhanFlag == 0) {
            if (markerClusterer != null) {
                markerClusterer.clearMarkers();
            }
        }
        else if ($scope.jizhanFlag == 1) {
        }*/
        if ($scope.elecMarker != null) {
            map.addOverlay($scope.elecMarker);
        }
    };

   /* $scope.goHome = function () {
        $state.reload('home');
    };*/

    var navBtn = $(".home-title a");
    navBtn.click(function () {
        navBtn.removeClass("active");
        $(this).addClass("active");

    });
    //轨迹选择日期
    $('#homeDateStart').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $scope.startTimeForTrace=$scope.doDateStr(new Date())
    $('#homeDateEnd').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $scope.endTimeForTrace=$scope.doDateStr(new Date())
    $scope.setCog = function(){
        $scope.center = map.getCenter();
        $scope.centerLat = $scope.center.lat;
        $scope.centerLng = $scope.center.lng;
        $scope.zoomNow = map.getZoom();
       // console.log($scope.zoomNow);
        //console.log($scope.centerLat);
       // console.log($scope.centerLng)
    };

    //单击获取点击的经纬度
   /* map.addEventListener("dblclick",function(e){
        alert("您的经度和纬度分别为："+ e.point.lng + " , " + e.point.lat+'\n'+"当前地图级别为：" + map.getZoom());
    });*/


    //设置参数
    $scope.setConfig = function () {
        if($scope.configTim!= null && $scope.configTim > 0){
            $http.get('/i/user/updateUserSetting', {
                params: {
                    "userId" : $scope.user.user_id,
                    "fixedZoom": $scope.zoomNow,
                    "fixedLon": $scope.centerLng,
                    "fixedLat": $scope.centerLat,
                    "fixedQueryTime": $scope.configTim
                }
            }).success(function (data) {
                
                $scope.user.fixed_lat = $scope.centerLat;
                $scope.user.fixed_lon = $scope.centerLng;
                $scope.user.fixed_zoom = $scope.zoomNow;
                $scope.user.fixed_query_time = $scope.configTim;
                //$scope.load();
                timeelect();
                /*if($scope.user.fixed_lat != null && $scope.user.fixed_lat != undefined && $scope.user.fixed_lon != null && $scope.user.fixed_lon != undefined){
                    map.centerAndZoom(new BMap.Point($scope.user.fixed_lon, $scope.user.fixed_lat), $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别

                }*/
                $("#cog").modal("hide");
            }).error(function () {
                alert("请输入完整的信息！")
            });
        }else{
            alert("时间必须大于0！");
        }

    };

    //定是函数
    


    //显示基站聚合开关选项控制
    $scope.jizhanFlag = 1;
    $('#xsjizhan').parent(".swichWrap").click(function () {
        if ($scope.jizhanFlag == 0) {
            alert("显示基站聚合");
            //map.clearOverlays();
            //if (markerClusterer != null) {
            //    markerClusterer.clearMarkers();
            //}
            showStation();
            $scope.jizhanFlag = 1;
        }
        else if ($scope.jizhanFlag == 1) {
            alert("隐藏基站");
            $scope.jizhanFlag = 0;
            if (markerClusterer != null) {
            	markerClusterer._redraw();
                markerClusterer.clearMarkers();
            }
            map.clearOverlays();
        }
        showCssFlag('#xsjizhan');
    });

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
});
