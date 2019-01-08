$(function(){
	$("#tbForm td").css("padding", "10px");
	loadData();
});

//加载数据
function loadData(){
	$("#menuTree").html("正在加载数据，请稍候......");
	var param = {method:"queryDept"};
	doAjax("../json/DataService!listAll",{method:"queryDept"},{},function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}
		savelog("部门管理","查询数据",JSON.stringify(param));
	});
}

//创建树结构
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#menuTree").tree({
		animate:true,
		lines:true,
		onContextMenu: function(e,node){
			e.preventDefault();
			$(this).tree('select',node.target);
			$('#mm').menu('show',{
				left: e.pageX,
				top: e.pageY
			});
		},onClick:function(node){
			editNode = node;
			$("#pid").val(node.id);
			$("#part_id").val(node.id);
			$("#part_code").val(node.code);
			$("#part_name").val(node.text);
		}
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
			treeNode.children = getTreeChildren(record.code);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}

//添加树节点
function append(){
	top.layer.prompt({
		title:"请输入你要添加的部门名称"
	},function(value,index,elem){
		top.layer.close(index);
		var nodeText = value;
		var t = $('#menuTree');
		var node = t.tree('getSelected');
		var pcode = node.code;
		var param = new Object();
		param.name = nodeText;
		param.pcode = pcode;
		param.method = "addDept";
		var children = t.tree("getChildren",node.target);
		var count = children.length;
		var code = count<10?"0"+count:""+count;
		param.code = pcode+code;
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../json/DataService!saveData",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(rsMap.data!="-1"){
				top.layer.msg("添加部门数据成功！",{icon:6});
			}
			loadData();
			savelog("部门管理","添加部门数据",JSON.stringify(param));
		});
	});
}

//修改功能菜单节点
function updateMenu(){
	var paramArr = new Array();
	var id = $("#part_id").val();
	var name = $("#part_name").val();
	var param = new Object();
	param.id = id;
	param.name = name;
	param.method = "editDept";
	if(id==null||id==""){
		top.layer.msg("请从左边功能树中选择你要修改的部门！");
		return;
	}
	var t = $('#menuTree');
	var treeNode = t.tree('find',id);
	top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
	doAjax("../json/DataService!editData",param,{},function(rsMap,op){
		top.layer.close(top.layerIndex);
		if(rsMap.data=="1"){
			top.layer.msg("部门信息修改成功！",{icon:6});
			treeNode.text = name;
			t.tree('update',treeNode);
		}else{
			top.layer.msg("部门信息修改失败！",{icon:5});
		}
		savelog("部门管理","修改部门数据",JSON.stringify(param));
	});
}
//删除部门信息
function removeit(){
	var node = $('#menuTree').tree('getSelected');
	if(node.id==1){
		top.layer.msg("部门根节点不允许删除！");
		return;
	}
	top.layer.confirm("确定要删除该部门信息吗？",{icon:3},function(index){
		top.layer.close(index);
		var param = new Object();
		param.method="deleteDept";
		param.id = node.id;
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../json/DataService!editData",param,{},function(rsMap,op){
			top.layer.close(top.layerIndex);
			var data = rsMap.data;
			if(data>0){
				top.layer.msg("部门信息删除成功！",{icon:6});
				loadData();
			}else{
				top.layer.msg("部门信息删除失败！",{icon:5});
			}
			savelog("部门管理","删除部门数据",JSON.stringify(param));
		});
	})
}