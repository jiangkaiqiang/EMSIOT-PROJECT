coldWeb.controller('electManage', function ($rootScope, $scope, $state, $cookies, $http, Upload, $location) {
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
				    			$scope.provincesForSearch = data;
				    			var province = {
				    				"province_id" : "-1",
				    				"name" : "不限"
				    			};
				    			$scope.provincesForSearch.push(province);
				    			$scope.proID = "-1";
				    		});
				    	}
				    	if($scope.userPowerDto.sysUser.pro_power != "-1"){
				    		$http.get('/i/city/findCitysByProvinceId', {
				                params: {
				                    "provinceID": $scope.userPowerDto.sysUser.pro_power
				                }
				            }).success(function (data) {
				            	$scope.citisForSearch = data;
				            	var city = {"city_id":"-1","name":"不限"};
				            	$scope.citisForSearch.push(city);
				            });
				    	}
				    	if($scope.userPowerDto.sysUser.city_power != "-1"){
				    		$http.get('/i/city/findAreasByCityId', {
				                params: {
				                    "cityID": $scope.userPowerDto.sysUser.city_power
				                }
				            }).success(function (data) {
				            	$scope.areasForSearch = data;
				            	var area = {"area_id":"-1","name":"不限"};
				            	$scope.areasForSearch.push(area);
				            });
				    	}
				    	$scope.getElects();
			    });
		   });
	};
	
	$scope.addElectPic = function () {
		
    };
    $scope.dropElectPic = function(electPic){
    	$scope.electPic = null;
    };
    $scope.dropElectPicForUpdate = function(electPic){
    	$scope.updateElect.electrombile.elect_pic = null;
    };

    $scope.addIndentityCardPic = function () {
		
    };
    $scope.dropIndentityCardPic = function(indentityCardPic){
    	$scope.indentityCardPic = null;
    };
    $scope.dropIndentityCardPicForUpdate = function(recordPic){
    	$scope.updateElect.electrombile.indentity_card_pi = null;
    };
    
    $scope.addRecordPic = function () {
		
    };
    $scope.dropRecordPic = function(recordPic){
    	$scope.recordPic = null;
    };
    $scope.dropRecordPicForUpdate = function(recordPic){
    	$scope.updateElect.electrombile.record_pic = null;
    };
    
    
    $scope.addInstallCardPic = function () {
		
    };
    $scope.dropInstallCardPic = function(installCardPic){
    	$scope.installCardPic = null;
    };
    $scope.dropInstallCardPicForUpdate = function(installCardPic){
    	$scope.updateElect.electrombile.install_card_pic = null;
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
				proPower : $scope.userPowerDto.sysUser.pro_power,
				cityPower : $scope.userPowerDto.sysUser.city_power,
				areaPower : $scope.userPowerDto.sysUser.area_power,
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
    
    //获取全部省For ADD
    $http.get('/i/city/findProvinceList').success(function (data) {
        $scope.provinces = data;
        $scope.addProvinceID = data[0].province_id;
    });
    //根据省ID获取全部市For ADD
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
   //根据市ID获取全部区For ADD
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
    
    //获取全部省For Search；并添加上不限的权限
//    $http.get('/i/city/findProvinceList').success(function (data) {
//        $scope.provincesForSearch = data;
//        var province = {"province_id":"-1","name":"不限"};
//        $scope.provincesForSearch.push(province);
//        $scope.proID = "-1";
//    });
    //根据省ID获取全部市For Search；并添加上不限的权限
    $scope.getCitisForSearch = function () {
    	$http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.proID
            }
        }).success(function (data) {
        	$scope.citisForSearch = data;
        	var city = {"city_id":"-1","name":"不限"};
        	$scope.citisForSearch.push(city);
            $scope.cityID = "-1";
        });
    }
    //根据市ID获取全部省For Search；并添加上不限的权限
    $scope.getAreasForSearch = function () {
    	$http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.cityID
            }
        }).success(function (data) {
        	$scope.areasForSearch = data;
        	var area = {"area_id":"-1","name":"不限"};
        	$scope.areasForSearch.push(area);
            $scope.areaID = "-1";
        });
    }
    
    
    $scope.getCitisForUpdate = function () {
    	$http.get('/i/city/findCitysByProvinceId', {
            params: {
                "provinceID": $scope.updateElect.electrombile.pro_id
            }
        }).success(function (data) {
        	$scope.citisForUpdate = data;
            $scope.updateElect.electrombile.city_id = data[0].city_id;
        });
    }
    
    $scope.getAreasForUpdate = function () {
    	$http.get('/i/city/findAreasByCityId', {
            params: {
                "cityID": $scope.updateElect.electrombile.city_id
            }
        }).success(function (data) {
        	$scope.areasForUpdate = data;
            $scope.updateElect.electrombile.area_id = data[0].area_id;
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
    
    $scope.AllInsurType = [
        {id:"1",name:"投保"},
        {id:"2",name:"未投保"}
    ];
    $scope.addInsurDetail = "2";
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
    			    'elect_type' : $("input[name='addElectType']:checked").val(),
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
    
    $scope.goViewElect = function(electID) {
    	for (var i=0;i<$scope.AllElectDtos.length;i++)
    	{
    		if($scope.AllElectDtos[i].electrombile.elect_id == electID){
    		   $scope.viewElect = $scope.AllElectDtos[i];
    		   if($scope.viewElect.electrombile.elect_type=="1"){
    			   $scope.viewElect.electrombile.elect_type = "摩托车";
    		   }
    		   else if($scope.viewElect.electrombile.elect_type=="2"){
    			   $scope.viewElect.electrombile.elect_type = "两轮";
    		   }
    		   else if($scope.viewElect.electrombile.elect_type=="3"){
    			   $scope.viewElect.electrombile.elect_type = "三轮";
    		   }
    		   else if($scope.viewElect.electrombile.elect_type=="4"){
    			   $scope.viewElect.electrombile.elect_type = "四轮";
    		   }
    		   else{
    			   
    		   }
    		   if($scope.viewElect.electrombile.insur_detail=="1"){
    			   $scope.viewElect.electrombile.insur_detail = "已投保";
    		   }
    		   else if($scope.viewElect.electrombile.insur_detail=="2"){
    			   $scope.viewElect.electrombile.insur_detail = "未投保";
    		   }
    		   break;
    		}
    	}
	};
    
	 $scope.goUpdateElect = function(electID) {
		 for (var i=0;i<$scope.AllElectDtos.length;i++)
	    	{
	    		if($scope.AllElectDtos[i].electrombile.elect_id == electID){
	    		   $scope.updateElect = $scope.AllElectDtos[i];
	    		   $scope.updateElect.electrombile.pro_id = $scope.updateElect.electrombile.pro_id+"";
	    		   $scope.updateElect.electrombile.city_id = $scope.updateElect.electrombile.city_id+"";
	    		   $scope.updateElect.electrombile.area_id = $scope.updateElect.electrombile.area_id+"";
	    		   $scope.updateElect.electrombile.insur_detail = $scope.updateElect.electrombile.insur_detail+"";
	    		   $http.get('/i/city/findCitysByProvinceId', {
	    	            params: {
	    	                "provinceID": $scope.updateElect.electrombile.pro_id
	    	            }
	    	        }).success(function (data) {
	    	        	$scope.citisForUpdate = data;
	    	        });
	    		   $http.get('/i/city/findAreasByCityId', {
	    	            params: {
	    	                "cityID": $scope.updateElect.electrombile.city_id
	    	            }
	    	        }).success(function (data) {
	    	        	$scope.areasForUpdate = data;
	    	        });
	    		    if($scope.updateElect.electrombile.elect_type!=undefined){
	    		        $(":radio[name='updateElectType'][value='" + $scope.updateElect.electrombile.elect_type + "']").prop("checked", "checked");
	    		    }
	    		   break;
	    		}
	    	}
		};
		function checkInputForUpdate(){
			 var flag = true;
		        // 检查必须填写项
		        if ($scope.updateElect.electrombile.gua_card_num == undefined || $scope.updateElect.electrombile.gua_card_num == '') {
		            flag = false;
		        }
		        if ($scope.updateElect.electrombile.plate_num == undefined || $scope.updateElect.electrombile.plate_num == '') {
		            flag = false;
		        }
		        return flag;
	    }
		 $scope.update = function(){
			 if (checkInputForUpdate()){
				 data = {
						    'elect_id':$scope.updateElect.electrombile.elect_id,
		        			'gua_card_num': $scope.updateElect.electrombile.gua_card_num,
		    			    'plate_num' : $scope.updateElect.electrombile.plate_num,
		    			    've_id_num' : $scope.updateElect.electrombile.ve_id_num,
		    			    'elect_brand' : $scope.updateElect.electrombile.elect_brand,
		    			    'buy_date' : $scope.updateElect.electrombile.buy_date,
		    			    'elect_color' : $scope.updateElect.electrombile.elect_color,
		    			    'motor_num' : $scope.updateElect.electrombile.motor_num,
		    			    'note' : $scope.updateElect.electrombile.note,
		    			    'pro_id' : $scope.updateElect.electrombile.pro_id,
		    			    'city_id' : $scope.updateElect.electrombile.city_id,
		    			    'area_id' : $scope.updateElect.electrombile.area_id,
		    			    'elect_type' : $("input[name='updateElectType']:checked").val(),
		    			    'insur_detail' : $scope.updateElect.electrombile.insur_detail,
		    			    'elect_pic' : $scope.updateElect.electrombile.elect_pic,
		    			    'indentity_card_pic' : $scope.updateElect.electrombile.indentity_card_pic,
		    			    'record_pic' : $scope.updateElect.electrombile.record_pic,
		    			    'install_card_pic' : $scope.updateElect.electrombile.install_card_pic,
		    			    'owner_tele' : $scope.updateElect.electrombile.owner_tele,
		    			    'owner_name' : $scope.updateElect.electrombile.owner_name,
		    			    'owner_address' : $scope.updateElect.electrombile.owner_address,
		    			    'owner_id' : $scope.updateElect.electrombile.owner_id,
		    			    'recorder_id' : $rootScope.admin.user_id,
		    			    'elect_state' : 1
			            };
			       Upload.upload({
			                url: '/i/elect/updateElect',
			                headers :{ 'Content-Transfer-Encoding': 'utf-8' },
			                data: data
			            }).success(function (data) {
		            if(data.success){
		            	 alert(data.message);
		    			 $scope.getElects();
		                 $("#updateCar").modal("hide"); 
		            }
		        });
		          } else {
		        	  alert("防盗芯片编号和车牌号不能为空");
		        }
		    }
		 
		$scope.goSearchForTrace = function(gua_card_num) {
			    $scope.gua_card_numForTrace = gua_card_num;
				$http.get('/i/elect/findElectTrace', {
						params : {
//							'pageNum' : $scope.bigTotalItemsForTrace,
//							'pageSize' : $scope.maxSizeForTrace,
							"plateNum" : null,
							"guaCardNum" : gua_card_num,
							"startTimeForTrace" : $scope.searchTraceStart,
							"endTimeForTrace" : $scope.searchTraceEnd
						}
					}).success(function(data) {
						$scope.traceStations = data;
						//$scope.bigTotalItemsForTrace = data.total;
					});
		 }
		$scope.goSearchForTraceWithTime = function() {
			$scope.goSearchForTrace($scope.gua_card_numForTrace);
		}
//		$scope.pageChangedForTrace = function() {
//			$scope.goSearchForTrace($scope.gua_card_numForTrace);
//		}
		 
		 //选择日期
		 $('#buyCarDateUpdate').datetimepicker({
		        format: 'yyyy-mm-dd',
		        autoclose:true,
		        maxDate:new Date(),
		        pickerPosition: "bottom-left"
	    });
		 $('#byCarDate').datetimepicker({
		        format: 'yyyy-mm-dd',
		        autoclose:true,
		        maxDate:new Date(),
		        pickerPosition: "bottom-left"
	    });
		 $('#guijiSearchStart').datetimepicker({
		     format: 'yyyy-mm-dd - hh:mm:ss',
		     //minView: "month",
		     autoclose:true,
		     maxDate:new Date(),
		     pickerPosition: "bottom-left"
		 });
		 $("#guijiSearchEnd").datetimepicker({
		     format : 'yyyy-mm-dd - hh:mm:ss',
		     //minView: 'month',
		     autoclose:true,
		     maxDate:new Date(),
		     pickerPosition: "bottom-left"
		 }); 
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
//		 $scope.Preview=function(){ //打印预览
//			   $("#viewModalElec").printThis({loadCSS: ["emsiot-web/src/main/webapp/assets/css/bootstrap.css","emsiot-web/src/main/webapp/app/css/home.css",
//"emsiot-web/src/main/webapp/app/css/app.css", "emsiot-web/src/main/webapp/app/css/electManage.css"],importCSS: false,importStyle: false,  pageTitle: "车辆登记证",
//printContainer: true,  removeInline: false, formValues: true});//  loadCSS: "/Content/Themes/Default/style.css"
//		 };
		 $("#djPrint").on('click',function(){
			    window.print();
			})
});

