coldWeb.controller('stationManage', function ($rootScope, $scope, $state,
                                              $cookies, Upload, $http, $location) {
    var mapStation = new BMap.Map("stationMap", {
        minZoom: 5,
        maxZoom: 30
    }); // 创建Map实例
    $scope.load = function () {
        $.ajax({
            type: "GET",
            cache: false,
            dataType: 'json',
            url: '/i/user/findUser'
        }).success(
            function (data) {
                $rootScope.admin = data;
                if ($rootScope.admin == null
                    || $rootScope.admin.user_id == 0
                    || admin.user_id == undefined) {
                    url = "http://" + $location.host() + ":"
                    + $location.port() + "/login.html";
                    window.location.href = url;
                }
                $scope.getStations();
            });
        // 根据用户的区域权限定位城市，如果为超级管理员暂时定位喀什
        $http.get('/i/city/findCityNameByUserPower', {
            params: {
                "areaID": $scope.admin.area_power,
                "cityID": $scope.admin.city_power,
                "proID": $scope.admin.pro_power
            }
        }).success(function (data) {

            $scope.cityName = data.name;
            var myGeo = new BMap.Geocoder();
            myGeo.getPoint($scope.cityName, function (point) {
                $scope.point = point;
                if ($rootScope.admin.fixed_lat == null || $rootScope.admin.fixed_lat == undefined || $rootScope.admin.fixed_lon == null || $rootScope.admin.fixed_lon == undefined) {
                    mapStation.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
                } else {
                    mapStation.centerAndZoom(new BMap.Point($rootScope.admin.fixed_lon, $rootScope.admin.fixed_lat), $rootScope.admin.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                    //map.centerAndZoom($scope.cityName, $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                }

                //mapStation.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
                mapStation.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
                mapStation.disableDoubleClickZoom();
                // 获取基站
                $http.get('/i/station/findAllStationsForMap', {
                    params: {
                        "proPower": $scope.admin.pro_power,
                        "cityPower": $scope.admin.city_power,
                        "areaPower": $scope.admin.area_power
                    }
                }).success(function (data) {
                    $scope.stations = data;
                    showStation();
                });
            });
        });
    };

    // 添加地图搜索框
    function G(id) {
        return document.getElementById(id);
    }

    // 建立一个自动完成的对象
    var ac = new BMap.Autocomplete({
        "input": "suggestId",
        "location": mapStation
    });

    // 鼠标放在下拉列表上的事件
    ac.addEventListener("onhighlight", function (e) {
        var str = "";
        var _value = e.fromitem.value;
        var value = "";
        if (e.fromitem.index > -1) {
            value = _value.province + _value.city + _value.district
            + _value.street + _value.business;
        }
        str = "FromItem<br />index = " + e.fromitem.index + "<br />value = "
        + value;

        value = "";
        if (e.toitem.index > -1) {
            _value = e.toitem.value;
            value = _value.province + _value.city + _value.district
            + _value.street + _value.business;
        }
        str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = "
        + value;
        G("searchResultPanel").innerHTML = str;
    });

    var myValue;
    ac.addEventListener("onconfirm", function (e) { // 鼠标点击下拉列表后的事件
        var _value = e.item.value;
        myValue = _value.province + _value.city + _value.district
        + _value.street + _value.business;
        G("searchResultPanel").innerHTML = "onconfirm<br />index = "
        + e.item.index + "<br />myValue = " + myValue;
        setPlace();
    });

    function setPlace() {
        mapStation.clearOverlays(); // 清除地图上
        function myFun() {
            var pp = local.getResults().getPoi(0).point;// 获取第一个智能搜索信息
            mapStation.centerAndZoom(pp, 18);
            mapStation.addOverlay(new BMap.Marker(pp));

        }

        var local = new BMap.LocalSearch(mapStation, {
            onSearchComplete: myFun
        });
        local.search(myValue);
    }

    // 通过双击地图添加基站，弹出窗口
    var geoc = new BMap.Geocoder();

    mapStation.addEventListener("dblclick", function (e) { // 双击添加基站，显示图标
        if ($rootScope.rootUserPowerDto.stationAdd != "1" || $rootScope.rootUserPowerDto.sysUser.opt_power != "1") {
            alert("没有添加基站权限！");
            return;
        }
        //mapStation.clearOverlays();
        var currentpt = new BMap.Point(e.point.lng, e.point.lat);
        $scope.addStationLng = e.point.lng;
        $scope.addStationLat = e.point.lat;
        geoc.getLocation(e.point, function (rs) {
            if (rs == null) {

                $scope.addStationAddress = "获取不到具体位置";
            } else {
                var addComp = rs.addressComponents;
                $scope.addStationAddress = addComp.province + ","
                + addComp.city + "," + addComp.district + ", "
                + addComp.street + "," + addComp.streetNumber;
            }
            $("#addStation").modal("show");
            $("#addStationLng").val($scope.addStationLng);
            $("#addStationLat").val($scope.addStationLat);
            $("#addStationAddress").val($scope.addStationAddress);
        });

        // var myIcon = new BMap.Icon("../app/img/station.jpg", new
        // BMap.Size(43,50));
        var marker = new BMap.Marker(currentpt);
        marker.addEventListener("click", function () {
            // this.openInfoWindow(stationWin);
            $('#addStation').modal('show')
        });
        mapStation.addOverlay(marker);
        // $(".disModal").click(function(){
        // mapStation.clearOverlays();
        // });
    });

    $(function () {$('#addStation').on('hide.bs.modal',function(){

    	var allOverlay = mapStation.getOverlays()
    	if(allOverlay!=null && allOverlay.length > 0){
    		mapStation.removeOverlay(allOverlay[allOverlay.length-1]);
    	}
    })})
    $scope.goStationLocation = function (stationID) {
        $http.get('/i/station/findStationByID', {
            params: {
                "stationID": stationID
            }
        }).success(function (data) {
            $scope.locationStation = data;
            // 这里我得到了基站的信息包括经纬度等，需要将其显示在地图
            $scope.doBounce(data.longitude, data.latitude);
            //单个基站跳动
            /*mapStation.clearOverlays();
             var findStationPt = new BMap.Point(data.longitude, data.latitude);
             var findStationMarker = new BMap.Marker(findStationPt);
             mapStation.addOverlay(findStationMarker);
             findStationMarker.setAnimation(BMAP_ANIMATION_BOUNCE);
             mapStation.centerAndZoom(findStationPt, 17);*/

        });
    }

    $('#electAlarmDate').datetimepicker({
        format: 'yyyy-mm-dd  hh:ii:ss.s',
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $scope.load();
    // 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
    $scope.AllStations = [];
    $scope.optAudit = '8';
    // 获取当前基站的列表
    $scope.getStations = function () {
        $http({
            method: 'POST',
            url: '/i/station/findAllStations',
            params: {
                pageNum: $scope.bigCurrentPage,
                pageSize: $scope.maxSize,
                startTime: $scope.startTime,
                endTime: $scope.endTime,
                stationPhyNum: $scope.stationPhyNum,
                stationName: $scope.stationName,
                proPower: $rootScope.admin.pro_power,
                cityPower: $rootScope.admin.city_power,
                areaPower: $rootScope.admin.area_power,
                stationStatus: $scope.stationStatus
            }
        }).success(function (data) {
            $scope.bigTotalItems = data.total;
            $scope.AllStations = data.list;
            //console.log($scope.AllStations);
        });
    }

    $scope.pageChanged = function () {
        $scope.getStations();
    }
    // 获取当前基站的列表
    $scope.auditChanged = function (optAudiet) {
        $scope.getStations();
    }

    $scope.goSearch = function () {
        $scope.getStations();
    }

    $scope.showAll = function () {
        $state.reload();
    }

    function delcfm() {
        if (!confirm("确认要删除？")) {
            return false;
        }
        return true;
    }

    $scope.goDeleteStation = function (stationID) {
        if (delcfm()) {
            $http.get('/i/station/deleteStationByID', {
                params: {
                    "stationID": stationID
                }
            }).success(function (data) {
                $scope.getStations();
                alert("删除成功");
            });
        }
    }
    $scope.goDeleteStations = function () {
        if (delcfm()) {
            var stationIDs = [];
            for (var i=0 ;i< $scope.selected.length;i++) {
                stationIDs.push($scope.selected[i].station_id);
            }
            if (stationIDs.length > 0) {
                $http({
                    method: 'DELETE',
                    url: '/i/station/deleteStationByIDs',
                    params: {
                        'stationIDs': stationIDs
                    }
                }).success(function (data) {
                    $scope.getStations();
                    alert("删除成功");
                });
            }
        }
    }

    $scope.selected = [];
    $scope.toggle = function (station, list) {
        var idx = list.indexOf(station);
        if (idx > -1) {
            list.splice(idx, 1);
        } else {
            list.push(station);
        }
    };
    $scope.exists = function (station, list) {
        return list.indexOf(station) > -1;
    };
    $scope.isChecked = function () {
        return $scope.selected.length === $scope.AllStations.length;
    };
    $scope.toggleAll = function () {
        if ($scope.selected.length === $scope.AllStations.length) {
            $scope.selected = [];
        } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
            $scope.selected = $scope.AllStations.slice(0);
        }
    };

    $scope.getStationIDsFromSelected = function (audit) {
        var stationIDs = [];
        for (var i=0 ;i< $scope.selected.length;i++) {
            if (audit != undefined)
                $scope.selected[i].audit = audit;
            stationIDs.push($scope.selected[i].id);
        }
        return stationIDs;
    }

    $scope.AllStationStatusForShow = [{
        id: "1",
        name: "隐藏基站"
    }, {
        id: "2",
        name: "显示异常基站"
    }];
    $scope.stationStatusForShow = "1";
    $scope.stationStatusShow = function () {
        if ($scope.stationStatusForShow == "2") {
            $http.get('/i/station/findStationsByStatus', {
                params: {
                    "stationStatus": "1",
                    "proPower": $rootScope.admin.pro_power,
                    "cityPower": $rootScope.admin.city_power,
                    "areaPower": $rootScope.admin.area_power
                }
            })
                .success(
                function (data) { // 在地图上显示异常基站
                    $scope.errorStation = data;
                    var len = data.length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var abnormalPt = new BMap.Point(
                                data[i].longitude,
                                data[i].latitude);
                            var abnormalMarker = new BMap.Marker(
                                abnormalPt);
                            mapStation.addOverlay(abnormalMarker);
                        }
                    } else {
                        alert("没有异常基站！");
                    }
                });
        } else {
            // 隐藏显示的异常基站
            mapStation.clearOverlays();
        }
    }

    $scope.AllStationType = [{
        id: "1",
        name: "SP-RFS-336-391"
    }, {
        id: "2",
        name: "RG-RFS-336-392"
    }];
    $scope.AllStationStatus = [{
        id: "0",
        name: "正常"
    }, {
        id: "1",
        name: "故障"
    }];
    $scope.addStationType = "1";
    $scope.addStationStatus = "0";
    function checkInput() {
        var flag = true;
        // 检查必须填写项
        if ($scope.addStationPhyNum == undefined
            || $scope.addStationPhyNum == '') {
            flag = false;
        }
        if ($scope.addStationName == undefined || $scope.addStationName == '') {
            flag = false;
        }
        if ($scope.addInstallDate == undefined || $scope.addInstallDate == '') {
            flag = false;
        }
        return flag;
    }

    $scope.addInstallPic = function () {

    };
    $scope.dropInstallPic = function (installPic) {
        $scope.installPic = null;
    };

    $scope.submit = function () {
        if (checkInput()) {
            var stationType = "";
            if ($scope.addStationType == "1")
                stationType = "SP-RFS-336-391";
            else
                stationType = "RG-RFS-336-392";
            data = {
                'station_phy_num': $scope.addStationPhyNum,
                'station_name': $scope.addStationName,
                'longitude': $scope.addStationLng,
                'latitude': $scope.addStationLat,
                'station_type': stationType,
                'station_status': $scope.addStationStatus,
                'install_date': $scope.addInstallDate,
                'soft_version': $scope.addSoftVersion,
                'contact_person': $scope.addcontactPerson,
                'contact_tele': $scope.addcontactTele,
                'stick_num': "123123",
                'station_address': $scope.addStationAddress,
                'install_pic': $scope.installPic
            };
            Upload.upload({
                url: '/i/station/addStation',
                headers: {
                    'Content-Transfer-Encoding': 'utf-8'
                },
                data: data
            }).success(function (data) {
                if (data.success) {
                    alert(data.message);
                    $scope.getStations();
                    $("#addStation").modal("hide");
                } else {
                    alert(data.message);
                }
            });
        } else {
            alert("请填写基站编号、基站名和安装时间!");
        }
    }
    $('#datetimepicker1').datetimepicker({
        autoclose: true
    }).on('dp.change', function (e) {
    });
    $('#datetimepicker2').datetimepicker({
        autoclose: true
    }).on('dp.change', function (e) {
    });
    // ----------------基站收缩功能-----------------

    $(".closeStationPosition").click(function () {
        $(this).parents('.station-content').toggleClass("leftToggle");
        if ($(this).hasClass("fa-angle-left")) {
            $(this).removeClass("fa-angle-left").addClass("fa-angle-right")
        } else {
            $(this).removeClass("fa-angle-right").addClass("fa-angle-left");
        }
    });
    $scope.myClass = {
        "background-color": "rgba(204, 204, 204, 0.48)",
    }


    $scope.jizhanBounce = null;
    var markerClusterer = null;
    var marker2;

    function showStation() {
        var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><img src='app/img/station.png'/><h4>";

        var sHtml4 = "</p><hr/><div class='tableArea margin-top2' style='overflow-y: hidden;'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th style='text-align: center; width: 25%!important; '>基站物理编号</th><th style='text-align: center; width: 25%!important;'>状态</th><th style='text-align: center;width: 50%!important;'>记录时间</th></tr></thead><tbody>";
        //var sHtml4 = "</p><ul class='flex flex-time'><li class='active searchTime'>1分钟</li><li class='searchTime'>5分钟</li><li class='searchTime'>1小时</li></ul><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>车辆编号</th><th>经过时间</th></tr></thead><tbody>";
        var endHtml = "</tbody></table></div></div>";
        var pt;
        //var marker2;
        var markers = []; //存放聚合的基站
        //var infoWindow;
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
            marker2.setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_name + '\t' + tmpStation.station_status);
            marker2.addEventListener("click", function (e) {
                var title_add = new Array();
                title_add = this.getTitle().split('\t');
                showStationRecord(title_add[0])
                var electInfo = '';
                var sHtml2 = '';
                var sHtml3 = '';
                var status = "";
                var statusColor = ""
                if (title_add[2] == 0) {
                    status = "正常";
                } else if (title_add[2] == 1) {
                    status = "故障";
                    statusColor = "style='background:#CCC!important;'";
                } else if (title_add[2] == 2) {
                    status = "检测中";
                    statusColor = "style='background:#fad733!important;'";
                }
                sHtml2 += "</h4>" + title_add[0] + "</li><li " + statusColor + " >" + status;
                sHtml3 += "</li></ul><p class='flex-items'><i class='glyphicon glyphicon-map-marker'></i>" + title_add[1] + "<span>";
                var station_status = '';
                for (var k = 0; k < $scope.stationRecord.length; k++) {
                    if ($scope.stationRecord[k].station_status == 0) {
                        station_status = "正常";
                    } else {
                        station_status = "故障";
                    }
                    electInfo += "<tr><td title='" + $scope.stationRecord[k].station_phy_num + "'>" + $scope.stationRecord[k].station_phy_num + "</td>" + "<td title='" + station_status + "'>" + station_status + "</td>" + "<td title='" + $scope.stationRecord[k].update_time + "'>" + $scope.stationRecord[k].update_time + "</td></tr>";
                }
                var infoWindow = new BMap.InfoWindow(sHtml + sHtml2 + sHtml3 + sHtml4 + electInfo + endHtml);
                var p = e.target;
                var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
                mapStation.openInfoWindow(infoWindow, point);
            });
            markers.push(marker2);
        }
        if ($scope.jizhanjuheFlag == 0) {
            markerClusterer = new BMapLib.MarkerClusterer(mapStation, {markers: markers});
        }

        mapStation.clearOverlays();
        for (var i = 0; i < markers.length; i++) {
            mapStation.addOverlay(markers[i]);
        }

        //用于基站跳动
        $scope.jizhanBounce = mapStation.getOverlays();
    }


    //表格选中行，对应标注体现出来
    $scope.doBounce = function (longitude, latitude) {
        if ($scope.tiao != null) {
            $scope.tiao.setAnimation(null);
        }
        var allOverlay = $scope.jizhanBounce;
        var Oe = null
        for (var i = 0; i < allOverlay.length; i++) {
            Oe = allOverlay[i].point
            if (Oe.lng == longitude && Oe.lat == latitude) {
            	mapStation.centerAndZoom(new BMap.Point(longitude,latitude), 15);
                $scope.tiao = allOverlay[i];//保存上一次跳动的基站
                allOverlay[i].setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
                return;
            } else {
                //markers=null;
            }
        }

    };
    function showSingleStation(stationID) {
        $.ajax({
            method: 'GET',
            url: '/i/station/findStationByID',
            async: false,
            data: {
                "stationID": stationID
            }
        }).success(function (data) {
            $scope.singleStation = data;
        })
    }
    $scope.limit = 10;//目前默认10条
    function showStationRecord(stationPhyNum) {
        $.ajax({
            method: 'GET',
            url: '/i/station/findStationRecordByStationNumAndLimit',
            async: false,
            data: {
                "stationPhyNum": stationPhyNum,
                "limit": $scope.limit
            }
        }).success(function (data) {
            $scope.stationRecord = data;
        })
    }


});
