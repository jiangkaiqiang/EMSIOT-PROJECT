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
		   });	 
		 
	};

	console.log("车辆报警页面展示成功");

	$scope.goHome = function() {
		$state.reload();
	}

	//选择日期

	$('#alarmDateStart').datetimepicker({
		format : 'yyyy-mm-dd - hh:mm:ss',
		//minView: "month",
		autoclose : true,
		maxDate : new Date(),
		pickerPosition : "bottom-left"
	});
	$("#alarmDateEnd").datetimepicker({
		format : 'yyyy-mm-dd - hh:mm:ss',
		//minView: 'month',
		autoclose : true,
		maxDate : new Date(),
		pickerPosition : "bottom-left"
	});

	
});