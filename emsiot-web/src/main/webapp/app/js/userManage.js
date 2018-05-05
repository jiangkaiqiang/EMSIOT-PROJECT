coldWeb.controller('userManage', function ($rootScope, $scope, $state, $cookies, $http, $location) {
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
	$scope.Allusers = [];
	$scope.optAudit = '8';
	 // 获取当前用户的列表

	  
    $scope.getUsers = function() {
		$http({
			method : 'POST',
			url : '/i/user/findUserList',
			params : {
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
	$scope.getUsers();
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

    //获取全部省
    $http.get('/i/city/findProvinceList').success(function (data) {
        $scope.provinces = data;
        $scope.addProvinceID = data[0].province_id;
    });
	
    $scope.getCitis = function () {
    	$http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.addProvinceID
            }
        }).success(function (data) {
        	$scope.citis = data;
            $scope.addCityID = data[0].city_id;
        });
    }
    
    $scope.getAreas = function () {
    	$http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.addCityID
            }
        }).success(function (data) {
        	$scope.areas = data;
            $scope.addAreaID = data[0].area_id;
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
					 if($scope.userForUpdate.console=="1"){
						 $("#consoleChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.traceSearch=="1"){
						 $("#traceSearchChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.location=="1"){
						 $("#locationChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.electManage=="1"){
						 $("#electManageChange").prop("checked",true);
					 }
					 
					 if($scope.userForUpdate.electRecord=="1"){
						 $("#electRecordChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.blackManage=="1"){
						 $("#blackManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.alarmTrack=="1"){
						 $("#alarmTrackChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.electAdd=="1"){
						 $("#electAddChange").prop("checked",true);
					 }
					 
					 if($scope.userForUpdate.electDelete=="1"){
						 $("#electDeleteChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.electExport=="1"){
						 $("#electExportChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.electEdit=="1"){
						 $("#electEditChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.blackAdd=="1"){
						 $("#blackAddChange").prop("checked",true);
					 }
					 
					 if($scope.userForUpdate.blackDelete=="1"){
						 $("#blackDeleteChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.blackEdit=="1"){
						 $("#blackEditChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.alarmDelete=="1"){
						 $("#alarmDeleteChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.stationManageWhole=="1"){
						 $("#stationManageWholeChange").prop("checked",true);
					 }
					 
					 if($scope.userForUpdate.stationManage=="1"){
						 $("#stationManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.stationDeviceManage=="1"){
						 $("#stationDeviceManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.stationAdd=="1"){
						 $("#stationAddChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.stationDelete=="1"){
						 $("#stationDeleteChange").prop("checked",true);
					 }
					 
					 if($scope.userForUpdate.stationDeviceDelete=="1"){
						 $("#stationDeviceDeleteChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.stationDeviceUpdate=="1"){
						 $("#stationDeviceUpdateChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.specialAreaManage=="1"){
						 $("#specialAreaManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.limitAreaManage=="1"){
						 $("#limitAreaManageChange").prop("checked",true);
					 }
					 
					 if($scope.userForUpdate.sensitiveAreaManage=="1"){
						 $("#sensitiveAreaManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.AreaAlarmManage=="1"){
						 $("#AreaAlarmManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.limitAlarmManage=="1"){
						 $("#limitAlarmManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.sensitiveAlarmManage=="1"){
						 $("#sensitiveAlarmManageChange").prop("checked",true);
					 }
					 
					 if($scope.userForUpdate.dataAnalysis=="1"){
						 $("#dataAnalysisChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.userManage=="1"){
						 $("#userManageChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.userAdd=="1"){
						 $("#userAddChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.userDelete=="1"){
						 $("#userDeleteChange").prop("checked",true);
					 }
					 if($scope.userForUpdate.userEdit=="1"){
						 $("#userEditChange").prop("checked",true);
					 }
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
