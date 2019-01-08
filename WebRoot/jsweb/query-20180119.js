/**
 * 主要脚本方法
 */
$(function(){
	$("#tbForm td").css("padding", "10px");
	loadData();
	$(window).resize(function() {
		fitPage();
	});
});

function initPager(id){
	var pager = $("#"+id).datagrid("getPager");
	pager.pagination({layout:['first','prev','manual','next','last','refresh']});
}

//页面自适应
function fitPage(){
	$("#layout").layout();
	$("#result-panel").panel({fit:true});
	//$("#dg").datagrid({fit:true});
	//$("#dg-bd").datagrid({fit:true});
}

//加载数据
function loadData(){
	showLoading("正在加载数据，请稍候......");
	doAjax("../json/DataService!listAll",{method:"queryLayerInfo"},{},function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			data.push({id:"BAIDU-POI-1",code:"BD-00",name:"百度POI信息点",pcode:"0",type:"",iconurl:""});
			data.push({id:"BAIDU-POI-2",code:"BD-0001",name:"餐饮",pcode:"BD-00",type:"",iconurl:""});
			data.push({id:"BAIDU-POI-3",code:"BD-0002",name:"酒店",pcode:"BD-00",type:"",iconurl:""});
			data.push({id:"BAIDU-POI-4",code:"BD-0003",name:"银行",pcode:"BD-00",type:"",iconurl:""});
			data.push({id:"BAIDU-POI-5",code:"BD-0004",name:"休闲娱乐",pcode:"BD-00",type:"",iconurl:""});
			data.push({id:"BAIDU-POI-6",code:"BD-0005",name:"购物",pcode:"BD-00",type:"",iconurl:""});
			//data.push({id:"BAIDU-POI-7",code:"BD-0006",name:"出行",pcode:"BD-00",type:"",iconurl:""});
			//data.push({id:"BAIDU-POI-8",code:"BD-0007",name:"生活",pcode:"BD-00",type:"",iconurl:""});
			//data.push({id:"BAIDU-POI-9",code:"BD-0008",name:"景点",pcode:"BD-00",type:"",iconurl:""});
			cache["treeData"]=data;
			initTree();
		}else{
			
		}
	});
}

//创建树结构
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#menuTree").tree({
		animate:true,
		lines:true,
		checkbox:true
	});
	$("#menuTree").tree("loadData",treeData);
}
//获取树节点孩子节点
function getTreeChildren(pcode){
	var treeArr = new Array();
	var treeData = cache["treeData"];
	var count = treeData.length;
	for(var i=0;i<count;i++){
		var record = treeData[i];
		if(record.pcode==pcode){
			var treeNode = new Object();
			treeNode.id = record.id;
			treeNode.code = record.code;
			treeNode.text = record.name;
			treeNode.pcode = record.pcode;
			treeNode.type = record.type;
			treeNode.tablename = record.tablename;
			treeNode.syscolumns = record.syscolumns;
			treeNode.showcolumns = record.showcolumns;
			treeNode.template = record.templatepath;
			treeNode.iconurl = record.iconurl;
			treeNode.children = getTreeChildren(record.code);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}
//返回到条件输入框
function goback(){
	$("#formdiv").show();
	$("#resultdiv").hide();
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
	top.clearQueryArea();
	top.clearLayerMarkers();
	top.clearPoiMarkers();
	top.clearJobLines();
	$("#dg").datagrid("loadData",[]);
	$("#dg-bd").datagrid("loadData",[]);
	//绘制查询圆形区域
	top.drawQueryArea(cx,cy,parseInt(radius));
	var nodes = $("#menuTree").tree("getChecked","checked");
	if(nodes.length==0){
		top.layer.msg("请选择你要查询的数据！",{icon:0});
		return;
	}
	var bddata = [];
	var node = null;
	var layers = [];
	var pcode = "";
	for(var i=0;i<nodes.length;i++){
		node = nodes[i];
		console.log(node);
		pcode = node.pcode;
		if(pcode=="BD-00"){
			bddata.push(node.text);
		}else if(pcode=="2"||pcode=="3"||pcode=="4"){
			layers.push(node.tablename);
		}
	}
	var layercount = layers.length;
	$("#formdiv").hide();
	$("#resultdiv").show();
	$("#result-panel").panel({fit:true});
	if(layercount>0){
		queryDataLayer(cx,cy,radius,layers);
		$("#result").tabs("select","业务图层数据");
	}
	if(bddata.length>0){
		queryBaiduLayer(cx,cy,radius,bddata);
		$("#result").tabs("select","百度POI数据");
	}
	initPager("dg");
	initPager("dg-bd");
}

//查询业务数据图层
function queryDataLayer(cx,cy,radius,layers){
	var dataurl = "../json/Layer!queryNearLayerData?lon="+cx+"&lat="+cy+"&r="+radius+"&queryInfo="+layers.join(",");
	$("#dg").datagrid({
		fit:true,
		url:dataurl,
		pageNumber:1,
		pageSize:10,
		onClickRow:function(index, row){
			var lon = row.longitude;
			var lat = row.latitude;
			top.jumpTo(lon,lat);
		},
		onLoadSuccess:function(json){
			var rows = json.rows;
			console.log(rows);
			top.showLayerMarkers(rows);
		}
	});
}
//查询百度数据图层
function queryBaiduLayer(cx,cy,radius,bddata){
	var keywords = bddata.join("$");
	var dataurl = "../json/Layer!queryNearPoiData?lon="+cx+"&lat="+cy+"&r="+radius+"&keywords="+keywords;
	$("#dg-bd").datagrid({
		fit:true,
		url:dataurl,
		pageNumber:1,
		pageSize:10,
		onClickRow:function(index, row){
			var lon = row.location.lng;
			var lat = row.location.lat;
			top.jumpTo(lon,lat);
		},
		onLoadSuccess:function(json){
			var rows = json.rows;
			top.showPoiMarkers(rows);
		}
	});
}

//操作字段格式化
function opFormatter(value,row,index){
	var lon = row.location.lng;
	var lat = row.location.lat;
	return '<a href="javascript:void(0)" lon="'+lon+'",lat="'+lat+'" style="color:orange;font-weight:bold;">定位</a>';
}

//百度POI图层分类
function bdLayerFormatter(value,row,index){
	var layer = row.detail_info.tag;
	return layer;
}
//清除周边查询事件
function clearQueryNearData(){
	top.clearCircleMarker();
	top.clearQueryArea();
	top.clearLayerMarkers();
	top.clearPoiMarkers();
	top.clearJobLines();
	$("#pointinfo").html("");
}