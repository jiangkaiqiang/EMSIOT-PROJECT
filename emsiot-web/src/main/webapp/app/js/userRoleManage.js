coldWeb.controller('userRoleManage', function ($rootScope, $scope, $state, $cookies, $http, $location) {
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
	$scope.AlluserRoles = [];
	 // 获取当前冷库的列表
    $scope.getUserRoles = function() {
		$http({
			method : 'POST',
			url : '/i/userrole/findUserRoleList',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				startTime : $scope.startTime,
				endTime : $scope.endTime,
				keyword : encodeURI($scope.keyword,"UTF-8"),
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.AlluserRoles = data.list;
		});
	}

	$scope.pageChanged = function() {
		$scope.getUserRoles();
	}
	$scope.getUserRoles();
    
	$scope.goSearch = function () {
		$scope.getUserRoles();
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
	
    $scope.goDeleteUserRole = function (userRoleID) {
    	if(delcfm()){
    	$http.get('/i/userrole/deleteUserRoleByID', {
            params: {
                "userRoleID": userRoleID
            }
        }).success(function (data) {
        	$scope.getUserRoles();
        	alert("删除成功");
        });
    	}
    }
  
    function checkInput(){
        var flag = true;
        // 检查必须填写项
        if ($scope.userRoleName == undefined || $scope.userRoleName == '') {
            flag = false;
        }
        return flag;
    }
    $scope.submit = function(){
        if (checkInput()){
        	var menus="";
        	if($scope.overView) menus += "1;";
        	if($scope.compManage) menus += "2;";
        	if($scope.processManage) menus += "3;";
        	if($scope.projectManage) menus += "4;";
        	if($scope.compFactoryManage) menus += "5;";
        	if($scope.userManage) menus += "6;";
        	if($scope.roleManage) menus += "7;";
        	if($scope.logManage) menus += "8;";
        	if($scope.productManage) menus += "9;";
        	if($scope.personalManage) menus += "10;";
        	if($scope.pcManage) menus += "11;";
        	if($scope.bindManage) menus += "12;";
        	if($scope.compReadOnlyManage) menus += "13;";
            $http({
            	method : 'GET',
            	url:'/i/userrole/addUserRole',
    			params:{
    				'user_role_name': $scope.userRoleName,
    				'menu_ids': menus,
    				'creater' :  $rootScope.admin.user_name,
    				'user_role_note' : $scope.note
    			}
    		}).then(function (resp) {
    			 alert("添加成功");
                 $scope.getUserRoles();
                 $("#addUserRole").modal("hide"); 
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                console.log('progress: ' + progressPercentage + '% ' + evt.name);
            });
          } else {
            alert("请填写用户角色名称!");
        }
    }
	 $scope.goUpdate = function(userRoleID) {
	    	$http.get('/i/userrole/findUserRoleByID', {
	            params: {
	                "userRoleID": userRoleID
	            }
	        }).success(function(data){
			    if(data!=null&&data.userRole.user_role_id!=undefined){
					 $scope.userRoleDtoForUpdate = data;
			    }
		     });
		};
		
		function checkInputForUpdate(){
	        var flag = true;
	        // 检查必须填写项
	        if ($scope.userRoleDtoForUpdate.userRole.user_role_name == undefined || $scope.userRoleDtoForUpdate.userRole.user_role_name == '') {
	            flag = false;
	        }
	        return flag;
	    }
		 $scope.update = function(){
			 if (checkInputForUpdate()){
		        	var menus="";
		        	if($scope.userRoleDtoForUpdate.overView) menus += "1;";
		        	if($scope.userRoleDtoForUpdate.compManage) menus += "2;";
		        	if($scope.userRoleDtoForUpdate.processManage) menus += "3;";
		        	if($scope.userRoleDtoForUpdate.projectManage) menus += "4;";
		        	if($scope.userRoleDtoForUpdate.compFactoryManage) menus += "5;";
		        	if($scope.userRoleDtoForUpdate.userManage) menus += "6;";
		        	if($scope.userRoleDtoForUpdate.roleManage) menus += "7;";
		        	if($scope.userRoleDtoForUpdate.logManage) menus += "8;";
		        	if($scope.userRoleDtoForUpdate.productManage) menus += "9;";
		        	if($scope.userRoleDtoForUpdate.personalManage) menus += "10;";
		        	if($scope.userRoleDtoForUpdate.pcManage) menus += "11;";
		        	if($scope.userRoleDtoForUpdate.bindManage) menus += "12;";
		        	if($scope.userRoleDtoForUpdate.compReadOnlyManage) menus += "13;";
		            $http({
		            	method : 'GET',
		            	url:'/i/userrole/updateUserRole',
		    			params:{
		    				'user_role_id':  $scope.userRoleDtoForUpdate.userRole.user_role_id,
		    				'user_role_name':  $scope.userRoleDtoForUpdate.userRole.user_role_name,
		    				'menu_ids': menus,
		    				'creater' :  $rootScope.admin.user_name,
		    				'user_role_note' :  $scope.userRoleDtoForUpdate.userRole.user_role_note
		    			}
		    		}).then(function (resp) {
		    			 alert("更新成功");
		                 $scope.getUserRoles();
		                 $("#updateUserRole").modal("hide"); 
		            }, function (resp) {
		                console.log('Error status: ' + resp.status);
		            }, function (evt) {
		                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
		                console.log('progress: ' + progressPercentage + '% ' + evt.name);
		            });
		          } else {
		            alert("请填写用户角色名称!");
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
