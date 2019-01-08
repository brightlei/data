var map = null;
$(function(){
	$("#tbForm td").css("padding","10px 5px");
});

//页面加载完成事件
function onloadComplete(){
	initDataTable();
	loadData();
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
		//closed:true,
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'lonstr',title:'经度(度分秒)',width:'110',align:"center"},
			{field:'latstr',title:'纬度(度分秒)',width:'110',align:"center"},
			{field:'mileage',title:'里程',width:'100',align:"center"},   
			{field:'name',title:'线名',width:'100',align:"center"},   
			{field:'linenum',title:'线号',width:'80',align:"center"},   
			{field:'direction',title:'方向',width:'80',align:"center"},   
			{field:'longchain',title:'长链',width:'110',align:"center"},
			{field:'longitude',title:'经度',width:'100',align:"center"},
			{field:'latitude',title:'纬度',width:'100',align:"center"},
			{field:'createtime',title:'创建时间',width:'170',align:"center"}
		]]
	});
}
//加载铁路线坐标点数据
function loadData(){
	$("#dg").datagrid("loading");
	var param = {method:"listLinePoint"};
	doAjax("../json/DataService!listAll",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			$("#dg").datagrid("loadData",data);
			cache["data"] = data;
		}else{
			top.showAlertMsg("获取铁路线坐标点失败！", 6, function() {
				//top.layer.close();
			});
		}
		savelog("铁路线管理","查询数据",JSON.stringify(param));
	});
}
//地图上显示坐标点
var pointMarkers = [];
function showPointOnMap(){
	var data =cache["data"];
	var count = data.length;
	var record = null;
	var marker = null;
	pointMarkers = [];
	var lonlat = null;
	for(var i=0;i<count;i++){
		record = data[i];
		lonlat = PointConvert.wgs84tobd09(parseFloat(record.longitude),parseFloat(record.latitude));
		marker = addMarker(lonlat.lon,lonlat.lat,record.mileage);
		pointMarkers.push(marker);
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

/**
 * 度分秒转经纬度
 */
function dmfTojwd(old_obj,dest_obj){
	var dfmstr = $("#"+old_obj).textbox("getValue");
	var lonlat = degreenToNumLonlat(dfmstr);
	$("#"+dest_obj).textbox("setValue", lonlat);
}
/**
 * 经纬度转度分秒
 */
function jwdTodfm(old_obj,dest_obj){
	var lonlat = $("#"+old_obj).textbox("getValue");
	var dfmstr = numToDegreenLonlat(lonlat);
	$("#"+dest_obj).textbox("setValue", dfmstr);
	if(old_obj=="longitude"){
		dfmstrIntoInput(dfmstr,"lon");
	}else{
		dfmstrIntoInput(dfmstr,"lat");
	}
}
/**
 * 度分秒字符串分解填入表单
 */
function dfmstrIntoInput(dfmstr,type){
	var dindex = dfmstr.indexOf("°");
	var findex = dfmstr.indexOf("′");
	var mindex = dfmstr.indexOf("″");
	var degree = dfmstr.substring(0,dindex);
	var minute = dfmstr.substring(dindex+1,findex);
	var second = dfmstr.substring(findex+1,mindex);
	$("#degree_"+type).val(degree);
	$("#minute_"+type).val(minute);
	$("#second_"+type).val(second);
}

/**
 * 初始化经度度分秒字符串
 */
function initLon(){
	var degree = $("#degree_lon").val();
	var minute = $("#minute_lon").val();
	var second = $("#second_lon").val();
	var dfmstr = degree+"°"+minute+"′"+second+"″";
	$("#lonstr").textbox("setValue",dfmstr);
	var lon = degreenToNumLonlat(dfmstr);
	$("#longitude").textbox("setValue", lon);
}

/**
 * 初始化纬度度分秒字符串
 */
function initLat(){
	var degree = $("#degree_lat").val();
	var minute = $("#minute_lat").val();
	var second = $("#second_lat").val();
	var dfmstr = degree+"°"+minute+"′"+second+"″";
	$("#latstr").textbox("setValue",dfmstr);
	var lat = degreenToNumLonlat(dfmstr);
	$("#latitude").textbox("setValue", lat);
}

var loadLayerIndex = 0;
//提交保存数据
function doSubmitAction(){
	var formdata = $("#myform").serializeArray();
	$('#myform').form('submit', {
	    url:"../json/DataService!saveData",
	    onSubmit: function(){
	    	var isValid = $(this).form('validate');
	    	if(isValid){
	    		loadLayerIndex = top.showLayerLoading("正在提交数据，请稍候……");
	    	}
	    	return true;
	    },
	    success:function(jsonstr){
	    	top.hideloadLayer(loadLayerIndex);
	    	var rsMap = eval("("+jsonstr+")");
	    	var id = $("#id").val();
	    	var opmsg = "添加";
	    	if(id!=""){
	    		opmsg = "修改";
	    		savelog("铁路线管理","修改数据",JSON.stringify(formdata));
	    	}else{
	    		savelog("铁路线管理","修改数据",JSON.stringify(formdata));
	    	}
	        if(rsMap.state){
	        	$("#formWin").window("close");
	        	loadData();
	        	top.layer.msg(opmsg+"铁路线坐标点成功！",{icon:6});
	        }else{
	        	top.layer.msg(opmsg+"铁路线坐标点失败！"+rsMap.error,{icon:5});
	        }
	    }    
	});
}

//添加按钮事件
function addAction(){
	$("#method").val("addLinePoint");
	$("#id").val("");
	$("#formWin").window("open");
}

var selectRow = null;
//修改按钮事件
function editAction(){
	selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要修改的数据！",{icon:0});
	}else{
		$("#method").val("editLinePoint");
		$("#id").val(selectRow.id);
		$("#name").textbox("setValue",selectRow.name);
		$("#linenum").textbox("setValue",selectRow.linenum);
		$("#direction").textbox("setValue",selectRow.direction);
		$("#longchain").textbox("setValue",selectRow.longchain);
		$("#lonstr").textbox("setValue",selectRow.lonstr);
		$("#latstr").textbox("setValue",selectRow.latstr);
		$("#longitude").textbox("setValue",selectRow.longitude);
		$("#latitude").textbox("setValue",selectRow.latitude);
		$("#mileage").numberbox("setValue",selectRow.mileage);
		dfmstrIntoInput(selectRow.lonstr,"lon");
		dfmstrIntoInput(selectRow.latstr,"lat");
		$("#formWin").window("open");
	}
}

//删除数据
function deleteAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的数据！",{icon:0});
	}else{
		top.layer.confirm("确定要删除该数据吗？", function(index){
    		top.layer.close(index);
    		var param = new Object();
			param.method="deleteLinePoint";
			param.id = selectRow.id;
			doAjax("../json/DataService!editData",param,{},function(rsMap,op){
				var state = rsMap.state;
				if(state){
					loadData();
					top.layer.alert("数据删除成功！",{icon:6});
				}else{
					top.layer.alert("数据删除失败！"+rsMap.error,{icon:5});
				}
				savelog("铁路线管理","删除数据",JSON.stringify(selectRow));
			});
    	});
	}
}
//重置表单
function cancel(){
	$("#formWin").window("close");
}
//显示文件上传窗口
function showUploadWin(){
	top.openLayerWindow("数据批量导入", 0.5, ["600px","300px"], 2, "../upload-excel-point.jsp", function(index) {
		top.layer.close(index);
		loadData();
	});
}
var isFirstOpen = false;
//显示地图窗口
function showMapWin(){
	$("#mapWin").window("open");
	//如果是第一次打开地图窗口
	if(!isFirstOpen){
		// 百度地图API功能
		map = new BMap.Map("allmap");    // 创建Map实例
		map.addControl(new BMap.MapTypeControl());   //添加地图类型控件
		map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
		showPointOnMap();
		var mk = pointMarkers[0];
		map.centerAndZoom(mk.point, 13);  // 初始化地图,设置中心点坐标和地图级别
	}
}