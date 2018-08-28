coldWeb.controller('home', function ($rootScope, $scope, $state, $cookies, $http, $location) {
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
            //$scope.cityName = data.name;
            $scope.cityName = "芒市";
            //console.log($scope.cityName)
            map.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
            map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
            //map.setMapStyle({style:'light'})
            //map.disableDoubleClickZoom();
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
                //console.log(data)
                showStation();
            });
            // 获取登记车辆数量
            $http.get('/i/elect/findElectsList', {
                params: {
                    "areaPower": $scope.user.area_power,
                    "cityPower": $scope.user.city_power,
                    "proPower": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.elects = data;
            });
            // 获取黑名单车辆数量
            $http.get('/i/blackelect/findBlackelectsList', {
                params: {
                    "areaPower": $scope.user.area_power,
                    "cityPower": $scope.user.city_power,
                    "proPower": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.blackElects = data;
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
            $http.get('/i/electalarm/findElectAlarmsList', {
                params: {
                    "areaPower": $scope.user.area_power,
                    "cityPower": $scope.user.city_power,
                    "proPower": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.electAlarms = data;
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

    //显示基站和聚合
    var markerClusterer=null;
    function showStation() {
        var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><img src='app/img/station.png'/><h4>";
        var sHtml2 = "</h4></li><li>";
        var sHtml3 = "</li></ul><p class='flex-items'><i class='glyphicon glyphicon-map-marker'></i><span>";
        var sHtml4 = "</p><ul class='flex flex-time'><li class='active searchTime'>1分钟</li><li class='searchTime'>5分钟</li><li class='searchTime'>1小时</li></ul><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>车辆编号</th><th>经过时间</th></tr></thead><tbody>";
        var endHtml = "</tbody></table></div></div>";
        var pt;
        var marker2;
        var markers = []; //存放聚合的基站
        //var infoWindow;
        var tmpStation;

        //给每一个基站添加监听事件 和窗口信息
        for (var i = 0; i < $scope.stations.length; i++) {
            tmpStation = $scope.stations[i];
            pt = new BMap.Point(tmpStation.longitude, tmpStation.latitude);
            marker2 = new BMap.Marker(pt);

            //统计一分钟内经过改基站的车辆的数
            var time = new Date().getTime();//当前时间
            var start = new Date(time - 60*1000*60*60*2);//一分钟
            var end = new Date(time);

            var num = tmpStation.station_phy_num;
            function FormatDate (strTime) {
                var date = new Date(strTime);
                return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+ date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
            }
            //console.log(FormatDate(start) ,FormatDate(end) );
            showElectsInStation(FormatDate(start), FormatDate(end), num);
            //console.log(num);
            var carNum=$scope.electsInStation.length;
           // console.log(carNum)

            var label = new BMap.Label(carNum,{offset:new BMap.Size(0,-15)});
            if(carNum==0){
                label.setStyle({
                    color : "transparent",
                    fontSize : "16px",
                    backgroundColor :"transparent",
                    border :"0",
                    fontWeight :"bold",
                    width:'80px',
                    display:"none"
                });
            }else{
            label.setStyle({
                color : "rgb(102, 179, 255)",
                fontSize : "16px",
                backgroundColor :"transparent",
                border :"0",
                fontWeight :"bold",
                width:'80px',
                display:"block"
            });
            }
            marker2.setLabel(label);
            marker2.setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address);
            //console.log($scope.stations.length);

            marker2.addEventListener("click", function (e) {
                var title_add = new Array();
                title_add = this.getTitle().split('\t');
                showElectsInStation(null,null,title_add[0]);  //根据物理编号查找
                var electInfo='';
                for (var k = 0; k < $scope.electsInStation.length; k++) {
                    electInfo += "<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + (k + 1) + "</td>" + "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].plate_num + "</td>" + "<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.electsInStation[k].corssTime + "</td></tr>";
                }
                var infoWindow = new BMap.InfoWindow(sHtml + title_add[0] + sHtml2 + $scope.electsInStation.length + sHtml3 + title_add[1] + sHtml4 + electInfo + endHtml);
                var p = e.target;
                var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
                map.openInfoWindow(infoWindow, point);

            });
            markers.push(marker2);
        }
        if ($scope.jizhanjuheFlag == 0) {
            markerClusterer = new BMapLib.MarkerClusterer(map, {markers: markers});
        }
        else if ($scope.jizhanjuheFlag == 1) {
            map.clearOverlays();
            for (var i = 0; i < markers.length; i++) {
                map.addOverlay(markers[i]);
            }
        }
    }

    function clusterStation() {  //对基站进行聚合
        var markerClusterer = new BMapLib.MarkerClusterer(map, {markers: markers});
    }

//	 //鼠标绘制多边形，选择区域并弹出信息框，展示显示的基站
//	    var overlaysDraw = [];
//		var overlaycomplete = function(e){
//	      		console.log(e.overlay.getPath());  //多边形轨迹
//	      		
//	      		overlaysDraw.push(e.overlay);
//		};
//	    var styleOptions = {
//	            strokeColor:"blue",    //边线颜色。
//	            fillColor:"red",      //填充颜色。当参数为空时，圆形将没有填充效果。
//	            strokeWeight: 3,       //边线的宽度，以像素为单位。
//	            strokeOpacity: 0.8,	   //边线透明度，取值范围0 - 1。
//	            fillOpacity: 0.6,      //填充的透明度，取值范围0 - 1。
//	            strokeStyle: 'solid' //边线的样式，solid或dashed。
//	        }
//	    //实例化鼠标绘制工具
//	    var drawingManager = new BMapLib.DrawingManager(map, {
//	        isOpen: false, //是否开启绘制模式
//	        enableDrawingTool: true, //是否显示工具栏
//	        drawingToolOptions: {
//	            anchor: BMAP_ANCHOR_TOP_LEFT, //位置
//	            offset: new BMap.Size(5, 5), //偏离值
//	          drawingModes : [
//	            BMAP_DRAWING_POLYGON,
//	         ]
//	        },
//	        polygonOptions: styleOptions //多边形的样式
//	    }); 
//	    drawingManager.addEventListener('overlaycomplete', overlaycomplete);
//	    function clearAll() {
//			for(var i = 0; i < overlaysDraw.length; i++){
//	            map.removeOverlay(overlaysDraw[i]);
//	        }
//			overlaysDraw.length = 0   
//	    }
//

    //根据时间和基站id获取基站下面的当前所有车辆
    function showElectsInStation(startTime, endTime, stationPhyNum) {
        $.ajax({
            method: 'GET',
            url :'/i/elect/findElectsByStationIdAndTime',
            async:false,
            data : {
                "startTime": startTime,
                "endTime": endTime,
                "stationPhyNum": stationPhyNum
            }
        }).success(function(data){
           // console.log(data);
            $scope.electsInStation = data;
            //$scope.electsInStation1 = data.slice(2,8);
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
    var elecMarker;
    $scope.clearElectLocation = function () {
        // map.clearOverlays()
        elecMarker.hide();
        $("#dingweiModal").modal("hide");


    };
    //根据条件定位车辆
    $scope.findElectLocation = function () {
        if ($scope.keywordTypeForLocation == "1") {
            $scope.plateNum = $scope.keywordForLocation;
        }
        else if ($scope.keywordTypeForLocation == "0") {
            $scope.guaCardNum = $scope.keywordForLocation;
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

            stationIDforDelete = data.station_phy_num;
            var elecPt = new BMap.Point($scope.longitude, $scope.latitude);
            var elecIcon = new BMap.Icon("../app/img/eb.jpg", new BMap.Size(67, 51));
            elecMarker = new BMap.Marker(elecPt, {icon: elecIcon});

            map.addOverlay(elecMarker);
            map.centerAndZoom(elecPt, 17);
            //console.log(elecMarker);
        });
        $("#dingweiModal").modal("hide");
    };

    //根据条件查询车辆轨迹
    /*----------重要内容，不能删除----------------*/
    //var walking;
    //function drawLine(p1, p2) {
    //   // walking = new BMap.WalkingRoute(map, {renderOptions:{map: map, autoViewport: true}});
    //    walking.search(p1, p2);
    //}
    /*----------重要内容，不能删除----------------*/

    $scope.findElectTrace = function () {            //显示车辆轨迹
        map.clearOverlays();
        showStation();
        if ($scope.keywordTypeForTrace == "1") {
            $scope.plateNum = $scope.keywordForTrace;
            $scope.electNumForTraceTable = $scope.plateNum;
        }
        else if ($scope.keywordTypeForTrace == "0") {
            $scope.guaCardNum = $scope.keywordForTrace;
            $scope.electNumForTraceTable = $scope.keywordForTrace;
        }
        else {

        }
        $http.get('/i/elect/findElectTrace', {
            params: {
                "plateNum": $scope.plateNum,
                "guaCardNum": $scope.guaCardNum,
                "startTimeForTrace": $scope.startTimeForTrace,
                "endTimeForTrace": $scope.endTimeForTrace
            }
        }).success(function (data) {
            $scope.traceStations = data;
            $scope.traceStationsLength = data.length;
            //$("#positionTable").addClass("rightToggle");
            //console.log(data);
            $scope.pointsArr = [];
            for(var j=0;j<data.length;j++){
                var pointsJ = data[j].station.longitude;
                var pointsW = data[j].station.latitude;
                $scope.pointsArr.push(new BMap.Point(pointsJ,pointsW));
            }
            console.log($scope.pointsArr);
            //var arrPois=[new BMap.Point(116.403984,39.914004),new BMap.Point(116.402116,39.913938),new BMap.Point(116.402116,39.913938)];
            var lushu=null;
            if (data.length == 1)
                alert("该车辆仅经过一个基站：" + data[0].station.station_name);
            else if (data.length == 0)
                alert("该车辆没有经过任何基站！");
            else
                for (var i = 0; i < data.length - 1; i++) {
                    var marker;
                    var lushu;
                    var arrPois=$scope.pointsArr;
                    //    sensitiveAreaMap.addOverlay(new BMap.Polyline(arrPois, {strokeColor: '#111'})); //不画线
                    map.setViewport(arrPois);
                    if(lushu==null){
	                    marker=new BMap.Marker(arrPois[0],{
	                        icon  : new BMap.Icon('http://developer.baidu.com/map/jsdemo/img/car.png', new BMap.Size(52,26),{anchor : new BMap.Size(27, 13)})
	                    });
	                    var label = new BMap.Label("粤A30780",{offset:new BMap.Size(0,-30)});
	                    label.setStyle({border:"1px solid rgb(204, 204, 204)",color: "rgb(0, 0, 0)",borderRadius:"10px",padding:"5px",background:"rgb(255, 255, 255)",});
	                    marker.setLabel(label);
	                    map.addOverlay(marker);lushu=1
                    }
                    BMapLib.LuShu.prototype._move=function(initPos,targetPos,effect) {
                        var pointsArr=[initPos,targetPos];  //点数组
                        var me = this,
                        //当前的帧数
                            currentCount = 0,
                        //步长，米/秒
                            timer = 10,
                            step = this._opts.speed / (1000 / timer),
                        //初始坐标
                            init_pos = this._projection.lngLatToPoint(initPos),
                        //获取结束点的(x,y)坐标
                            target_pos = this._projection.lngLatToPoint(targetPos),
                        //总的步长
                            count = Math.round(me._getDistance(init_pos, target_pos) / step);
                        //显示折线 syj201607191107
                        this._map.addOverlay(new BMap.Polyline(pointsArr, {
                            strokeColor : "#111",
                            strokeWeight : 5,
                            strokeOpacity : 0.5
                        })); // 画线
                        //如果小于1直接移动到下一点
                        if (count < 1) {
                            me._moveNext(++me.i);
                            return;
                        }
                        me._intervalFlag = setInterval(function() {
                            //两点之间当前帧数大于总帧数的时候，则说明已经完成移动
                            if (currentCount >= count) {
                                clearInterval(me._intervalFlag);
                                //移动的点已经超过总的长度
                                if(me.i > me._path.length){
                                    return;
                                }
                                //运行下一个点
                                me._moveNext(++me.i);
                            }else {
                                currentCount++;
                                var x = effect(init_pos.x, target_pos.x, currentCount, count),
                                    y = effect(init_pos.y, target_pos.y, currentCount, count),
                                    pos = me._projection.pointToLngLat(new BMap.Pixel(x, y));
                                //设置marker
                                if(currentCount == 1){
                                    var proPos = null;
                                    if(me.i - 1 >= 0){
                                        proPos = me._path[me.i - 1];
                                    }
                                    if(me._opts.enableRotation == true){
                                        me.setRotation(proPos,initPos,targetPos);
                                    }
                                    if(me._opts.autoView){
                                        if(!me._map.getBounds().containsPoint(pos)){
                                            me._map.setCenter(pos);
                                        }
                                    }
                                }
                                //正在移动
                                me._marker.setPosition(pos);
                                //设置自定义overlay的位置
                                me._setInfoWin(pos);
                            }
                        },timer);
                    };

                    
                    lushu = new BMapLib.LuShu(map,arrPois,{
                        defaultContent:"粤A30780",//"从天安门到百度大厦"
                        autoView:true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                        icon  : new BMap.Icon('http://developer.baidu.com/map/jsdemo/img/car.png', new BMap.Size(52,26),{anchor : new BMap.Size(27, 13)}),
                        speed: 500,
                        enableRotation:true,//是否设置marker随着道路的走向进行旋转
                        landmarkPois:[
                            {lng:$scope.pointsArr[0],lat:$scope.pointsArr[1],html:'加油站',pauseTime:2}
                        ]

                    });

                    

                    marker.addEventListener("click",function(){
                        marker.enableMassClear();   //设置后可以隐藏改点的覆盖物
                        marker.hide();
                        lushu.start();
                        //map.clearOverlays();  //清除所有覆盖物
                    });

                    //drv.search('天安门', '百度大厦');
                    // lushu.start();
                    // lushu.pause();
                    //绑定事件
                    $("run").onclick = function(){
                        marker.enableMassClear(); //设置后可以隐藏改点的覆盖物
                        marker.hide();
                        lushu.start();
                        //map.clearOverlays();    //清除所有覆盖物
                    }
                    $("stop").onclick = function(){
                        lushu.stop();
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




            /*-----------------重要内容，不要删除-------------------*/
                    //walking = new BMap.WalkingRoute(map, {renderOptions: {map: map, autoViewport: true},
                    //    onPolylinesSet:function(routes) {
                    //        searchRoute = routes[0].getPolyline();//导航路线
                    //        map.addOverlay(searchRoute);
                    //    },
                    //    onMarkersSet:function(routes) {
                    //        var myIcon3 = new BMap.Icon("/app/img/jingP.png", new BMap.Size(40,60));
                    //        var markerstart = new BMap.Marker(routes[0].marker.getPosition() ,{icon:myIcon3}); // 创建点
                    //        markerstart.setZIndex(1);
                    //
                    //        map.removeOverlay(routes[0].marker); //删除起点
                    //
                    //         map.addOverlay(markerstart);
                    //        var markerend = new BMap.Marker(routes[1].marker.getPosition() ,{icon:myIcon3}); // 创建点
                    //        markerend.setZIndex(1);
                    //
                    //        map.removeOverlay(routes[1].marker);//删除终点
                    //
                    //        //            map.addOverlay(markerend);
                    //    }
                    //
                    //
                    //
                    //});

                    //var p1 = new BMap.Point(data[i].station.longitude, data[i].station.latitude);
                    //var p2 = new BMap.Point(data[i + 1].station.longitude, data[i + 1].station.latitude);
                    //
                    //drawLine(p1, p2);

                //}
            //增加覆盖物
            //var startP = new BMap.Point(data[0].station.longitude, data[0].station.latitude);
            //var endP = new BMap.Point(data[data.length-1].station.longitude, data[data.length-1].station.latitude);
            //var marker2;
            //var marker3;
            //var marker4
            //var myIcon;
            //if(data[0].station.longitude== data[data.length-1].station.longitude  &&  data[0].station.latitude==data[data.length-1].station.latitude){
            //    myIcon = new BMap.Icon("/app/img/addP.png", new BMap.Size(40,60));
            //    marker4 = new BMap.Marker(startP,{icon:myIcon});
            //    map.addOverlay(marker4);
            //    marker4.setZIndex(3);
            //}else{
            //    myIcon = new BMap.Icon("/app/img/startP.png", new BMap.Size(40,60));
            //    marker2 = new BMap.Marker(startP,{icon:myIcon});  // 创建标注
            //    map.addOverlay(marker2);            // 将标注添加到地图中
            //    var endIcon = new BMap.Icon("/app/img/endP.png", new BMap.Size(40,60));
            //    marker3 = new BMap.Marker(endP,{icon:endIcon});  // 创建标注
            //    map.addOverlay(marker3);              // 将标注添加到地图中
            //    marker3.setZIndex(2);
            //    marker2.setZIndex(2);
            //}
            /*-----------------重要内容，不要删除-------------------*/


        });
        $("#guijiModal").modal("hide");
        $("#positionTable .dismisPosition").removeClass("fa-angle-left").addClass("fa-angle-right");
    };

    $scope.clearElectTrace = function () {
        //walking.clearResults();
        $("#guijiModal").modal("hide");
        map.clearOverlays();
        relitu1();
        if ($scope.jizhanFlag == 0) {
            if(markerClusterer!=null){
                markerClusterer.clearMarkers();
            }
        }
        else if ($scope.jizhanFlag == 1) {
            showStation();
        }
    };

    $scope.goHome = function () {
        $state.reload();
    };

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

    $('#homeDateEnd').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });

    //热力图
    var heatmapOverlay;
    function heatmap() {
        heatmapOverlay = new BMapLib.HeatmapOverlay({"radius": 20});
        map.addOverlay(heatmapOverlay);
        heatmapOverlay.setDataSet({data: $scope.thermodynamics, max: 5});
       // console.log($scope.thermodynamics);
        heatmapOverlay.show();
        //var label2 = new BMap.Label("标题显示",{offset:new BMap.Size(4,-15)});
        //heatmapOverlay.setLabel(label2);
    }
    $scope.showReLiTu = function () {
        $http.get('/i/elect/findElectsNumByStations', {
            params: {
                "proPower": $scope.user.pro_power,
                "cityPower": $scope.user.city_power,
                "areaPower": $scope.user.area_power
            }
        }).success(function (data) {
            $scope.thermodynamics = data;
            heatmap();
        });
    };

    //热力图开关选项控制
    $scope.relituFlag = 0;
    $('#relitu').parent(".swichWrap").click(function () {
        if ($scope.relituFlag == 0) {
            $scope.showReLiTu();
            $scope.relituFlag = 1;
        }
        else if ($scope.relituFlag == 1) {
            //alert("隐藏热力图");
            heatmapOverlay.hide();
            $scope.relituFlag = 0;
        }
        showCssFlag('#relitu');
    });

    function relitu1(){
        if($scope.relituFlag==1){
            //alert("显示热力图");
            $scope.relituFlag= 1;
            $scope.showReLiTu();
        }else if($scope.relituFlag==0){
            //alert("隐藏热力图");
            $scope.relituFlag= 0;
            if(heatmapOverlay){
                heatmapOverlay.hide();
            }
        }
    }

    //显示基站开关选项控制
    $scope.jizhanFlag = 1;
    $('#xsjizhan').parent(".swichWrap").click(function () {
        if ($scope.jizhanFlag == 0) {
            //alert("显示基站");
            showStation();
            $scope.jizhanFlag = 1;
            relitu1();
        }
        else if ($scope.jizhanFlag == 1) {
            //alert("隐藏基站");
            $("#jizhanjuhe").css('background-color', '#fff');
            $("#jizhanjuhe").parent().css('background-color', '#ccc');
            $("#jizhanjuhe").parent().parent().removeClass("active");
            $("#jizhanjuhe").removeClass("active");
            $scope.jizhanjuheFlag = 1;
            $scope.jizhanFlag = 0;
            if(markerClusterer!=null){
                markerClusterer.clearMarkers();
            }
            map.clearOverlays();
            relitu1();
        }
        showCssFlag('#xsjizhan');
    });

    //基站聚合开关选项控制
    $scope.jizhanjuheFlag = 1;
    $('#jizhanjuhe').parent(".swichWrap").click(function () {
        if($scope.jizhanFlag == 1){
            if ($scope.jizhanjuheFlag == 1) {
                $scope.jizhanjuheFlag = 0;
                showStation();
            }
            else if ($scope.jizhanjuheFlag == 0) {
                //markers=null;
                map.clearOverlays();
                $scope.jizhanjuheFlag = 1;
                showStation();
                if(markerClusterer!=null){
                    markerClusterer.clearMarkers();
                }
                relitu1();

            }
            showCssFlag('#jizhanjuhe');
        }else{
            alert("请打开基站按钮");
        }

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

    $(".dismis").click(function () {
        $(this).parents('#positionTable').toggleClass("rightToggle");
        if ($(this).hasClass("fa-angle-left")) {
            $(this).removeClass("fa-angle-left").addClass("fa-angle-right")
        } else {
            $(this).removeClass("fa-angle-right").addClass("fa-angle-left")
        }
    });
});
