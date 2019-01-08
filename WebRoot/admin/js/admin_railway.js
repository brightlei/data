var map = null;
$(function(){
	$("#formWin").css("display","none");
});

//页面加载完成事件
function onloadComplete(){
	initMap();
	loadLineData();
	initDataTable();
	$(".textbox-label").css("text-align","right");
}
var selectLineNo = null;
//加载铁路线数据
function loadLineData(){
	doAjax("../json/DataService!listAll",{method:"listRailwayLine"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["linedata"]=data;
			$("#line").combobox({
				onChange:function(newValue, oldValue){
					selectLineNo = newValue;
					loadPointData();
					loadMileagePointData();
				}
			}).combobox("loadData",data);
			if(data.length>0){
				$("#line").combobox("setValue",data[0].linenum);
			}
		}else{
			top.showAlertMsg("获取铁路线路失败！", 6, function() {
				//top.layer.close();
			});
		}
	});
}

//初始化数据表格
function initDataTable(){
	$("#dg").datagrid({
		title:"铁路线坐标点信息列表",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		remoteSort:false,
		toolbar:'#tb',
		closable:true,
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
		    {field:'id',title:'ID',hidden:true,width:'100',align:"center"},
			{field:'longitude',title:'经度',width:'100',align:"center"},
			{field:'latitude',title:'纬度',width:'100',align:"center"}
		]],
		onBeforeClose:function(){
			$(".dgbox").slideUp("fast");
			return false;
		},
		onClickRow:function(index, row){
			showPopupInfo(row.longitude,row.latitude,row.id);
		},
		onLoadSuccess:function(data){
			
		}
	});
}

//加载铁路线里程坐标点数据
function loadMileagePointData(){
	doAjax("../json/DataService!listAll",{method:"listLineMileagePoint",linenum:selectLineNo},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			showMileagePoints(data);
		}else{
			top.showAlertMsg("获取铁路线里程坐标点失败！", 6, function() {
				//top.layer.close();
			});
		}
	});
}
//加载铁路线坐标点数据
function loadPointData(){
	$("#dg").datagrid("loading");
	doAjax("../json/DataService!listAll",{method:"getRailwayLinePoint",lineno:selectLineNo},{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			$("#dg").datagrid("loadData",data);
			$("#pointCount").html(data.length);
			var record = null;
			clearMarkers();
			for(var i=0;i<data.length;i++){
				record = data[i];
				addPointMarker(record.longitude,record.latitude,record.id);
			}
		}else{
			top.showAlertMsg("获取铁路线坐标点失败！", 6, function() {
				//top.layer.close();
			});
		}
	});
}

var loadLayerIndex = 0;
//提交保存数据
function saveOrUpdateRailway(){
	$('#myform').form('submit', {
	    url:"../json/DataService!saveData",
	    onSubmit: function(param){
	    	param.method="addRailwayLine";
	    	var id = $("#id").val();
	    	if(id!=""){
	    		param.method="editRailwayLine";
	    	}
	    	var isValid = $(this).form('validate');
	    	if(isValid){
	    		loadLayerIndex = top.showLayerLoading("正在提交数据，请稍候……");
	    	}
	    	return isValid;
	    },
	    success:function(jsonstr){
	    	top.hideloadLayer(loadLayerIndex);
	    	var rsMap = eval("("+jsonstr+")");
	    	var id = $("#id").val();
	    	var opmsg = "添加";
	    	if(id!=""){
	    		opmsg = "修改";
	    	}
	        if(rsMap.state){
	        	layer.close(layerIndex);
	        	loadLineData();
	        	top.layer.msg(opmsg+"铁路线成功！",{icon:6});
	        }else{
	        	top.layer.msg(opmsg+"铁路线失败！"+rsMap.error,{icon:5});
	        }
	    }    
	});
}
//添加线路事件
function addLineAction(){
	$("#name").textbox("setValue","");
	$("#linenum").textbox("setValue","");
	$("#id").val("");
	openLayerWin("添加铁路线路");
}
//修改铁路线路
function editLineAction(){
	var data = cache["linedata"];
	var record = null;
	for(var i=0;i<data.length;i++){
		record = data[i];
		if(record.id==selectLineNo){
			break;
		}
	}
	$("#name").textbox("setValue",record.name);
	$("#linenum").textbox("setValue",record.linenum);
	$("#id").val(record.id);
	openLayerWin("修改铁路线路");
}
//删除铁路线
function deleteLineAction(){
	layer.confirm('确定要删除该条铁路线路吗？删除之后该线路上的坐标点也会被删除！', {icon: 3, title:'提示'}, function(index){
		layerIndex = index;
		layer.close(layerIndex);
		doAjax("../json/DataService!editData",{method:"deleteRailway",lineno:selectLineNo},{},function(rsMap,op){
			$("#dg").datagrid("loaded");
			if(rsMap.state){
				top.layer.msg("铁路线删除成功！",{icon:6});
				loadLineData();
			}else{
				top.showAlertMsg("删除铁路线失败！", 6, function() {
					//top.layer.close();
				});
			}
		});
	});
}
//弹出层
var layerIndex=null;
function openLayerWin(title){
	layer.open({
		type : 1,
		title : [title,'height:40px;background:#f7f7f7;border-width:0px' ],
		area : ['420px' ],
		content : $('#formWin'),
		btn : ['保存数据', '取消' ],
		yes : function() {
			saveOrUpdateRailway();
		},
		btn2 : function() {
			layer.close(layerIndex);
		},
		success : function(layero, index) {
			layerIndex = index;
		}
	});
}

// 添加按钮事件
function startAddAction(){
	if(selectLineNo==null||selectLineNo==""){
		top.layer.msg("请先创建铁路线路！",{icon:0});
		return;
	}
	map.addEventListener("click", clickEventCallback);
	map.setDefaultCursor("crosshair");
}
//结束添加事件
function stopAddAction(){
	map.removeEventListener("click", clickEventCallback);
	map.setDefaultCursor("url('bird.cur')");
}
var isClickMarker = false;
//点击回调事件
function clickEventCallback(e){
	if(isClickMarker){
		return;
	}
	savePoint(e.point.lng,e.point.lat);
}
//保存铁路线坐标点
function savePoint(lon,lat){
	loadLayerIndex = top.showLayerLoading("正在保存坐标点数据，请稍候……");
	doAjax("../json/DataService!saveData",{method:"addRailwayPoint",lineno:selectLineNo,lon:lon,lat:lat},{},function(rsMap,op){
		top.hideloadLayer(loadLayerIndex);
		if(rsMap.state){
			var newid = rsMap.datakey;
			$('#dg').datagrid('appendRow',{
				id:newid,
				longitude:lon,
				latitude:lat
			});
			var pointCount = $("#pointCount").text();
			$("#pointCount").html(parseInt(pointCount)+1);
			addPointMarker(lon,lat,newid);
		}else{
			top.showAlertMsg("添加铁路线坐标点失败！", 6, function() {
				//top.layer.close();
			});
		}
	});
}
//里程坐标点数组
var mileagePntArr = [];
function showMileagePoints(data){
	clearMileagePoints();
	var count = data.length;
	var record = null;
	var marker = null;
	pointMarkers = [];
	var lonlat = null;
	for(var i=0;i<count;i++){
		record = data[i];
		lonlat = PointConvert.wgs84tobd09(parseFloat(record.longitude),parseFloat(record.latitude));
		marker = addMarker(lonlat.lon,lonlat.lat,record.mileage);
		mileagePntArr.push(marker);
	}
	if(count>0){
		map.centerAndZoom(new BMap.Point(lonlat.lon,lonlat.lat), 11);
	}
}
//在地图上添加标注点
function addMarker(lon,lat,text){
	 var point = new BMap.Point(lon,lat);
	 // 创建标注对象并添加到地图
	 var marker = new BMap.Marker(point);    
	 map.addOverlay(marker);
	 var label = new BMap.Label(text,{offset:new BMap.Size(20,0)});
	 marker.setLabel(label);
	 return marker;
}
//清除里程坐标点
function clearMileagePoints(){
	for(var i=0;i<mileagePntArr.length;i++){
		map.removeOverlay(mileagePntArr[i]);
	}
	mileagePntArr.length=0;
}

var pntArr = [];
//添加点标注
function addPointMarker(lon,lat,id){
	var pnt = new BMap.Point(lon, lat);
	var myIcon = new BMap.Icon("../images/icon/green_20.png", new BMap.Size(20,20));
	var marker = new BMap.Marker(pnt,{icon:myIcon});
	map.addOverlay(marker);
	marker.addEventListener("click", function(){
		isClickMarker = true;
		showPopupInfo(lon,lat,id);
		setTimeout(function(){
			isClickMarker = false;
		},100);
	});
	pntArr.push(marker);
}
//显示气泡
function showPopupInfo(lon,lat,id){
	var pnt = new BMap.Point(lon, lat);
	var opts = {
			width:100,
			height:50,
			title:"坐标点信息"
	}
	var sb = new Array();
	sb.push('<div>');
	sb.push('<span>经度：'+lon+'<br/>纬度：'+lat+'</span>');
	sb.push('<span style="position:absolute;left:130px;top:28px;"><input type="button" value="删除该点" onclick="deletePoint(\''+id+'\')"/>');
	sb.push('</div>');
	var text = sb.join("");
	var infoWindow = new BMap.InfoWindow(text,opts);
	map.openInfoWindow(infoWindow,pnt);
}

//删除指定点
function deletePoint(id){
	layer.confirm('确定要删除该坐标点吗？', {icon: 3, title:'提示'}, function(index){
		layerIndex = index;
		layer.close(layerIndex);
		loadLayerIndex = top.showLayerLoading("正在删除坐标点数据，请稍候……");
		doAjax("../json/DataService!editData",{method:"deleteRailwayPoint",id:id},{},function(rsMap,op){
			top.hideloadLayer(loadLayerIndex);
			if(rsMap.state){
				top.layer.msg("坐标点删除成功！",{icon:6});
				var rows = $("#dg").datagrid("getRows");
				var index = 0;
				for(var i=0;i<rows.length;i++){
					if(rows[i].id==id){
						index = i;
						break;
					}
				}
				$("#dg").datagrid("deleteRow",index);
				map.removeOverlay(pntArr[index]);
				pntArr.splice(index,1);
				map.closeInfoWindow();
				var pointCount = $("#pointCount").text();
				$("#pointCount").html(parseInt(pointCount)-1);
			}else{
				top.showAlertMsg("坐标点删除失败！", 6, function() {
					//top.layer.close();
				});
			}
		});
	});
}

//清除点标注
function clearMarkers(){
	for(var i=0;i<pntArr.length;i++){
		map.removeOverlay(pntArr[i]);
	}
	pntArr.length=0;
}