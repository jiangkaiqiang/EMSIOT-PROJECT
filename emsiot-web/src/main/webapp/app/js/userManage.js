coldWeb.controller('userManage', function ($rootScope, $scope, $state, $cookies, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
					return;
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
				adminId : $scope.userPowerDto.sysUser.user_id,
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				startTime : $scope.startTime,
				endTime : $scope.endTime,
				keyword : encodeURI($scope.keyword,"UTF-8"),
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.Allusers = data.list;
			console.log($scope.Allusers)
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
    	for(var i=0 ;i< $scope.selected.length;i++){
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
    	for(var i=0 ;i< $scope.selected.length;i++){
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
        	  //附加基站异常记录管理
        	  if($("#stationExceptionRecprdManage").is(":checked")){
        		  menuPower = menuPower+"stationExceptionRecprdManage;";
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
        	  if($("#stationExceptionRecprdDelete").is(":checked")){
        		  menuPower = menuPower+"stationExceptionRecprdDelete;";
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
        	  if($("#sensitiveAreaAdd").is(":checked")){
        		  menuPower = menuPower+"sensitiveAreaAdd;";
        	  }
        	  if($("#sensitiveAreaDelete").is(":checked")){
        		  menuPower = menuPower+"sensitiveAreaDelete;";
        	  }
        	  if($("#sensitiveAreaUpdate").is(":checked")){
        		  menuPower = menuPower+"sensitiveAreaUpdate;";
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
        	  if($("#sensitiveAlarmDelete").is(":checked")){
        		  menuPower = menuPower+"sensitiveAlarmDelete;";
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
        		  menuPower = menuPower+"userEdit;";
        	  }
        	  if($("#privatePower").is(":checked")){
        		  menuPower = menuPower+"privatePower";
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
        	  if($scope.company==undefined || $scope.company==null){
        		  if($scope.userPowerDto.sysUser.company!=-1){
        			  $scope.company = $scope.userPowerDto.sysUser.company;
        		  }else{
        			  $scope.company = -1;
        		  }
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
    				'menu_power' : menuPower,
    				'company' : $scope.company
    			}
    		}).then(function (resp) {
    		   if(resp.data.success){
    			 alert(resp.data.message);
                 $scope.getUsers();
                 $("#addUser").modal("hide"); 
    		   }
    		   else{
	            	 alert(resp.data.message);
	           }
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
						 $("#optPowerChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#optPowerChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.console=="1"){
						 $("#consoleChange").prop("checked",true);
					 }
					 else{
						 $("#consoleChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.traceSearch=="1"){
						 $("#traceSearchChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#traceSearchChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.location=="1"){
						 $("#locationChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#locationChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.electManage=="1"){
						 $("#electManageChange").prop("checked",true);
					 }
					 else{
						 $("#electManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.electRecord=="1"){
						 $("#electRecordChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#electRecordChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.blackManage=="1"){
						 $("#blackManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#blackManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.alarmTrack=="1"){
						 $("#alarmTrackChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#alarmTrackChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.electAdd=="1"){
						 $("#electAddChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#electAddChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.electDelete=="1"){
						 $("#electDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#electDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.electExport=="1"){
						 $("#electExportChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#electExportChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.electEdit=="1"){
						 $("#electEditChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#electEditChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.blackAdd=="1"){
						 $("#blackAddChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#blackAddChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.blackDelete=="1"){
						 $("#blackDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#blackDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.blackEdit=="1"){
						 $("#blackEditChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#blackEditChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.alarmDelete=="1"){
						 $("#alarmDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#alarmDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.stationManageWhole=="1"){
						 $("#stationManageWholeChange").prop("checked",true);//1
					 }
					 else{
						 $("#stationManageWholeChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.stationManage=="1"){
						 $("#stationManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.stationDeviceManage=="1"){
						 $("#stationDeviceManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationDeviceManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					//附加基站异常记录管理
					 if($scope.userForUpdate.stationExceptionRecprdManage=="1"){
						 $("#stationExceptionRecprdManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationExceptionRecprdManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.stationAdd=="1"){
						 $("#stationAddChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationAddChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.stationDelete=="1"){
						 $("#stationDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.stationDeviceDelete=="1"){
						 $("#stationDeviceDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationDeviceDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.stationExceptionRecprdDelete=="1"){
						 $("#stationExceptionRecprdDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationExceptionRecprdDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.stationDeviceUpdate=="1"){
						 $("#stationDeviceUpdateChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#stationDeviceUpdateChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.specialAreaManage=="1"){
						 $("#specialAreaManageChange").prop("checked",true);//1
					 }
					 else{
						 $("#specialAreaManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.limitAreaManage=="1"){
						 $("#limitAreaManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#limitAreaManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.sensitiveAreaManage=="1"){
						 $("#sensitiveAreaManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#sensitiveAreaManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 
					 if($scope.userForUpdate.sensitiveAreaAdd=="1"){
						 $("#sensitiveAreaAddChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#sensitiveAreaAddChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.sensitiveAreaDelete=="1"){
						 $("#sensitiveAreaDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#sensitiveAreaDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.sensitiveAreaUpdate=="1"){
						 $("#sensitiveAreaUpdateChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#sensitiveAreaUpdateChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 
					 if($scope.userForUpdate.AreaAlarmManage=="1"){
						 $("#AreaAlarmManageChange").prop("checked",true);//1
					 }
					 else{
						 $("#AreaAlarmManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.limitAlarmManage=="1"){
						 $("#limitAlarmManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#limitAlarmManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.sensitiveAlarmManage=="1"){
						 $("#sensitiveAlarmManageChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#sensitiveAlarmManageChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.sensitiveAlarmDelete=="1"){
						 $("#sensitiveAlarmDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#sensitiveAlarmDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.dataAnalysis=="1"){
						 $("#dataAnalysisChange").prop("checked",true);//1
					 }
					 else{
						 $("#dataAnalysisChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.userManage=="1"){
						 $("#userManageChange").prop("checked",true);//1
					 }
					 else{
						 $("#userManageChange").prop("checked",false);
					 }
					 if($scope.userForUpdate.userAdd=="1"){
						 $("#userAddChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#userAddChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.userDelete=="1"){
						 $("#userDeleteChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#userDeleteChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.userEdit=="1"){
						 $("#userEditChange").prop("checked",true).parent(".btn-white").addClass("active");
					 }
					 else{
						 $("#userEditChange").prop("checked",false).parent(".btn-white").removeClass("active");
					 }
					 if($scope.userForUpdate.privatePower=="1"){
						 $("#privatePowerChange").prop("checked",true);//1
					 }
					 else{
						 $("#privatePowerChange").prop("checked",false);
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
	        	//附加基站异常记录管理
	        	  if($("#stationExceptionRecprdManageChange").is(":checked")){
	        		  menuPower = menuPower+"stationExceptionRecprdManage;";
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
	        	  if($("#stationExceptionRecprdDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"stationExceptionRecprdDelete;";
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
	        	  if($("#sensitiveAreaAddChange").is(":checked")){
	        		  menuPower = menuPower+"sensitiveAreaAdd;";
	        	  }
	        	  if($("#sensitiveAreaDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"sensitiveAreaDelete;";
	        	  }
	        	  if($("#sensitiveAreaUpdateChange").is(":checked")){
	        		  menuPower = menuPower+"sensitiveAreaUpdate;";
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
	        	  if($("#sensitiveAlarmDeleteChange").is(":checked")){
	        		  menuPower = menuPower+"sensitiveAlarmDelete;";
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
	        		  menuPower = menuPower+"userEdit;";
	        	  }
	        	  if($("#privatePowerChange").is(":checked")){
	        		  menuPower = menuPower+"privatePower";
	        	  }
		            $http({
		            	method : 'GET', 
		    			url:'/i/user/updateUser',
		    			params:{
		    				'user_id': $scope.userForUpdate.sysUser.user_id,
		    				'user_name': $scope.userForUpdate.sysUser.user_name,
		    				'nickname': $scope.userForUpdate.sysUser.nickname,
		    				'password': null,
		    				'opt_power' : optPower,
		    				'area_power' : $scope.userForUpdate.sysUser.area_power,
		    				'pro_power' : $scope.userForUpdate.sysUser.pro_power,
		    				'city_power' : $scope.userForUpdate.sysUser.city_power,
		    				'menu_power' : menuPower,
		    				'user_tel' : $scope.userForUpdate.sysUser.user_tel,
		    				'company' : $scope.userForUpdate.sysUser.company
		    			}
		    		}).then(function (resp) {
		    		  if(resp.data.success){
		    			 alert(resp.data.message);
		                 $scope.getUsers();
		                 $("#updateUser").modal("hide"); 
		                 }
		    		  else{
			            	 alert(resp.data.message);
			            }
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
	//单选checkbox
	var inputCheck = $(".addUserModal .search-container .btn-white");
	//console.log(inputCheck)
	for(var a=0;a<inputCheck.length;a++){
		$(inputCheck[a]).click(function(){
			$(this).toggleClass("active");
			if($(this).hasClass("active")){
				$(this).children("input[type=checkbox]").prop("checked",true);
			}else{
				$(this).children("input[type=checkbox]").prop("checked",false);
			}
		});
	}

	//实现全选checkbox
	var allAhecked =$(".addUserModal .title-brand input");
	//console.log(allAhecked);
	for(var j=0;j<allAhecked.length;j++){
		$(allAhecked[j]).click(function(){
			var inputs = $(this).parents(".title-brand").next(".search-container").children().children(".btn-white");
			if($(this).prop("checked")){
				console.log($(this));
				for(var k=0;k<inputs.length;k++){
					$(inputs[k]).addClass("active");
					$(inputs[k]).children("input").prop("checked",true);
				}
			}else{
				for(var k=0;k<inputs.length;k++){
					$(inputs[k]).removeClass("active");
					$(inputs[k]).children("input").prop("checked",false);
				}
			}
		});
	}

	var firstChecked =$(".userModalSwitch ul .btn-white");
	for(var b=0;b<firstChecked.length;b++){
		$(firstChecked[b]).click(function(){
			$(this).toggleClass("active");
			if($(this).hasClass("active")){
				$(this).children("input[type=checkbox]").prop("checked",true);
			}else{
				$(this).children("input").prop("checked",false);
			}
		});
	}




});
