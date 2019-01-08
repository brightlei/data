$(function(){
	$("#tbForm td").css("padding", "10px");
	loadData();
});

//加载数据
function loadData(){
	//showLoading("正在加载数据，请稍候......");
	$("#menuTree").html("正在加载数据，请稍候......");
	doAjax("../json/DataService!listAll",{method:"getRoleRight"},{},function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
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
			$("#right_id").val(node.id);
			$("#right_code").val(node.code);
			$("#right_name").val(node.text);
			$("#right_pageurl").val(node.pageurl);
			$("#right_operate").val(node.operate);
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
			treeNode.pageurl = record.pageurl;
			treeNode.operate = record.operate;
			treeNode.children = getTreeChildren(record.code);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}

//添加树节点
function append(){
	top.layer.prompt({
		title:"请输入你要添加的页面菜单名称"
	},function(value,index,elem){
		top.layer.close(index);
		var nodeText = value;
		var t = $('#menuTree');
		var node = t.tree('getSelected');
		var pcode = node.code;
		var param = new Object();
		param.name = nodeText;
		param.pcode = pcode;
		param.method = "addRoleRight";
		var children = t.tree("getChildren",node.target);
		var count = children.length;
		var code = count<10?"0"+count:""+count;
		param.code = pcode+code;
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../json/DataService!saveData",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(rsMap.data!="-1"){
				top.layer.msg("添加系统功能菜单成功！",{icon:6});
			}
			loadData();
		});
	});
}

//修改功能菜单节点
function updateMenu(){
	var paramArr = new Array();
	var id = $("#right_id").val();
	var code = $("#right_code").val();
	var name = $("#right_name").val();
	var pageurl = $("#right_pageurl").val();
	var operate = $("#right_operate").val();
	var param = new Object();
	param.id = id;
	param.code = code;
	param.name = name;
	param.pageurl = pageurl;
	param.operate = operate;
	param.method = "editRoleRight";
	if(id==null||id==""){
		top.layer.msg("请从左边功能树中选择你要修改的项！",{icon:0});
		//showErrorMsg("opMessage","请从左边功能树中选择你要修改的项！");
		return;
	}
	var t = $('#menuTree');
	var treeNode = t.tree('find',id);
	doAjax("../json/DataService!editData",param,{},function(rsMap,op){
		if(rsMap.data=="1"){
			top.layer.msg("权限菜单项信息修改成功！",{icon:6});
			treeNode.text = name;
			treeNode.pageurl = pageurl;
			treeNode.operate = operate;
			t.tree('update',treeNode);
		}else{
			top.layer.msg("权限菜单项信息修改失败！",{icon:5});
		}
	});
}
//删除部门信息
function removeit(){
	var selectRow = $('#menuTree').tree('getSelected');
	if(selectRow.id==1){
		top.layer.msg('权限根节点不允许删除！',{icon:0});
		return;
	}
	top.layer.confirm("确定要删除该权限菜单项信息吗？", function(index){
		top.layer.close(index);
		var param = new Object();
		param.method="deleteRoleRight";
		param.id = selectRow.id;
		param.code = selectRow.code;
		doAjax("../json/DataService!editData",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				top.layer.msg("权限菜单项信息删除成功！",{icon:6});
				$("#menuTree").tree("remove",selectRow.target);
				//loadData();
			}
			savelog("权限管理","删除系统权限菜单",JSON.stringify(selectRow));
		});
	});
}