$(function(){
	$("#tbForm td").css("padding", "10px");
	initDataGrid();
	loadPageData();
	loadLayerData();
});
//当前选中的行记录
var selectRow = null;
var selectRowIndex = 0;
//初始化数据表格
function initDataGrid(){
	$("#dg").datagrid({
		title:"用户角色信息列表",
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		remoteSort:false,
		toolbar:'#tb',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'name',title:'角色名称',width:'180'}
		]],
		onSelect:function(index, row){
			selectRowIndex = index;
			selectRow = row;
			showRoleInfo();
		}
	});
	loadRoleData();
}
//加载数据
function loadRoleData(){
	$("#dg").datagrid("loading");
	doAjax("../json/DataService!listAll",{method:"queryRole"},{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			cache["roleTreeData"]=data;
			$("#dg").datagrid("loadData",data);
			savelog("用户角色管理","加载角色数据","");
		}else{
			
		}
	});
}

//显示角色权限信息
function showRoleInfo(){
	var pageright = selectRow.pageright;
	var dataright = selectRow.dataright;
	var pageData = cache["rightTreeData"];
	var dataData = cache["dataTreeData"];
	if(pageright!=null&&pageright!=""){
		var treeData = getCheckedTreeChildren("0",pageData,pageright.split(","));
		$("#pageTree").tree("loadData",treeData);
	}else{
		var treeData = getCheckedTreeChildren("0",pageData,[]);
		$("#pageTree").tree("loadData",treeData);
	}
	if(dataright!=null&&dataright!=""){
		var treeData = getCheckedTreeChildren("0",dataData,dataright.split(","));
		$("#dataTree").tree("loadData",treeData);
	}else{
		var treeData = getCheckedTreeChildren("0",dataData,[]);
		$("#dataTree").tree("loadData",treeData);
	}
}

//加载数据
function loadPageData(){
	//showLoading("正在加载数据，请稍候......");
	$("#pageTree").html("正在加载数据，请稍候......");
	doAjax("../json/DataService!listAll",{method:"getRoleRight"},{},function(rsMap,op){
		//hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			cache["rightTreeData"]=data;
			initRightTree();
		}else{
			
		}
	});
}

//创建树结构
function initRightTree(){
	var data = cache["rightTreeData"];
	var treeData = getTreeChildren("0",data);
	$("#pageTree").tree({
		animate:true,
		lines:true,
		checkbox:true,
		onClick:function(node){
			editNode = node;
		}
	});
	$("#pageTree").tree("loadData",treeData);
}
//获取树节点孩子节点
function getTreeChildren(pcode,data){
	var treeArr = new Array();
	//var treeData = cache["rightTreeData"];
	var count = data.length;
	for(var i=0;i<count;i++){
		var record = data[i];
		if(record.pcode==pcode){
			var treeNode = new Object();
			treeNode.id = record.id;
			treeNode.code = record.code;
			treeNode.text = record.name;
			treeNode.children = getTreeChildren(record.code,data);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}

//获取选中的树节点孩子节点
function getCheckedTreeChildren(pcode,data,ids){
	var treeArr = new Array();
	var count = data.length;
	for(var i=0;i<count;i++){
		var record = data[i];
		if(record.pcode==pcode){
			var treeNode = new Object();
			treeNode.id = record.id;
			treeNode.code = record.code;
			treeNode.text = record.name;
			treeNode.children = getCheckedTreeChildren(record.code,data,ids);
			if(treeNode.children.length==0){
				if(ids!=null&&ids.hasChild(record.id)){
					treeNode.checked = true;
				}else{
					treeNode.checked = false;
				}
			}
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}

//加载数据
function loadLayerData(){
	//showLoading("正在加载数据，请稍候......");
	$("#dataTree").html("正在加载数据，请稍候......");
	doAjax("../json/DataService!listAll",{method:"queryLayerInfo"},{},function(rsMap,op){
		//hideLoading();
		if(rsMap.state){
			var data = rsMap.data;
			cache["dataTreeData"]=data;
			initDataTree();
		}else{
			
		}
	});
}

//创建树结构
function initDataTree(){
	var data = cache["dataTreeData"];
	var treeData = getTreeChildren("0",data);
	$("#dataTree").tree({
		animate:true,
		lines:true,
		checkbox:true,
		onClick:function(node){
			editNode = node;
		}
	});
	$("#dataTree").tree("loadData",treeData);
}
//添加角色
function addRole(){
	top.layer.prompt({
		title:"请输入你要添加的角色名称"
	},function(value,index,elem){
		top.layer.close(index);
		var nodeText = value;
		var param = new Object();
		param.name = nodeText;
		param.method = "addRole";
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../json/DataService!saveData",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(rsMap.data!="-1"){
				top.layer.msg("添加角色["+nodeText+"]成功!请在右侧设置该角色的页面权限和数据权限！",{icon:6});
				loadRoleData();
				savelog("用户角色管理","添加角色",JSON.stringify(param));
			}
		});
	});
}
//获取选中的功能节点
function getCheckedTreeIds(treeId,attr){
	var nodes = $('#'+treeId).tree('getChecked');
	var ids = new Array();
	var pNodeArr = new Array();
	for(var i=0; i<nodes.length; i++){
		ids.push(nodes[i][attr]);
		pNodeArr = getNodeParents(treeId,nodes[i],attr);
		ids = $.merge(pNodeArr,ids);
	}
	var newIds = new Array();
	for(var i=0;i<ids.length;i++){
		var hasChild = newIds.hasChild(ids[i]);
		if(!hasChild){
			newIds.push(ids[i]);
		}
	}
	return newIds;
}
//获取当前节点的父节点
function getNodeParents(treeId,node,attr){
	var pNodeArr = new Array();
	var pnode = $("#"+treeId).tree("getParent",node.target);
	while(pnode!=null){
		pNodeArr.push(pnode[attr]);
		pnode = $("#"+treeId).tree("getParent",pnode.target);
	}
	pNodeArr = pNodeArr.reverse();
	return pNodeArr;
}
//获取数据权限并保存
function saveDataRole(){
	var ids = getCheckedTreeIds("dataTree","id");
	if(ids.length==0){
		top.layer.msg("请选择该角色对应的数据权限！",{icon:0});
	}else{
		if(selectRow==null){
			top.layer.msg("请选择你要设置权限的角色！",{icon:0});
			return;
		}
		var param = new Object();
		param.id = selectRow.id;
		param.method = "saveDataRole";
		param.dataright = ids.join(",");
		top.layerIndex=top.showLayerLoading("正在设置用户角色数据权限，请稍候……");
		doAjax("../json/DataService!editData",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(rsMap.data!="-1"){
				top.layer.msg("设置角色["+selectRow.name+"]数据权限成功!",{icon:6});
				$('#dg').datagrid('updateRow',{
					index: selectRowIndex,
					row: {
						dataright:ids.join(",")
					}
				}).datagrid("selectRow",selectRowIndex);
			}else{
				top.layer.msg("设置角色["+selectRow.name+"]数据权限失败!",{icon:6});
			}
			savelog("用户角色管理","设置数据权限",JSON.stringify(param));
		});
	}
}

//获取页面权限并保存
function savePageRole(){
	var ids = getCheckedTreeIds("pageTree","id");
	if(ids.length==0){
		top.layer.msg("请选择该角色对应的页面权限！",{icon:0});
	}else{
		if(selectRow==null){
			top.layer.msg("请选择你要设置权限的角色！",{icon:0});
			return;
		}
		var param = new Object();
		param.id = selectRow.id;
		param.method = "savePageRole";
		param.pageright = ids.join(",");
		top.layerIndex=top.showLayerLoading("正在设置用户角色页面权限，请稍候……");
		doAjax("../json/DataService!editData",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(rsMap.data!="-1"){
				top.layer.msg("设置角色["+selectRow.name+"]数据页面权限成功!",{icon:6});
				$('#dg').datagrid('updateRow',{
					index: selectRowIndex,
					row: {
						pageright:ids.join(",")
					}
				}).datagrid("selectRow",selectRowIndex);;
			}else{
				top.layer.msg("设置角色["+selectRow.name+"]数据页面权限失败!",{icon:6});
			}
			savelog("用户角色管理","设置页面权限",JSON.stringify(param));
		});
	}
}
//删除用户角色
function deleteRole(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的数据！",{icon:0});
	}else{
		top.layer.confirm("确定要删除该数据吗？", function(index){
    		top.layer.close(index);
    		var param = new Object();
			param.method="deleteRole";
			param.id = selectRow.id;
			doAjax("../json/DataService!editData",param,{},function(rsMap,op){
				var state = rsMap.state;
				if(state){
					loadRoleData();
					top.layer.alert("角色删除成功！",{icon:6});
				}else{
					top.layer.alert("角色删除失败！"+rsMap.error,{icon:5});
				}
				savelog("用户角色管理","删除角色",JSON.stringify(selectRow));
			});
    	});
	}
}