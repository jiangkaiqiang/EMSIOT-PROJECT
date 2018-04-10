/**
 * Created by SAIHE01 on 2018/4/8.
 */
coldWeb.controller('sensitiveAreaAlarm', function ($rootScope, $scope, $state, $cookies, $http, $location) {
    //$.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
    //    var user = data;
    //
    //});


console.log("报警页面展示成功");

    $scope.goHome = function () {
        $state.reload();
    }

    //选择日期

    $('#alarmDateStart').datetimepicker({
        format: 'yyyy-mm-dd - hh:mm:ss',
        //minView: "month",
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
    $("#alarmDateEnd").datetimepicker({
        format : 'yyyy-mm-dd - hh:mm:ss',
        //minView: 'month',
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
});