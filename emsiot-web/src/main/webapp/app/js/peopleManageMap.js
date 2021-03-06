coldWeb.controller('peopleManageMap', function ($rootScope, $scope, $state, $cookies, $http, $location) {
    var map = new BMap.Map("allmap", {
        minZoom: 5,
        maxZoom: 30
    });    // 创建Map实例
    $.ajax({type: "GET", cache: false, dataType: 'json', url: '/i/user/findUser'}).success(function (data) {
        $scope.user = data;

        if ($scope.user == null || $scope.user.user_id == 0 || $scope.user.user_id == undefined) {
            url = "http://" + $location.host() + ":" + $location.port() + "/login.html";
            window.location.href = url;
        }
        // 根据用户的区域权限定位城市，如果为超级管理员暂时定位喀什
        $http.get('/i/city/findCityNameByUserPower', {
            params: {
                "areaID": $scope.user.area_power,
                "cityID": $scope.user.city_power,
                "proID": $scope.user.pro_power
            }
        }).success(function (data) {
            $scope.cityName = data.name;
            //$scope.cityName = "芒市";
            var myGeo = new BMap.Geocoder();
            myGeo.getPoint($scope.cityName, function (point) {
                $scope.point = point;
                //console.log($scope.user.fixed_lon);
                if($scope.user.fixed_lat == null || $scope.user.fixed_lat == undefined || $scope.user.fixed_lon == null || $scope.user.fixed_lon == undefined){
                    //map.centerAndZoom(new BMap.Point($scope.point.lng, $scope.point.lat), 15); // 初始化地图,设置中心点坐标和地图级别
                    map.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
                }else{
                    map.centerAndZoom(new BMap.Point($scope.user.fixed_lon, $scope.user.fixed_lat), $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                    //map.centerAndZoom($scope.cityName, $scope.user.fixed_zoom); // 初始化地图,设置中心点坐标和地图级别
                }
            })
           // map.centerAndZoom($scope.cityName, 15); // 初始化地图,设置中心点坐标和地图级别
            map.enableScrollWheelZoom(true); //开启鼠标滚轮缩放

            showCssFlag('#xsjizhan');

            $scope.peopleLocation();

            // 获取在线人数
            $http.get('/i/people/findInlinePeoplesNum', {
                params: {
                    "areaPower": $scope.user.area_power,
                    "cityPower": $scope.user.city_power,
                    "proPower": $scope.user.pro_power
                }
            }).success(function (data) {
                $scope.inlineElects = data;
            });

        });
    });
    var lushu=null;
    function G(id) {
        return document.getElementById(id);
    }

    var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
        {
            "input": "suggestId"
            , "location": map
        });

    ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
        var str = "";
        var _value = e.fromitem.value;
        var value = "";
        if (e.fromitem.index > -1) {
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

        value = "";
        if (e.toitem.index > -1) {
            _value = e.toitem.value;
            value = _value.province + _value.city + _value.district + _value.street + _value.business;
        }
        str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
        G("searchResultPanel").innerHTML = str;
    });

    var myValue;
    ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
        var _value = e.item.value;
        myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
        G("searchResultPanel").innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
        setPlace();
    });

    function setPlace() {
        map.clearOverlays();    //清除地图上
        function myFun() {
            var pp = local.getResults().getPoi(0).point;//获取第一个智能搜索信息
            map.centerAndZoom(pp, 18);
            map.addOverlay(new BMap.Marker(pp));
        }

        var local = new BMap.LocalSearch(map, {
            onSearchComplete: myFun
        });
        local.search(myValue);
    }


    $scope.labelSet=function(value1,value2){
        var label = new BMap.Label(value1,{offset:new BMap.Size(0,-30)});
            label.setStyle({
                color: "#fff",
                fontSize: "14px",
                backgroundColor: "#66B3FF",
                border: "1px solid rgb(102, 179, 255)",
                fontWeight: "normal",
                display: "block",
                borderShadow: "0 5px 15px rgba(0, 0, 0, 0.6)",
                minHeight: "20px",
                minWidth: "40px",
                lineHeight: "20px",
                borderRadius: "5px",
                textAlign: "center",
                padding:"1px 8px"
            });
        //}
        value2.setLabel(label);
    }

    var isAddMarker=true
    function markerClickListener(marker2){
    	 var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><i class='iconfont icon-yonghuguanli' style='color: #707070; font-size: 20px;' onclick='alert(1)'></i><h4>";
         var sHtml2 = "</h4></li><li>";
         var sHtml3 = "</li></ul><p class='flex-items'><i class='glyphicon glyphicon-map-marker'></i><span>";
         var sHtml4 = "</p><hr/><div class='tableArea margin-top2'><table class='table table-striped ' id='tableArea' ng-model='AllElects'><thead><tr><th>序号</th><th>基站名称</th><th>经过时间</th></tr></thead><tbody>";
         var endHtml = "</tbody></table></div></div>";
         var showCount=""
         var selectCardNum = "";
         var option="";
         var overlay = map.getOverlays();
         //console.log(overlay)
         var marker = null;
         var markerTitle
         isAddMarker=true;
         var list = [];
         var firstObj = {};
         var obj = {};

         for (var i = 0; i < overlay.length; i++) {
        	 marker = overlay[i]
        	 if(marker.point.lat == marker2.point.lat && marker.point.lng == marker2.point.lng){

                 if(marker != null){
                	 isAddMarker=false
                	 if(marker.test != null && marker.test != undefined){
                		 list = marker.test
                	 }else{
                		 var obj = {};
                		 marker2Title = marker.getTitle().split('\t');
                		 firstObj["card_num"]=marker2Title[0]
                		 firstObj["station_name"]=marker2Title[1]
                		 firstObj["name"]=marker2Title[2]
            	         list.push(firstObj)
                	 }

                	 marker2Title = marker2.getTitle().split('\t');
                	 obj["card_num"]=marker2Title[0]
			         obj["station_name"]=marker2Title[1]
                	 obj["name"]=marker2Title[2]
			         list.push(obj)
                	 marker2 = marker;
                	 marker2["test"]=list
                	 $scope.labelSet(list[0].name+"：+"+list.length,marker2);
                 }
        		 break;
        	 }
		 }
         //console.log(marker2)




    	marker2.addEventListener("click", function (e) {
            var title_add = new Array();
            title_add = this.getTitle().split('\t');
            showPeopleInStation($scope.peopleStartTime, $scope.peopleEndTime+" 23:59:59",title_add[0]);  //根据物理编号查找
            var sHtml = "<div id='positionTable' class='shadow position-car-table'><ul class='flex-between'><li class='flex-items'><i class='iconfont icon-yonghuguanli' style='color: #707070; font-size: 20px;'></i><h4>";
            if(selectCardNum==""){
	            if(list.length>1){
		            for (var i = 0; i < list.length; i++) {
		            		option += "<option value='"+list[i].card_num+"'>"+list[i].name+"</option>"
					}
		            selectCardNum = "<select  id='"+title_add[0]+"' class='form-control'>"+option+"</select>";
		            showCount = title_add[0] + ",经过"+ $scope.peopleInStation.length+"个 基站";
	            }else{
	            	selectCardNum = title_add[0];
	            	showCount = "经过"+ $scope.peopleInStation.length+"个 基站";
	            }
            }

            var electInfo='';
            for (var k = 0; k < $scope.peopleInStation.length; k++) {
            	electInfo += "<tr><td title='" + (k + 1) + "'>" + (k + 1) + "</td>" + "<td title='" + $scope.peopleInStation[k].station_name + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.peopleInStation[k].station_name + "</td>" + "<td title='" + $scope.peopleInStation[k].corssTime + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.peopleInStation[k].corssTime + "</td></tr>";
            }
            var infoWindow = new BMap.InfoWindow(sHtml + selectCardNum + sHtml2 + showCount + sHtml3 + title_add[1] + sHtml4 + electInfo + endHtml);
            var p = e.target;
            var point = new BMap.Point(p.getPosition().lng, p.getPosition().lat);
            map.openInfoWindow(infoWindow, point);

            setTimeout(() => {
        		const device = $("#"+title_add[0])
        		device.off("change")
        		device.on("change", function(){
        			console.log("change")
        			showPeopleInStation($scope.peopleStartTime, $scope.peopleEndTime+" 23:59:59",$(this).val());  //根据物理编号查找
        			electInfo="";
        			for (var k = 0; k < $scope.peopleInStation.length; k++) {
                    	electInfo += "<tr><td title='" + (k + 1) + "'>" + (k + 1) + "</td>" + "<td title='" + $scope.peopleInStation[k].station_name + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.peopleInStation[k].station_name + "</td>" + "<td title='" + $scope.peopleInStation[k].corssTime + "'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + $scope.peopleInStation[k].corssTime + "</td></tr>";
                    }
                    $(this).parent().parent().next().html($(this).val()+",经过"+ $scope.peopleInStation.length+"个 基站")

        			$("#tableArea tbody").html(electInfo)

        		})
        	}, 0);
        });


    }

    //显示基站和聚合
    var markerClusterer=null;
    var marker2;
    //var markers = []
    function showStation() {
    	if(lushu!=null){
    		lushu.stop();
    		lushu = null;
    	}
        var pt;
        //var marker2;
        var markers = []; //存放聚合的基站
        //var infoWindow;
        var tmpStation;

        //给每一个基站添加监听事件 和窗口信息
        for (var i = 0; i < $scope.peopleData.length; i++) {
            /*tmpStation = $scope.peopleData[i].station;
            tmpPeople = $scope.peopleData[i].people;*/

            pt = new BMap.Point($scope.peopleData[i].longitude, $scope.peopleData[i].latitude);
            //人员图标
            var myIcon = new BMap.Icon("../app/img/people-icon.png", new BMap.Size(67, 51));
            marker2 = new BMap.Marker(pt,{icon:myIcon});
            $scope.labelSet($scope.peopleData[i].people_name,marker2);
            marker2.setTitle($scope.peopleData[i].people_gua_card_num + '\t' + $scope.peopleData[i].station_name + '\t' +$scope.peopleData[i].people_name);
            //console.log(tmpPeople.people_gua_card_num)
            //console.log(tmpStation.station_name)
           // console.log(tmpStation)

            markerClickListener(marker2);
            if(isAddMarker){
            	map.addOverlay(marker2);
            }
        }

      //用于基站跳动
        $scope.jizhanBounce=map.getOverlays();
    }

  //用于基站跳动的参数
    $scope.jizhanBounce=null;
    $scope.tiao=null;
    //表格选中行，对应标注体现出来
    /*$('#tableArea tbody').on('click','tr',function(e){
    	if($scope.tiao!=null){
    		$scope.tiao.setAnimation(null);
    	}
        var tablePoint =  $(this).context.cells[1].innerHTML;//获取单击表格时的地址
        for(var g =0;g < $scope.peopleData.length; g++){
        	if($scope.peopleData[g].station==null)continue;
            if(tablePoint==$scope.peopleData[g].ownerPlateNum){//表格获取到的地址等于循环基站时的基站地址

                var allOverlay = $scope.jizhanBounce;

                var Oe=null
                for (var i = 0; i < allOverlay.length; i++) {
					Oe = allOverlay[i].point;
					if(Oe==null)continue;
	                if(Oe.lng==$scope.peopleData[g].station.longitude && Oe.lat==$scope.peopleData[g].station.latitude){
	                	$scope.tiao = allOverlay[i];//保存上一次跳动的基站
	                	//allOverlay[i].setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
	                    return;
	                }
                }
                return;
            }
        }
    });*/

    //根据时间和基站id获取基站下面的当前所有车辆
    function showPeopleInStation(startTime, endTime, peopleGuaCardNum) {
        $.ajax({
            method: 'GET',
            url :'/i/people/findPeoplesByStationIdAndTime',
            async:false,
            data : {
                "startTime": startTime,
                "endTime": endTime,
                "peopleGuaCardNum": peopleGuaCardNum
            }
        }).success(function(data){
            $scope.peopleInStation = data;
        })
    }

    /*$scope.AllPeopleType = [
        {id: "1", name: "学生"},
        {id: "2", name: "老人"},
        {id: "3", name: "戒毒者"},
        {id: "4", name: "犯罪者"}
    ];
    var objType = []
    var objStrIds = "";
    if($rootScope.rootUserPowerDto.kidsManage){
    	objType[1]={id: "1", name: "学生"}
    	objStrIds+="1,"
    }
    if($rootScope.rootUserPowerDto.oldPeopleManage){
    	objType[2] = {id: "2", name: "老人"}
    	objStrIds+="2,"
    }
    if($rootScope.rootUserPowerDto.gowsterPeopleManage){
    	objType[3] = {id: "3", name: "戒毒者"}
    	objStrIds+="3,"
    }
    
    if(objStrIds != ""){
    	objStrIds = objStrIds.substring(0,objStrIds.length-1);
    }
    objType[0] = {id: objStrIds, name: "全部"}
    $scope.AllPeopleType=objType
    console.log(objStrIds)
    $scope.peopleType = "0";*/

    //定义轨迹及定位查询条件的类型
    $scope.AllKeywordType = [
        {id: "0", name: "防盗芯片ID"},
        {id: "1", name: "身份证"}
    ];
    $scope.keywordTypeForLocation = "0";
    $scope.keywordTypeForTrace = "0";
    //清除定位
    var stationIDforDelete;
    $scope.clearElectLocation = function() {
    	if($scope.elecMarker!=null){
    		$scope.elecMarker.hide();
    		$scope.elecMarker = null;

    		//map.clearOverlays();
    		//showStation();
    		$scope.peopleLocation()
    	}

        $("#dingweiModal").modal("hide");
    };
    //根据条件定位车辆
    $scope.findElectLocation = function () {
        $scope.showOperation = false;
        $scope.showTable = false;
       if ($scope.keywordTypeForLocation == "1") {
        	if($scope.keywordForLocation==null || $scope.keywordForLocation==""){
        		alert("请输入身份证！");
        		return;
            }
            $scope.peopleIdCards = $scope.keywordForLocation;
            $scope.peopleGuaCardNum = null;
        }
        else if ($scope.keywordTypeForLocation == "0") {
        	if($scope.keywordForLocation==null || $scope.keywordForLocation==""){
        		alert("请输入防盗芯片号！");
        		return;
            }
            $scope.peopleGuaCardNum = $scope.keywordForLocation;
            $scope.peopleIdCards = null;
        }
        else {

        }

        $http.get('/i/people/findPeopleLocation', {
            params: {
                "peopleIdCards": $scope.peopleIdCards,
                "peopleGuaCardNum": $scope.peopleGuaCardNum
            }
        }).success(function (data) {

            $scope.longitude = data.longitude;
            $scope.latitude = data.latitude;

            if($scope.longitude == undefined || $scope.longitude == null){
            	return alert("未查到相应数据！");
            }
            if(lushu!=null){
        		lushu.stop();
        		lushu=null;
        	}
            map.clearOverlays();
            $scope.showOperation = false;
            stationIDforDelete = data.station_phy_num;
            $scope.elecPt = new BMap.Point($scope.longitude, $scope.latitude);
            $scope.elecIcon = new BMap.Icon("../app/img/people-icon.png", new BMap.Size(67, 51));
            $scope.elecMarker = new BMap.Marker($scope.elecPt, {icon: $scope.elecIcon});
            $scope.labelSet($scope.keywordForLocation+":"+data.station_name,$scope.elecMarker);
            map.addOverlay($scope.elecMarker);
            $scope.elecMarker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
            map.centerAndZoom($scope.elecPt, 17);


          //用于基站跳动
            $scope.jizhanBounce=map.getOverlays();
        }).error(function(){
        	alert("搜索条件有误！");
        });
        $("#dingweiModal").modal("hide");
    };

    $scope.findElectTrace = function () {            //显示车辆轨迹
    	//$scope.jizhanjuheFlag = 0;
    	//$scope.jizhankongzhi();


        //showStation();
        if ($scope.keywordTypeForTrace == "1") {
        	if($scope.keywordForTrace == null || $scope.keywordForTrace == ""){
        		alert("请输入身份证！")
        		return;
        	}
            $scope.peopleIdCards = $scope.keywordForTrace;
            $scope.peopleGuaCardNum = null

        }
        else if ($scope.keywordTypeForTrace == "0") {
        	if($scope.keywordForTrace == null || $scope.keywordForTrace == ""){
        		alert("请输入防盗芯片号！")
        		return;
        	}
            $scope.peopleGuaCardNum = $scope.keywordForTrace;
            $scope.peopleIdCards = null;

        }
        else {

        }
        var endTimeForTrace = "";
        if($scope.endTimeForTrace != null && $scope.endTimeForTrace != undefined && $scope.endTimeForTrace != ""){
        	endTimeForTrace = $scope.endTimeForTrace + " 23:59:59";
        }else{
        	endTimeForTrace = $scope.endTimeForTrace ;
        }
        $http.get('/i/people/findPeopleTrace', {
            params: {
                "peopleIdCards": $scope.peopleIdCards,
                "peopleGuaCardNum": $scope.peopleGuaCardNum,
                "startTimeForTrace": $scope.startTimeForTrace,
                "endTimeForTrace": endTimeForTrace
            }
        }).success(function (data) {

        	//$scope.bigTotalItems = data.total;
            $scope.AllPeoplelarms = data;
            $scope.traceStationsLength = data.length;
            $scope.pointsArr = [];
            for(var j=0;j<data.length;j++){
                var pointsJ = data[j].longitude;
                var pointsW = data[j].latitude;
                $scope.pointsArr.push(new BMap.Point(pointsJ,pointsW));
            }

            if (data.length == 1)
                alert("该车辆仅经过一个基站：" + data[0].station_name);
            else if (data.length == 0)
                alert("该车辆没有经过任何基站！");
            else{
            	if(lushu!=null){
            		lushu.stop();
            		lushu=null;
            	}
            	$scope.elecMarker = null;
            	map.clearOverlays();
            	var showLable="";
            	
            	var marker;
                // var lushu;
                 var arrPois=$scope.pointsArr;
                 map.setViewport(arrPois);
                 if(lushu==null){
	                    marker=new BMap.Marker(arrPois[0],{
	                        icon  : new BMap.Icon('../app/img/people-icon.png', new BMap.Size(50,30),{anchor : new BMap.Size(27, 23)})
	                    });
	                    var label = new BMap.Label($scope.keywordForTrace+":"+data[0].station_name,{offset:new BMap.Size(0,-30)});
	                    label.setStyle({border:"1px solid rgb(204, 204, 204)",color: "rgb(0, 0, 0)",borderRadius:"10px",padding:"5px 10px",background:"rgb(255, 255, 255)"});
	                    marker.setLabel(label);
	                    map.addOverlay(marker);lushu=1
                 }
            	
                for (var i = 0; i < data.length - 1; i++) {
                    
                    BMapLib.LuShu.prototype._move=function(initPos,targetPos,effect) {
                        var pointsArr=[initPos,targetPos];  //点数组
                        var me = this,

                        //当前的帧数
                            currentCount = 0,
                        //步长，米/秒
                            timer = 10,
                            step = this._opts.speed / (1000 / timer),
                        //初始坐标
                            init_pos = this._projection.lngLatToPoint(initPos),
                        //获取结束点的(x,y)坐标
                            target_pos = this._projection.lngLatToPoint(targetPos),
                        //总的步长
                            count = Math.round(me._getDistance(init_pos, target_pos) / step);
                        //显示折线 syj201607191107
                        this._map.addOverlay(new BMap.Polyline(pointsArr, {
                            strokeColor : "rgb(102, 179, 255)",
                            strokeWeight : 2,
                            strokeOpacity : 1
                        })); // 画线
                      //显示轨迹当前点的数据

                        //如果小于1直接移动到下一点
                        if (count < 1) {
                            me._moveNext(++me.i);
                            return;
                        }
                        me._intervalFlag = setInterval(function() {
                            //两点之间当前帧数大于总帧数的时候，则说明已经完成移动
                            if (currentCount >= count) {
                                clearInterval(me._intervalFlag);
                                //移动的点已经超过总的长度
                                if(me.i > me._path.length){
                                    return;
                                }
                                //运行下一个点
                                me._moveNext(++me.i);
                            }else {
                                currentCount++;
                                var x = effect(init_pos.x, target_pos.x, currentCount, count),
                                    y = effect(init_pos.y, target_pos.y, currentCount, count),
                                    pos = me._projection.pointToLngLat(new BMap.Pixel(x, y));
                                //设置marker
                                if(currentCount == 1){
                                    var proPos = null;
                                    if(me.i - 1 >= 0){
                                        proPos = me._path[me.i - 1];
                                    }
                                    if(me._opts.enableRotation == true){
                                        me.setRotation(proPos,initPos,targetPos);
                                    }
                                    if(me._opts.autoView){
                                        if(!me._map.getBounds().containsPoint(pos)){
                                            me._map.setCenter(pos);
                                        }
                                    }
                                }
                                //正在移动
                                me._marker.setPosition(pos);
                                //设置自定义overlay的位置
                                me._setInfoWin(pos);
                            }
                        },timer);
                        if(showLable == ""){
                        	showLable = me._opts.defaultContent
                        }

                        me._opts.defaultContent=showLable+":"+data[me.i+1].station_name

                    };


                }

                lushu = new BMapLib.LuShu(map,arrPois,{
                    defaultContent:$scope.keywordForTrace,//"从天安门到百度大厦"
                    autoView:true,//是否开启自动视野调整，如果开启那么路书在运动过程中会根据视野自动调整
                    icon  : new BMap.Icon('../app/img/people-icon.png', new BMap.Size(50,30),{anchor : new BMap.Size(27, 23)}),
                    speed: 350,
                    enableRotation:true,//是否设置marker随着道路的走向进行旋转
                    landmarkPois:[
                        {lng:$scope.pointsArr[0],lat:$scope.pointsArr[1],html:'加油站',pauseTime:2}
                    ]

                });
                marker.addEventListener("click",function(){
                    marker.enableMassClear();   //设置后可以隐藏改点的覆盖物
                    marker.hide();
                    lushu.start();
                    //map.clearOverlays();  //清除所有覆盖物
                });

                //drv.search('天安门', '百度大厦');
                // lushu.start();
                // lushu.pause();
                //绑定事件
                $("run").onclick = function(){
                    marker.enableMassClear(); //设置后可以隐藏改点的覆盖物
                    marker.hide();
                    lushu.start();
                    //map.clearOverlays();    //清除所有覆盖物
                }
                //$("stop").onclick = function(){
                //    marker.enableMassClear();
                //    marker.hide();
                //    lushu.stop();
                //}
                $("pause").onclick = function(){
                    lushu.pause();
                }
                $("hide").onclick = function(){
                    lushu.hideInfoWindow();
                }
                $("show").onclick = function(){
                    lushu.showInfoWindow();
                }
                function $(element){
                    return document.getElementById(element);
                }
                
            }
            $scope.showOperation = true;
            $scope.showTable = true;
            $scope.electNumForTraceTable = $scope.keywordForTrace;
        }).error(function(){
            $("#positionTable").removeClass("rightToggle");
            $scope.showOperation = false;
            $scope.showTable = false;
        	alert("搜索条件有误！");
        	return ;
        });


        $("#guijiModal").modal("hide");
        //用于基站跳动
        $scope.jizhanBounce=map.getOverlays();
    };
    $scope.showOperation = false;
    $scope.showTable = false;

    $scope.showDetail = function () {
    	$scope.showTable = !($scope.showTable);
        $("#detail i").toggleClass("down-up");
    }
    $scope.clearElectTrace = function () {


        $("#guijiModal").modal("hide");
        $scope.showOperation = false;
        $scope.showTable = false;
        $scope.traceStationsLength=null
        //map.clearOverlays();
        $scope.peopleLocation()
        //showStation();
        if($scope.elecMarker!=null){
            //map.addOverlay($scope.elecMarker);
        	$scope.elecMarker.hide();
    		$scope.elecMarker = null;
        }
        //$scope.getElectalarmsByOptions();

    };


    var navBtn = $(".home-title a");
    navBtn.click(function () {
        navBtn.removeClass("active");
        $(this).addClass("active");

    });
    //轨迹选择日期
    $('#homeDateStart').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $scope.startTimeForTrace=$scope.doDateStr(new Date())
    $('#homeDateEnd').datetimepicker({
        format: 'yyyy-mm-dd',
        minView: "month",
        autoclose: true,
        maxDate: new Date(),
        pickerPosition: "bottom-left"
    });
    $scope.endTimeForTrace=$scope.doDateStr(new Date())
    //报警车辆日期
    $('#peopleStartTime').datetimepicker({
    	format: 'yyyy-mm-dd',
    	minView: "month",
    	autoclose: true,
    	maxDate: new Date(),

    	pickerPosition: "bottom-left"
    });

    $('#peopleEndTime').datetimepicker({
    	format: 'yyyy-mm-dd',
    	minView: "month",
    	autoclose: true,
    	maxDate: new Date(),
    	pickerPosition: "bottom-left"
    });
    $(function(){
    	var sysTime = new Date().getTime();//当天
    	var month = new Date(sysTime - 60*1000*60*24*30)
    	$scope.peopleStartTime=FormatDate(month,1);
    	$scope.peopleEndTime=FormatDate(sysTime,1);

    })

    function showCssFlag(param) {
        $(param).toggleClass("active");
        var left = $(param).css('left');
        left = parseInt(left);
        if (left == 0) {
            $(param).css('background-color', '#66b3ff');
                $(param).parent().css('background-color', '#66b3ff');
            $(param).parent().parent().addClass("active");
        } else {
            $(param).css('background-color', '#fff');
                $(param).parent().css('background-color', '#ccc');
            $(param).parent().parent().removeClass("active");
        }
    }

    $(".dismis").click(function () {
        $(this).parents('#positionTable').toggleClass("rightToggle");
        if ($(this).hasClass("fa-angle-left")) {
            $(this).removeClass("fa-angle-left").addClass("fa-angle-right")
        } else {
            $(this).removeClass("fa-angle-right").addClass("fa-angle-left")
        }
    });





	/*JS.Engine.on(
			{
				limitData : function(msgData){

					var message = msgData.split(";");
					if($rootScope.admin.pro_power==-1){
						alert(message[3]);
					}
					else if($rootScope.admin.pro_power==message[0]) {
						if($rootScope.admin.city_power==-1){
							alert(message[3]);
						}
						else if($rootScope.admin.city_power==message[1]){
							if($rootScope.admin.area_power==-1||$rootScope.admin.area_power==message[2]){
								alert(message[3]);
							}
						}
					}
				}
			}
	);*/

	$scope.peopleLocation = function() {
		$scope.showOperation = false;
        $scope.showTable = false;
        $.ajax({
            method: 'GET',
            url :'/i/people/selectPeopleStationPeopleLocationByTime',
            data : {
                "startTime": $scope.peopleStartTime,
                "endTime": $scope.peopleEndTime + " 23:59:59",
                "proPower": $scope.user.pro_power,
                "cityPower": $scope.user.city_power,
                "areaPower": $scope.user.area_power

            }
        }).success(function(data){
            $scope.peopleData = data;
            map.clearOverlays();
            //$scope.clearElectTrace();
            showStation();

           /* for (var i = 0; i < $scope.peopleData.length; i++) {
				if($scope.peopleData[i].station!=null){
		            var pt = new BMap.Point($scope.peopleData[i].station.latitude, $scope.peopleData[i].station.longitude);
					var myIcon = new BMap.Icon("../app/img/eb-1.jpg", new BMap.Size(67, 51));
					var marker2 = new BMap.Marker(pt,{icon:myIcon});
		            map.addOverlay(marker2);
		            marker2.setAnimation(BMAP_ANIMATION_BOUNCE);
				}
            }*/
        })
    }

	//统计时间内经过改基站的车辆的数
    var time = new Date().getTime();//当前时间
    var start = new Date(time - 60*1000*60);//一小时
    var end = new Date(time);
    function FormatDate (strTime,elect) {
        var date = new Date(strTime);
        //2018-10-15 修改
        var year = date.getFullYear();
        var month = (date.getMonth()+1) < 10?"0"+(date.getMonth()+1):(date.getMonth()+1);
        var day = date.getDate() < 10?"0"+date.getDate():date.getDate();
        var hours = date.getHours() < 10?"0"+date.getHours():date.getHours();
        var min = date.getMinutes() < 10?"0"+date.getMinutes():date.getMinutes();
        var seconds = date.getSeconds() < 10?"0"+date.getSeconds():date.getSeconds();
        if(elect==0){
        	return year + "-" + month + "-" + day + " " + hours + ":" + min + ":" +seconds
        }else{
        	return year + "-" + month + "-" + day
        }
        //return date.getFullYear()+"-"+(date.getMonth()+1)+"-"+date.getDate()+" "+ date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }

});
