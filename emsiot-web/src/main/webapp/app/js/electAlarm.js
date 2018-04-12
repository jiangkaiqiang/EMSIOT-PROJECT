coldWeb.controller('electAlarm', function ($rootScope, $scope, $state, $cookies, $http, $location) {
  console.log("车辆报警页面展示");
    //选择日期

    $('#electAlarmDate').datetimepicker({
        format: 'yyyy-mm-dd  hh:mm:ss',
        autoclose:true,
        maxDate:new Date(),
        pickerPosition: "bottom-left"
    });
});
