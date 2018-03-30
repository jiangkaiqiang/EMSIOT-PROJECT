coldWeb.controller('operationLog', function ($scope, $state, $cookies, $http, $location) {
	// 显示最大页数
    $scope.maxSize = 10;
    // 总条目数(默认每页十条)
    $scope.bigTotalItems = 12;
    // 当前页
    $scope.bigCurrentPage = 1;
	$scope.operationLog = [];
	$scope.getOperationLog = function(){
	    $http({
	    	method:'GET',
	    	url:'/i/operationLog/findOperationLogList',
	    	params:{
	    		pageNum : $scope.bigCurrentPage,
	    		pageSize : $scope.maxSize,
	    		adminName : $scope.adminName
	    	}}).success(function (data) {
	    	 $scope.bigTotalItems = data.total;
		     $scope.operationLog = data.list;
	    });
	}
    $scope.pageChanged = function () {
    	 $scope.getOperationLog();
    }
    $scope.getOperationLog();

    $scope.goSearch = function(){
    	$scope.getOperationLog();
    }
});