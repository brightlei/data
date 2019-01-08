/**
 * 地图相关脚本
 */
//全局地图对象
var map;
//地图叠加图层对象
var mapOverlayers={};
//初始化百度地图
function initMap(){
	map = new BMap.Map("allmap",{enableMapClick:false});    //创建Map实例
	map.addControl(new BMap.MapTypeControl());   //添加地图类型控件
	//添加放大缩小按钮
	var top_right_navigation = new BMap.NavigationControl({anchor: BMAP_ANCHOR_BOTTOM_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL}); 
	map.addControl(top_right_navigation);
	//map.setCurrentCity("襄阳");          // 设置地图显示的城市 此项是必须设置的
	map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
	map.centerAndZoom(new BMap.Point(112.15201,32.060642), 13);// 初始化地图,设置中心点坐标和地图级别
	getGpsLocation();
	$("#allmap").append("<div id='overlayer' class='overlayer'><p class='layertitle'>图层列表</p></div>");
	//设置用户姓名
	$("#userxm").html(user.xm);
}
var gpsPoint = null;
//获取GPS定位信息
function getGpsLocation(){
	//自动定位到当前位置
	var geolocation = new BMap.Geolocation();
	geolocation.getCurrentPosition(function(r) {
		if (this.getStatus() == BMAP_STATUS_SUCCESS) {
			gpsPoint = r.point;
			//var mk = new BMap.Marker(r.point);
			//map.addOverlay(mk);
			//map.panTo(r.point);
			//map.centerAndZoom(r.point, 16);  // 初始化地图,设置中心点坐标和地图级别
			//alert("当前位置经度为:" + r.point.lng + "纬度为:" + r.point.lat);
			//setLocation(r.point);
		} else {
			map.centerAndZoom(new BMap.Point(112.15201,32.060642), 13);// 初始化地图,设置中心点坐标和地图级别
			alert('无法定位到您的当前位置，导航失败，请手动输入您的当前位置！' + this.getStatus());
		}
		showUserPosition();
	}, {
		enableHighAccuracy : true
	});
}

var userMarker = null;
//显示用户位置信息
function showUserPosition(){
	var lon = 112.15201;
	var lat = 32.060642;
	if(gpsPoint!=null){
		lon = gpsPoint.lng;
		lat = gpsPoint.lat;
	}else{
		lon = parseFloat(user.lon);
		lat = parseFloat(user.lat);
	}
	var point = new BMap.Point(lon,lat);
	var myIcon = new BMap.Icon(user.headimg, new BMap.Size(48,48));
	userMarker = new BMap.Marker(point,{icon:myIcon});
	map.removeOverlay(userMarker);
	map.addOverlay(userMarker);
	var label = new BMap.Label("<label style='padding:4px;font-weight:bold;'>"+user.xm+"</label>",{offset:new BMap.Size(0,-20)});
	userMarker.setLabel(label);
	map.centerAndZoom(point, 19);
	top.queryCirclePoint = {lon:lon,lat:lat};
}


/**
//添加图层列表
function addOverLayer(type,name){
	var layer = $("#layer-"+type);
	if(layer[0]==null){
		$("#overlayer").append('<p id="layer-'+type+'"><img src="images/cross.png" title="删除该图层" onclick="deleteOverLayer(\''+type+'\')"/><input class="ckbcss" type="checkbox" id="ckb-'+type+'" value="\''+type+'\'" onclick="setOverLayer(this)"/><label for="ckb-'+type+'">'+name+'</label></p>');
		$("#overlayer").slideDown();
	}
}
//设置图层显示
function setOverLayer(obj){
	var checked = obj.checked;
	var type = obj.value;
	if(checked){
		showOverLayer(type);
	}else{
		hideOverLayer(type);
	}
}

//显示图层数据
function showOverLayer(type){
	var data = mapOverlayers["layer-"+type];
	if(data!=null&&typeof(data)!="undefined"){
		for(var i=0;i<data.length;i++){
			map.addOverlay(data[i]);
		}
	}
}

//隐藏图层数据
function hideOverLayer(type){
	var data = mapOverlayers["layer-"+type];
	if(data!=null&&typeof(data)!="undefined"){
		for(var i=0;i<data.length;i++){
			map.removeOverlay(data[i]);
		}
	}
}
//删除图层数据
function deleteOverLayer(type){
	var data = mapOverlayers["layer-"+type];
	if(data!=null&&typeof(data)!="undefined"){
		for(var i=0;i<data.length;i++){
			map.removeOverlay(data[i]);
			data[i]=null;
		}
		mapOverlayers["layer-"+type].length=0;
		$("#layer-"+type).remove();
	}
}

//定位跳转
function jumpTo(lon,lat){
	var point = new BMap.Point(lon, lat);
	map.panTo(point);
}
var searchArea = null;
//绘制查询区域
function drawQueryArea(lon,lat,r){
	var point = new BMap.Point(lon, lat); // 创建点坐标
	searchArea = new BMap.Circle(point, r, {
		fillColor: "blue",
		strokeWeight: 1,
		fillOpacity: 0.1,
		strokeOpacity: 0.6
	});
	map.addOverlay(searchArea);
}
//清除查询区域
function clearQueryArea(){
	map.removeOverlay(searchArea);
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
//业务图层数据数组
var layerMarkerArray = [];
//显示业务图层数据
function showLayerData(data,type){
	//clearLayerMarkers();
	//clearJobLines();
	//clearPoiMarkers();
	//clearQueryArea();
	var count = data.length;
	var record = null;
	var lon = null;
	var lat  = null;
	for(var i=0;i<count;i++){
		record = data[i];
		if(record.longitude!=""&&record.latitude!=""){
			if(type=="t_shortmsg"){
				var points = getLonlatByMileage(record.kslcs,record.jslcs);
				var point1 = points[0];
				var point2 = points[1];
				var lineno = point1.linenum;
				var lon1 = parseFloat(point1.longitude);
				var lat1 = parseFloat(point1.latitude);
				var lon2 = parseFloat(point2.longitude);
				var lat2 = parseFloat(point2.latitude);
				var x1 = 0;
				var y1 = 0;
				var x2 = 0;
				var y2 = 0;
				if(lon1<lon2){
					x1 = lon1;
					y1 = lat1;
					x2 = lon2;
					y2 = lat2;
				}else{
					x1 = lon2;
					y1 = lat2;
					x2 = lon1;
					y2 = lat1;
				}
				var linepoints = [{x:x1,y:y1},{x:x2,y:y2}];
				drawJobLine(linepoints,record);
			}else{
				var mk = addLayerMarker(record,type);
				layerMarkerArray.push(mk);
			}
		}
	}
}
//周边查询-显示业务图层数据
function showLayerMarkers(data){
	clearLayerMarkers();
	clearJobLines();
	var count = data.length;
	var record = null;
	var lon = null;
	var lat  = null;
	var type = "";
	for(var i=0;i<count;i++){
		record = data[i];
		if(record.longitude!=""&&record.latitude!=""){
			if(record.layer=="t_shortmsg"){
				var points = getLonlatByMileage(record.kslcs,record.jslcs);
				var point1 = points[0];
				var point2 = points[1];
				var lineno = point1.linenum;
				var lon1 = parseFloat(point1.longitude);
				var lat1 = parseFloat(point1.latitude);
				var lon2 = parseFloat(point2.longitude);
				var lat2 = parseFloat(point2.latitude);
				var x1 = 0;
				var y1 = 0;
				var x2 = 0;
				var y2 = 0;
				if(lon1<lon2){
					x1 = lon1;
					y1 = lat1;
					x2 = lon2;
					y2 = lat2;
				}else{
					x1 = lon2;
					y1 = lat2;
					x2 = lon1;
					y2 = lat1;
				}
				var linepoints = [{x:x1,y:y1},{x:x2,y:y2}];
				//var linepoints = getJobLinePoints(lineno,x1,y1,x2,y2);
				//alert(linepoints);
				drawJobLine(linepoints,record);
			}else{
				var mk = addLayerMarker(record,record.layer);
				layerMarkerArray.push(mk);
			}
		}
	}
}
//加载单个标注数据
function addLayerMarker(info,type){
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
	var columns = getShowColumnObject(info.showcolumns);
	var infohtml = getPopupInfoHtml(info,columns);
	var infoWindow = new BMap.InfoWindow(infohtml);//创建信息窗口对象
	infoWindow.dataid = info.dataid;
	map.addOverlay(marker);
	var label = new BMap.Label('<label style="padding:5px;">'+info[viewColumn]+'</label>',{offset:new BMap.Size(left,top)});
	label.setStyle(markerStyle);
	label.layertype = type;
	marker.setLabel(label);
	marker.addEventListener("click", function(){          
	   this.openInfoWindow(infoWindow);
	});
	return marker;
}
//获取显示的字段对象
function getShowColumnObject(showcolumns){
	var columns = [];
	var tmpArr = showcolumns.split(";");
	var count = tmpArr.length;
	var tArr = null;
	for(var i=0;i<count;i++){
		tArr = tmpArr[i].split(":");
		columns.push({field:tArr[0],text:tArr[1]});
	}
	return columns;
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
	var html = sb.toString();
	return html;
}
//清除指定的标注点
function clearLayerTyoeMarkers(type){
	var allOverlay = map.getOverlays();
	for (var i = 0; i < allOverlay.length - 1; i++){
		if(allOverlay[i].getLabel().content == "我是id=1"){
			map.removeOverlay(allOverlay[i]);
			return false;
		}
	}
}

//清除业务图层数据
function clearLayerMarkers(){
	for(var i=0;i<layerMarkerArray.length;i++){
		map.removeOverlay(layerMarkerArray[i]);
		layerMarkerArray[i]=null;
	}
	layerMarkerArray.length = 0;
}

//POI标注点数组
var poiMarkerArray = [];
//显示查询结果POI点
function showPoiMarkers(data){
	clearPoiMarkers();
	var count = data.length;
	var poimk = null;
	for(var i=0;i<count;i++){
		poimk = addPoiMarker(data[i]);
		poiMarkerArray.push(poimk);
	}
}
//添加POI标注
function addPoiMarker(record){
	var lon = record.location.lng;
	var lat = record.location.lat;
	var point = new BMap.Point(lon,lat);
	var poimk = new BMap.Marker(point);
	map.addOverlay(poimk);
	var label = new BMap.Label("<label style='padding:4px;font-weight:bold;'>"+record.name+"</label>",{offset:new BMap.Size(20,0)});
	poimk.setLabel(label);
	var infohtml = getPoiPopupInfoHtml(record);
	var infoWindow = new BMap.InfoWindow(infohtml);//创建信息窗口对象
	infoWindow.dataid = record.uid;
	poimk.addEventListener("click", function(){          
	   this.openInfoWindow(infoWindow);
	});
	return poimk;
}

//获取气泡详细信息
function getPoiPopupInfoHtml(record){
	var sb = new Array();
	sb.push('<div class="myPopupInfo" style="border-bottom:2px solid #DDDDDD;padding:2px;font:12px 微软雅黑;color:#2D3BAE;font-weight:bold;">'+record.name+'</div>');
	var telephone = "";
	if(record.telephone){
		telephone = record.telephone;
	}
	sb.push('<table class="info-table" cellpadding="0" cellspacing="0" style="width:100%;">');
	sb.push('<tr><th>联系电话：</th><td>'+telephone+'</td></tr>');
	sb.push('<tr><th vAlign="top">详细地址：</th><td>'+record.address+'</td></tr>');
	if(record.detail_info.detail_url){
		sb.push('<tr><td colspan="2"><a href="'+record.detail_info.detail_url+'" target="_blank">点击查看详细信息</a></td></tr>');
	}
	sb.push('</table>');
	var html = sb.join("");
	return html;
}

//清除所有POI标注点
function clearPoiMarkers(){
	for(var i=0;i<poiMarkerArray.length;i++){
		map.removeOverlay(poiMarkerArray[i]);
		poiMarkerArray[i]=null;
	}
	poiMarkerArray.length = 0;
}

//根据作业里程获取铁路线坐标点
function getJobLinePoints(lineno,x1,y1,x2,y2){
	var points = [];
	points.push({x:x1,y:y1});
	var data = cache["railway-points"];
	var count = data.length;
	var rcd = null;
	var x = 0;
	var y = 0;
	for(var i=0;i<count;i++){
		rcd = data[i];
		x = parseFloat(rcd.longitude);
		y = parseFloat(rcd.latitude);
		if(rcd.linenum==lineno){
			if(x>=x1&&x<=x2&&y>=y1&&y<=y2){
				points.push({x:x,y:y});
			}
		}
	}
	points.push({x:x2,y:y2});
	return points;
}
//工作里程线路
var jobPolygonLines = [];
//根据线路坐标点绘制作业线路
function drawJobLine(points,record){
	var count = points.length;
	var pntArr = [];
	var point = null;
	var bdpoint = null;
	for(var i=0;i<count;i++){
		point = points[i];
		bdpoint = PointConvert.wgs84tobd09(parseFloat(points[i].x),parseFloat(points[i].y));
		pntArr.push(new BMap.Point(bdpoint.lon, bdpoint.lat));
	}
	var line = new BMap.Polyline(pntArr, {strokeColor:"red", strokeWeight:6, strokeOpacity:0.8});   //创建折线
  	map.addOverlay(line);
  	var showcolumns = record.showcolumns;
  	//var columnstr = webconfigJson["t_shortmsg_showcolumns"];
	var columns = getShowColumnObject(showcolumns);
	var infohtml = getPopupInfoHtml(record,columns);
	var infoWindow = new BMap.InfoWindow(infohtml);//创建信息窗口对象
	//infoWindow.dataid = record.dataid;
  	jobPolygonLines.push(line);
  	bdpoint = PointConvert.wgs84tobd09(parseFloat(points[0].x),parseFloat(points[0].y));
  	var point = new BMap.Point(bdpoint.lon, bdpoint.lat);
  	var myIcon = new BMap.Icon("images/icon-start.png", new BMap.Size(parseInt(36,10),parseInt(36,10)));
	marker = new BMap.Marker(point,{icon:myIcon});
	map.addOverlay(marker);
	marker.addEventListener("click", function(){
  		this.openInfoWindow(infoWindow);
 	});
	jobPolygonLines.push(marker);
	bdpoint = PointConvert.wgs84tobd09(parseFloat(points[count-1].x),parseFloat(points[count-1].y));
  	point = new BMap.Point(bdpoint.lon, bdpoint.lat);
	myIcon = new BMap.Icon("images/icon-end.png", new BMap.Size(parseInt(36,10),parseInt(36,10)));
	marker = new BMap.Marker(point,{icon:myIcon});
	map.addOverlay(marker);
	marker.addEventListener("click", function(){
  		this.openInfoWindow(infoWindow);
 	});
	jobPolygonLines.push(marker);
}
//清除线路
function clearJobLines(){
	for(var i=0;i<jobPolygonLines.length;i++){
		map.removeOverlay(jobPolygonLines[i]);
		jobPolygonLines[i]=null;
	}
	jobPolygonLines.length = 0;
}
//点击进行定位
function clickRowAction(index,row){
	if(row.layertable=="t_shortmsg"){
		var points = getLonlatByMileage(row.kslcs,row.jslcs);
		if(points.length>0){
			row.longitude = points[0].longitude;
			row.latitude = points[0].latitude;
		}
	}
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
var circleMarker = null;
//在地图上添加选点事件
function initClickPointEvent(){
	map.addEventListener("click", setQueryCenterPoint);
}
//注销点击事件
function removeClickPointEvent(){
	map.removeEventListener("click", setQueryCenterPoint);
}
//点击回调事件
function setQueryCenterPoint(e){
	removeClickPointEvent();
	clearCircleMarker();
	var lon = e.point.lng;
	var lat = e.point.lat;
	top.queryCirclePoint = {lon:lon,lat:lat};
	var point = new BMap.Point(lon, lat);
	circleMarker = new BMap.Marker(point);// 创建标注
	map.addOverlay(circleMarker);
	window.frames["queryframe"].setClickPoint(lon,lat);
}
//清除定位点
function clearCircleMarker(){
	map.removeOverlay(circleMarker);
}

/**
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
*/
/**
//根据圆形范围查询周边信息
function queryByCircle(lon,lat,r){
	var point = new BMap.Point(lon, lat); // 创建点坐标
	var options = {
		renderOptions: {
			map: map
		},
		pageCapacity:100,
		onSearchComplete: function(results) {
			console.log(results);
			//alert('Search Completed');
			//可添加自定义回调函数
		}
	};
	var localSearch = new BMap.LocalSearch(map, options);
	var circle = new BMap.Circle(point, r, {
		fillColor: "blue",
		strokeWeight: 1,
		fillOpacity: 0.1,
		strokeOpacity: 0.6
	});		
	map.addOverlay(circle);
	localSearch.searchNearby(["餐饮","银行"], point, r);
//	localSearch.searchNearby('酒店', point, r);
//	localSearch.searchNearby('银行', point, r);
//	localSearch.searchNearby('娱乐', point, r);
//	localSearch.searchNearby('购物', point, r);
//	localSearch.searchNearby('出行', point, r);
//	localSearch.searchNearby('生活', point, r);
	//localSearch.searchNearby('景点', point, r);
}
*/
