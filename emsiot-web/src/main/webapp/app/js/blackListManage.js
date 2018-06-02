/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('blackListManage', function ($rootScope, $scope, $state, Upload,$cookies, $http, $location) {
	$scope.load = function(){
		 $.ajax({type: "GET",cache: false,dataType: 'json',url: '/i/user/findUser'}).success(function(data){
			   $rootScope.admin = data;
				if($rootScope.admin == null || $rootScope.admin.user_id == 0 || admin.user_id==undefined){
					url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
					window.location.href = url;
				}
		   });	 
		 
	};


console.log("报警页面展示成功");

    $scope.goHome = function () {
        $state.reload();
    }
    $('#electAlarmDate').datetimepicker({
        format: 'yyyy-mm-dd  hh:mm:ss',
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
    $('#electAlarmDateUpdate').datetimepicker({
        format: 'yyyy-mm-dd  hh:mm:ss',
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
    
    $scope.load();
	// 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
	$scope.AllBlackelects = [];
    //根据条件加载用户所有的黑名单
    $scope.getBlackelectsByOptions = function() {
		$http({
			method : 'POST',
			url : '/i/blackelect/findAllBlackelectByOptions',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				ownerTele: $scope.ownerTele,
				plateNum: $scope.plateNum,
				DealStatus: $scope.DealStatus,
				proPower : $scope.admin.pro_power,
				cityPower : $scope.admin.city_power,
				areaPower : $scope.admin.area_power
			}
		}).success(function(data) {
			$scope.bigTotalItems = data.total;
			$scope.AllBlackelects = data.list;
		});
	}
//    $scope.getBlackelects();
    $scope.getBlackelectsByOptions();
    
    $scope.selected = [];
    $scope.exists = function (blackelectDto, list) {
    	return list.indexOf(blackelectDto) > -1;
    };
    $scope.toggle = function (blackelectDto, list) {
		  var idx = list.indexOf(blackelectDto);
		  if (idx > -1) {
		    list.splice(idx, 1);
		  }
		  else {
		    list.push(blackelectDto);
		  }
  };
  $scope.isChecked = function() {
      return $scope.selected.length === $scope.AllBlackelects.length;
  };
  $scope.toggleAll = function() {
      if ($scope.selected.length === $scope.AllBlackelects.length) {
      	$scope.selected = [];
      } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
      	$scope.selected = $scope.AllBlackelects.slice(0);
      }
  };
  
  $scope.getState = function(deal_status){
  	if(deal_status==1)
  		return '已处理';
      else{
      	return '未处理';
      }
  }
  $scope.pageChanged = function() {
	  $scope.getBlackelectsByOptions();
 }
  $scope.goSearch = function () {
		$scope.getBlackelectsByOptions();
  }
  function delcfm() {
      if (!confirm("确认要删除？")) {
          return false;
      }
      return true;
}
  //根据id删除黑名单 goDeleteBlackElect(blackelectDto.blackelect.black_id)
  $scope.goDeleteBlackElect = function (blackID,plate_num) {
  	if(delcfm()){
  	$http.get('/i/blackelect/deleteBlackelectByID', {
          params: {
              "blackID": blackID,
              "plate_num":plate_num
          }
      }).success(function (data) {
    	  $scope.getBlackelectsByOptions();
      });
  	}
  }
  //批量删除黑名单
  $scope.goDeleteBlackElects = function(){
  	if(delcfm()){
  	var BlackIDs = [];
  	var plate_nums = [];
  	for(i in $scope.selected){
  		BlackIDs.push($scope.selected[i].blackelect.black_id);
  		plate_nums.push($scope.selected[i].blackelect.plate_num);
  	}
  	if(BlackIDs.length >0 ){
  		$http({
  			method:'DELETE',
  			url:'/i/blackelect/deleteBlackelectByIDs',
  			params:{
  				"BlackIDs": BlackIDs,
  				"plate_nums":plate_nums
  			}
  		}).success(function (data) {
  			 $scope.getBlackelectsByOptions();
          });
  	}
  	}
  }
  //获取省市区
  //获取全部省
  $http.get('/i/city/findProvinceList').success(function (data) {
      $scope.provinces = data;
      $scope.addProvinceID = data[0].province_id;
  });
	
  //获取全部管理员
  $http.get('/i/user/findAllUsers').success(function (data) {
      $scope.sysUsers = data;
      $scope.sysUserID = data[0].user_id;
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
  
  $scope.getCitisForUpdate = function () {
  	$http.get('/i/city/findCitysByProvinceId', {
          params: {
              "provinceID": $scope.blackElectForUpdate.blackelect.pro_id
          }
      }).success(function (data) {
      	  $scope.citisForUpdate = data;
      	  $scope.blackElectForUpdate.blackelect.city_id = data[0].city_id;
      });
  }
  
  $scope.getAreasForUpdate = function () {
  	$http.get('/i/city/findAreasByCityId', {
          params: {
              "cityID": $scope.blackElectForUpdate.blackelect.city_id
          }
      }).success(function (data) {
      	$scope.areasForUpdate = data;
      	$scope.blackElectForUpdate.blackelect.area_id = data[0].area_id;
      });
  }
  //添加黑名单
  function checkInput(){
      var flag = true;
      // 检查必须填写项
      if ($scope.addPlateNum == undefined || $scope.addPlateNum == '') {
          flag = false;
      }
      if ($scope.addProvinceID == undefined || $scope.addProvinceID == '') {
          flag = false;
      }
      if ($scope.addAddressType == undefined || $scope.addAddressType == '') {
          flag = false;
      }
      return flag;
  }
  
  function checkInputInfo(){
      var flag = true;
      // 检查必须填写项
      if ($scope.addPlateNum == undefined || $scope.addPlateNum == '') {
          flag = false;
      }
      return flag;
  }
  $scope.submit = function(){
      if (checkInput()){
    	  $http({
  			method : 'POST',
  			url : '/i/blackelect/addBlackelect',
  			params : {
      			plate_num: $scope.addPlateNum,
  			    case_occur_time : $scope.addOccurDate,
  			    owner_tele : $scope.addOwnerTele,
  			    owner_name : $scope.addOwnerName,
  			    pro_id : $scope.addProvinceID,
  			    city_id : $scope.addCityID,
  			    area_id : $scope.addAreaID,
  			    case_address_type : $scope.addAddressType,
  			    case_detail : $scope.addCaseDetail,
  			    detail_address : $scope.addDetailAddress,
  			    deal_status : 1
  			}
	            }).success(function (data) {
          if(data.success){
          	 alert(data.message);
          	 $scope.getBlackelectsByOptions();
               $("#addBlackelect").modal("hide"); 
          }
      });
        }
     else {
          alert("车牌号、案发区域、案发地不能为空");
      }
  }
  
 //刷新模态框
  $scope.refreshInfo = function(){
      if (checkInputInfo()){
    	  $http({
  			method : 'POST',
  			url : '/i/blackelect/refreshBlackelect',
  			params : {
  				plate_num: $scope.addPlateNum
  			    }
	        }).success(function (data) {
	          $scope.addOwnerTele = data.owner_tele;
	          $scope.addOwnerName = data.owner_name;
//        	  $("#addOwnerTele").val(data.owner_tele);
//        	  $("#addOwnerName").val(data.owner_name);
      });
        }
     else {
          alert("车牌号不能为空");
      }
  }
  //弹出黑名单
  $scope.goUpdateBlackelect = function(black_id) {
  	$http.get('/i/blackelect/findBlackelectByID', {
          params: {
              "BlakcID": black_id
          }
      }).success(function(data){
		    $scope.blackElectForUpdate = data;
		    $scope.blackElectForUpdate.blackelect.pro_id = $scope.blackElectForUpdate.blackelect.pro_id+"";
		    $scope.blackElectForUpdate.blackelect.city_id = $scope.blackElectForUpdate.blackelect.city_id+"";
		    $scope.blackElectForUpdate.blackelect.area_id = $scope.blackElectForUpdate.blackelect.area_id+"";
		    $scope.blackElectForUpdate.blackelect.deal_status = $scope.blackElectForUpdate.blackelect.deal_status+"";
		    
		    $http.get('/i/city/findCitysByProvinceId', {
	            params: {
	                "provinceID": $scope.blackElectForUpdate.blackelect.pro_id
	            }
	        }).success(function (data) {
	        	$scope.citisForUpdate = data;
	        });
		   $http.get('/i/city/findAreasByCityId', {
	            params: {
	                "cityID": $scope.blackElectForUpdate.blackelect.city_id
	            }
	        }).success(function (data) {
	        	$scope.areasForUpdate = data;
	        });
	     });
	$scope.statusForUpdate = [{deal_status:"0",name:"未处理"},{deal_status:"1",name:"已处理"}];
  };
	//更新黑名单  goUpdateBlackelectConfirm()
	$scope.goUpdateBlackelectConfirm = function(){
    	data = {
    			'black_id':$scope.blackElectForUpdate.blackelect.black_id,
    			'plate_num': $scope.blackElectForUpdate.blackelect.plate_num,
    			'case_occur_time':$scope.blackElectForUpdate.blackelect.case_occur_time,
    			'owner_tele':$scope.blackElectForUpdate.blackelect.owner_tele,
    			'owner_name':$scope.blackElectForUpdate.blackelect.owner_name,
    			'pro_id':$scope.blackElectForUpdate.blackelect.pro_id,
    			'city_id':$scope.blackElectForUpdate.blackelect.city_id,
    			'area_id':$scope.blackElectForUpdate.blackelect.area_id,
    			'case_address_type':$scope.blackElectForUpdate.blackelect.case_address_type,
    			'case_detail':$scope.blackElectForUpdate.blackelect.case_detail,
    			'deal_status':$scope.blackElectForUpdate.blackelect.deal_status
            };
       Upload.upload({
                url: '/i/blackelect/updateBlackelect',
                headers :{ 'Content-Transfer-Encoding': 'utf-8' },
                data: data
            }).success(function (data) {
        if(data.success){
        	 alert(data.message);
			 $scope.getBlackelectsByOptions();
             $("#updateBlackelect").modal("hide"); 
        }
    });
  }
	//
});