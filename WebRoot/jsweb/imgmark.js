/**
 * 主要脚本方法
 */
$(function(){
	$("#tbForm td").css("padding", "10px");
	$("#mytab").tabs({
		onSelect:function(title){
			if(title=="图片显示"){
				createImgMark();
			}
		}
	});
	loadImgMarks();
});
//加载数据
function loadData(){
	showLoading("正在加载数据，请稍候......");
	doAjax("../json/DataService!listAll",{method:"queryLayerInfo"},{},function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}else{
			
		}
	});
}
//加载图片标注
function loadImgMarks(){
	var data = [];
	data.push({name:"东进站口",description:"火车站东进站口",xrate:0.23,yrate:0.23,createtime:"2017-05-22 07:00:00",person:"管理员"});
	data.push({name:"东出站口",description:"火车站东出站口",xrate:0.32,yrate:0.43,createtime:"2017-05-22 07:00:00",person:"管理员"});
	data.push({name:"A进站口",description:"火车站A进站口",xrate:0.43,yrate:0.33,createtime:"2017-05-22 07:00:00",person:"管理员"});
	$("#dg-mark").datagrid("loadData",data);
	cache["data"]=data;
}

function createImgMark(){
	var data = cache["data"];
	var w = $("#img").width();
	var h = $("#img").height();
	var count = data.length;
	var left = 0;
	var top = 0;
	var rcd = null;
	$("#imgdiv .mk").remove();
	for(var i=0;i<count;i++){
		rcd = data[i];
		left = parseInt(w*rcd.xrate);
		top = parseInt(h*rcd.yrate);
		$("#imgdiv").append('<img class="mk" src="../images/icon_location_red.png" style="position:absolute;left:'+left+'px;top:'+top+'px;z-index:999;"/>');
	}
}