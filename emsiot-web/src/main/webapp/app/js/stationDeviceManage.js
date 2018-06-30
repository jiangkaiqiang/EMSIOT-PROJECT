/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('stationDeviceManage', function ($rootScope, $scope, $state, $cookies, Upload, $http, $location) {
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
$scope.AllStations = [];
$scope.stationStatus;
 // 获取当前基站的列表
$scope.getStations = function() {
	$http({
		method : 'POST',
		url : '/i/station/findAllStations',
		params : {
			pageNum : $scope.bigCurrentPage,
			pageSize : $scope.maxSize,
			startTime : $("#startTime").val(),
			endTime : $("#endTime").val(),
			stationPhyNum : $scope.stationPhyNum,
			stationName : $scope.stationName,
			proPower : $rootScope.admin.pro_power,
			cityPower : $rootScope.admin.city_power,
			areaPower : $rootScope.admin.area_power,
			stationStatus : $scope.stationStatus
		}
	}).success(function(data) {
		$scope.bigTotalItems = data.total;
		$scope.AllStations = data.list;
		$scope.stationStatus = '0';
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
$scope.goDeleteStations = function(){
	if(delcfm()){
	var stationIDs = [];
	for(i in $scope.selected){
		stationIDs.push($scope.selected[i].station_id);
	}
	if(stationIDs.length >0 ){
		$http({
			method:'DELETE',
			url:'/i/station/deleteStationByIDs',
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
    return $scope.selected.length === $scope.AllStations.length;
};
$scope.toggleAll = function() {
    if ($scope.selected.length === $scope.AllStations.length) {
    	$scope.selected = [];
    } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
    	$scope.selected = $scope.AllStations.slice(0);
    }
};

$scope.getStationIDsFromSelected = function(audit){
	var stationIDs = [];
	for(i in $scope.selected){
		if(audit != undefined)
			$scope.selected[i].audit = audit;
		stationIDs.push($scope.selected[i].id);
	}
	return stationIDs;
}

$scope.AllStationType = [
    {id:"1",name:"SP-RFS-336-391"},
    {id:"2",name:"RG-RFS-336-392"}
];
$scope.AllStationStatus = [
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
function checkInput(){
    var flag = true;
    // 检查必须填写项
    if ($scope.addStationPhyNum == undefined || $scope.addStationPhyNum == '') {
        flag = false;
    }
    if ($scope.addStationName == undefined || $scope.addStationName == '') {
        flag = false;
    }
    return flag;
}
$scope.addInstallPic = function () {
	
};
$scope.dropInstallPic = function(installPic){
	$scope.stationForUpdate.install_pic = null;
};

 $scope.goStationUpdate = function(stationID) {
    	$http.get('/i/station/findStationByID', {
            params: {
                "stationID": stationID
            }
        }).success(function(data){
		    $scope.stationForUpdate = data;
		    $scope.stationForUpdate.station_status = $scope.stationForUpdate.station_status+"";
		    if($scope.stationForUpdate.station_type=="SP-RFS-336-391")  
		    	$scope.stationForUpdate.station_type = "1";
	    	else ($scope.stationForUpdate.station_type=="SP-RFS-336-391")
	    		$scope.stationForUpdate.station_type = "2";
		    if($scope.stationForUpdate.install_pic!=null&&$scope.stationForUpdate.install_pic!=undefined){
		    	
		    }
	     });
	};
	
	$scope.goStationView = function(stationID) {
		$http.get('/i/station/findStationByID', {
            params: {
                "stationID": stationID
            }
        }).success(function(data){ 
        	$scope.viewStation = data;
        	if($scope.viewStation.station_status=="0"){
 			   $scope.viewStation.station_status = "正常";
 		   }
 		   else if($scope.viewStation.station_status=="1"){
 			   $scope.viewStation.station_status = "故障";
 		   }
 		   else{
 			   
 		   }
        });
	};
	
	function checkInputForUpdate(){
        var flag = true;
        // 检查必须填写项
        if ($scope.stationForUpdate.station_phy_num == undefined || $scope.stationForUpdate.station_phy_num == '') {
            flag = false;
        }
        if ($scope.stationForUpdate.station_name == undefined || $scope.stationForUpdate.station_name == '') {
            flag = false;
       }
        return flag;
	}   
    $scope.goUpdateStation = function(){
	        if (!checkInputForUpdate()){
	    	  alert("请填写基站编号和基站名!");
    	      return;
            }
	    	var stationType="";
	    	if($scope.stationForUpdate.station_type=="1")  
	    		stationType = "SP-RFS-336-391";
	    	else  
	    		stationType = "RG-RFS-336-392";
	    	data = {
	    			'station_id': $scope.stationForUpdate.station_id,
	    			'station_phy_num': $scope.stationForUpdate.station_phy_num,
					'station_name': $scope.stationForUpdate.station_name,
					'longitude' : $scope.stationForUpdate.longitude,
					'latitude': $scope.stationForUpdate.latitude,
					'station_type' : stationType,
					'station_status' : $scope.stationForUpdate.station_status,
					'install_date' : $scope.stationForUpdate.install_date,
					'soft_version' : $scope.stationForUpdate.soft_version,
					'contact_person' : $scope.stationForUpdate.contact_person,
					'contact_tele' : $scope.stationForUpdate.contact_tele,
					'stick_num' : "123123",
					'station_address' : $scope.stationForUpdate.station_address,
					'install_pic' : $scope.stationForUpdate.install_pic
	            };
	       Upload.upload({
	                url: '/i/station/updateStation',
	                headers :{ 'Content-Transfer-Encoding': 'utf-8' },
	                data: data
	            }).success(function (data) {
	        if(data.success){
	        	 alert(data.message);
				 $scope.getStations();
	             $("#updateStation").modal("hide"); 
	        }
	        else{
	           	 alert(data.message);
	           }
	    });
	  }
	
    //选择日期
     $('#electAlarmDate').datetimepicker({
        format: 'yyyy-mm-dd  hh:mm:ss',
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
     });
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
});