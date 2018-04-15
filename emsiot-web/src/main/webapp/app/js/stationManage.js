coldWeb.controller('stationManage', function ($rootScope, $scope, $state, $cookies, $http, $location) {
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
		 var stationInfo = "<div style=\"width:300px;height:400px;\">";
		 var stationWin;

		 var currentpt = new BMap.Point(e.point.lng,e.point.lat);
		 stationInfo += "此处的经度为："+e.point.lng+"<br>此处的纬度为："+e.point.lat+"<br>";
		 geoc.getLocation(e.point, function(rs){
			 	if(rs==null){
			 		stationInfo+="获取不到具体位置";
			 		console.log("null++++++");
					stationWin = new BMap.InfoWindow(stationInfo+"<div>"); 
			 	}
			 	else{
			 		var addComp = rs.addressComponents;
			 		console.log(addComp.province);
			 		//stationInfo += addComp.province;
			 		stationInfo += addComp.province +"," + addComp.city +"," + addComp.district + ", " + addComp.street + "," + addComp.streetNumber;
			 	}
				 stationWin = new BMap.InfoWindow(stationInfo+"<div>"); 
	     
	     }); 
		 

		 //var myIcon = new BMap.Icon("../app/img/station.jpg", new BMap.Size(43,50));
		 var marker = new BMap.Marker(currentpt); 
		 marker.addEventListener("click", function(){          
		        this.openInfoWindow(stationWin);
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
	 

	$scope.load();
	// 显示最大页数
    $scope.maxSize = 7;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
	$scope.AllStations = [];
	$scope.optAudit = '8';
	 // 获取当前用户的列表

	  
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
	// 获取当前冷库的列表
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
    
    // 获取角色
//    $http.get('/i/userrole/findAllUserRole').success(function (data) {
//        $scope.userRoles = data;
//        $scope.addUserRoleid = data[0].user_role_id;
//    });
	
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
    
    $scope.getAudit = function(i){
    	if(i==1)
    		return '有效';
        else{
    		return '无效';
    	}
    }
    
    
    function checkInput(){
        var flag = true;
        // 检查必须填写项
        if ($scope.username == undefined || $scope.username == '') {
            flag = false;
        }
        if ($scope.password == undefined || $scope.password == '') {
            flag = false;
        }
        return flag;
    }

    
    
    $scope.submit = function(){
        if (checkInput()){
          if($scope.password==$scope.password1){
        	var valid;
        	if($scope.validforadd)  valid = 1;
        	else  valid = 2;
            $http({
            	method : 'GET', 
    			url:'/i/user/addUser',
    			params:{
    				'user_name': $scope.username,
    				'password': $scope.password,
    				'user_role_id' : $scope.addUserRoleid,
    				'company': $scope.company,
    				'pro_id' : $scope.addProjectid,
    				'comp_factory_id' : $scope.addcompfactoryid,
    				'valid_status' : valid,
    				'user_tel' : $scope.telephone
    			}
    		}).then(function (resp) {
    			 alert(resp.data.message);
                 $scope.getUsers();
                 $("#addUser").modal("hide"); 
            });
           }
          else{
        	  alert("两次密码不一致!");
           }
          } else {
            alert("请填写用户名或密码!");
        }
    }
    
	 $scope.goUpdateUser = function(userID) {
		    $scope.validforupdate  = false;
	    	$http.get('/i/user/findUserByID', {
	            params: {
	                "spaceUserID": userID
	            }
	        }).success(function(data){
			    if(data!=null&&data.user_id!=undefined){
					 $scope.userForUpdate = data;
					 if($scope.userForUpdate.valid_status==1)
						 $scope.validforupdate = true;
			    }
		     });
		};
		function checkInputForUpdate(){
	        var flag = true;
	        // 检查必须填写项
	        if ($scope.userForUpdate.user_name == undefined || $scope.userForUpdate.user_name == '') {
	            flag = false;
	        }
	       /* if ($scope.userForUpdate.password == undefined ||  $scope.userForUpdate.password == '') {
	            flag = false;
	        }*/
	        return flag;
	    }
		 $scope.update = function(){
			 if (checkInputForUpdate()){
		          /*if($scope.passwordForUpdate==$scope.passwordForUpdate1){*/
		        	var valid;
		        	if($scope.validforupdate)  valid = 1;
		        	else  valid = 2;
		            $http({
		            	method : 'GET', 
		    			url:'/i/user/updateUser',
		    			params:{
		    				'user_id': $scope.userForUpdate.user_id,
		    				'user_name': $scope.userForUpdate.user_name,
		    				'password': '',
//		    				'password': null,
		    				'user_role_id' : $scope.userForUpdate.user_role_id,
		    				'company':  $scope.userForUpdate.company,
		    				'pro_id' : $scope.userForUpdate.pro_id,
		    				'comp_factory_id' : $scope.userForUpdate.comp_factory_id,
		    				'valid_status' : valid,
		    				'user_tel' : $scope.userForUpdate.user_tel
		    			}
		    		}).then(function (resp) {
		    			 alert(resp.data.message);
		                 $scope.getUsers();
		                 $("#updateUser").modal("hide"); 
		            });
		          /* }
		          else{
		        	  alert("两次密码不一致!");
		           }*/
		          } else {
		            alert("请填写用户名!");
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
