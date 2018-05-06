coldWeb.controller('userManage', function ($rootScope, $scope, $state, $cookies, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
				$http.get('/i/user/findUserByID', {
		            params: {
		                "spaceUserID": $rootScope.admin.user_id
		            }
		        }).success(function(data){
				    	$scope.userPowerDto = data;
				    	//获取全部省For add；要首先确定该用户是不是具有分配省权限用户的能力
				    	if($scope.userPowerDto.sysUser.pro_power == "-1") {
				    		$http.get('/i/city/findProvinceList').success(function(data) {
				    			$scope.provinces = data;
				    			var province = {
				    				"province_id" : "-1",
				    				"name" : "不限"
				    			};
				    			$scope.provinces.push(province);
				    			$scope.addProvinceID = "-1";
				    		});
				    	}
				    	if($scope.userPowerDto.sysUser.pro_power != "-1"){
				    		$http.get('/i/city/findCitysByProvinceId', {
				                params: {
				                    "provinceID": $scope.userPowerDto.sysUser.pro_power
				                }
				            }).success(function (data) {
				            	$scope.citis = data;
				            	var city = {"city_id":"-1","name":"不限"};
				            	$scope.citis.push(city);
				            });
				    	}
				    	if($scope.userPowerDto.sysUser.city_power != "-1"){
				    		$http.get('/i/city/findAreasByCityId', {
				                params: {
				                    "cityID": $scope.userPowerDto.sysUser.city_power
				                }
				            }).success(function (data) {
				            	$scope.areas = data;
				            	var area = {"area_id":"-1","name":"不限"};
				            	$scope.areas.push(area);
				            });
				    	}
				    	$scope.getUsers();
			    });
		   });
	};
	$scope.load();
	// 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
	$scope.Allusers = [];
	$scope.optAudit = '8';
	 // 获取当前用户的列表

	  
    $scope.getUsers = function() {
		$http({
			method : 'POST',
			url : '/i/user/findUserList',
			params : {
				proPower : $scope.userPowerDto.sysUser.pro_power,
				cityPower : $scope.userPowerDto.sysUser.city_power,
				areaPower : $scope.userPowerDto.sysUser.area_power,
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				startTime : $scope.startTime,
				endTime : $scope.endTime,
				keyword : encodeURI($scope.keyword,"UTF-8"),
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.Allusers = data.list;
		});
	}

	$scope.pageChanged = function() {
		$scope.getUsers();
	}
	
	// 获取当前冷库的列表
	$scope.auditChanged = function(optAudiet) {
		$scope.getUsers();
	}
    
	$scope.goSearch = function () {
		$scope.getUsers();
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
	
    $scope.goDeleteUser = function (userID) {
    	if(delcfm()){
    	$http.get('/i/user/deleteUserByID', {
            params: {
                "userID": userID
            }
        }).success(function (data) {
        	$scope.getUsers();
        	alert("删除成功");
        });
    	}
    }
    $scope.deleteUsers = function(){
    	if(delcfm()){
    	var userIDs = [];
    	for(i in $scope.selected){
    		userIDs.push($scope.selected[i].sysUser.user_id);
    	}
    	if(userIDs.length >0 ){
    		$http({
    			method:'DELETE',
    			url:'/i/user/deleteUserByIDs',
    			params:{
    				'userIDs': userIDs
    			}
    		}).success(function (data) {
    			$scope.getUsers();
            	alert("删除成功");
            });
    	}
    	}
    }
   
    
    $scope.selected = [];
    $scope.toggle = function (user, list) {
		  var idx = list.indexOf(user);
		  if (idx > -1) {
		    list.splice(idx, 1);
		  }
		  else {
		    list.push(user);
		  }
    };
    $scope.exists = function (user, list) {
    	return list.indexOf(user) > -1;
    };
    $scope.isChecked = function() {
        return $scope.selected.length === $scope.Allusers.length;
    };
    $scope.toggleAll = function() {
        if ($scope.selected.length === $scope.Allusers.length) {
        	$scope.selected = [];
        } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
        	$scope.selected = $scope.Allusers.slice(0);
        }
    };
    
    $scope.getUserIDsFromSelected = function(audit){
    	var userIDs = [];
    	for(i in $scope.selected){
    		if(audit != undefined)
    			$scope.selected[i].audit = audit;
    		userIDs.push($scope.selected[i].id);
    	}
    	return userIDs;
    }
    
    $scope.getAudit = function(i){
    	if(i==1)
    		return '有效';
        else{
    		return '无效';
    	}
    }
    
	//根据省id获取全部市For add
	$scope.getCitis = function () {
    	$http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.addProvinceID
            }
        }).success(function (data) {
        	$scope.citis = data;
        	var city = {"city_id":"-1","name":"不限"};
        	$scope.citis.push(city);
            $scope.addCityID = "-1";
            $scope.addAreaID = "-1";
        });
    }
   //根据市id获取全部区For add
    $scope.getAreas = function () {
    	$http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.addCityID
            }
        }).success(function (data) {
        	$scope.areas = data;
        	var area = {"area_id":"-1","name":"不限"};
        	$scope.areas.push(area);
            $scope.addAreaID = "-1";
        });
    }
    
    $http.get('/i/city/findProvinceList').success(function(data) {
		$scope.provincesForUpdate = data;
		var province = {
			"province_id" : "-1",
			"name" : "不限"
		};
		$scope.provincesForUpdate.push(province);
	});
    //根据省id获取全部市For update
    $scope.getCitisForUpdate = function () {
    	$http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.userForUpdate.sysUser.pro_power
            }
        }).success(function (data) {
        	$scope.citisForUpdate = data;
        	var city = {"city_id":"-1","name":"不限"};
        	$scope.citisForUpdate.push(city);
        	$scope.userForUpdate.sysUser.city_power = "-1";
        });
    }
   //根据市id获取全部区For update
    $scope.getAreasForUpdate = function () {
    	$http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.userForUpdate.sysUser.city_power
            }
        }).success(function (data) {
        	$scope.areasForUpdate = data;
        	var area = {"area_id":"-1","name":"不限"};
        	$scope.areasForUpdate.push(area);
        	$scope.userForUpdate.sysUser.area_power = "-1";
        });
    }
    
    function checkInput(){
        var flag = true;
        // 检查必须填写项
        if ($scope.username == undefined || $scope.username == '') {
            flag = false;
        }
        if ($scope.password1 == undefined || $scope.password1 == '') {
            flag = false;
        }
        return flag;
    }
    $scope.submit = function(){
        if (checkInput()){
          if($scope.password2==$scope.password1){
        	  var optPower = 0;
        	  if($("#optPower").is(":checked")){
        		  optPower = 1;
        	  }
        	  var menuPower = "";
        	  if($("#console").is(":checked")){
        		  menuPower = menuPower+"console;";
        	  }
        	  if($("#traceSearch").is(":checked")){
        		  menuPower = menuPower+"traceSearch;";
        	  }
        	  if($("#location").is(":checked")){
        		  menuPower = menuPower+"location;";
        	  }
        	  if($("#electManage").is(":checked")){
        		  menuPower = menuPower+"electManage;";
        	  }
        	  
        	  if($("#electRecord").is(":checked")){
        		  menuPower = menuPower+"electRecord;";
        	  }
        	  if($("#blackManage").is(":checked")){
        		  menuPower = menuPower+"blackManage;";
        	  }
        	  if($("#alarmTrack").is(":checked")){
        		  menuPower = menuPower+"alarmTrack;";
        	  }
        	  if($("#electAdd").is(":checked")){
        		  menuPower = menuPower+"electAdd;";
        	  }
        	  
        	  if($("#electDelete").is(":checked")){
        		  menuPower = menuPower+"electDelete;";
        	  }
        	  if($("#electExport").is(":checked")){
        		  menuPower = menuPower+"electExport;";
        	  }
        	  if($("#electEdit").is(":checked")){
        		  menuPower = menuPower+"electEdit;";
        	  }
        	  if($("#blackAdd").is(":checked")){
        		  menuPower = menuPower+"blackAdd;";
        	  }
        	  if($("#blackDelete").is(":checked")){
        		  menuPower = menuPower+"blackDelete;";
        	  }
        	  if($("#blackEdit").is(":checked")){
        		  menuPower = menuPower+"blackEdit;";
        	  }
        	  if($("#alarmDelete").is(":checked")){
        		  menuPower = menuPower+"alarmDelete;";
        	  }
        	  if($("#stationManageWhole").is(":checked")){
        		  menuPower = menuPower+"stationManageWhole;";
        	  }
        	  if($("#stationManage").is(":checked")){
        		  menuPower = menuPower+"stationManage;";
        	  }
        	  if($("#stationDeviceManage").is(":checked")){
        		  menuPower = menuPower+"stationDeviceManage;";
        	  }
        	  if($("#stationAdd").is(":checked")){
        		  menuPower = menuPower+"stationAdd;";
        	  }
        	  
        	  if($("#stationDelete").is(":checked")){
        		  menuPower = menuPower+"stationDelete;";
        	  }
        	  if($("#stationDeviceDelete").is(":checked")){
        		  menuPower = menuPower+"stationDeviceDelete;";
        	  }
        	  if($("#stationDeviceUpdate").is(":checked")){
        		  menuPower = menuPower+"stationDeviceUpdate;";
        	  }
        	  if($("#specialAreaManage").is(":checked")){
        		  menuPower = menuPower+"specialAreaManage;";
        	  }
        	  
        	  if($("#limitAreaManage").is(":checked")){
        		  menuPower = menuPower+"limitAreaManage;";
        	  }
        	  if($("#sensitiveAreaManage").is(":checked")){
        		  menuPower = menuPower+"sensitiveAreaManage;";
        	  }
        	  if($("#AreaAlarmManage").is(":checked")){
        		  menuPower = menuPower+"AreaAlarmManage;";
        	  }
        	  if($("#limitAlarmManage").is(":checked")){
        		  menuPower = menuPower+"limitAlarmManage;";
        	  }
        	  
        	  if($("#sensitiveAlarmManage").is(":checked")){
        		  menuPower = menuPower+"sensitiveAlarmManage;";
        	  }
        	  if($("#dataAnalysis").is(":checked")){
        		  menuPower = menuPower+"dataAnalysis;";
        	  }
        	  if($("#userManagePower").is(":checked")){
        		  menuPower = menuPower+"userManage;";
        	  }
        	  if($("#userAdd").is(":checked")){
        		  menuPower = menuPower+"userAdd;";
        	  }
        	  if($("#userDelete").is(":checked")){
        		  menuPower = menuPower+"userDelete;";
        	  }
        	  if($("#userEdit").is(":checked")){
        		  menuPower = menuPower+"userEdit";
        	  }
        	  if($scope.addProvinceID==undefined || $scope.addProvinceID==null){
        		  $scope.addProvinceID = $scope.userPowerDto.sysUser.pro_power;
        	  }
        	  if($scope.addCityID==undefined || $scope.addCityID==null){
        		  $scope.addCityID = $scope.userPowerDto.sysUser.city_power;
        	  }
        	  if($scope.addAreaID==undefined || $scope.addAreaID==null){
        		  $scope.addAreaID = $scope.userPowerDto.sysUser.area_power;
        	  }
              $http({
            	method : 'GET', 
    			url:'/i/user/addUser',
    			params:{
    				'user_name': $scope.username,
    				'nickname': $scope.nickname,
    				'password': $scope.password1,
    				'opt_power' : optPower,
    				'user_tel' : $scope.telephone,
    				'pro_power' : $scope.addProvinceID,
    				'city_power' : $scope.addCityID,
    				'area_power' : $scope.addAreaID,
    				'menu_power' : menuPower
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
			    if(data!=null&&data.sysUser.user_id!=undefined){
			    	$scope.userForUpdate = data;
			    	$http.get('/i/city/findCitysByProvinceId', {
	    	            params: {
	    	                "provinceID": $scope.userForUpdate.sysUser.pro_power
	    	            }
	    	        }).success(function (data) {
	    	        	$scope.citisForUpdate = data;
	    	        });
	    		   $http.get('/i/city/findAreasByCityId', {
	    	            params: {
	    	                "cityID": $scope.userForUpdate.sysUser.city_power
	    	            }
	    	        }).success(function (data) {
	    	        	$scope.areasForUpdate = data;
	    	        });
					 if($scope.userForUpdate.sysUser.opt_power=="1"){
						 $("#optPowerChange").prop("checked",true);
					 }
					 else{
						 $("#optPowerChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.console=="1"){
						 $("#consoleChange").prop("checked",true);
					 }
					 else{
						 $("#consoleChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.traceSearch=="1"){
						 $("#traceSearchChange").prop("checked",true);
					 }
					 else{
						 $("#traceSearchChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.location=="1"){
						 $("#locationChange").prop("checked",true);
					 }
					 else{
						 $("#locationChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.electManage=="1"){
						 $("#electManageChange").prop("checked",true);
					 }
					 else{
						 $("#electManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.electRecord=="1"){
						 $("#electRecordChange").prop("checked",true);
					 }
					 else{
						 $("#electRecordChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.blackManage=="1"){
						 $("#blackManageChange").prop("checked",true);
					 }
					 else{
						 $("#blackManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.alarmTrack=="1"){
						 $("#alarmTrackChange").prop("checked",true);
					 }
					 else{
						 $("#alarmTrackChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.electAdd=="1"){
						 $("#electAddChange").prop("checked",true);
					 }
					 else{
						 $("#electAddChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.electDelete=="1"){
						 $("#electDeleteChange").prop("checked",true);
					 }
					 else{
						 $("#electDeleteChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.electExport=="1"){
						 $("#electExportChange").prop("checked",true);
					 }
					 else{
						 $("#electExportChange").prop("checked",false); 
					 }
					 if($scope.userForUpdate.electEdit=="1"){
						 $("#electEditChange").prop("checked",true);
					 }
					 else{
						 $("#electEditChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.blackAdd=="1"){
						 $("#blackAddChange").prop("checked",true);
					 }
					 else{
						 $("#blackAddChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.blackDelete=="1"){
						 $("#blackDeleteChange").prop("checked",true);
					 }
					 else{
						 $("#blackDeleteChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.blackEdit=="1"){
						 $("#blackEditChange").prop("checked",true);
					 }
					 else{
						 $("#blackEditChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.alarmDelete=="1"){
						 $("#alarmDeleteChange").prop("checked",true);
					 }
					 else{
						 $("#alarmDeleteChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationManageWhole=="1"){
						 $("#stationManageWholeChange").prop("checked",true);
					 }
					 else{
						 $("#stationManageWholeChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationManage=="1"){
						 $("#stationManageChange").prop("checked",true);
					 }
					 else{
						 $("#stationManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationDeviceManage=="1"){
						 $("#stationDeviceManageChange").prop("checked",true);
					 }
					 else{
						 $("#stationDeviceManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationAdd=="1"){
						 $("#stationAddChange").prop("checked",true);
					 }
					 else{
						 $("#stationAddChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationDelete=="1"){
						 $("#stationDeleteChange").prop("checked",true);
					 }
					 else{
						 $("#stationDeleteChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationDeviceDelete=="1"){
						 $("#stationDeviceDeleteChange").prop("checked",true);
					 }
					 else{
						 $("#stationDeviceDeleteChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationDeviceUpdate=="1"){
						 $("#stationDeviceUpdateChange").prop("checked",true);
					 }
					 else{
						 $("#stationDeviceUpdateChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.specialAreaManage=="1"){
						 $("#specialAreaManageChange").prop("checked",true);
					 }
					 else{
						 $("#specialAreaManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.limitAreaManage=="1"){
						 $("#limitAreaManageChange").prop("checked",true);
					 }
					 else{
						 $("#limitAreaManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.sensitiveAreaManage=="1"){
						 $("#sensitiveAreaManageChange").prop("checked",true);
					 }
					 else{
						 $("#sensitiveAreaManageChange").prop("checked",false); 
					 }
					 if($scope.userForUpdate.AreaAlarmManage=="1"){
						 $("#AreaAlarmManageChange").prop("checked",true);
					 }
					 else{
						 $("#AreaAlarmManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.limitAlarmManage=="1"){
						 $("#limitAlarmManageChange").prop("checked",true);
					 }
					 else{
						 $("#limitAlarmManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.sensitiveAlarmManage=="1"){
						 $("#sensitiveAlarmManageChange").prop("checked",true);
					 }
					 else{
						 $("#sensitiveAlarmManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.dataAnalysis=="1"){
						 $("#dataAnalysisChange").prop("checked",true);
					 }
					 else{
						 $("#dataAnalysisChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.userManage=="1"){
						 $("#userManageChange").prop("checked",true);
					 }
					 else{
						 $("#userManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.userAdd=="1"){
						 $("#userAddChange").prop("checked",true);
					 }
					 else{
						 $("#userAddChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.userDelete=="1"){
						 $("#userDeleteChange").prop("checked",true);
					 }
					 else{
						 $("#userDeleteChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.userEdit=="1"){
						 $("#userEditChange").prop("checked",true);
					 }
					 else{
						 $("#userEditChange").prop("checked",false);
					 }
			    }
		     });
		};
		
		function checkInputForUpdate(){
	        var flag = true;
	        // 检查必须填写项
	        if ($scope.userForUpdate.sysUser.user_name == undefined || $scope.userForUpdate.sysUser.user_name == '') {
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
				  var optPower = 0;
	        	  if($("#optPowerChange").is(":checked")){
	        		  optPower = 1;
	        	  }
	        	  var menuPower = "";
	        	  if($("#consoleChange").is(":checked")){
	        		  menuPower = menuPower+"console;";
	        	  }
	        	  if($("#traceSearchChange").is(":checked")){
	        		  menuPower = menuPower+"traceSearch;";
	        	  }
	        	  if($("#locationChange").is(":checked")){
	        		  menuPower = menuPower+"location;";
	        	  }
	        	  if($("#electManageChange").is(":checked")){
	        		  menuPower = menuPower+"electManage;";
	        	  }
	        	  
	        	  if($("#electRecordChange").is(":checked")){
	        		  menuPower = menuPower+"electRecord;";
	        	  }
	        	  if($("#blackManageChange").is(":checked")){
	        		  menuPower = menuPower+"blackManage;";
	        	  }
	        	  if($("#alarmTrackChange").is(":checked")){
	        		  menuPower = menuPower+"alarmTrack;";
	        	  }
	        	  if($("#electAddChange").is(":checked")){
	        		  menuPower = menuPower+"electAdd;";
	        	  }
	        	  
	        	  if($("#electDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"electDelete;";
	        	  }
	        	  if($("#electExportChange").is(":checked")){
	        		  menuPower = menuPower+"electExport;";
	        	  }
	        	  if($("#electEditChange").is(":checked")){
	        		  menuPower = menuPower+"electEdit;";
	        	  }
	        	  if($("#blackAddChange").is(":checked")){
	        		  menuPower = menuPower+"blackAdd;";
	        	  }
	        	  if($("#blackDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"blackDelete;";
	        	  }
	        	  if($("#blackEditChange").is(":checked")){
	        		  menuPower = menuPower+"blackEdit;";
	        	  }
	        	  if($("#alarmDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"alarmDelete;";
	        	  }
	        	  if($("#stationManageWholeChange").is(":checked")){
	        		  menuPower = menuPower+"stationManageWhole;";
	        	  }
	        	  if($("#stationManageChange").is(":checked")){
	        		  menuPower = menuPower+"stationManage;";
	        	  }
	        	  if($("#stationDeviceManageChange").is(":checked")){
	        		  menuPower = menuPower+"stationDeviceManage;";
	        	  }
	        	  if($("#stationAddChange").is(":checked")){
	        		  menuPower = menuPower+"stationAdd;";
	        	  }
	        	  
	        	  if($("#stationDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"stationDelete;";
	        	  }
	        	  if($("#stationDeviceDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"stationDeviceDelete;";
	        	  }
	        	  if($("#stationDeviceUpdateChange").is(":checked")){
	        		  menuPower = menuPower+"stationDeviceUpdate;";
	        	  }
	        	  if($("#specialAreaManageChange").is(":checked")){
	        		  menuPower = menuPower+"specialAreaManage;";
	        	  }
	        	  
	        	  if($("#limitAreaManageChange").is(":checked")){
	        		  menuPower = menuPower+"limitAreaManage;";
	        	  }
	        	  if($("#sensitiveAreaManageChange").is(":checked")){
	        		  menuPower = menuPower+"sensitiveAreaManage;";
	        	  }
	        	  if($("#AreaAlarmManageChange").is(":checked")){
	        		  menuPower = menuPower+"AreaAlarmManage;";
	        	  }
	        	  if($("#limitAlarmManageChange").is(":checked")){
	        		  menuPower = menuPower+"limitAlarmManage;";
	        	  }
	        	  
	        	  if($("#sensitiveAlarmManageChange").is(":checked")){
	        		  menuPower = menuPower+"sensitiveAlarmManage;";
	        	  }
	        	  if($("#dataAnalysisChange").is(":checked")){
	        		  menuPower = menuPower+"dataAnalysis;";
	        	  }
	        	  if($("#userManageChange").is(":checked")){
	        		  menuPower = menuPower+"userManage;";
	        	  }
	        	  if($("#userAddChange").is(":checked")){
	        		  menuPower = menuPower+"userAdd;";
	        	  }
	        	  if($("#userDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"userDelete;";
	        	  }
	        	  if($("#userEditChange").is(":checked")){
	        		  menuPower = menuPower+"userEdit";
	        	  }
		            $http({
		            	method : 'GET', 
		    			url:'/i/user/updateUser',
		    			params:{
		    				'user_id': $scope.userForUpdate.sysUser.user_id,
		    				'user_name': $scope.userForUpdate.sysUser.user_name,
		    				'nickname': $scope.userForUpdate.sysUser.nickname,
		    				'password': '',
		    				'opt_power' : optPower,
		    				'area_power' : $scope.userForUpdate.sysUser.area_power,
		    				'pro_power' : $scope.userForUpdate.sysUser.pro_power,
		    				'city_power' : $scope.userForUpdate.sysUser.city_power,
		    				'menu_power' : menuPower,
		    				'user_tel' : $scope.userForUpdate.sysUser.user_tel
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



/*-------------------t4月3号添加，以下内容不可删除------------------------------*/



	//开关选项控制
	$('.btnCircle').click(function () {

		var left = $(this).css('left');
		left = parseInt(left);
		if (left == 0) {
			$(this).css('left', '25px'),
				$(this).css('background-color', '#66b3ff'),
				$(this).parent().css('background-color', '#66b3ff');
			$(this).parent().parent().addClass("active");
		} else {
			$(this).css('left', '0px'),
				$(this).css('background-color', '#fff'),
				$(this).parent().css('background-color', '#ccc');
			$(this).parent().parent().removeClass("active");
		}

	});

	//选择按钮显示隐藏ok图标
	var btnWhite=$(".addUserModal .search-container .btn-white");
	console.log(btnWhite);
	$(btnWhite).click(function (){
		$(this).toggleClass("active");
		$(this).children(".btn-ok").toggleClass("icon-active");
	});


	//tabs切换功能实现
 $("#addUser .tabs input").click(function(){
	 $("#addUser .tabs input").prop("checked","false");
	 $(this).prop("checked","true");
	 $("ul.search-container li").css("display","none");
	 var a=$(this).attr("id")+"-car";
	 $("."+a).css("display","block");
 });



});
