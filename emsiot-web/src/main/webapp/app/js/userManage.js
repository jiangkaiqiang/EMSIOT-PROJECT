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
    $scope.maxSize = 12;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 12;
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
    
    // 获取角色
    $http.get('/i/userrole/findAllUserRole').success(function (data) {
        $scope.userRoles = data;
        $scope.addUserRoleid = data[0].user_role_id;
    });
	
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
