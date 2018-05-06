/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('alarmTrack', function ($rootScope, $scope, $state, $cookies, $http, $location) {
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

    $scope.goHome = function () {
        $state.reload();
    }

    //选择日期

    $('#alarmDateStart').datetimepicker({
        format: 'yyyy-mm-dd - hh:mm:ss',
        //minView: "month",
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
    $("#alarmDateEnd").datetimepicker({
        format : 'yyyy-mm-dd - hh:mm:ss',
        //minView: 'month',
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
    
    $scope.load();
	// 显示最大页数
	$scope.maxSize = 10;
	// 总条目数(默认每页十条)
	$scope.bigTotalItems = 10;
	// 当前页
	$scope.bigCurrentPage = 1;
	$scope.AllElectalarms = [];
	//根据条件加载用户所有的黑名单
	$scope.getElectalarmsByOptions = function() {
		$http({
			method : 'POST',
			url : '/i/electalarm/findAllElectAlarmByOptions',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				alarmTime : $scope.alarmTime,
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.AllElectalarms = data.list;
		});
	}
	$scope.getElectalarmsByOptions();
	 $scope.selected = [];
	    $scope.exists = function (electAlarmDto, list) {
	    	return list.indexOf(electAlarmDto) > -1;
	    };
	    $scope.toggle = function (electAlarmDto, list) {
			  var idx = list.indexOf(electAlarmDto);
			  if (idx > -1) {
			    list.splice(idx, 1);
			  }
			  else {
			    list.push(electAlarmDto);
			  }
	  };
	  
	  $scope.pageChanged = function() {
		  $scope.getElectalarmsByOptions();
	 }
	  $scope.goSearch = function () {
			$scope.getElectalarmsByOptions();
	  }
	  function delcfm() {
	      if (!confirm("确认要删除？")) {
	          return false;
	      }
	      return true;
	}
});