coldWeb.controller('home', function ($rootScope, $scope, $state, $cookies, $http, $location) {
    var map = new BMap.Map("allmap", {
        minZoom: 5,
        maxZoom: 30
    });    // 创建Map实例
    $.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
        $scope.user = data;
        //console.log(data);
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
            var myGeo = new BMap.Geocoder();
            myGeo.getPoint($scope.cityName, function (point) {
                $scope.point = point;
                map.centerAndZoom(new BMap.Point($scope.point.lng, $scope.point.lat), 15); // 初始化地图,设置中心点坐标和地图级别
                map.centerAndZoom($scope.cityName, 16); // 初始化地图,设置中心点坐标和地图级别
                map.setCurrentCity($scope.cityName);
                map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
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
                $http.get('/i/elect/findElectsList', {
                    params: {
                        "areaPower": $scope.user.area_power,
                        "cityPower": $scope.user.city_power,
                        "proPower": $scope.user.pro_power
                    }
                }).success(function (data) {
                    $scope.elects = data;
                });
            });
            //});
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
            var time = new Date().getTime();//当前时间
            var start = new Date(time - 60 * 1000 * 60);//一小时
            var end = new Date(time);
            var num = tmpStation.station_phy_num;

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

            //console.log(FormatDate(start) ,FormatDate(end) );
            showElectsInStation(FormatDate(start), FormatDate(end), num);
            var carNum = $scope.electsInStation.length;
            //$scope.labelSet(carNum, marker2);
            //marker2.setTitle(carNum);
            marker2.setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address + '\t' + carNum);

            marker2.addEventListener("click", function (e) {
                var title_add = new Array();
                title_add = this.getTitle().split('\t');
                //title_add = tmpStation.station_phy_num;
                showElectsInStation(FormatDate(start), FormatDate(end), title_add[0]);  //根据物理编号查找
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
        var markerClusterer = new BMapLib.MarkerClusterer(map,
            {
                markers: markers,
                girdSize: 100,
                styles: [{
                    url: '../app/img/blue.png',
                    size: new BMap.Size(92, 92),
                    backgroundColor: '#4783E7',
                    textColor: '#fff',
                    textSize: 14,
                    lineHeight: 25
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
        console.log(tablePoint);
        for (var g = 0; g < $scope.stations.length; g++) {
            if (tablePoint == $scope.stations[g].station_address) {//表格获取到的地址等于循环基站时的基站地址
                console.log(tablePoint == $scope.stations[g].station_address)
                var allOverlay = $scope.jizhanBounce;
                console.log($scope.jizhanBounce)
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
    function showElectsInStation(startTime, endTime, stationPhyNum) {
        $.ajax({
            method: 'GET',
            url: '/i/elect/findElectsByStationIdAndTime',
            async: false,
            data: {
                "startTime": startTime,
                "endTime": endTime,
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
            console.log(data);
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
        var guijiModal = $("#guijiModal");
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
            $scope.pointsArr = [];
            $scope.showOperation = true;
            $scope.showTable = true;
            for (var j = 0; j < data.length; j++) {
                var pointsJ = data[j].station.longitude;
                var pointsW = data[j].station.latitude;
                $scope.pointsArr.push(new BMap.Point(pointsJ, pointsW));
            }

            if (data.length == 1)
                alert("该车辆仅经过一个基站：" + data[0].station.station_name);
            else if (data.length == 0)
                alert("该车辆没有经过任何基站！");
            else
                for (var i = 0; i < data.length - 1; i++) {
                    var marker;
                    // var lushu;
                    var arrPois = $scope.pointsArr;
                    map.setViewport(arrPois);
                    if (lushu == null) {
                        marker = new BMap.Marker(arrPois[0], {
                            icon: new BMap.Icon('../app/img/eb.png',
                                new BMap.Size(50, 30),
                                {anchor: new BMap.Size(27, 23)})
                        });
                        marker.setZIndex(10);//设置单个marker的index
                        var label = new BMap.Label($scope.electNumForTraceTable, {offset: new BMap.Size(0, -30)});
                        label.setStyle({
                            border: "1px solid rgb(204, 204, 204)",
                            color: "rgb(0, 0, 0)",
                            borderRadius: "10px",
                            padding: "5px 10px",
                            background: "rgb(255, 255, 255)"
                        });
                        marker.setLabel(label);
                        map.addOverlay(marker);
                        lushu = 1
                    }
                    BMapLib.LuShu.prototype._move = function (initPos, targetPos, effect) {
                        var pointsArr = [initPos, targetPos];  //点数组
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
                            strokeColor: "rgb(102, 179, 255)",
                            strokeWeight: 2,
                            strokeOpacity: 1
                        })); // 画线
                        //如果小于1直接移动到下一点
                        if (count < 1) {
                            me._moveNext(++me.i);
                            return;
                        }
                        me._intervalFlag = setInterval(function () {
                            //两点之间当前帧数大于总帧数的时候，则说明已经完成移动
                            if (currentCount >= count) {
                                clearInterval(me._intervalFlag);
                                //移动的点已经超过总的长度
                                if (me.i > me._path.length) {
                                    return;
                                }
                                //运行下一个点
                                me._moveNext(++me.i);
                            } else {
                                currentCount++;
                                var x = effect(init_pos.x, target_pos.x, currentCount, count),
                                    y = effect(init_pos.y, target_pos.y, currentCount, count),
                                    pos = me._projection.pointToLngLat(new BMap.Pixel(x, y));
                                //设置marker
                                if (currentCount == 1) {
                                    var proPos = null;
                                    if (me.i - 1 >= 0) {
                                        proPos = me._path[me.i - 1];
                                    }
                                    if (me._opts.enableRotation == true) {
                                        me.setRotation(proPos, initPos, targetPos);
                                    }
                                    if (me._opts.autoView) {
                                        if (!me._map.getBounds().containsPoint(pos)) {
                                            me._map.setCenter(pos);
                                        }
                                    }
                                }
                                //正在移动
                                me._marker.setPosition(pos);
                                //设置自定义overlay的位置
                                me._setInfoWin(pos);
                            }
                        }, timer);
                    };


                    lushu = new BMapLib.LuShu(map, arrPois, {
                        defaultContent: $scope.electNumForTraceTable,//"从天安门到百度大厦"
                        autoView: true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                        icon: new BMap.Icon('../app/img/eb.png', new BMap.Size(50, 30), {anchor: new BMap.Size(27, 23)}),
                        speed: 350,
                        enableRotation: true,//是否设置marker随着道路的走向进行旋转
                        landmarkPois: [
                            {lng: $scope.pointsArr[0], lat: $scope.pointsArr[1], html: '加油站', pauseTime: 2}
                        ]

                    });
                    marker.addEventListener("click", function () {
                        marker.enableMassClear();   //设置后可以隐藏改点的覆盖物
                        marker.hide();
                        lushu.start();
                    });

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
        if ($scope.jizhanFlag == 0) {
            if (markerClusterer != null) {
                markerClusterer.clearMarkers();
            }
        }
        else if ($scope.jizhanFlag == 1) {
        }
        if ($scope.elecMarker != null) {
            map.addOverlay($scope.elecMarker);
        }
    };

    $scope.goHome = function () {
        $state.reload('home');
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

    $scope.getZoom = function () {
        $scope.center = map.getCenter();
        $scope.centerLat = $scope.center.lat;
        $scope.centerLng = $scope.center.lng;
        $scope.zoomNow = map.getZoom();
        console.log($scope.zoomNow);
        alert("当前地图中心点坐标为：" + $scope.centerLng + ", " + $scope.centerLat + "当前地图缩放级别为：" + $scope.zoomNow)
    };

    //显示基站开关选项控制
    $scope.jizhanFlag = 1;
    $('#xsjizhan').parent(".swichWrap").click(function () {
        if ($scope.jizhanFlag == 0) {
            alert("显示基站");
            showStation();
            $scope.jizhanFlag = 1;
        }
        else if ($scope.jizhanFlag == 1) {
            alert("隐藏基站");
            $scope.jizhanFlag = 0;
            if (markerClusterer != null) {
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