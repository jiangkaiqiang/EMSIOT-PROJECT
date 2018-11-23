/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('stationExceptionRecprdManage', function ($rootScope, $scope, $state, $cookies, Upload, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
		   });	 
		 
	};

$scope.load();
// 显示最大页数
$scope.maxSize = 10;
// 总条目数(默认每页十条)
$scope.bigTotalItems = 10;
// 当前页
$scope.bigCurrentPage = 1;
$scope.AllStationRecord = [];
$scope.stationStatus = "";
 // 获取当前基站的列表
$scope.getStations = function() {
	$http({
		method : 'POST',
		url : '/i/station/findAllStationStatusRecord',
		params : {
			pageNum : $scope.bigCurrentPage,
			pageSize : $scope.maxSize,
			startTime : $("#startTime").val(),
			endTime : $("#endTime").val(),
			stationPhyNum : $scope.stationPhyNum,
			proPower : $rootScope.admin.pro_power,
			cityPower : $rootScope.admin.city_power,
			areaPower : $rootScope.admin.area_power,
			stationStatus : $scope.stationStatus
		}
	}).success(function(data) {
		$scope.bigTotalItems = data.total;
		$scope.AllStationRecord = data.list;

	});
}

$scope.pageChanged = function() {
	$scope.getStations();
}
$scope.getStations();
// 获取当前基站的列表
$scope.auditChanged = function(optAudiet) {
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
	if(delcfm()){
	$http.get('/i/station/deleteStationRecordByID', {
        params: {
            "stationID": stationID
        }
    }).success(function (data) {
    	$scope.getStations();
    	alert("删除成功");
    });
	}
}
$scope.goDeleteStations = function(){
	if(delcfm()){
	var stationIDs = [];
	for(var i=0 ;i< $scope.selected.length;i++){
		stationIDs.push($scope.selected[i].station_status_id);
	}
	if(stationIDs.length >0 ){
		$http({
			method:'DELETE',
			url:'/i/station/deleteStationRecordByIDs',
			params:{
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
	  }
	  else {
	    list.push(station);
	  }
};
$scope.exists = function (station, list) {
	return list.indexOf(station) > -1;
};
$scope.isChecked = function() {
    return $scope.selected.length === $scope.AllStationRecord.length;
};
$scope.toggleAll = function() {
    if ($scope.selected.length === $scope.AllStationRecord.length) {
    	$scope.selected = [];
    } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
    	$scope.selected = $scope.AllStationRecord.slice(0);
    }
};

$scope.getStationIDsFromSelected = function(audit){
	var stationIDs = [];
	for(var i=0 ;i< $scope.selected.length;i++){
		if(audit != undefined)
			$scope.selected[i].audit = audit;
		stationIDs.push($scope.selected[i].id);
	}
	return stationIDs;
}

$scope.AllStationStatus = [
	{id:"",name:"全部"},
    {id:"0",name:"正常"},
    {id:"1",name:"故障"}
];

$scope.getStatus = function(i){
	if(i==0)
		return '正常';
    else{
		return '故障';
	}
}

$scope.addStationType = "1";
$scope.addStationStatus = "1";
$scope.addInstallPic = function () {
	
};

    $('#alarmDateStart').datetimepicker({
        format: 'yyyy-mm-dd - hh:ii:ss.s',
        //minView: "month",
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
    $("#alarmDateEnd").datetimepicker({
        format : 'yyyy-mm-dd - hh:ii:ss.s',
        //minView: 'month',
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
});