/**
 * 主要脚本方法
 */
$(function(){
	$("#tbForm td").css("padding", "10px");
	$(window).resize(function() {
		fitPage();
	});
	loadLayerInfo();
	fitPage();
});

//页面自适应
function fitPage(){
	$("#layout").layout();
	$("#dg").datagrid({
		title:"查询结果数据列表",
		fit:true,
		striped:true,
		singleSelect:true,
		remoteSort:false,
		loadMsg:"正在加载数据，请稍候……",
		onClickRow:function(index,row){
			row.longitude = row.lon;
			row.latitude = row.lat;
			top.clickRowAction(index,row);
		}
	}).datagrid("loadData",[]);
}
//加载业务图层分类信息
function loadLayerInfo(){
	doAjax("../json/DataService!listAll",{method:"queryLayerInfo"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["layerInfo"]=data;
		}
	});
}

var isFirstClickPoint = true;
//添加在地图上选点事件
function addClickEvent(){
	if(isFirstClickPoint){
		top.layer.msg("请在地图上点击选择坐标点！");
		isFirstClickPoint = false;
	}
	top.initClickPointEvent();
}
//显示选择的坐标点
function setClickPoint(lon,lat){
	$("#pointinfo").html("x:"+lon+",y:"+lat);
}

var queryCircleRadiu = 1000;
//周边查询方法
function queryNearData(){
	var radius = $("#radius").val();
	if(radius==""){
		top.layer.msg("请输入查询半径！");
		return;
	}else{
		var r = parseFloat(radius);
		var isNumber = isNaN(r);
		if(isNumber){
			top.layer.msg("请输入数值！");
			return;
		}else{
			if(r>150000){
				top.layer.msg("查询半径不能超过150公里！");
				return;
			}
		}
	}
	var cx = null;
	var cy = null;
	if(top.queryCirclePoint){
		cx = top.queryCirclePoint.lon;
		cy = top.queryCirclePoint.lat;
	}
	if(cx==null || cy==null){
		top.layer.msg("请先在地图上选择查询范围的中心坐标点！");
	}
	//清除所有区域
	clearQueryNearData();
	queryCircleRadiu = parseInt(radius);
	//添加圆心标注点
	top.addClickPointMarker();
	//绘制查询圆形区域
	top.drawQueryArea(cx,cy,parseInt(radius));
	getNearLayerData(cx,cy,parseInt(radius));
	return;
}
//查询区域范围内的数据
function getNearLayerData(cx,cy,r){
	var allMapOverLayers = top.mapOverlayers;
	var alldata = [];
	var layerdata = [];
	for(var layertype in allMapOverLayers){
		layerdata = allMapOverLayers[layertype];
		alldata = alldata.concat(layerdata);
	}
	var newdata = [];
	var t = null;
	var lon = null;
	var lat = null;
	var distance = 0;
	for(var i=0;i<alldata.length;i++){
		t = alldata[i];
		if(t.info){
			lon = t.info.lon;
			lat = t.info.lat;
			distance = top.getPointDistance(cx,cy,lon,lat);
			//console.log(cx+","+cy+"="+lon+","+lat+"==="+distance+",,"+r,"distance");
			if(distance<=r){
				t.info.layername = getLayerName(t.info.layertype);
				t.info.layertable = t.info.layertype;
				newdata.push(t.info);
			}
		}
	}
	$("#dg").datagrid("loadData",newdata);
}
/**
 * 根据业务图层类型查询图层名称
 * @param layertype 图层类型
 */
function getLayerName(layertype){
	var layerName = "";
	var data = cache["layerInfo"];
	var count = data.length;
	for(var i=0;i<count;i++){
		if(data[i].tablename==layertype){
			layerName = data[i].name;
			break;
		}
	}
	return layerName;
}

//清除周边查询事件
function clearQueryNearData(){
	top.clearCircleMarker();
	top.clearQueryArea();
	$("#pointinfo").html("");
	$("#dg").datagrid("loadData",[]);
}