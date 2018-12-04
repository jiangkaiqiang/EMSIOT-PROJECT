coldWeb.controller('gowsterPeopleManage', function ($rootScope, $scope, $state, $cookies, $http, Upload, $location) {
    console.log('吸毒者管理')
    $scope.load = function () {
        $.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
            $rootScope.admin = data;
            //console.log(data);
            //验证是否登录成功
            if ($rootScope.admin == null || $rootScope.admin.user_id == 0 || $rootScope.admin.user_id == undefined) {
                url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
                window.location.href = url;
                return;
            }
            $http.get('/i/user/findUserByID', {
                params: {
                    "spaceUserID": $rootScope.admin.user_id
                }
            }).success(function (data) {
                $scope.userPowerDto = data;
                //console.log(data)
                //获取全部省For add；要首先确定该用户是不是具有分配省权限用户的能力
                if ($scope.userPowerDto.sysUser.pro_power == "-1") {
                    //获取省列表
                    $http.get('/i/city/findProvinceList').success(function (data) {
                        $scope.provincesForSearch = data;
                        var province = {
                            "province_id": "-1",
                            "name": "不限"
                        };
                        $scope.provincesForSearch.push(province);
                        $scope.proID = "-1";
                    });
                }

                if ($scope.userPowerDto.sysUser.pro_power != "-1") {
                    $http.get('/i/city/findCitysByProvinceId', {
                        params: {
                            "provinceID": $scope.userPowerDto.sysUser.pro_power
                        }
                    }).success(function (data) {
                        $scope.citis = data;
                        $scope.citisForUpdate = data;
                        $scope.citisForSearch = data;
                        $scope.addProvinceID = $scope.userPowerDto.sysUser.pro_power;
                        var city = {"city_id": "-1", "name": "不限"};
                        $scope.citisForSearch.push(city);
                    });
                } else {
                    $scope.getPro();
                }
                if ($scope.userPowerDto.sysUser.city_power != "-1") {
                    $http.get('/i/city/findAreasByCityId', {
                        params: {
                            "cityID": $scope.userPowerDto.sysUser.city_power
                        }
                    }).success(function (data) {
                        $scope.areas = data;
                        $scope.areasForUpdate = data;
                        $scope.areasForSearch = data;
                        $scope.addCityID = $scope.userPowerDto.sysUser.city_power;
                        var area = {"area_id": "-1", "name": "不限"};
                        $scope.areasForSearch.push(area);
                    });
                    if ($scope.userPowerDto.sysUser.area_power != "-1") {
                        $scope.addAreaID = $scope.userPowerDto.sysUser.area_power;
                    }
                } else {
                    $scope.getCitis();
                }
                //获取全部管理员
                $http.get('/i/user/findAllUsers', {
                    params: {
                        "proPower": $scope.userPowerDto.sysUser.pro_power,
                        "cityPower": $scope.userPowerDto.sysUser.city_power,
                        "areaPower": $scope.userPowerDto.sysUser.area_power
                    }
                }).success(function (data) {
                    $scope.sysUsers = data;
                    var user = {"user_id": "0", "user_name": "全部"};
                    $scope.sysUsers.push(user);
                    $scope.sysUserID = "0";
                });
                $scope.getPeoples();
            });
        });
    };
    $scope.addPeoplePic = function () {

    };
    $scope.dropPeoplePic = function (peoplePic) {
        $scope.peoplePic = null;
    };
    $scope.dropPeoplePicForUpdate = function (peoplePic) {
        $scope.updatePeople.people.people_pic = null;
    };


    //显示下拉搜索条件
    $("#searchBlock").click(function () {
        $("#unblock").toggleClass("unblock_active");
        //console.log($(this).children("i"));
        if ($(this).children("i").hasClass("fa-angle-down")) {
            $(this).children("i").removeClass("fa-angle-down").addClass("fa-angle-up");
        } else {
            $(this).children("i").removeClass("fa-angle-up").addClass("fa-angle-down");
        }
        ;
        isDisabled();
    });

    function isDisabled() {
        var selectes = $("#unblock .form-control");
        if ($("#unblock").hasClass("unblock_active")) {
            for (var i = 0; i < selectes.length; i++) {
                $(selectes[i]).prop("disabled", false);
            }
        } else {
            for (var i = 0; i < selectes.length; i++) {
                $(selectes[i]).prop("disabled", true);
            }
        }
    }

    isDisabled();

    $scope.load();
    // 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
    $scope.AllPeopleDtos = [];
    $scope.electState = "8";
    $scope.AllElectState = [
        {id: "1", name: "正常"},
        {id: "2", name: "黑名单"},
        {id: "8", name: "全部"}
    ];
    $scope.insurDetail = "8";
    $scope.AllInsurStatus = [
        {id: "1", name: "已投保"},
        {id: "2", name: "未投保"},
        {id: "8", name: "全部"}
    ];
    $scope.searchKeyType = "1";
    $scope.AllSearchKeyType = [
        {id: "1", name: "手机号"},
        {id: "2", name: "姓名"},
        {id: "3", name: "身份证号"},
        {id: "4", name: "防盗芯片编号"},
        {id: "5", name: "监护人手机号"}
    ];
    $scope.peopleType = '3';

    // 获取当前车辆的列表
    $scope.getPeoples = function () {
        if ($scope.startTime != null && $scope.startTime != '' && $scope.startTime != undefined) {
            $scope.startDate = $scope.startTime + " 00:00:00";
        }
        if ($scope.endTime != null && $scope.endTime != '' && $scope.endTime != undefined) {
            $scope.endDate = $scope.endTime + " 23:59:59";
        }

        $http({
            method: 'POST',
            url: '/i/people/findPeopleList',
            params: {
                pageNum: $scope.bigCurrentPage,
                pageSize: $scope.maxSize,
                startTime: $scope.startDate,
                endTime: $scope.endDate,
                recorderID: $scope.sysUserID,
                peopleType: $scope.peopleType,
                proPower: $scope.userPowerDto.sysUser.pro_power,
                cityPower: $scope.userPowerDto.sysUser.city_power,
                areaPower: $scope.userPowerDto.sysUser.area_power,
                proID: $scope.proID,
                cityID: $scope.cityID,
                areaID: $scope.areaID,
                peopleTele: $scope.peopleTele,
                peopleIdCards: $scope.peopleIdCards,
                peopleName: $scope.peopleName,
                peopleGuaCardNum: $scope.peopleGuaCardNum,
                guardianName: $scope.guardianName,
                guardianTele: $scope.guardianTele
            }
        }).success(function (data) {
            $scope.bigTotalItems = data.total;
            $scope.AllPeopleDtos = data.list;
            //console.log(data.list)
        });
    }

    $scope.pageChanged = function () {
        $scope.getPeoples();
    }
    // 获取当前冷库的列表
    $scope.auditChanged = function (optAudiet) {
        $scope.getPeoples();
    }

    $scope.goSearch = function () {
        if ($scope.searchKeyType == "1") {
            $scope.peopleTele = $scope.searchKey;
        }
        else if ($scope.searchKeyType == "2") {
            $scope.peopleName = $scope.searchKey;
        }
        else if ($scope.searchKeyType == "3") {
            $scope.peopleIdCards = $scope.searchKey;
        }
        else if ($scope.searchKeyType == "4") {
            $scope.peopleGuaCardNum = $scope.searchKey;
        }
        else if ($scope.searchKeyType == "5") {
            $scope.guardianTele = $scope.searchKey;
        }
        else {

        }
        $scope.getPeoples();
    }
    $scope.showAll = function () {
        $scope.reload();
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

    //获取全部省For ADD
    $scope.getPro = function () {

        $http.get('/i/city/findProvinceList').success(function (data) {
            $scope.provinces = data;
            //console.log(data);
            $scope.addProvinceID = data[0].province_id;
            $scope.getCitis();
        });
    }
    //根据省ID获取全部市For ADD
    $scope.getCitis = function () {
        $http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.addProvinceID
            }
        }).success(function (data) {
            $scope.citis = data;
            //console.log(data)
            var addCity = {"city_id": "-1", "name": "不限"};
            $scope.citis.push(addCity);
            $scope.addCityID = "-1";
            $scope.getAreas();
        });
    }
    //根据市ID获取全部区For ADD
    $scope.getAreas = function () {
        $http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.addCityID
            }
        }).success(function (data) {
            $scope.areas = data;
            var addArea = {"area_id": "-1", "name": "不限"};
            $scope.areas.push(addArea)
            $scope.addAreaID = "-1";
        });
    }

    //获取全部省For Search；并添加上不限的权限
//    $http.get('/i/city/findProvinceList').success(function (data) {
//        $scope.provincesForSearch = data;
//        var province = {"province_id":"-1","name":"不限"};
//        $scope.provincesForSearch.push(province);
//        $scope.proID = "-1";
//    });
    //根据省ID获取全部市For Search；并添加上不限的权限
    $scope.getCitisForSearch = function () {
        $http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.proID
            }
        }).success(function (data) {
            $scope.citisForSearch = data;
            var city = {"city_id": "-1", "name": "不限"};
            $scope.citisForSearch.push(city);
            $scope.cityID = "-1";
            $scope.getAreasForSearch();
        });
    };
    //根据市ID获取全部省For Search；并添加上不限的权限
    $scope.getAreasForSearch = function () {
        $http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.cityID
            }
        }).success(function (data) {
            $scope.areasForSearch = data;
            var area = {"area_id": "-1", "name": "不限"};
            $scope.areasForSearch.push(area);
            $scope.areaID = "-1";
        });
    };


    $scope.getCitisForUpdate = function () {
        $http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.updatePeople.people.pro_id
            }
        }).success(function (data) {
            $scope.citisForUpdate = data;
            var updateCity = {"city_id": "-1", "name": "不限"};
            $scope.citisForUpdate.push(updateCity);
            $scope.updatePeople.people.city_id = "-1";
            $scope.getAreasForUpdate();
        });
    }

    $scope.getAreasForUpdate = function () {
        $http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.updatePeople.people.city_id
            }
        }).success(function (data) {
            $scope.areasForUpdate = data;
            var updateArea = {"area_id": "-1", "name": "不限"};
            $scope.areasForUpdate.push(updateArea);
            $scope.updatePeople.people.area_id = "-1";
        });
    }


    $scope.goDeletePeople = function (peopleID) {
        if (delcfm()) {
            $http.get('/i/people/deletePeopleByID', {
                params: {
                    "peopleID": peopleID
                }
            }).success(function (data) {
                $scope.getPeoples();
            });
        }
    }
    $scope.goDeletePeoples = function () {
        if (delcfm()) {
            var peopleIDs = [];
            for (var i = 0; i < $scope.selected.length; i++) {
                peopleIDs.push($scope.selected[i].people.people_id);
            }
            if (peopleIDs.length > 0) {
                $http({
                    method: 'DELETE',
                    url: '/i/people/deletePeopleByIDs',
                    params: {
                        'peopleIDs': peopleIDs
                    }
                }).success(function (data) {
                    $scope.getPeoples();
                });
            }
        }
    }

    $scope.selected = [];
    $scope.toggle = function (electDto, list) {
        var idx = list.indexOf(electDto);
        if (idx > -1) {
            list.splice(idx, 1);
        }
        else {
            list.push(electDto);
        }
    };
    $scope.exists = function (electDto, list) {
        return list.indexOf(electDto) > -1;
    };
    $scope.isChecked = function () {
        return $scope.selected.length === $scope.AllPeopleDtos.length;
    };
    $scope.toggleAll = function () {
        if ($scope.selected.length === $scope.AllPeopleDtos.length) {
            $scope.selected = [];
        } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
            $scope.selected = $scope.AllPeopleDtos.slice(0);
        }
    };

    $scope.getpeopleIDsFromSelected = function (audit) {
        var peopleIDs = [];
        for (var i = 0; i < $scope.selected.length; i++) {
            if (audit != undefined)
                $scope.selected[i].audit = audit;
            peopleIDs.push($scope.selected[i].id);
        }
        return peopleIDs;
    }

    $scope.getInsur = function (insur_detail) {
        if (insur_detail == 1)
            return '已投保';
        else if (insur_detail == null) {
            return '未知';
        }
        else {
            return '未投保';
        }
    }

    $scope.getState = function (elect_state) {
        if (elect_state == 1)
            return '正常';
        else {
            return '黑名单';
        }
    }

    function checkInput() {
        var flag = true;
        // 检查必须填写项
        if ($scope.addPeopleGuaCardNum == undefined || $scope.addPeopleGuaCardNum == '') {
            flag = false;
        }
        return flag;
    }

    $scope.AllPeopleType = [
        {id: "1", name: "小孩"},
        {id: "2", name: "老人"},
        {id: "3", name: "吸毒者"},
        {id: "4", name: "犯罪者"}
    ];

    $scope.AllSexType = [
        {id: "0", name: "女"},
        {id: "1", name: "男"}
    ];
    $scope.addPeopleAge = "25"
    $scope.addPeopleSex = "0"
    $scope.addPeopleType = "3";
    $scope.submit = function () {
        if (checkInput()) {
            data = {
                'people_gua_card_num': $scope.addPeopleGuaCardNum,
                'people_tele': $scope.addPeopleTele,
                'people_name': $scope.addPeopleName,
                'people_sex': $scope.addPeopleSex,
                'people_id_cards': $scope.addPeopleIdCards,
                'people_age': $scope.addPeopleAge,
                'people_type': $scope.addPeopleType,
                'guardian_name': $scope.addGuardianName,
                'guardian_tele': $scope.addGuardianTele,
                'guardian_relation': $scope.addGuardianRelation,
                'contact_address': $scope.addContactAddress,
                'pro_id': $scope.addProvinceID,
                'city_id': $scope.addCityID,
                'area_id': $scope.addAreaID,
                'people_pic': $scope.peoplePic,
                'recorder_id': $rootScope.admin.user_id
            };
            Upload.upload({
                url: '/i/people/addPeople',
                headers: {'Content-Transfer-Encoding': 'utf-8'},
                data: data
            }).success(function (data) {
                if (data.success) {
                    alert(data.message);
                    $scope.getPeoples();
                    $("#addCar").modal("hide");
                }
                else {
                    alert(data.message);
                }
            });
        }
        else {
            alert("防盗芯片编号不能为空");
        }
    }
    $scope.goUpdatePeople = function (peopleID) {
        for (var i = 0; i < $scope.AllPeopleDtos.length; i++) {
            if ($scope.AllPeopleDtos[i].people.people_id == peopleID) {
                $scope.updatePeople = $scope.AllPeopleDtos[i];
                $scope.updatePeopleGuaCardNum = $scope.updatePeople.people.people_gua_card_num;
                $scope.updatePeople.people.pro_id = $scope.updatePeople.people.pro_id == null ? "-1" : $scope.updatePeople.people.pro_id + "";
                $scope.updatePeople.people.city_id = $scope.updatePeople.people.city_id == null ? "-1" : $scope.updatePeople.people.city_id + "";
                $scope.updatePeople.people.area_id = $scope.updatePeople.people.area_id == null ? "-1" : $scope.updatePeople.people.area_id + "";
                $scope.updatePeople.people.people_sex = $scope.updatePeople.people.people_sex + "";
                $scope.updatePeople.people.people_type = $scope.updatePeople.people.people_type + "";
                $http.get('/i/city/findCitysByProvinceId', {
                    params: {
                        "provinceID": $scope.updatePeople.people.pro_id
                    }
                }).success(function (data) {
                    $scope.citisForUpdate = data;
                });
                $http.get('/i/city/findAreasByCityId', {
                    params: {
                        "cityID": $scope.updatePeople.people.city_id
                    }
                }).success(function (data) {
                    $scope.areasForUpdate = data;
                });
                if ($scope.updatePeople.people.elect_type != undefined) {
                    $(":radio[name='updatePeopleType'][value='" + $scope.updatePeople.people.elect_type + "']").prop("checked", "checked");
                }
                break;
            }
        }
    };
    function checkInputForUpdate() {
        var flag = true;
        // 检查必须填写项
        if ($scope.updatePeople.people.people_gua_card_num == undefined || $scope.updatePeople.people.people_gua_card_num == '') {
            flag = false;
        }
        return flag;
    }

    $scope.update = function () {
        if (checkInputForUpdate()) {
            data = {
                'people_id': $scope.updatePeople.people.people_id,
                'people_gua_card_num': $scope.updatePeople.people.people_gua_card_num,
                'people_tele': $scope.updatePeople.people.people_tele,
                'people_name': $scope.updatePeople.people.people_name,
                'people_sex': $scope.updatePeople.people.people_sex,
                'people_id_cards': $scope.updatePeople.people.people_id_cards,
                'people_age': $scope.updatePeople.people.people_age,
                'people_type': $scope.updatePeople.people.people_type,
                'guardian_name': $scope.updatePeople.people.guardian_name,
                'guardian_tele': $scope.updatePeople.people.guardian_tele,
                'guardian_relation': $scope.updatePeople.people.guardian_relation,
                'contact_address': $scope.updatePeople.people.contact_address,
                'pro_id': $scope.updatePeople.people.pro_id,
                'city_id': $scope.updatePeople.people.city_id,
                'area_id': $scope.updatePeople.people.area_id,
                'people_pic': $scope.updatePeople.people.people_pic,
                'update_id': $rootScope.admin.user_id,
                'wornCardNum': $scope.updatePeopleGuaCardNum
            };
            Upload.upload({
                url: '/i/people/updatePeople',
                headers: {'Content-Transfer-Encoding': 'utf-8'},
                data: data
            }).success(function (data) {
                if (data.success) {
                    alert(data.message);
                    $scope.getPeoples();
                    $("#updateCar").modal("hide");
                }
                else {
                    alert(data.message);
                }
            });
        } else {
            alert("防盗芯片编号不能为空");
        }
    }

    $scope.goSearchForTrace = function (gua_card_num) {
        $scope.gua_card_numForTrace = gua_card_num;
        $http.get('/i/elect/findElectTrace', {
            params: {
//							'pageNum' : $scope.bigTotalItemsForTrace,
//							'pageSize' : $scope.maxSizeForTrace,
                "plateNum": null,
                "guaCardNum": gua_card_num,
                "startTimeForTrace": $scope.searchTraceStart,
                "endTimeForTrace": $scope.searchTraceEnd
            }
        }).success(function (data) {
            $scope.traceStations = data;
            //$scope.bigTotalItemsForTrace = data.total;
        });
    }
    $scope.goSearchForTraceWithTime = function () {
        $scope.goSearchForTrace($scope.gua_card_numForTrace);
    }
//		$scope.pageChangedForTrace = function() {
//			$scope.goSearchForTrace($scope.gua_card_numForTrace);
//		}

    //选择日期
    $('#buyCarDateUpdate').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $('#byCarDate').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $('#guijiSearchStart').datetimepicker({
        format: 'yyyy-mm-dd - hh:ii:ss.s',
        //minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $("#guijiSearchEnd").datetimepicker({
        format: 'yyyy-mm-dd - hh:ii:ss.s',
        //minView: 'month',
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $('#electDateStart').datetimepicker({
        format: 'yyyy-mm-dd',//时间格式
        minView: "month",//最小选择到月
        autoclose: true,//选择好时间关闭弹框
        maxDate: new Date(),//默认当前时间
        pickerPosition: "bottom-left"//位置左下
    });
    $("#electDateEnd").datetimepicker({
        format: 'yyyy-mm-dd',
        minView: 'month',
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $("#djPrint").on('click', function () {
        window.print();
    })
});

