coldWeb.controller('sensitiveArea', function ($rootScope, $scope, $state, $cookies, $http, Upload, $location) {
    var sensitiveAreaMap = new BMap.Map("sensitiveAreaMap", {
        minZoom: 5,
        maxZoom: 30
    });
    $scope.load = function () {
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
                    if ($rootScope.admin.fixed_lat == null || $rootScope.admin.fixed_lat == undefined || $rootScope.admin.fixed_lon == null || $rootScope.admin.fixed_lon == undefined) {
                        sensitiveAreaMap.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
                    } else {
                        sensitiveAreaMap.centerAndZoom(new BMap.Point($rootScope.admin.fixed_lon, $rootScope.admin.fixed_lat), $rootScope.admin.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                        //map.centerAndZoom($scope.cityName, $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                    }
                //sensitiveAreaMap.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
                sensitiveAreaMap.enableScrollWheelZoom(true); //开启鼠标滚轮缩放
                // 获取基站
                $http.get('/i/station/findAllStationsForMap', {
                    params: {
                        "proPower": $scope.user.pro_power,
                        "cityPower": $scope.user.city_power,
                        "areaPower": $scope.user.area_power
                    }
                }).success(function (data) {
                    $scope.stations = data;
                    showStationForAera(sensitiveAreaMap);
                });

            });

            $scope.getSensitiveAreaByOptions();
            $scope.electNormalVehicle();
            $scope.selectedMultiselect();
        });
        });
    };
    $scope.load();

    //获取敏感区域列表
    // 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
    $scope.AllSensitiveAreas = [];
    $scope.getSensitiveAreaByOptions = function () {
        $http({
            method: 'POST',
            url: '/i/sensitiveArea/findSensitiveByOptions',
            params: {
                pageNum: $scope.bigCurrentPage,
                pageSize: $scope.maxSize,
                sensitiveAreaName: $scope.sensitiveAreaName,
                sensitiveAreaID: $scope.sensitiveAreaID,
                proPower: $scope.user.pro_power,
                cityPower: $scope.user.city_power,
                areaPower: $scope.user.area_power
            }
        }).success(function (data) {
            $scope.bigTotalItems = data.total;
            $scope.AllSensitiveAreas = data.list;
            //console.log(data.list)
        });
    };


    $scope.goSearch = function () {
        $scope.getSensitiveAreaByOptions();
    }
    $scope.pageChanged = function () {
        $scope.getSensitiveAreaByOptions();
    }

    $scope.selected = [];
    $scope.exists = function (user, list) {
        return list.indexOf(user) > -1;
    };
    $scope.toggle = function (user, list) {
        var idx = list.indexOf(user);
        if (idx > -1) {
            list.splice(idx, 1);
        }
        else {
            list.push(user);
        }
    };
    $scope.isChecked = function () {
        return $scope.selected.length === $scope.AllSensitiveAreas.length;
    };
    $scope.toggleAll = function () {
        if ($scope.selected.length === $scope.AllSensitiveAreas.length) {
            $scope.selected = [];
        } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
            $scope.selected = $scope.AllSensitiveAreas.slice(0);
        }
    };
    function delcfm() {
        if (!confirm("确认要删除？")) {
            return false;
        }
        return true;
    }

    $scope.goDeleteSensitiveAreas = function (userID) {
        if (delcfm()) {
            $http.get('/i/sensitiveArea/deleteSensitiveAreaByID', {
                params: {
                    "sensitiveAreaID": userID
                }
            }).success(function (data) {
                $scope.getSensitiveAreaByOptions();
                alert("删除成功！")
            });
        }
    }

    $scope.goDeleteLimitAreas = function () {
        if (delcfm()) {
            var limitAreaIDs = [];
            for (var i=0 ;i< $scope.selected.length;i++) {
                limitAreaIDs.push($scope.selected[i].sensitive_area_id);
            }
            if (limitAreaIDs.length > 0) {
                $http({
                    method: 'DELETE',
                    url: '/i/sensitiveArea/deleteSensitiveAreaByIDs',
                    params: {
                        "sensitiveAreaIDs": limitAreaIDs
                    }
                }).success(function (data) {
                    $scope.getSensitiveAreaByOptions();
                    alert("删除成功！")
                });
            }
        }
    }

    //显示基站
    function showStationForAera(map) {
        var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><img src='app/img/station.png'/><h4>";
        var sHtml2 = "</h4></li><li>";
        var sHtml3 = "</li></ul><p class='flex-items'><i class='glyphicon glyphicon-map-marker'></i><span>";
        var sHtml4 = "</p ><ul class='flex flex-time'><li class='active'>1分钟</li><li>5分钟</li><li>1小时</li></ul><hr/><div class='tableArea margin-top2'> <table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th style='width:25%!important'>限制区域</th><th style='width:25%!important'>车辆编号</th><th style='width:50%!important'>经过时间</th></tr></thead><tbody>";
        var endHtml = "</tbody></table></div></div>";
        var pt;
        var marker2;
        var sContent = sHtml;
        var markers = []; //存放聚合的基站
        //var infoWindow;
        var tmpStation;
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
            // var num = tmpStation.station_phy_num;
            function FormatDate(strTime) {
                var date = new Date(strTime);
                //2018-10-15 修改
                var year = date.getFullYear();
                var month = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1);
                var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
                var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
                var min = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
                var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
                return year + "-" + month + "-" + day + " " + hours + ":" + min + ":" + seconds
                //return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+ date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
            }

            //marker2 = new BMap.Marker(pt);
            marker2.setTitle(tmpStation.station_phy_num + '\t' + tmpStation.station_address);
            //console.log($scope.stations.length);

            marker2.addEventListener("click", function (e) {
                var title_add = new Array();
                title_add = this.getTitle().split('\t');
                showElectsInStation(FormatDate(start), FormatDate(end), title_add[0]);  //根据物理编号查找
                var electInfo = '';
                for (var k = 0; k < $scope.electsInStation.length; k++) {
                    electInfo += "<tr><td title='" + $scope.electsInStation[k].area_name + "'>" + $scope.electsInStation[k].area_name + "</td>" + "<td title='" + $scope.electsInStation[k].enter_plate_num + "'>" + $scope.electsInStation[k].enter_plate_num + "</td>" + "<td title='" + $scope.electsInStation[k].enter_time + "'>" + $scope.electsInStation[k].enter_time + "</td></tr>";
                }

                var infoWindow = new BMap.InfoWindow(sHtml + title_add[0] + sHtml2 + $scope.electsInStation.length + sHtml3 + title_add[1] + sHtml4 + electInfo + endHtml);

                var p = e.target;
                var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
                map.openInfoWindow(infoWindow, point);
            });
            map.addOverlay(marker2);

        }
        $scope.jizhanBounce = map.getOverlays();
    }

    //根据时间和基站id获取基站下面的当前所有车辆
    function showElectsInStation(startTime, endTime, stationPhyNum) {
        $.ajax({
            method: "GET",
            url: "/i/sensitiveArea/findSensitiveAreaAlarmByStationNumAndTime",
            async: false,
            data: {
                "alarmDateStart": startTime,
                "alarmDateEnd": endTime,
                "alarmStationPhyNum": stationPhyNum
            }
        }).success(function (data) {
            //$scope.electsInStation = data.slice(2,8);
            $scope.electsInStation = data;
        });
    }

    //鼠标绘制多边形，选择区域并弹出信息框，展示显示的基站
    var overlaysDraw = [];
    var overlaycomplete = function (e) {
        $scope.borderPoints = e.overlay.getPath();//多边形轨迹数据点
        //console.log($scope.borderPoints);
        //	//返回上一部轨迹
        //$scope.backOne = function(){
        //	$scope.borderPoints.pop();
        //	console.log($scope.borderPoints);
        //	console.log(e.overlay);
        //
        //	drawingManager.addEventListener('overlaycomplete', overlaycomplete);
        //};
        e.overlay.addEventListener("click", function () {

            if ($rootScope.rootUserPowerDto.sensitiveAreaAdd != "1" || $rootScope.rootUserPowerDto.sysUser.opt_power != "1") {
                alert("没有添加区域权限！");
                return;
            }

            $http({
                method: 'POST',
                url: '/i/specialArea/findStations',
                params: {
                    area_power: $scope.user.area_power,
                    city_power: $scope.user.city_power,
                    pro_power: $scope.user.pro_power,
                    borderPoints: $scope.borderPoints
                }
            }).success(function (data) {
                $scope.addStationNames = data;
                $("#addSensitiveArea").modal("show");
            })

        });

        overlaysDraw.push(e.overlay);

    };

    var styleOptions = {
        strokeColor: "blue",    //边线颜色。
        fillColor: "red",      //填充颜色。当参数为空时，圆形将没有填充效果。
        strokeWeight: 3,       //边线的宽度，以像素为单位。
        strokeOpacity: 0.8,	   //边线透明度，取值范围0 - 1。
        fillOpacity: 0.6,      //填充的透明度，取值范围0 - 1。
        strokeStyle: 'solid', //边线的样式，solid或dashed。
        //enableEditing: true
    }
    //实例化鼠标绘制工具
    var drawingManager = new BMapLib.DrawingManager(sensitiveAreaMap, {
        isOpen: false, //是否开启绘制模式
        enableDrawingTool: true, //是否显示工具栏
        drawingToolOptions: {
            anchor: BMAP_ANCHOR_TOP_RIGHT, //位置
            offset: new BMap.Size(5, 5), //偏离值
            drawingModes: [
                BMAP_DRAWING_POLYGON,
            ]
        },
        polygonOptions: styleOptions //多边形的样式
    });
    drawingManager.addEventListener('overlaycomplete', overlaycomplete);
    $scope.clearAll = function () {
        for (var i = 0; i < overlaysDraw.length; i++) {
            sensitiveAreaMap.removeOverlay(overlaysDraw[i]);
        }
        cancelTiao()
        overlaysDraw.length = 0
    }

    //新增选择日期
    $('#startTime').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });

    $('#endTime').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $('#viewStartTime').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $('#viewEndTime').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });

    $scope.electDefault = "0";
    $scope.electsList = [
        {id: "0", name: "全部"},
        {id: "1", name: "电G22H65"},
        {id: "2", name: "电Q11H82"},
        {id: "3", name: "电Q11H83"},
        {id: "4", name: "电Q19H82"}
    ];


    //添加
    function checkInputInfo() {
        var flag = true;
        // 检查必须填写项
        if ($scope.addSensitiveAreaName == undefined || $scope.addLimitAreaName == '') {
            flag = false;
            alert("敏感区域名不可为空！")
        }
        if ($scope.addStationNames == undefined || $scope.addStationNames == '') {
            flag = false;
            alert("基站名不可为空！")
        }
        if ($scope.enterNum == undefined || $scope.enterNum == '') {
            flag = false;
            alert("出现次数不可为空！")
        } else if ($scope.enterNum <= 0) {
            flag = false;
            alert("出现次数必须大于0！")
        }
        return flag;
    }

    //var inputStatus=$("#sentivesStatus").get(0).checked;
    function aaa() {
        var inputStatus = $("#sentivesStatus").get(0).checked;
        //console.log(inputStatus)
        if (inputStatus) {
            return $scope.status = 1
        } else {
            return $scope.status = 0
        }
    }

    function updateSentivesStatus() {
        var inputStatus = $("#updateStatus").get(0).checked;
        //console.log(inputStatus)
        if (inputStatus) {
            return $scope.updateSensitiveArea.status = 1
        } else {
            return $scope.updateSensitiveArea.status = 0
        }
    }

    $scope.enterNum = 1;
    $scope.addSensitiveArea = function () {
        $scope.addBlackelectPlatenum = $scope.selectLimitVehicle("addBlackelectPlatenum");
        if($scope.addBlackelectPlatenum==null || $scope.addBlackelectPlatenum==""){
            alert("请选择限制车辆");
            return;
        }
        if (checkInputInfo()) {
            $http({
                method: 'POST',
                url: '/i/sensitiveArea/addSensitiveArea',
                params: {
                    addSensitiveAreaName: $scope.addSensitiveAreaName,
                    addStationNames: $scope.addStationNames,
                    addBlackelectPlatenum: $scope.addBlackelectPlatenum,
                    addElectPlatenum: $scope.addElectPlatenum,
                    enterNum: $scope.enterNum,
                    enterNum: $scope.enterNum,
                    status: aaa(),
                    sensStartTime: $scope.sensStartTime,
                    sensEndTime: $scope.sensEndTime,
                    proPower: $scope.user.pro_power,
                    cityPower: $scope.user.city_power,
                    areaPower: $scope.user.area_power
                }
            }).success(function (data) {
                if (data.success) {
                    //console.log(data);
                    alert(data.message);
                    $scope.getSensitiveAreaByOptions();
                    $("#addSensitiveArea").modal("hide");
                    $scope.clearAll();
                }
                else {
                    alert(data.message);
                }
            })
        }
        ;
    }


    //------------修改-------------------
    $scope.goUpdateSensitiveArea = function (electID) {
        $http({
            method: 'POST',
            url: '/i/sensitiveArea/findSensitiveAreaByID',
            params: {
                sensitiveAreaID: electID,
            }
        }).success(function (data) {
            $scope.updateSensitiveArea = data;

            var valObj = "";
            if ($scope.updateSensitiveArea.black_list_elects != null) {
                valObj = $scope.updateSensitiveArea.black_list_elects.split(";")
            }
            if ($scope.updateSensitiveArea.status == 0) {
                $(":checkbox[id='updateStatus']").prop("checked", "");
            } else if ($scope.updateSensitiveArea.status == 1) {
                $(":checkbox[id='updateStatus']").prop("checked", "checked");
            }
            $('#updateBlackelectPlatenum').multiselect("select", valObj)

        })

    };
    $scope.update = function () {
        $scope.updateBlackelectPlatenum = $scope.selectLimitVehicle("updateBlackelectPlatenum");
        if($scope.updateBlackelectPlatenum==null || $scope.updateBlackelectPlatenum==""){
            alert("请选择限制车辆");
            return;
        }
        if (checkInputForUpdate()) {
            Upload.upload({
                method: 'POST',
                url: '/i/sensitiveArea/updateSensitiveArea',
                params: {
                    sensitive_area_id: $scope.updateSensitiveArea.sensitive_area_id,
                    updateSensitiveAreaName: $scope.updateSensitiveArea.sensitive_area_name,
                    updateStationNames: $scope.updateSensitiveArea.station_names,
                    updateBlackelectPlatenum: $scope.updateBlackelectPlatenum,
                    updateElectPlatenum: $scope.updateSensitiveArea.list_elects,
                    enterNum: $scope.updateSensitiveArea.enter_num,
                    status: updateSentivesStatus(),
                    sensStartTime: $scope.updateSensitiveArea.sens_start_time,
                    sensEndTime: $scope.updateSensitiveArea.sens_end_time,
                    proPower: $scope.user.pro_power,
                    cityPower: $scope.user.city_power,
                    areaPower: $scope.user.area_power
                }
            }).success(function (data) {
                if (data.success) {
                    //console.log(data);
                    alert(data.message);
                    $scope.getSensitiveAreaByOptions();
                    $("#updateSensitiveArea").modal("hide");
                    $scope.clearAll();
                }
                else {
                    alert(data.message);
                }
            })
        }
        ;
    };

    //修改验证
    function checkInputForUpdate() {
        var flag = true;
        // 检查必须填写项
        if ($scope.updateSensitiveArea.sensitive_area_name == undefined || $scope.updateSensitiveArea.sensitive_area_name == '') {
            flag = false;
            alert("敏感区域名不可为空！")
        }
        if ($scope.updateSensitiveArea.station_names == undefined || $scope.updateSensitiveArea.station_names == '') {
            flag = false;
            alert("基站名不可为空！")
        }
        if ($scope.updateSensitiveArea.enter_num == undefined || $scope.updateSensitiveArea.enter_num == '') {
            flag = false;
            alert("出现次数不可为空！")
        } else if ($scope.updateSensitiveArea.enter_num <= 0) {
            flag = false;
            alert("出现次数必须大于0！")
        }
        return flag;
    }


    //----------------表格收缩功能-----------------

    $(".closeStationPosition").click(function () {
        $(this).parents('.limitArea-content').toggleClass("leftToggle");
        if ($(this).hasClass("fa-angle-left")) {
            $(this).removeClass("fa-angle-left").addClass("fa-angle-right")
        } else {
            $(this).removeClass("fa-angle-right").addClass("fa-angle-left");
        }
    });

    //获取选择的车辆ID用 逗号","分割
    $scope.selectLimitVehicle = function (elementId) {
        var selected = [];
        $('#' + elementId + ' option:selected').each(function () {

            selected.push([$(this).val(), $(this).text()]);
        });
        selected.sort(function (a, b) {
            return a[1] - b[1];
        });

        var text = '';
        for (var i = 0; i < selected.length; i++) {
            text += selected[i][0] + ';';
        }
        text = text.substring(0, text.length - 1);
        return text;
    }

    //多选下拉框插件
    $scope.selectedMultiselect = function () {
        $('.addBlackelectPlatenum').multiselect({
            enableClickableOptGroups: true,
            //optgroups将是可折叠
            enableCollapsibleOptGroups: true,
            enableFiltering: true,
            includeSelectAllOption: true,
            selectAllJustVisible: false,
            selectAllText: "全部",
            allSelectedText: "全部选中",
            nSelectedText: "选中",
            nonSelectedText: "请选择",
            maxHeight: 400,

        });
    }

    //多选下来分类：查询正常的车辆（正常：1，报警：2）
    $scope.electNormalVehicle = function () {

        $.ajax({
            method: 'POST',
            url: '/i/elect/findElectsListByElectState',
            async: false,
            data: {
                electState: 1,
                proPower: $scope.user.pro_power,
                cityPower: $scope.user.city_power,
                areaPower: $scope.user.area_power
            }
        }).success(function (data) {
            $scope.normalVehicle = data
            var normalStr = "";
            for (var i = 0; i < data.length; i++) {
                normalStr += '<option value="' + data[i].elect_id + '">' + data[i].plate_num + '</option>'
            }
            $(".addBlackelectPlatenum").find("#normal").html(normalStr);
        })

        $.ajax({
            method: 'POST',
            url: '/i/elect/findElectsListByElectState',
            async: false,
            data: {
                electState: 2,
                proPower: $scope.user.pro_power,
                cityPower: $scope.user.city_power,
                areaPower: $scope.user.area_power
            }
        }).success(function (data) {
            $scope.alarmVehicle = data;
            var alarmStr = "";
            for (var i = 0; i < data.length; i++) {
                alarmStr += '<option value="' + data[i].elect_id + '">' + data[i].plate_num + '</option>'
            }
            $(".addBlackelectPlatenum").find("#alarm").html(alarmStr);

        })

        //$scope.selectedMultiselect();
    }


    //显示详情
    $scope.showSensitiveArea = function (sensitiveAreaID) {
        $http({
            method: 'POST',
            url: '/i/sensitiveArea/findSensitiveAreaByID',
            params: {
                sensitiveAreaID: sensitiveAreaID,
            }
        }).success(function (data) {
            $scope.sensitiveArea = data
            var valObj = "";
            if (data.black_list_elects != null) {
                valObj = data.black_list_elects.split(";")
            }
            if ($scope.sensitiveArea.status == 0) {
                $(":checkbox[id='viewStatus']").prop("checked", "");
            } else if ($scope.sensitiveArea.status == 1) {
                $(":checkbox[id='viewStatus']").prop("checked", "checked");
            }
            $('#viewBlackelectPlatenum').multiselect("select", valObj)
        })

    }


    $scope.goStationLocation = function (stationIDs) {


        $http.get('/i/station/findStationByIDs', {
            params: {
                "stationIDs": stationIDs
            }
        }).success(function (data) {
            $scope.locationStation = data;
            // 这里我得到了基站的信息包括经纬度等，需要将其显示在地图
            $scope.doBounce()
        });

    }


    //用于基站跳动的参数
    $scope.jizhanBounce = null;
    $scope.tiao = [];

    //表格选中行，对应标注体现出来
    $scope.doBounce = function () {

        for (var i = 0; i < $scope.tiao.length; i++) {
            $scope.tiao[i].setAnimation(null);
        }
        var allOverlay = $scope.jizhanBounce;
        var Oe = null
        var index = 0;
        $scope.tiao = [];
        for (var k = 0; k < $scope.locationStation.length; k++) {
            for (var i = 0; i < allOverlay.length; i++) {
                Oe = allOverlay[i].point
                if (Oe == null) {
                    continue;
                }
                if (Oe.lng == $scope.locationStation[k].longitude && Oe.lat == $scope.locationStation[k].latitude) {
                    $scope.tiao[index] = allOverlay[i];//保存上一次跳动的基站
                    index++
                    allOverlay[i].setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
                    //return;
                }
            }
        }
    };

    function cancelTiao() {
        //取消跳动
        for (var i = 0; i < $scope.tiao.length; i++) {
            $scope.tiao[i].setAnimation(null);
        }
        $scope.tiao = [];
    }

});
Array.prototype.contains = function (obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}
