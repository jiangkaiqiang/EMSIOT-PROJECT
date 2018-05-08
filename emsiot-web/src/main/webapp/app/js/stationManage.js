coldWeb.controller('stationManage', function ($rootScope, $scope, $state, $cookies, Upload, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
				$scope.getStations();
		   });	 
		 
	};
	
	 var mapStation = new BMap.Map("stationMap",{
   	  minZoom:5,
   	  maxZoom:30
   	 });    // 创建Map实例
	 function G(id) {
		    return document.getElementById(id);
	 }
	 var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
			    {
			        "input": "suggestId"
			        , "location": mapStation
			    });

			ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
			    var str = "";
			    var _value = e.fromitem.value;
			    var value = "";
			    if (e.fromitem.index > -1) {
			        value = _value.province + _value.city + _value.district + _value.street + _value.business;
			    }
			    str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

			    value = "";
			    if (e.toitem.index > -1) {
			        _value = e.toitem.value;
			        value = _value.province + _value.city + _value.district + _value.street + _value.business;
			    }
			    str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
			    G("searchResultPanel").innerHTML = str;
			});

			var myValue;
			ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
			    var _value = e.item.value;
			    myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
			    G("searchResultPanel").innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
			    setPlace();
			});

			function setPlace() {
				mapStation.clearOverlays();    //清除地图上
			    function myFun() {
			        var pp = local.getResults().getPoi(0).point;//获取第一个智能搜索信息
			        mapStation.centerAndZoom(pp, 18);
			        mapStation.addOverlay(new BMap.Marker(pp));

			    }

			    var local = new BMap.LocalSearch(mapStation, {
			        onSearchComplete: myFun
			    });
			    local.search(myValue);
			}
	 mapStation.centerAndZoom("喀什", 10);  // 初始化地图,设置中心点坐标和地图级别
	 mapStation.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
	 mapStation.disableDoubleClickZoom();
   	 //通过双击地图添加基站，弹出窗口
	 var geoc = new BMap.Geocoder();    

	 mapStation.addEventListener("dblclick",function(e){  //双击添加基站，显示图标
		 if($rootScope.rootUserPowerDto.stationAdd!="1"){
			alert("没有添加基站权限！");
			return;
		 }
		 mapStation.clearOverlays();
		 var currentpt = new BMap.Point(e.point.lng,e.point.lat);
		 $scope.addStationLng = e.point.lng;
		 $scope.addStationLat = e.point.lat;
		 geoc.getLocation(e.point, function(rs){
			 	if(rs==null){
			 		
					$scope.addStationAddress = "获取不到具体位置";
			 	}
			 	else{
			 		var addComp = rs.addressComponents;
			 		$scope.addStationAddress = addComp.province +"," + addComp.city +"," + addComp.district + ", " + addComp.street + "," + addComp.streetNumber;
			 	}
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
	        	var findStationPt = new BMap.Point(data.longitude,data.latitude);
	     	   	var findStationMarker = new BMap.Marker(findStationPt); 
	     	   	mapStation.addOverlay(findStationMarker);
	     	   	mapStation.centerAndZoom(findStationPt,17);
	        	
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
				proPower : $rootScope.admin.pro_power,
				cityPower : $rootScope.admin.city_power,
				areaPower : $rootScope.admin.area_power,
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
    
    $scope.AllStationStatusForShow = [
        {id:"1",name:"隐藏基站"},
        {id:"2",name:"显示异常基站"}
    ];
    $scope.stationStatusForShow = "1";
    $scope.stationStatusShow = function () {
    	if($scope.stationStatusForShow =="2"){
    		$http.get('/i/station/findStationsByStatus', {
                params: {
                    "stationStatus": "1"
                }
            }).success(function (data) {  //在地图上显示异常基站
	            	$scope.errorStation = data;
	            	var len=data.length;
	            	if(len>0){
	            		for(var i=0;i<len;i++){
	            		 	var abnormalPt = new BMap.Point(data[i].longitude,data[i].latitude);
	        	     	   	var abnormalMarker = new BMap.Marker(abnormalPt); 
	        	     	   	mapStation.addOverlay(abnormalMarker);
	            		}
	            	}
	            	else{
	            		alert("没有异常基站！");
	            	}
            });
    	}
    	else{
    		//隐藏显示的异常基站
    		mapStation.clearOverlays();
    		}
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
		//----------------基站收缩功能-----------------

		 $(".closeStationPosition").click(function(){
		    $(this).parents('.station-content').toggleClass("leftToggle");
		    if($(this).hasClass("glyphicon-chevron-left")){
		       $(this).removeClass("glyphicon-chevron-left").addClass("glyphicon-chevron-right")
		    }else{
		       $(this).removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-left");
		    }
		 });
});
