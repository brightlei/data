$(function(){
	$("#tbForm td").css("padding", "10px");
	$("#dicForm td").css("padding", "10px");
	loadDic();
});

//加载数据
function loadDic(){
	//var index = top.showLayerLoading("正在加载数据，请稍候......");
	var param = {method:"queryDic"};
	doAjax("../json/DataService!listAll",{method:"queryDic"},{},function(rsMap,op){
		//top.hideLayerLoading(index);
		if(rsMap.state){
			var data = rsMap.data;
			cache["dic_data"]=data;
			$("#dg").datagrid("loadData",data);
			$("#dg").datagrid("selectRow",0);
			$("#dicdata_diccode").val(data[0].code);
			getDicdata(data[0].code);
		}else{
			
		}
		savelog("字典管理","查询数据",JSON.stringify(param));
	});
}
//点击字典获取字典明细数据
function onClickRow(index, row){
	var diccode = row.code;
	getDicdata(diccode);
}
//添加字典
function addAction(){
	$("#dic_id").val("");
	$("#dic_code").val("");
	$("#dic_name").val("");
	$("#dicWin").window("open");
}

//编辑字典分类数据
function editAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.showAlertMsg("请从列表中选择您要修改的数据！",0,null);
		//showSuccessMsg("opMessage","请从下面列表中选择您要修改的数据！");
	}else{
		$("#dic_id").val(selectRow.id);
		$("#dic_code").val(selectRow.code);
		$("#dic_name").val(selectRow.name);
		$("#dicWin").window("open");
	}
}
//删除字典数据
function removeAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.showAlertMsg("请从列表中选择您要删除的数据！",0,null);
	}else{
		top.layer.confirm('确定要删除该字典分类数据吗？',{icon: 3, title:'提示'}, function(index){
			top.layer.close(index);
			var param = new Object();
			param.method="deleteDic";
			param.id = selectRow.id;
			doAjax("../json/DataService!editData",param,{},function(rsMap,op){
				var data = rsMap.data;
				if(data>0){
					top.layer.msg('字典分类信息删除成功！',{time: 1000});
					loadDic();
				}
				savelog("字典管理","删除数据",JSON.stringify(param));
			});
		});
		return;
	}
}
//保存或者修改数据
function saveOrUpdate(){
	var id = $("#dic_id").val();
	var code = $("#dic_code").val();
	var name = $("#dic_name").val();
	if(code==null||code==""){
		top.layer.msg('字典分类编码不能为空！',{time: 1000});
		return;
	}
	if(name==null||name==""){
		top.layer.msg('字典分类名称不能为空！',{time: 1000});
		return;
	}
	var optype = "add";
	var param = {};
	param.method="addDic";
	if(id!=null&&id!=""){
		optype = "edit";
		param.method="editDic";
		param.id=id;
	}else{
		var isExist = checkExist(cache["dic_data"],"code",code);
		if(isExist){
			top.layer.msg("字典分类编码【"+code+"】已存在！",{time: 1000});
			return;
		}
		isExist = checkExist(cache["dic_data"],"name",name);
		if(isExist){
			top.layer.msg("字典分类名称【"+name+"】已存在！",{time: 1000});
			return;
		}
	}
	param.code = code;
	param.name = name;
	doAjax("../json/DataService!saveData",param,{},
	function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#dicWin").window("close");
				top.showAlertMsg("字典数据保存成功！",6,null);
				//top.layer.msg("字典数据保存成功！",{time: 1000});
				loadDic();
			}else{
				
			}
		}else{
			
		}
		if(optype=="add"){
			savelog("字典管理","添加字典分类数据",JSON.stringify(param));
		}else{
			savelog("字典管理","修改字典分类数据",JSON.stringify(param));
		}
	});
}

//加载字典明细
function getDicdata(diccode){
	$("#dicdataWin").window("close");
	$("#dicdata_diccode").val(diccode);
	var param = {diccode:diccode};
	param.method="queryDicData";
	//showLoading("正在加载数据，请稍候......");
	doAjax("../json/DataService!listAll",param,{},
	function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			cache["dicdata_data"]=data;
			$("#dglist").datagrid("loadData",data);
		}else{
			
		}
		savelog("字典管理","获取字典明细数据",JSON.stringify(param));
	});
}
//保存字典明细数据
function saveOrUpdateDicData(){
	var id = $("#dicdata_id").val();
	var diccode = $("#dicdata_diccode").val();
	var code = $("#dicdata_code").val();
	var name = $("#dicdata_name").val();
	var rank = $("#dicdata_rank").val();
	if(code==null||code==""){
		top.layer.msg("字典明细编码不能为空！",{time: 1000});
		return;
	}
	if(name==null||name==""){
		top.layer.msg("字典明细名称信息删除成功！",{time: 1000});
		return;
	}
	var param = {};
	param.method="addDicData";
	if(id!=null&&id!=""){
		param.method="editDicData";
		param.id=id;
	}else{
		var isExist = checkExist(cache["dicdata_data"],"code",code);
		if(isExist){
			top.layer.msg("字典明细编码【"+code+"】已存在！",{time: 1000});
			return;
		}
		isExist = checkExist(cache["dicdata_data"],"name",name);
		if(isExist){
			top.layer.msg("字典明细名称【"+name+"】已存在！",{time: 1000});
			return;
		}
	}
	param.diccode = diccode;
	param.code = code;
	param.name = name;
	param.rank = rank;
	doAjax("../json/DataService!saveData",param,{},
	function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			if(parseInt(data,10)==1){
				$("#dicdataWin").window("close");
				top.layer.msg("字典明细数据保存成功！",{time: 1000});
				var diccode = $("#dicdata_diccode").val();
				getDicdata(diccode);
			}else{
				
			}
		}else{
			
		}
		if(param.method="addDicData"){
			savelog("字典管理","添加字典明细",JSON.stringify(param));
		}else{
			savelog("字典管理","修改字典明细",JSON.stringify(param));
		}
	});
}
//点击添加字典明细事件
function addDicAction(){
	$("#dicdata_id").val("");
	$("#dicdata_code").val("");
	$("#dicdata_name").val("");
	var data = cache["dicdata_data"];
	if(data!=null){
		$("#dicdata_rank").val(data.length+1*1);
	}
	$("#dicdataWin").window("open");
}
//点击编辑字典明细事件
function editDicAction(){
	var selectRow = $("#dglist").datagrid("getSelected");
	if(selectRow==null){
		top.showAlertMsg("请从列表中选择您要修改的数据！",0,null);
	}else{
		$("#dicdata_id").val(selectRow.id);
		$("#dicdata_diccode").val(selectRow.diccode);
		$("#dicdata_code").val(selectRow.code);
		$("#dicdata_name").val(selectRow.name);
		$("#dicdata_rank").val(selectRow.rank);
		$("#dicdataWin").window("open");
	}
}
//点击删除字典明细事件
function removeDicAction(){
	var selectRow = $("#dglist").datagrid("getSelected");
	if(selectRow==null){
		top.showAlertMsg("请从列表中选择您要删除的数据！",0,null);
	}else{
		top.layer.confirm('确定要删除该字典明细数据吗？',{icon: 3, title:'提示'}, function(index){
			top.layer.close(index);
			var param = new Object();
			param.method="deleteDicData";
			param.id = selectRow.id;
			doAjax("../json/DataService!editData",param,{},function(rsMap,op){
				var data = rsMap.data;
				if(data>0){
					top.layer.msg('字典明细信息删除成功！',{time: 1000});
					var diccode = $("#dicdata_diccode").val();
					getDicdata(diccode);
				}
				savelog("字典管理","删除字典明细",JSON.stringify(param));
			});
		});
	}
}
//检查是否有重复的数据
function checkExist(data,attr,value){
	var isExist = false;
	var count = data.length;
	var rcd = null;
	for(var i=0;i<count;i++){
		rcd = data[i];
		if(rcd[attr]==value){
			isExist = true;
			break;
		}
	}
	return isExist;
}