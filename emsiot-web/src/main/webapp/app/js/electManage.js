coldWeb.controller('electManage', function ($rootScope, $scope, $state, $cookies, $http, Upload, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
		   });
	};
	//文件上传
	var uploadUrl = 'https://xxxxxxx';
	function upfile(event) {
		var event = event || window.event,
			dom = '',
			upfile = $("#upfile").get(0).files[0],
			otype = upfile.type || '获取失败',
			osize = upfile.size / 1054000,
			ourl = window.URL.createObjectURL(upfile); //文件临时地址
			console.log(upfile);
			console.log(otype);
		//$('#file_type').text("选择上传文件类型：" + otype);
		//$('#file_size').text("选择上传文件大小，共" + osize.toFixed(2) + "MB。");
        //
		//console.log("文件类型：" + otype); //文件类型
		//console.log("文件大小：" + osize); //文件大小
        //
		//if ('video/mp4' == otype || 'video/avi' == otype || 'video/x-msvideo' == otype) {
		//	dom = '<video id="video" width="100%" height="100%" controls="controls" autoplay="autoplay" src=' + ourl + '></video>';
		//}
		//if ('audio/mp3' == otype || 'audio/wav' == otype  || 'audio/x-m4a' == otype) {
		//	dom = '<audio id="audio" width="100%" height="100%" controls="controls" autoplay="autoplay" loop="loop" src=' + ourl + ' ></audio>';
		//}
		if ('image/jpeg' == otype || 'image/png' == otype || 'image/gif' == otype) {
			dom = '<img id="photo" width="100%" height="100%" alt="我是image图片文件" src=' + ourl + ' title="" />';
		}
		$('#carPhoto').html(dom);
	};
	
	$scope.addElectPic = function () {
		
    };
    $scope.dropElectPic = function(electPic){
    	$scope.electPic = null;
    };

    $scope.addIndentityCardPic = function () {
		
    };
    $scope.dropIndentityCardPic = function(indentityCardPic){
    	$scope.indentityCardPic = null;
    };
    
    $scope.addRecordPic = function () {
		
    };
    $scope.dropRecordPic = function(recordPic){
    	$scope.recordPic = null;
    };
    
    $scope.addInstallCardPic = function () {
		
    };
    $scope.dropInstallCardPic = function(installCardPic){
    	$scope.installCardPic = null;
    };
    
	//显示下拉搜索条件
	$scope.searchBlock=function(){
	var  unblockContent=$(".search-container .unblock-content");
	console.log(unblockContent);
		unblockContent.toggleClass("no-block");
    }
	$scope.load();
	// 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
	$scope.AllElectDtos = [];
	$scope.electState = "8";
	$scope.AllElectState = [
        {id:"1",name:"正常"},
        {id:"2",name:"黑名单"},
        {id:"8",name:"全部"}
    ];
	$scope.insurDetail = "8";
	$scope.AllInsurStatus = [
        {id:"1",name:"已投保"},
        {id:"2",name:"未投保"},
        {id:"8",name:"全部"}
    ];
	$scope.searchKeyType = "1";
	$scope.AllSearchKeyType = [
        {id:"1",name:"车主手机号"},
        {id:"2",name:"身份证号"},
        {id:"3",name:"车牌号"},
        {id:"4",name:"防盗芯片编号"},
        {id:"5",name:"车主姓名"}
    ];
	
	 // 获取当前车辆的列表
    $scope.getElects = function() {
		$http({
			method : 'POST',
			url : '/i/elect/findElectList',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				startTime : $scope.startTime,
				endTime : $scope.endTime,
				recorderID : $scope.recorderID,
				electState : $scope.electState,
				insurDetail : $scope.insurDetail,
				proID : $scope.proID,
				cityID : $scope.cityID,
				areaID : $scope.areaID,
				ownerTele : $scope.ownerTele,
				ownerID : $scope.ownerID,
				plateNum : $scope.plateNum,
				guaCardNum : $scope.guaCardNum,
				ownerName : $scope.ownerName
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.AllElectDtos = data.list;
		});
	}

	$scope.pageChanged = function() {
		$scope.getElects();
	}
	$scope.getElects();
	// 获取当前冷库的列表
	$scope.auditChanged = function(optAudiet) {
		$scope.getElects();
	}
    
	$scope.goSearch = function () {
		if($scope.searchKeyType=="1"){
			$scope.ownerTele = $scope.searchKey;
		}
		else if($scope.searchKeyType=="2"){
			$scope.ownerID = $scope.searchKey;
		}
		else if($scope.searchKeyType=="3"){
			$scope.plateNum = $scope.searchKey;
		}
		else if($scope.searchKeyType=="4"){
			$scope.guaCardNum = $scope.searchKey;
		}
		else if($scope.searchKeyType=="5"){
			$scope.ownerName = $scope.searchKey;
		}
		else{
			
		}
		$scope.getElects();
    }
	$scope.showAll = function () {
		$scope.reload();
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
    //获取全部管理员
    $http.get('/i/user/findAllUsers').success(function (data) {
        $scope.sysUsers = data;
        var user = {"user_id":"0","user_name":"全部"};
        $scope.sysUsers.push(user);
        $scope.sysUserID = "0";
    });
    
    //获取全部省
    $http.get('/i/city/findProvinceList').success(function (data) {
        $scope.provinces = data;
        $scope.provincesForSearch = data;
        var province = {"province_id":"-1","name":"全部"};
        $scope.provincesForSearch.push(province);
        $scope.addProvinceID = data[0].province_id;
        $scope.proID = "-1";
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
    
    $scope.goDeleteElect = function (electID) {
    	if(delcfm()){
    	$http.get('/i/elect/deleteElectByID', {
            params: {
                "electID": electID
            }
        }).success(function (data) {
        	$scope.getElects();
        });
    	}
    }
    $scope.goDeleteElects = function(){
    	if(delcfm()){
    	var electIDs = [];
    	for(i in $scope.selected){
    		electIDs.push($scope.selected[i].electrombile.elect_id);
    	}
    	if(electIDs.length >0 ){
    		$http({
    			method:'DELETE',
    			url:'/i/elect/deleteElectByIDs',
    			params:{
    				'electIDs': electIDs
    			}
    		}).success(function (data) {
    			$scope.getElects();
            });
    	}
    	}
    }
    
    $scope.goExportElects = function(){
    	var electIDs = [];
    	for(i in $scope.selected){
    		electIDs.push($scope.selected[i].electrombile.elect_id);
    	}
    	if(electIDs.length >0 ){
    		window.location.href="/i/elect/exportElectByIDs?electIDs="+electIDs;
    	}
    	else{
    		alert("请先选择要导出的车辆！");
    	}
    }
   
    $scope.selected = [];
    $scope.toggle = function (electDto, list) {
		  var idx = list.indexOf(electDto);
		  if (idx > -1) {
		    list.splice(idx, 1);
		  }
		  else {
		    list.push(electDto);
		  }
    };
    $scope.exists = function (electDto, list) {
    	return list.indexOf(electDto) > -1;
    };
    $scope.isChecked = function() {
        return $scope.selected.length === $scope.AllElectDtos.length;
    };
    $scope.toggleAll = function() {
        if ($scope.selected.length === $scope.AllElectDtos.length) {
        	$scope.selected = [];
        } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
        	$scope.selected = $scope.AllElectDtos.slice(0);
        }
    };
    
    $scope.getElectIDsFromSelected = function(audit){
    	var electIDs = [];
    	for(i in $scope.selected){
    		if(audit != undefined)
    			$scope.selected[i].audit = audit;
    		electIDs.push($scope.selected[i].id);
    	}
    	return electIDs;
    }
    
    $scope.getInsur = function(insur_detail){
    	if(insur_detail==1)
    		return '投保';
        else if(insur_detail==null){
    		return '未知';
    	}
        else{
        	return '未投保';
        }
    }
    
    $scope.getState = function(elect_state){
    	if(elect_state==1)
    		return '正常';
        else{
        	return '黑名单';
        }
    }
    
    function checkInput(){
        var flag = true;
        // 检查必须填写项
        if ($scope.addGuaCardNum == undefined || $scope.addGuaCardNum == '') {
            flag = false;
        }
        if ($scope.addPlateNum == undefined || $scope.addPlateNum == '') {
            flag = false;
        }
        return flag;
    }

    $scope.submit = function(){
        if (checkInput()){
        	data = {
        			'gua_card_num': $scope.addGuaCardNum,
    			    'plate_num' : $scope.addPlateNum,
    			    've_id_num' : $scope.addVeIdNum,
    			    'elect_brand' : $scope.addElectBrand,
    			    'buy_date' : $scope.addBuyDate,
    			    'elect_color' : $scope.addElectColor,
    			    'motor_num' : $scope.addMotorNum,
    			    'note' : $scope.addNote,
    			    'pro_id' : $scope.addProvinceID,
    			    'city_id' : $scope.addCityID,
    			    'area_id' : $scope.addAreaID,
    			    'elect_type' : $scope.addElectType,
    			    'insur_detail' : $scope.addInsurDetail,
    			    'elect_pic' : $scope.electPic,
    			    'indentity_card_pic' : $scope.indentityCardPic,
    			    'record_pic' : $scope.recordPic,
    			    'install_card_pic' : $scope.installCardPic,
    			    'owner_tele' : $scope.addOwnerTele,
    			    'owner_name' : $scope.addOwnerName,
    			    'owner_address' : $scope.addOwnerAddress,
    			    'owner_id' : $scope.addOwnerID,
    			    'recorder_id' : $rootScope.admin.user_id,
    			    'elect_state' : 1
	            };
	       Upload.upload({
	                url: '/i/elect/addElect',
	                headers :{ 'Content-Transfer-Encoding': 'utf-8' },
	                data: data
	            }).success(function (data) {
            if(data.success){
            	 alert(data.message);
    			 $scope.getElects();
                 $("#addCar").modal("hide"); 
            }
        });
          }
       else {
            alert("防盗芯片编号和车牌号不能为空");
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
		//选择日期

		 $('#electDateStart').datetimepicker({
		     format: 'yyyy-mm-dd - hh:mm:ss',
		     minView: "month",
		     autoclose:true,
		     maxDate:new Date(),
		     pickerPosition: "bottom-left"
		 });
		 $("#electDateEnd").datetimepicker({
		     format : 'yyyy-mm-dd - hh:mm:ss',
		     minView: 'month',
		     autoclose:true,
		     maxDate:new Date(),
		     pickerPosition: "bottom-left"
		 }); 
});

