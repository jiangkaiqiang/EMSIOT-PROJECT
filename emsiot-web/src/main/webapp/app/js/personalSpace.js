coldWeb.controller('personalSpace', function ($rootScope, $scope, $state, $cookies, $http, $location) {
	 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
		   $rootScope.admin = data;
			if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
				url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
				window.location.href = url;
			}
			else{
				$http.get('/i/userrole/findUserRoleByUserID', {
		            params: {
		                "userID": $rootScope.admin.user_id
		            }
		        }).success(function(data){
				    if(data!=null&&data.userRole.user_role_id!=undefined){
				    	$scope.adminRole = data.userRole;
				    }
			     });
			}
	   });
	  function checkInput(){
	        var flag = true;
	        // 检查必须填写项
	        if ($scope.newPassword == undefined || $scope.rnewPassword == '' ||
	        	$scope.rnewPassword == undefined || $scope.newPassword == ''||  
	        	$scope.rnewPassword!= $scope.newPassword) {
	            flag = false;
	        }
	        return flag;
	    }
	    $scope.submit = function(){
	        if (checkInput()){
	            $http({
	            	method : 'GET',
	            	url:'/i/user/changePwd',
	    			params:{
	    				'password': $scope.newPassword,
	    				'userID': $rootScope.admin.user_id
	    				}
	    		}).then(function (resp) {
	    			 alert("修改成功");
	                 window.location.reload();
	            }, function (resp) {
	                console.log('Error status: ' + resp.status);
	            }, function (evt) {
	                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
	                console.log('progress: ' + progressPercentage + '% ' + evt.name);
	            });
	          } else {
	            alert("密码输入有误!");
	        }
	    }
});
