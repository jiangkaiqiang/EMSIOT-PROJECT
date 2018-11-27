/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('limitAreaAlarm', function($rootScope, $scope, $state, $cookies, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
			 //获取全部管理员
			 $http.get('/i/user/findAllUsers',{
				 params :{
					 "proPower" :$rootScope.admin.pro_power,
					 "areaPower" : $rootScope.admin.area_power,
					 "cityPower" : $rootScope.admin.city_power
				 }
			 }).success(function(data){
				 $scope.sysUsers = data;
				 $scope.sysUserID = data[0].user_id;
			 });
		   });	 
	};

	//获取限制区域报警列表
	//显示最大页数
	$scope.maxSize = 10;
	// 总条目数(默认每页十条)
	$scope.bigTotalItems = 10;
	// 当前页
	$scope.bigCurrentPage = 1;
	$scope.AllLimitAlarmElects = [];
	$scope.limitAlarmElects=function(){
	$http({
		method : 'POST',
		url: '/i/limitareaalarm/findAllLimitAreaAlarmByOptions',
		params :{
			"pageNum" : $scope.bigCurrentPage,
			"pageSize" :$scope.maxSize,
			"plateNum" :$scope.plateNum,
			"areaName" :$scope.limitAreaName,
			"alarmDateStart" : $scope.startTime ,
			"alarmDateEnd" : $scope.endTime,
			proPower : $scope.admin.pro_power,
			cityPower : $scope.admin.city_power,
			areaPower : $scope.admin.area_power
		}
	}).success(function(data){
		$scope.bigTotalItems = data.data.total;
		$scope.AllLimitAlarmElects = data.data.list;
	});
	};
	$scope.load();
	$scope.limitAlarmElects();
	$scope.pageChanged = function() {
		$scope.limitAlarmElects();
	}
	$scope.goSearchForPlate = function(){
		$scope.limitAlarmElects();
	};


 //实现表格全选或者单选
	$scope.selected = [];
	$scope.toggle = function (alarm, list) {
		var idx = list.indexOf(alarm);
		if (idx > -1) {
			list.splice(idx, 1);
		}
		else {
			list.push(alarm);
		}
	};
	$scope.exists = function (alarm, list) {
		return list.indexOf(alarm) > -1;
	};
	$scope.isChecked = function() {
		return $scope.selected.length === $scope.AllLimitAlarmElects.length;
	};
	$scope.toggleAll = function() {
		if ($scope.selected.length === $scope.AllLimitAlarmElects.length) {
			$scope.selected = [];
		} else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
			$scope.selected = $scope.AllLimitAlarmElects.slice(0);
		}
	};


	//删除报警信息
	function delAlarm() {
		if (!confirm("确认要删除？")) {
			return false;
		}
		return true;
	}

	//单独删除
	$scope.goDeleteAlarm = function (alarmId) {
		if(delAlarm()){
			$http.get('/i/limitareaalarm/deletelimitAreaAlarmByID', {
				params: {
					"area_alarm_id": alarmId
				}
			}).success(function (data) {
				alert(data.message);
				$scope.limitAlarmElects();
			});
		}
	}

	//批量删除
	$scope.goDeleteAlarms = function(){
		if(delAlarm){
			var alarmIds = [];
			for(var i=0 ;i< $scope.selected.length;i++){
				alarmIds.push($scope.selected[i].alarm_id);
			}
			if(alarmIds.length >0 ){
				$http({
					method:'DELETE',
					url:'/i/limitareaalarm/deleteLimitAreaAlarmByIDs',
					params:{
						'area_alarm_ids': alarmIds
					}
				}).success(function(data){
					$scope.limitAlarmElects();
					alert(data.message);
				});
			}

		}
	};



	//选择日期

	$('#alarmDateStart').datetimepicker({
		format : 'yyyy-mm-dd - hh:ii:ss.s',
		//minView: "month",
		autoclose : true,
		maxDate : new Date(),
		pickerPosition : "bottom-left"
	});
	$("#alarmDateEnd").datetimepicker({
		format : 'yyyy-mm-dd - hh:ii:ss.s',
		//minView: 'month',
		autoclose : true,
		maxDate : new Date(),
		pickerPosition : "bottom-left"
	});

	
});