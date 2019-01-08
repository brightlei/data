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
	//loadConfigs();
	//loadRailwayLinePoints();
}



/**
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
	doAjax("json/DataService!listAll",{method:"listLinePoint"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["railway-points"]=data;
		}
	});
}

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


//显示GPS模块
function showGpsData(data){
	var count = data.length;
	var rcd = null;
	for(var i=0;i<count;i++){
		rcd = data[i];
		var pt = new BMap.Point(parseFloat(rcd.lon),parseFloat(rcd.lat));
		var myIcon = new BMap.Icon("images/people.png", new BMap.Size(48,48));
		var marker = new BMap.Marker(pt,{icon:myIcon});
		map.addOverlay(marker);
		var label = new BMap.Label("<label style='padding:5px 5px;font-weight:bold;'>"+rcd.xm+"</label>",{offset:new BMap.Size(0,48)});
		marker.setLabel(label);
	}
	var point = new BMap.Point(parseFloat(rcd.lon), parseFloat(rcd.lat));
	map.panTo(point);
}
var ztArr = ["在岗","离岗","执勤中"];
//定位显示模块
var GPS={
	showWin:function(){
		$("#gpsWin").window({left:5,top:100}).window("open");
		GPS.initDataTable();
	},
	initDataTable:function(){
		$("#dg").datagrid({
			rownumbers:true,
			singleSelect:true,
			autoRowHeight:false,
			striped:true,
			nowrap:false,
			fit:true,
			pagination:true,
			loadMsg:"正在加载数据，请稍候……",
			columns:[[    	       
				{field:'xm',title:'姓名',width:'70',align:'left'},
				{field:'zc',title:'职称',width:'90',align:'left'},
				{field:'bm',title:'部门',width:'120',align:'left'},
				{field:'zt',title:'状态',width:'80',align:'center',
				formatter:function(value,row,index){
					return '<span style="font-weight:bold;color:blue;">'+ztArr[parseInt(value,10)]+'</span>';
				}}
			]],
			onClickRow:function(index, row){
				var point = new BMap.Point(parseFloat(row.lon), parseFloat(row.lat));
				map.panTo(point);
			}
		});
		GPS.loadData();
	},
	loadData:function(){
		var data = [];
		data.push({xm:"张三丰",zc:"科员",bm:"信息化",zt:"0",lon:113.24,lat:30.23,imgurl:""});
		data.push({xm:"刘德华",zc:"科员",bm:"信息化",zt:"0",lon:113.54,lat:30.33,imgurl:""});
		data.push({xm:"赵大宝",zc:"科员",bm:"信息化",zt:"1",lon:113.74,lat:30.53,imgurl:""});
		data.push({xm:"胡三军",zc:"科员",bm:"信息化",zt:"1",lon:113.64,lat:30.29,imgurl:""});
		data.push({xm:"叶家队",zc:"科员",bm:"信息化",zt:"2",lon:113.44,lat:30.63,imgurl:""});
		data.push({xm:"陈家英",zc:"科员",bm:"信息化",zt:"2",lon:113.61,lat:30.43,imgurl:""});
		$("#dg").datagrid("loadData",data);
		showGpsData(data);
	},
	showDataOnMap:function(){
		
	}
};
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
/ ***固定信息显示开始**** /
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
	var columnstr = webconfigJson[type+"_showcolumns"];
	var columns = eval("("+columnstr+")");
	var infohtml = getPopupInfoHtml(info,columns);
	var infoWindow = new BMap.InfoWindow(infohtml);//创建信息窗口对象
	infoWindow.dataid = info.dataid;
	map.addOverlay(marker);
	var label = new BMap.Label('<label style="padding:5px;">'+info[viewColumn]+'</label>',{offset:new BMap.Size(left,top)});
	label.setStyle(markerStyle);
	marker.setLabel(label);
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