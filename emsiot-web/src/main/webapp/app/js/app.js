var coldWeb = angular.module('ColdWeb', ['ui.bootstrap', 'ui.router', 'ui.checkbox',
    'ngCookies', 'xeditable', 'isteven-multi-select', 'angucomplete', 'angular-table','ngFileUpload','remoteValidation']);
angular.element(document).ready(function ($ngCookies, $http, $rootScope) {
	angular.bootstrap(document, ['ColdWeb']);
});
coldWeb.run(function (editableOptions, adminService, $location) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs2', 'default'
    $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
      	admin = data;
      	if(admin == null || admin.user_id == 0 || admin.user_id==undefined){
  			url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
  			window.location.href = url;
  		}
  		adminService.setAdmin(admin);
      });
});

coldWeb.factory('adminService',['$rootScope','$http', function($rootScope,$http){
	return {
		setAdmin: function(admin){
	    	$rootScope.admin = admin;
//	    	if(admin != null && admin.user_id != 0 && admin.user_id!=undefined){
//	    	$http.get('/i/userrole/findUserRoleByUserID', {
//	            params: {
//	                "userID": $rootScope.admin.user_id
//	            }
//	        }).success(function(data){
//			    if(data!=null&&data.userRole.user_role_id!=undefined){
//			    	$rootScope.adminRoleDto = data;
//			    }
//		     });
//	    	}
	    	$rootScope.logout = function () {
	        	$http.get('/i/user/logout').success(function(data){
	        		$rootScope.admin = null;
	            });
	        	window.location.reload();
	        };
	    },
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
    });
});