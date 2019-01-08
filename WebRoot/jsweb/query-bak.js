/**
 * 主要脚本方法
 */
$(function(){
	$("#tbForm td").css("padding", "10px");
	loadData();
});

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
			treeNode.iconurl = record.iconurl;
			treeNode.children = getTreeChildren(record.code);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
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
			if(r>10000){
				top.layer.msg("查询半径不能超过10公里！");
				return;
			}
		}
	}
	var cx = top.user.lon;
	var cy = top.user.lat;
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
	var layerdata = [];
	var bddata = [];
	var node = null;
	var layerInfo = {"0000":[],"0001":[],"0002":[]};
	var pcode = "";
	for(var i=0;i<nodes.length;i++){
		node = nodes[i];
		pcode = node.pcode;
		if(pcode=="BD-00"){
			bddata.push(node.text);
		}else if(pcode=="0000"||pcode=="0001"||pcode=="0002"){
			layerInfo[node.pcode].push("'"+node.code+"'");
		}
	}
	var layercount = layerInfo["0000"].length+layerInfo["0001"].length+layerInfo["0002"].length;
	if(layercount>0){
		var info = {
			"t_fixdata":layerInfo["0000"].join(","),
			"t_shortmsg":layerInfo["0001"].join(","),
			"t_rengongxx":layerInfo["0002"].join(",")
		};
		var dataurl = "../json/DataService!queryNearLayerData?lon="+cx+"&lat="+cy+"&r="+radius+"&queryInfo="+JSON.stringify(info);
		$("#dg").datagrid({
			url:dataurl,
			onClickRow:function(index, row){
				var lon = row.longitude;
				var lat = row.latitude;
				top.jumpTo(lon,lat);
			},
			onLoadSuccess:function(json){
				var rows = json.rows;
				top.showLayerMarkers(rows);
			}
		});
		$("#result").tabs("select","业务图层数据");
	}
	if(bddata.length>0){
		var keywords = bddata.join("$");
		var dataurl = "../json/DataService!queryNearPoiData?lon="+cx+"&lat="+cy+"&r="+radius+"&keywords="+keywords;
		$("#dg-bd").datagrid({
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
		$("#result").tabs("select","百度POI数据");
	}
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