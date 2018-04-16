coldWeb.controller('stationManage', function ($rootScope, $scope, $state, $cookies, Upload, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
		   });	 
		 
	};
	 var mapStation = new BMap.Map("stationMap",{
   	  minZoom:5,
   	  maxZoom:30
   	 });    // 创建Map实例
	 mapStation.centerAndZoom("喀什", 10);  // 初始化地图,设置中心点坐标和地图级别
	 mapStation.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
	 mapStation.disableDoubleClickZoom();
   	 //通过双击地图添加基站，弹出窗口
	 var geoc = new BMap.Geocoder();    

	 mapStation.addEventListener("dblclick",function(e){
		 //var stationInfo = "<div style=\"width:300px;height:400px;\">";
		 //var stationWin;
		 var currentpt = new BMap.Point(e.point.lng,e.point.lat);
		 $scope.addStationLng = e.point.lng;
		 $scope.addStationLat = e.point.lat;
		 //stationInfo += "此处的经度为："+e.point.lng+"<br>此处的纬度为："+e.point.lat+"<br>";
		 geoc.getLocation(e.point, function(rs){
			 	if(rs==null){
			 		//stationInfo+="获取不到具体位置";
			 		//console.log("null++++++");
					//stationWin = new BMap.InfoWindow(stationInfo+"<div>"); 
					$scope.addStationAddress = "获取不到具体位置";
			 	}
			 	else{
			 		var addComp = rs.addressComponents;
			 		//console.log(addComp.province);
			 		//stationInfo += addComp.province;
			 		//stationInfo += addComp.province +"," + addComp.city +"," + addComp.district + ", " + addComp.street + "," + addComp.streetNumber;
			 		$scope.addStationAddress = addComp.province +"," + addComp.city +"," + addComp.district + ", " + addComp.street + "," + addComp.streetNumber;
			 	}
		    //stationWin = new BMap.InfoWindow(stationInfo+"<div>"); 
		    $("#addStation").modal("show");
		    $("#addStationLng").val($scope.addStationLng);
		    $("#addStationLat").val($scope.addStationLat);
		    $("#addStationAddress").val($scope.addStationAddress);
	     }); 
		 
		 //var myIcon = new BMap.Icon("../app/img/station.jpg", new BMap.Size(43,50));
		 var marker = new BMap.Marker(currentpt); 
		 marker.addEventListener("click", function(){          
		        //this.openInfoWindow(stationWin);
			 $('#addStation').modal('show')
		  });
		 mapStation.addOverlay(marker); 
	 });
	 
	 $scope.goStationLocation = function (stationID) {
	    	$http.get('/i/station/findStationByID', {
	            params: {
	                "stationID": stationID
	            }
	        }).success(function (data) {
	        	$scope.locationStation = data;
	        	//这里我得到了基站的信息包括经纬度等，需要将其显示在地图上
	        	alert($scope.locationStation.longitude);
	        });
	    	
	}
	 
	$('#electAlarmDate').datetimepicker({
	        format: 'yyyy-mm-dd  hh:mm:ss',
	        autoclose:true,
	        maxDate:new Date(),
	        pickerPosition: "bottom-left"
    });
	$scope.load();
	// 显示最大页数
    $scope.maxSize = 7;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
	$scope.AllStations = [];
	$scope.optAudit = '8';
	 // 获取当前基站的列表
    $scope.getStations = function() {
		$http({
			method : 'POST',
			url : '/i/station/findAllStations',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				startTime : $scope.startTime,
				endTime : $scope.endTime,
				stationPhyNum : $scope.stationPhyNum,
				stationName : $scope.stationName,
				stationStatus : $scope.stationStatus
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.AllStations = data.list;
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
    $scope.addStationType = "1";
    $scope.addStationStatus = "0";
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
    	$scope.installPic = null;
    };
    
    $scope.submit = function(){
        if (checkInput()){
        	var stationType="";
        	if($scope.addStationType=="1")  
        		stationType = "SP-RFS-336-391";
        	else  
        		stationType = "RG-RFS-336-392";
        	data = {
        			'station_phy_num': $scope.addStationPhyNum,
    				'station_name': $scope.addStationName,
    				'longitude' : $scope.addStationLng,
    				'latitude': $scope.addStationLat,
    				'station_type' : stationType,
    				'station_status' : $scope.addStationStatus,
    				'install_date' : $scope.addInstallDate,
    				'soft_version' : $scope.addSoftVersion,
    				'contact_person' : $scope.addcontactPerson,
    				'contact_tele' : $scope.addcontactTele,
    				'stick_num' : "123123",
    				'station_address' : $scope.addStationAddress,
    				'install_pic' : $scope.installPic
	            };
	       Upload.upload({
	                url: '/i/station/addStation',
	                headers :{ 'Content-Transfer-Encoding': 'utf-8' },
	                data: data
	            }).success(function (data) {
            if(data.success){
            	 alert(data.message);
    			 $scope.getStations();
                 $("#addStation").modal("hide"); 
            }
        });
          }
       else {
    	   alert("请填写基站编号和基站名!");
        }
    }
		 $('#datetimepicker1').datetimepicker({  
		    	autoclose:true
		    }).on('dp.change', function (e) {  
		    });  
		 $('#datetimepicker2').datetimepicker({  
		    	autoclose:true
		    }).on('dp.change', function (e) {  
		    });  
});
