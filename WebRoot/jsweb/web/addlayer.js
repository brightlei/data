/**
 * 主要脚本方法
 */
$(function(){
	loadLayerTypeData();
	loadIcons();
});
//加载图层类型数据
function loadLayerTypeData(){
	doAjax("json/DataService!listAll",{method:"queryDicData",diccode:"LAYER_TYPE"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			var count = data.length;
			$("#layertype").empty();
			for(var i=0;i<count;i++){
				if(_type==data[i].name){
					$("#layertype").append('<option selected value="'+data[i].name+'">'+data[i].name+'</option>');
				}else{
					$("#layertype").append('<option value="'+data[i].name+'">'+data[i].name+'</option>');
				}
			}
			//Demo
			layui.use('form', function() {
				var form = layui.form();
				//监听提交
				form.on('submit(formDemo)', function(data) {
					layer.msg(JSON.stringify(data.field));
					editLayer(data.field);
					return false;
				});
			});
		}else{
			
		}
	});
}
//修改图层数据
function editLayer(data){
	data.type = data.layertype;
	doAjax("json/DataService!editData",data,{},function(rsMap,op){
		if(rsMap.state){
			if(parseInt(rsMap.data)>0){
				top.layer.close(top.editLayerIndex);
				top.showAlertMsg("修改图层数据成功", 6,null);
			}else{
				top.showAlertMsg("修改图层数据失败", 5,null);
			}
		}
	});
}

//加载图层图标
function loadIcons(){
	doAjax("json/Web!loadIcons",{},{},function(rsMap,op){
		var data = rsMap.data;
		var count = data.length;
		var sb = new Array();
		var index = -1;
		for(var i=0;i<count;i++){
			if(data[i]==_iconurl){
				index = i;
			}
			sb.push('<img class="imgicon" path="'+data[i]+'" src="'+data[i]+'"/>');
		}
		$("#iconlist").html(sb.join(""));
		$("#iconlist img").click(function(){
			var path = $(this).attr("path");
			$("#iconlist img").css("border","1px solid #eee");
			$(this).css("border","1px solid red");
			$("#iconurl").val(path);
		});
		if(index>=0){
			$("#iconlist img").eq(index).css("border","1px solid red");
		}
	});
}

function showIcons(){
	$("#iconlist").css("display","");
	$("#iconlist").attr("display","block");
}
function hideIcons(){
	$("#iconlist").css("display","none");
	$("#iconlist").attr("display","none");
}
//保存图导数据
function saveLayerData(){
	var id = $("#layerdata_id").val();
	var layercode = $("#layer_code").val();
	var name = $("#name").textbox("getValue");
	var address = $("#address").textbox("getValue");
	var longitude = $("#longitude").val();
	var latitude = $("#latitude").val();
	var description = $("#description").textbox("getValue");
	var param = new Object();
	param.layercode = layercode;
	param.name = name;
	param.address = address;
	param.longitude = longitude;
	param.latitude = latitude;
	param.description = description;
	param.method = "addLayerData";
	if(layercode==null||layercode==""){
		showErrorMsg("opMessage","请从左边功能树中选择你要操作的图层！");
		return;
	}
	doAjax("../json/DataService!saveData",param,{},function(rsMap,op){
		if(rsMap.data=="1"){
			showSuccessMsg("opMessage","图层数据添加成功！");
		}else{
			showErrorMsg("opMessage","图层数据添加失败！");
		}
	});
}

function opFormatter(value,row,index){
	return '<a href="javascript:void(0)" style="color:orange;font-weight:bold;">平面图管理</a>';
}