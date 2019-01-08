/**
 * 主要脚本方法
 */
//全局缓存对象
var cache = {};
//页面初始化脚本方法
$(function(){
	setCalendar();
	$(window).resize(function(){autoHeight();});
});
//页面自适应方法
function autoHeight(){
	var w = $("#regionCenter").width();
	var h = $("#regionCenter").height();
}
//页面加载完成事件
function onloadComplete(){
	initMap();
	loadRailwayMileagePoints();
}
//左侧面板状态:默认关闭
var westPanel = "close";

//菜单点击事件
function showNavMenuPage(menuid,pageurl,title){
	if(menuid=="imgmarkWin"){
		return;
	}
	$("#westRegion").panel("setTitle",title);
	if(westPanel=="close"){
		$("#layout").layout("expand","west");
		westPanel = "open";
	}
	$("#queryframe").hide();
	$("#dataframe").hide();
	$("#"+menuid).show();
	var pagesrc = $("#"+menuid).attr("src");
	if(pagesrc==null || pagesrc==""){
		$("#"+menuid).attr("src",pageurl);
	}
	if(menuid=="dataframe"){
		window.frames["queryframe"].clearQueryNearData();
	}
}

//加载铁路线里程坐标点
function loadRailwayMileagePoints(){
	doAjax("json/DataService!listAll",{method:"listLinePoint"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["mileage-points"]=data;
		}
	});
}
/**
 * //关闭周边查询窗口
function closeQueryWin(){
	//clearQueryArea();
	//clearPoiMarkers();
	//clearLayerMarkers();
	//clearJobLines();
}
var webconfigs = [];
var webconfigJson = {};
//加载配置文件
function loadConfigs(){
	doAjax("json/Web!loadConfigs",{},{},function(rsMap,op){
		if(rsMap.state){
			webconfigs = rsMap.list;
			var config = null;
			for(var i=0;i<webconfigs.length;i++){
				config = webconfigs[i];
				webconfigJson[config.nodeName] = config.nodeValue;
			}
		}
	});
}
//加载铁路线坐标点
function loadRailwayLinePoints(){
	doAjax("json/DataService!listAll",{method:"queryAllLinePoint"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["railway-points"] = data;
		}
	});
}

//根据里程获取坐标点信息
function getLonlatByMileage(mileage1,mileage2){
	var points = [];
	var matchRecord1 = {};
	var matchRecord2 = {};
	var data = cache["mileage-points"];
	var count = data.length;
	var record = null;
	var _mileage = 0;
	var min_dx1 = 0;
	var min_dx2 = 0;
	var dx1 = 0;
	var dx2 = 0;
	for(var i=0;i<count;i++){
		record = data[i];
		_mileage = parseFloat(record.mileage);
		dx1 = Math.abs(_mileage-mileage1);
		dx2 = Math.abs(_mileage-mileage2);
		if(i==0){
			min_dx1 = dx1;
			min_dx2 = dx2;
			matchRecord1 = record;
			matchRecord2 = record;
		}else{
			if(dx1 < min_dx1){
				min_dx1 = dx1;
				matchRecord1 = record;
			}
			if(dx2 < min_dx2){
				min_dx2 = dx2;
				matchRecord2 = record;
			}
		}
	}
	
	points.push(matchRecord1);
	points.push(matchRecord2);
	return points;
}
*/
/***业务图层标注点信息显示开始
//点击进行定位
function clickRowAction(index,row){
	var lon = row.longitude;
	var lat = row.latitude;
	if(lon!=""&&lat!=""){
		lon = parseFloat(lon);
		lat = parseFloat(lat);
		if(isNaN(lon)||isNaN(lat)){
			top.layer.msg("经纬度数据格式不正确，无法定位！", {icon:0});
		}else{
			parent.jumpTo(lon, lat);
		}
	}else{
		top.layer.msg("经纬度数据格式不正确，无法定位！", {icon:0});
	}
}
var markerArray = new Array();
function showLayerData(data,type){
	clearMarkers();
	var count = data.length;
	var record = null;
	var lon = null;
	var lat  = null;
	for(var i=0;i<count;i++){
		record = data[i];
		if(record.longitude!=""&&record.latitude!=""){
			var mk = addOneMarker(record,type);
			markerArray.push(mk);
		}
	}
}
//加载单个标注数据
function addOneMarker(info,type){
	var lon = parseFloat(info.longitude);
	var lat = parseFloat(info.latitude);
	var point = new BMap.Point(lon, lat);
	var marker = null;
	var left = 0;
	var top = 0;
	var viewColumn = "name";
	if(type=="t_shortmsg"){
		viewColumn = "job_number";
	}
	if(info.iconurl!=""){
		var name = info.iconurl.split(".")[0];
		var size = name.split("_")[1];
		var width = size.split("X")[0];
		var height = size.split("X")[1];
		var myIcon = new BMap.Icon(info.iconurl, new BMap.Size(parseInt(width,10),parseInt(height,10)));
		marker = new BMap.Marker(point,{icon:myIcon});
		left = 0;
		top = parseInt(height,10);
	}else{
		marker = new BMap.Marker(point);
		left = 20;
		top = 0;
	}
	//var columnstr = webconfigJson[type+"_showcolumns"];
	//var columns = eval("("+columnstr+")");
	//var infohtml = getPopupInfoHtml(info,columns);
	//var infoWindow = new BMap.InfoWindow(infohtml);//创建信息窗口对象
	//infoWindow.dataid = info.dataid;
	map.addOverlay(marker);
//	var label = new BMap.Label('<label style="padding:5px;">'+info[viewColumn]+'</label>',{offset:new BMap.Size(left,top)});
//	label.setStyle(markerStyle);
//	marker.setLabel(label);
	marker.addEventListener("click", function(){          
	   this.openInfoWindow(infoWindow);
	   //alert(infoWindow.dataid);
	   //图片加载完毕重绘infowindow
//	   document.getElementById('imgDemo').onload = function (){
//		   infoWindow.redraw();//防止在网速较慢，图片未加载时，生成的信息框高度比图片的总高度小，导致图片部分被隐藏
//	   }
	});
	//markerArray.push(marker);
	return marker;
}
//清除所有的标注
function clearMarkers(){
	for(var i=0;i<markerArray.length;i++){
		map.removeOverlay(markerArray[i]);
		markerArray[i]=null;
	}
	markerArray.length=0;
}
//获取气泡详细信息
function getPopupInfoHtml(record,columns){
	var sb = new StringBuilder();
	sb.append('<div style="border-bottom:2px solid #DDDDDD;padding:2px;font:12px 微软雅黑;color:#2D3BAE;font-weight:bold;">详细信息</div>');
	sb.append('<table class="dataTable" border="0" bordercolor="#EEEEEE" style="font:12px 微软雅黑;font-weight:bold;">');
	var field = "";
	var text = "";
	for(var i=0;i<columns.length;i++){
		field = columns[i].field;
		text = columns[i].text;
		sb.append('<tr><td class="sta_att">'+text+'</td><td class="sta_att_value">'+record[field]+'</td></tr>');
	}
	sb.append('</table>');
	//sb.append('<div class="imglist"></div>');
	var html = sb.toString();
	return html;
}
****/
/**
//获取铁路段坐标点信息
function getRailwayLineInfo(pointstr){
	var points = [];
	if(pointstr!=null&&pointstr!=""){
		var tmpArr = pointstr.split("至");
		var data = cache["railway-points"];
		var mileage = null;
		var _mileage = null;
		for(var i=0;i<tmpArr.length;i++){
			mileage = parseFloat(tmpArr[i]);
			if(!isNaN(mileage)){
				var min_dx = 0;
				var point = {};
				for(var k=0;k<data.length;k++){
					_mileage = parseFloat(data[k].mileage);
					var tmp_dx = Math.abs(_mileage-mileage);
					//console.log(tmp_dx+","+mileage+","+_mileage);
					if(k==0){
						min_dx = tmp_dx;
						point.lon = data[k].longitude;
						point.lat = data[k].latitude;
					}else{
						if(tmp_dx<min_dx){
							min_dx = tmp_dx;
							point.lon = data[k].longitude;
							point.lat = data[k].latitude;
						}
					}
				}
				points.push(point);
			}
		}
	}
	return points;
}

var iframehtml='<iframe noresize="noresize" height="100%" width="100%" border="0" frameborder="0" scrolling="auto" style="background-color:#FFFFff;"></iframe>';
//周边信息查询模块
function showQueryWin(){
	$("#queryWin").window("open");
	var html = '<iframe src="web/query.html" noresize="noresize" id="queryIframe" name="queryIframe" height="100%" width="100%" border="0" frameborder="0" scrolling="auto" style="background-color:#FFFFff;"></iframe>';
	$("#queryWin").html(html);
}
//业务图层数据管理模块
function showLayerWin(){
	$("#layerWin").window("open");
	var html = '<iframe src="admin/layerdata_manager.html?type=map" noresize="noresize" id="layerIframe" name="layerIframe" height="100%" width="100%" border="0" frameborder="0" scrolling="auto" style="background-color:#FFFFff;"></iframe>';
	$("#layerWin").html(html);
}
//图片标注点管理模块
function showImgMarkWin(){
	$("#imgmarkWin").window("open");
	var html = '<iframe src="web/imgmark.html" noresize="noresize" id="imgmarkIframe" name="imgmarkIframe" height="100%" width="100%" border="0" frameborder="0" scrolling="auto" style="background-color:#FFFFff;"></iframe>';
	$("#imgmarkWin").html(html);
}
//标注样式
var markerStyle = {
	border:"1px solid #cccccc",
	//fontWeight:"bold",
	padding:"0px",
	color : "blue",
	fontSize : "12px",
	height : "20px",
	lineHeight : "20px",
	fontFamily:"微软雅黑"
};

//查询图层数据图片
function loadDataImage(dataid){
	var param = {method:"queryLayerDataImage",id:dataid};
	doAjax("json/DataService!listAll",param,{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["imgdata"]=data;
			var count = data.length;
			var sb = new Array();
			var record = data[0];
			var localhref = window.location.href;
			var index = localhref.indexOf("data/");
			var basePath = localhref.substring(0,index);
			sb.push('<img id="imgView" src="'+record.imgpath+'"/>');
			$("#imglist").html(sb.join(""));
		}
	});
}
//添加地图点击事件
function addMapClickEvent(){
	map.addEventListener("click", clickCallback);
	top.layer.alert("请在地图上单击选择坐标点！",{icon:0});
}
var clickMarker = null;
//单击回调事件
function clickCallback(e){
	var longitude = e.point.lng;
	var latitude = e.point.lat;
	if(clickMarker!=null){
		map.removeOverlay(clickMarker);
	}
	var point = new BMap.Point(longitude, latitude);
	var marker = new BMap.Marker(point);
	map.addOverlay(marker);
	clickMarker = marker;
	window.frames[top.addLayerDataIframe].setDataPoint(longitude,latitude);
}
//重新加载图层数据
function reloadLayerData(){
	window.frames["layerIframe"].loadLayerData();
}

var railwayPolygonLine = null;
function drawPolygonLine(points){
	console.log(points);
	map.removeOverlay(railwayPolygonLine);
	var pntArr = [];
	var bdpoint = null;
	for(var i=0;i<points.length;i++){
		bdpoint = PointConvert.wgs84tobd09(parseFloat(points[i].lon),parseFloat(points[i].lat));
		console.log(bdpoint);
		pntArr.push(new BMap.Point(bdpoint.lon, bdpoint.lat));
	}
	railwayPolygonLine = new BMap.Polyline(pntArr, {strokeColor:"red", strokeWeight:5, strokeOpacity:0.5});   //创建折线
  	map.addOverlay(railwayPolygonLine);
  	jumpTo(points[0].lon, points[0].lat);
  	bdpoint = PointConvert.wgs84tobd09(parseFloat(points[0].lon),parseFloat(points[0].lat));
  	var point = new BMap.Point(bdpoint.lon, bdpoint.lat);
  	var myIcon = new BMap.Icon("images/icon-start.png", new BMap.Size(parseInt(36,10),parseInt(36,10)));
	marker = new BMap.Marker(point,{icon:myIcon});
	map.addOverlay(marker);
	bdpoint = PointConvert.wgs84tobd09(parseFloat(points[1].lon),parseFloat(points[1].lat));
  	point = new BMap.Point(bdpoint.lon, bdpoint.lat);
	myIcon = new BMap.Icon("images/icon-end.png", new BMap.Size(parseInt(36,10),parseInt(36,10)));
	marker = new BMap.Marker(point,{icon:myIcon});
	map.addOverlay(marker);
}

/ ***固定信息显示结束**** /
function getStringNumber(string){
	if (typeof string === "string") {
        var arr = string.match("\\d+.+\\d{2,}");
        console.log(arr);
        return arr[0];
//        return arr.map(function (item) {
//            return item;
//        });
    } else {
        return null;
    }
}
*/