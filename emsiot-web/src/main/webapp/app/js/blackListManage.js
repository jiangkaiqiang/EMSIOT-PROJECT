/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('blackListManage', function ($rootScope, $scope, $state, $cookies, $http, $location) {
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
    
    
    
    $scope.load();
	// 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 10;
    // 当前页
    $scope.bigCurrentPage = 1;
	$scope.AllBlackelects = [];
	
    //加载用户所有的黑名单
//    $scope.getBlackelects = function() {
//		$http({
//			method : 'POST',
//			url : '/i/blackelect/findAllBlackelectForMap',
//			params : {
//				pageNum : $scope.bigCurrentPage,
//				pageSize : $scope.maxSize,
//			}
//		}).success(function(data) {
//			$scope.bigTotalItems = data.total;
//			$scope.AllBlackelects = data.list;
//		});
//	}
    //根据条件加载用户所有的黑名单
    $scope.getBlackelectsByOptions = function() {
		$http({
			method : 'POST',
			url : '/i/blackelect/findAllBlackelectByOptions',
			params : {
				pageNum : $scope.bigCurrentPage,
				pageSize : $scope.maxSize,
				blackID : $scope.blackID,
				ownerTele: $scope.ownerTele,
				plateNum: $scope.plateNum,
				DealStatus: $scope.DealStatus
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
  $scope.goDeleteBlackElect = function (blackID) {
  	if(delcfm()){
  	$http.get('/i/blackelect/deleteBlackelectByID', {
          params: {
              "blackID": blackID
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
  	for(i in $scope.selected){
  		BlackIDs.push($scope.selected[i].blackelect.black_id);
  	}
  	if(BlackIDs.length >0 ){
  		$http({
  			method:'DELETE',
  			url:'/i/blackelect/deleteBlackelectByIDs',
  			params:{
  				"BlackIDs": BlackIDs
  			}
  		}).success(function (data) {
  			 $scope.getBlackelectsByOptions();
          });
  	}
  	}
  }
  //
});