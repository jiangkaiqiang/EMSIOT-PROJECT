var coldWeb = angular.module('ColdWeb', ['ui.bootstrap', 'ui.router', 'ui.checkbox',
    'ngCookies', 'xeditable', 'isteven-multi-select', 'angucomplete', 'angular-table', 'ngFileUpload', 'remoteValidation']);
angular.element(document).ready(function ($ngCookies, $http, $rootScope) {
    angular.bootstrap(document, ['ColdWeb']);
});
coldWeb.controller('appCtrl', function ($rootScope, $scope, $state, $cookies, $http, Upload, $location) {
    //学生手机验证
    $scope.phoneReg = /^[1][3,4,5,7,8][0-9]{9}$/;

    //身份证验证
    $scope.idCardReg = /^\d{6}(18|19|20)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$/i;

    //防盗芯片验证
    $scope.guaCardNumReg = /^\d{6}$/;
    $scope.doDateStr = function(dateTime,str){
    	var date = new Date(dateTime);
        var year = date.getFullYear();
        var month = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1);
        var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
        var hours = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
        var min = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
        var seconds = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
        if(str==1){
        	return year + "-" + month + "-" + day + " " + hours + ":" + min + ":" + seconds;
        }else{
        	return year + "-" + month + "-" + day;
        }
    }

});
coldWeb.run(function (editableOptions, adminService, $location) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
    $.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
        admin = data;
        if (admin == null || admin.user_id == 0 || admin.user_id == undefined) {
            url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
            window.location.href = url;
        }
        adminService.setAdmin(admin);
    });
});

coldWeb.factory('adminService', ['$rootScope', '$http', function ($rootScope, $http) {
    return {
        setAdmin: function (admin) {
            $rootScope.admin = admin;

            //权限
            if ($rootScope.admin != null && $rootScope.admin.user_id != 0 && $rootScope.admin.user_id != undefined) {
                $http.get('/i/user/findUserByID', {
                    params: {
                        "spaceUserID": $rootScope.admin.user_id
                    }
                }).success(function (data) {
                    $rootScope.rootUserPowerDto = data;
                });
            }


            $rootScope.logout = function () {
                $http.get('/i/user/logout').success(function (data) {
                    $rootScope.admin = null;
                });

                window.location.reload();
                 
            };
            JS.Engine.start('conn');
            JS.Engine.on(
                {
                    msgData: function (msgData) {
                        var message = msgData.split(";");
                        if ($rootScope.admin.pro_power == -1) {
                            $("#message").text(message[3]);
                        }
                        else if ($rootScope.admin.pro_power == message[0]) {
                            if ($rootScope.admin.city_power == -1) {
                                $("#message").text(message[3]);
                            }
                            else if ($rootScope.admin.city_power == message[1]) {
                                if ($rootScope.admin.area_power == -1 || $rootScope.admin.area_power == message[2]) {
                                    $("#message").text(message[3]);
                                }
                            }
                        }
                    }
                }
            );
            JS.Engine.on(
                {
                    limitData: function (msgData) {
                        var message = msgData.split(";");
                        if ($rootScope.admin.pro_power == -1) {
                            $("#limitMessage").text(message[3]);
                        }
                        else if ($rootScope.admin.pro_power == message[0]) {
                            if ($rootScope.admin.city_power == -1) {
                                $("#limitMessage").text(message[3]);
                            }
                            else if ($rootScope.admin.city_power == message[1]) {
                                if ($rootScope.admin.area_power == -1 || $rootScope.admin.area_power == message[2]) {
                                    $("#limitMessage").text(message[3]);
                                }
                            }
                        }
                    },
                }
            );
            function checkInput() {
                var flag = true;
                // 检查必须填写项
                if ($rootScope.newPassword == undefined || $rootScope.newPassword2 == '' ||
                    $rootScope.newPassword2 == undefined || $rootScope.newPassword == '' ||
                    $rootScope.newPassword2 != $rootScope.newPassword) {
                    flag = false;
                }
                return flag;
            }

            $rootScope.changePassword = function () {
                if (checkInput()) {
                    $http({
                        method: 'GET',
                        url: '/i/user/changePwd',
                        params: {
                            'password': $rootScope.newPassword,
                            'userID': $rootScope.admin.user_id
                        }
                    }).then(function (resp) {
                        alert("修改成功");
                        window.location.reload();
                    }, function (resp) {
                        console.log('Error status: ' + resp.status);
                    }, function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        console.log('progress: ' + progressPercentage + '% ' + evt.name);
                    });
                } else {
                    alert("密码输入有误!");
                }
            }
        }
    }
}])

coldWeb.config(function ($stateProvider, $urlRouterProvider) {
     
    $urlRouterProvider.otherwise("/home");
    $stateProvider.state('home', {
        url: '/home',
        controller: 'home',
        templateUrl: 'app/template/home.html'
    }).state('userManage', {
        url: '/userManage',
        controller: 'userManage',
        templateUrl: 'app/template/userManage.html'
    }).state('operationLog', {
        url: '/operationLog',
        controller: 'operationLog',
        templateUrl: 'app/template/operationLog.html'
    }).state('personalSpace', {
        url: '/personalSpace',
        controller: 'personalSpace',
        templateUrl: 'app/template/personalSpace.html'
    }).state('userRoleManage', {
        url: '/userRoleManage',
        controller: 'userRoleManage',
        templateUrl: 'app/template/userRoleManage.html'
    }).state('electManage', {
        url: '/electManage',
        controller: 'electManage',
        templateUrl: 'app/template/electManage.html'
    }).state('stationManage', {
        url: '/stationManage',
        controller: 'stationManage',
        templateUrl: 'app/template/stationManage.html'
    }).state('limitAreaAlarm', {
        url: '/limitAreaAlarm',
        controller: 'limitAreaAlarm',
        templateUrl: 'app/template/limitAreaAlarm.html'
    }).state('sensitiveAreaAlarm', {
        url: '/sensitiveAreaAlarm',
        controller: 'sensitiveAreaAlarm',
        templateUrl: 'app/template/sensitiveAreaAlarm.html'
    }).state('limitArea', {
        url: '/limitArea',
        controller: 'limitArea',
        templateUrl: 'app/template/limitArea.html'
    }).state('sensitiveArea', {
        url: '/sensitiveArea',
        controller: 'sensitiveArea',
        templateUrl: 'app/template/sensitiveArea.html'
    }).state('blackListManage', {
        url: '/blackListManage',
        controller: 'blackListManage',
        templateUrl: 'app/template/blackListManage.html'
    }).state('alarmTrack', {
        url: '/alarmTrack',
        controller: 'alarmTrack',
        templateUrl: 'app/template/alarmTrack.html'
    }).state('stationDeviceManage', {
        url: '/stationDeviceManage',
        controller: 'stationDeviceManage',
        templateUrl: 'app/template/stationDeviceManage.html'
    }).state('stationExceptionRecprdManage', {
        url: '/stationExceptionRecprdManage',
        controller: 'stationExceptionRecprdManage',
        templateUrl: 'app/template/stationExceptionRecprdManage.html'
    }).state('alarmTrackMap', {
        url: '/alarmTrackMap',
        controller: 'alarmTrackMap',
        templateUrl: 'app/template/alarmTrackMap.html'
    }).state('kidsManage', {
        url: '/kidsManage',
        controller: 'kidsManage',
        templateUrl: 'app/template/kidsManage.html'
    }).state('peopleManageMap', {
        url: '/peopleManageMap',
        controller: 'peopleManageMap',
        templateUrl: 'app/template/peopleManageMap.html'
    }).state('oldPeopleManage', {
        url: '/oldPeopleManage',
        controller: 'oldPeopleManage',
        templateUrl: 'app/template/oldPeopleManage.html'
    }).state('gowsterPeopleManage', {
        url: '/gowsterPeopleManage',
        controller: 'gowsterPeopleManage',
        templateUrl: 'app/template/gowsterPeopleManage.html'
    });
});