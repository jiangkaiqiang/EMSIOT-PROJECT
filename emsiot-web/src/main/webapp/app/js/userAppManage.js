coldWeb.controller('userAppManage', function ($rootScope, $scope, $state, $cookies, $http, $location) {
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
				    	$scope.getUserApps();
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
	$scope.AlluserApps = [];
	$scope.optAudit = '8';
	 // 获取当前用户的列表

	  
    $scope.getUserApps = function() {
		$http({
			method : 'POST',
			url : '/i/UserApp/findUserAppList',
			params : {
				proPower : $scope.userPowerDto.sysUser.pro_power,
				cityPower : $scope.userPowerDto.sysUser.city_power,
				areaPower : $scope.userPowerDto.sysUser.area_power,
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				startTime : $scope.startTime,
				endTime : $scope.endTime,
				endTime : $scope.userTele,
				keyword : encodeURI($scope.keyword,"UTF-8"),
			}
		}).success(function(data) {
			console.log(data)
			$scope.bigTotalItems = data.total;
			$scope.AlluserApps = data.list;
		});
	}

	$scope.pageChanged = function() {
		$scope.getUserApps();
	}
	
	// 获取当前冷库的列表
	$scope.auditChanged = function(optAudiet) {
		$scope.getUserApps();
	}
    
	$scope.goSearch = function () {
		$scope.getUserApps();
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
        	$scope.getUserApps();
        	alert("删除成功");
        });
    	}
    }
    $scope.deleteUsers = function(){
    	if(delcfm()){
    	var userIDs = [];
    	for(var i=0 ;i< $scope.selected.length;i++){
    		userIDs.push($scope.selected[i].appUser.user_id);
    	}
    	if(userIDs.length >0 ){
    		$http({
    			method:'DELETE',
    			url:'/i/user/deleteUserByIDs',
    			params:{
    				'userIDs': userIDs
    			}
    		}).success(function (data) {
    			$scope.getUserApps();
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
        return $scope.selected.length === $scope.AlluserApps.length;
    };
    $scope.toggleAll = function() {
        if ($scope.selected.length === $scope.AlluserApps.length) {
        	$scope.selected = [];
        } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
        	$scope.selected = $scope.AlluserApps.slice(0);
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
    $scope.getSource = function(source){
    	if(source == 1){
    		return "IOS";
    	}else if(source == 2){
    		return "Android";
    	}else if(source == 3){
    		return "小程序";
    	}
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
