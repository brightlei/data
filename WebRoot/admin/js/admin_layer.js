/**
 * 主要脚本方法
 */
$(function(){
	$(document.body).click(function(){
		
	});
	$("#tbForm td").css("padding", "10px");
	loadData();
	initDataTable();
	loadIcons();
	initIconEvent();
});
var timeout = null;
function initIconEvent(){
	$("#layer_iconurl").hover(function(){
		clearTimeout(timeout);
		$("#iconbox").show();
	},function(){
		timeout = setTimeout(function(){
			$("#iconbox").hide();
		},500);
	});
	$("#iconbox").hover(function(){
		clearTimeout(timeout);
		$("#iconbox").show();
	},function(){
		timeout = setTimeout(function(){
			$("#iconbox").hide();
		},500);
	});
}

//加载图层分类目录树数据
function loadData(){
	$("#menuTree").html("正在加载数据，请稍候......");
	var param = {method:"queryLayerInfo"};
	doAjax(basePath+"/json/DataService!listAll",param,{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}else{
			top.layer.msg("加载图层目录树数据失败！"+rsMap.error,{icon:5});
		}
		savelog("业务图层管理","加载数据",JSON.stringify(param));
	});
}

var editNode = null;
//创建图层目录树结构
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#menuTree").tree({
		animate:true,
		lines:true,
		//创建右键菜单
		onContextMenu: function(e,node){
			e.preventDefault();
			$(this).tree('select',node.target);
			if(node.nodetype==0){
				$("#appendClassBtn").show();
				$("#deleteClassBtn").show();
				$("#appendBtn").show();
				$("#deleteBtn").hide();
			}else{
				$("#appendClassBtn").hide();
				$("#deleteClassBtn").hide();
				$("#appendBtn").hide();
				$("#deleteBtn").show();
			}
			$('#mm').menu('show',{
				left: e.pageX,
				top: e.pageY
			});
			/**
			//只有选择了人工信息、固定信息和短期信息节点才能添加子节点
			if(sysNodeArray.hasChild(node.text)){
				$("#appendBtn").show();
				$("#deleteBtn").hide();
				$('#mm').menu('show',{
					left: e.pageX,
					top: e.pageY
				});
			}else{
				var isLeaf = $("#menuTree").tree("isLeaf",node.target);
				//只有选择了叶子节点，才能删除该节点
				if(isLeaf){
					var pnode = $("#menuTree").tree("getParent",node.target);
					if(sysNodeArray.hasChild(pnode.text)){
						$("#appendBtn").hide();
						$("#deleteBtn").show();
						$('#mm').menu('show',{
							left: e.pageX,
							top: e.pageY
						});
					}
				}
			}
			*/
		},
		//添加点击事件
		onSelect:function(node){
			editNode = node;
			clickTreeNode(node)
		},onLoadSuccess:function(node,data){
			initTreeNode(data);
		}
	});
	$("#menuTree").tree("loadData",treeData);
}
//初始化选择第一个叶子节点
function initTreeNode(data){
	var nodes = $("#menuTree").tree("getChildren",data[0]);
	var count = nodes.length;
	for(var i=0;i<count;i++){
		node = nodes[i];
		var tablename = node.tablename;
		var isLeaf = $("#menuTree").tree("isLeaf",node.target);
		if(isLeaf && tablename!=""){
			$('#menuTree').tree('select', node.target);
			break;
		}
	}
}
var sysNodeArray=["人工信息","固定信息","短期信息"];
//var sysNodeArray=["业务图层","人工信息","固定信息","短期信息"];
//树节点点击事件
function clickTreeNode(node){
	editNode = node;
	var isLeaf = $("#menuTree").tree("isLeaf",node.target);
	if(isLeaf){
		var nodetext = node.text;
		if(sysNodeArray.hasChild(nodetext)){
			return;
		}else{
			setEditForm(node);
		}
	}else{
		var nodetext = node.text;
		if(sysNodeArray.hasChild(nodetext)){
			return;
		}else{
			setEditForm(node);
		}
	}
	//设置表单项
	if(node.nodetype==0){
		$("#blankDiv").css("display","");
		$("#span_layername").html("图层分类名称：");
		$(".span_layer_icon").css("display","none");
		$("#layer_iconurl").css("display","none");
	}else{
		$("#blankDiv").css("display","none");
		$("#span_layername").html("图层名称：");
		$(".span_layer_icon").css("display","");
		$("#layer_iconurl").css("display","");
	}
}
//设置要修改的表单值
function setEditForm(node){
	$("#pid").val(node.id);
	$("#layer_id").val(node.id);
	$("#layer_code").val(node.code);
	$("#layer_name").val(node.text);
	var iconurl = node.iconurl;
	if(iconurl!=null&&iconurl!=""){
		$("#layer_iconurl").attr("path",node.iconurl);
		$("#layer_iconurl").attr("src",basePath+"/"+node.iconurl);
	}else{
		$("#layer_iconurl").attr("path","");
		$("#layer_iconurl").attr("src","");
	}
	if(node.tablename!=""){
		loadTableColumns();
	}
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
			treeNode.tablename = record.tablename;
			treeNode.iconurl = record.iconurl;
			treeNode.syscolumns = record.syscolumns;
			treeNode.showcolumns = record.showcolumns;
			treeNode.tablecolumns = record.tablecolumns;
			treeNode.nodetype = record.nodetype;
			if(treeNode.nodetype==0){
				treeNode.iconCls="icon-hamburg-folder";
			}else{
				treeNode.iconCls="icon-hamburg-database";
			}
			treeNode.children = getTreeChildren(record.code);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}
//添加图层分类
function addLayerClass(){
	top.layer.prompt({title:"请输入你要添加的图层分类名称"},function(val, index){
		top.layer.close(index);
		var nodeText = val;
		var t = $('#menuTree');
		var node = t.tree('getSelected');
		var pcode = node.code;
		var param = new Object();
		param.name = nodeText;
		param.pcode = pcode;
		param.method = "addLayer";
		param.nodetype = 0;
		parent.layerIndex = parent.showLayerLoading("正在保存数据，请稍候……");
		doAjax(basePath+"/json/DataService!saveData",param,{},
		function(rsMap,op){
			parent.hideLayerLoading(parent.layerIndex);
			if(rsMap.data!="-1"){
				var dataKey = rsMap.dataKey;
				loadData();
				top.layer.msg("添加图层分类["+nodeText+"]成功！",{icon:6});
			}
			savelog("业务图层管理","添加业务图层分类",JSON.stringify(param));
		});
	});
}
//删除图层分类
function removeLayerClass(){
	var node = $('#menuTree').tree('getSelected');
	if(node.text=="业务图层"||node.text=="固定信息"||node.text=="短期信息"||node.text=="人工信息"){
		top.layer.alert('图层根节点及系统图层分类不允许删除！', {icon: 0});
		return;
	}
	top.layer.confirm('删除该图层分类信息会同时将该分类下的所有分类及图层数据删除，确定要删除吗？', {icon: 3, title:'提示信息'}, function(index){
		top.layer.close(index);
		var children = $('#menuTree').tree("getChildren",node.target);
		var ids = [];
		ids.push("'"+node.id+"'");
		for(var i=0;i<children.length;i++){
			ids.push("'"+children[i].id+"'");
		}
		var param = new Object();
		param.method="deleteLayerByIds";
		param.ids = ids.join(",");
		var layerName = node.text;
		top.loadingIndex = top.showLayerLoading("执行中，请稍候……");
		doAjax(basePath+"/json/Layer!deleteLayerClass",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				top.layer.msg("图层分类["+layerName+"]删除成功！",{icon:6});
				$("#menuTree").tree("remove",node.target);
			}
			top.hideLayerLoading(top.loadingIndex);
			savelog("业务图层管理","删除业务图层分类",JSON.stringify(node));
		});
	});
}

//添加业务图层树节点
function appendTreeNode(){
	top.layer.prompt({title:"请输入你要添加的图层名称"},function(val, index){
		top.layer.close(index);
		var nodeText = val;
		var t = $('#menuTree');
		var node = t.tree('getSelected');
		var pcode = node.code;
		var param = new Object();
		param.name = nodeText;
		param.pcode = pcode;
		param.method = "addLayer";
		param.nodetype = 1;
		parent.layerIndex = parent.showLayerLoading("正在保存数据，请稍候……");
		doAjax(basePath+"/json/Layer!addLayer",param,{},
		function(rsMap,op){
			parent.hideLayerLoading(parent.layerIndex);
			if(rsMap.data!="-1"){
				var dataKey = rsMap.dataKey;
				loadData();
				top.layer.msg("添加图层["+nodeText+"]成功！",{icon:6});
			}
			savelog("业务图层管理","添加业务图层",JSON.stringify(param));
		});
	});
}
//删除业务图层分类信息
function removeit(){
	var node = $('#menuTree').tree('getSelected');
	if(node.text=="业务图层"||node.text=="固定信息"||node.text=="短期信息"||node.text=="人工信息"){
		top.layer.alert('图层根节点及分类图层不允许删除！', {icon: 0});
		return;
	}
	if(node.tablename=="t_shortmsg"){
		top.layer.alert('车间日计划图层为系统设置图层，不允许删除！', {icon: 0});
		return;
	}
	top.layer.confirm('删除该图层信息会同时删除该图层的所有数据，确定要删除吗？', {icon: 3, title:'提示信息'}, function(index){
		top.layer.close(index);
		var param = new Object();
		param.method="deleteLayer";
		param.id = node.id;
		var layerName = node.text;
		top.loadingIndex = top.showLayerLoading("执行中，请稍候……");
		doAjax(basePath+"/json/Layer!deleteLayer",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				top.layer.msg("图层["+layerName+"]删除成功！",{icon:6});
				$("#menuTree").tree("remove",node.target);
			}
			top.hideLayerLoading(top.loadingIndex);
			savelog("业务图层管理","删除业务图层",JSON.stringify(node));
		});
	});
}
//初始化数据表格
function initDataTable(){
	$("#dg").datagrid({
		title:"数据表字段信息列表",
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
			{field:'column_name',title:'字段名称',width:'90',align:"center"},
			{field:'data_type',title:'字段类型',width:'110',align:"center"},
			{field:'max_length',title:'字段长度',width:'110',align:"center"},   
			/**
			{field:'is_nullable',title:'是否允许为空',width:'120',align:"center",formatter:function(value,row,index){
				if(value=="true"){
					return '<span style="color:blue;font-weight:bold;">是</span>';
				}else{
					return '<span style="color:red;font-weight:bold;">否</span>';
				}
			}},
			*/
			{field:'description',title:'字段说明',width:'170',align:"center"}
		]]
	});
}
//加载数据表字段数据
function loadTableColumns(){
	var name = editNode.text;
	var table = editNode.tablename;
	$("#dg").datagrid({title:"["+name+"]数据表字段信息表"}).datagrid("loadData",[]);
	$("#dg").datagrid("loading");
	var param = {method:"getTableColumns",table:table};
	doAjax("../json/DataService!listAll",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			$("#dg").datagrid("loadData",data);
		}else{
			top.layer.msg("加载图层表结构数据["+nodeText+"]失败！",{icon:5});
		}
		savelog("业务图层管理","加载业务图层表结构",JSON.stringify(param));
	});
}
//显示字符串长度设置
function showLength(){
	var v = $("#columnType").val();
	if(v=="varchar"){
		$("#charlength").css("display","");
	}else{
		$("#charlength").css("display","none");
	}
}

//提交保存数据
function doSubmitAction(){
	var id = $("#id").val();
	var table = editNode.tablename;
	var name = $("#name").textbox("getValue");
	var columnType = $("#columnType").val();
	var column_length = $("#column_length").val();
	var description = $("#description").textbox("getValue");
	//alert(table+","+name+","+columnType+","+description);
	if(name==null||name==""){
		top.layer.alert("字段名称不能为空！",{icon:0});
		return;
	}
	if(description==null||description==""){
		top.layer.alert("字段说明不能为空！",{icon:0});
		return;
	}
	if(columnType=="varchar"){
		columnType = "varchar("+column_length+")";
	}
	var param = {};
	param.table = table;
	param.column_name = name;
	param.columnType = columnType;
	param.description = description;
	var op = $("#op").val();
	var serviceUrl = "../json/Layer!addColumn";
	var msgtips = "添加";
	if(op=="edit"){
		serviceUrl = "../json/Layer!editColumn";
		param.updatecomment = true;
		msgtips = "修改";
		if(selectRow.description==null||selectRow.description=="null"){
			param.addcomment = true;
		}else if(selectRow.description!=description){
			param.editcomment = true;
		}
	}
	parent.layerIndex = parent.showLayerLoading("正在保存数据，请稍候……");
	doAjax(serviceUrl,param,{},
	function(rsMap,op){
		parent.hideLayerLoading(parent.layerIndex);
		if(rsMap.state){
			loadTableColumns();
			$("#formWin").window("close");
			top.layer.alert(msgtips+"字段【"+name+"】成功！",{icon:6});
			saveExcelTemplate();
		}else{
			top.layer.alert(msgtips+"字段【"+name+"】失败！"+rsMap.error,{icon:2});
		}
		if(op=="add"){
			savelog("业务图层管理","添加业务图层属性字段",JSON.stringify(param));
		}else{
			savelog("业务图层管理","修改业务图层属性字段",JSON.stringify(param));
		}
	});
}
//添加按钮事件
function addAction(){
	if(editNode==null){
		return;
	}
	$("#name").textbox("setValue","");
	$("#description").textbox("setValue","");
	$("#op").val("add");
	$("#formWin").window("open");
	$("#name").textbox("enable");
	$("#columnType").attr("disabled",false);
}
var selectRow = null;
//修改按钮事件
function editAction(){
	if(editNode==null){
		return;
	}
	selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要修改的数据！",{icon:0});
	}else{
		var syscolumns = editNode.syscolumns;
		var syscols = syscolumns.split(",");
		var column_name = selectRow.column_name;
		if($.inArray(column_name,syscols)!=-1){
			top.layer.alert("该字段为表系统字段，不允许修改！",{icon:0});
		}else{
			$("#op").val("edit");
			$("#name").textbox("setValue",selectRow.column_name);
			$("#name").textbox("disable");
			$("#columnType").val(selectRow.data_type);
			$("#columnType").attr("disabled",true);
			showLength();
			$("#column_length").val(selectRow.max_length);
			$("#description").textbox("setValue",selectRow.description);
			$("#formWin").window("open");
		}
	}
}

//删除数据
function deleteAction(){
	if(editNode==null){
		return;
	}
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的字段！",{icon:0});
	}else{
		var syscolumns = editNode.syscolumns;
		var syscols = syscolumns.split(",");
		var column_name = selectRow.column_name;
		if($.inArray(column_name,syscols)!=-1){
			top.layer.alert("该字段为表系统字段，不允许删除！",{icon:0});
			return;
		}else{
			top.layer.confirm("确定要删除该表字段吗？删除后该表字段对应的所有数据也会被删除！", function(index){
	    		top.layer.close(index);
	    		var table = editNode.tablename;
	    		var columnName = selectRow.column_name;
	    		var param = new Object();
				param.method="deleteTableColumn";
				param.table = table;
				param.column_name = columnName;
				parent.layerIndex = parent.showLayerLoading("正在删除数据，请稍候……");
				doAjax("../json/Layer!deleteColumn",param,{},function(rsMap,op){
					parent.hideLayerLoading(parent.layerIndex);
					var state = rsMap.state;
					if(state){
						loadTableColumns();
						top.layer.alert("表字段【"+columnName+"】删除成功！",{icon:6});
						saveExcelTemplate();
					}else{
						top.layer.alert("表字段【"+columnName+"】删除失败！"+rsMap.error,{icon:5});
					}
					savelog("业务图层管理","删除业务图层属性字段",JSON.stringify(param));
				});
	    	});
		}
	}
}
//判断字段是否设置显示
function checkColumnShow(fields,field){
	var isShow = false;
	var fieldArray = fields.split(";");
	var count = fieldArray.length;
	var config = null;
	var fieldInfo = null;
	var tmpArr = null;
	for(var i=0;i<count;i++){
		fieldInfo = fieldArray[i];
		tmpArr = fieldInfo.split(":");
		if(tmpArr[0]==field){
			isShow = true;
			break;
		}
	}
	return isShow;
}

//显示字段设置窗口
function showSetWin(){
	if(editNode==null){
		return;
	}
	var showcolumns = editNode.showcolumns;
	var rows = $("#dg").datagrid("getRows");
	var count = rows.length;
	var sb = new Array();
	var row = null;
	sb.push('<li style="width:98%;border:0px;border-bottom:1px solid #eee;"><input type="checkbox" name="ckball" value="0" id="ckb-all"/><label for="ckb-all">全选</label><span style="margin-left:10px;color:red;font-weight:bold;">说明：请选择您需要在地图标注点详细信息中显示的字段！</span></li>');
	var isShow = false;
	for(var i=0;i<count;i++){
		row = rows[i];
		isShow = checkColumnShow(showcolumns,row.column_name);
		if(isShow){
			sb.push('<li style="background:#ffffcc;"><input checked type="checkbox" name="ckb" value="'+row.column_name+'" id="ckb-'+i+'" text="'+row.description+'"/><label for="ckb-'+i+'">'+row.description+'('+row.column_name+')</label></li>');
		}else{
			sb.push('<li style="background:#ffffff;"><input type="checkbox" name="ckb" value="'+row.column_name+'" id="ckb-'+i+'" text="'+row.description+'"/><label for="ckb-'+i+'">'+row.description+'('+row.column_name+')</label></li>');
		}
	}
	$("#column-list").html(sb.join(""));
	$("#showSetWin").window("open");
	$("#ckb-all").click(function(){
		var checked = $(this)[0].checked;
		$("input[name=ckb]").each(function(){
			$(this)[0].checked = checked;
			if(checked){
				$(this).parent().css("background","#ffffcc");
			}else{
				$(this).parent().css("background","#ffffff");
			}
		});
	});
	$("input[name=ckb]").click(function(){
		var checked = $(this)[0].checked;
		if(checked){
			$(this).parent().css("background","#ffffcc");
		}else{
			$(this).parent().css("background","#ffffff");
		}
		var ckbcount = $("input[name=ckb]").length;
		var check_ckbcount = $("input[name=ckb]:checked").length;
		if(check_ckbcount==ckbcount){
			$("#ckb-all")[0].checked=true;
		}else{
			$("#ckb-all")[0].checked=false;
		}
	});
}
//保存字段设置
function saveColumnShowAction(){
	var dataArray = new Array();
	$("input[name=ckb]").each(function(){
		var checked = $(this)[0].checked;
		if(checked){
			var field = $(this).val();
			var text = $(this).attr("text");
			dataArray.push(field+":"+text);
		}
	});
	var showcolumns = dataArray.join(";");
	var param = new Object();
	param.method = "saveLayerShowColumn";
	param.showcolumns = showcolumns;
	param.id = editNode.id;
	parent.layerIndex = parent.showLayerLoading("正在保存数据，请稍候……");
	doAjax("../json/DataService!editData",param,{},function(rsMap,op){
		parent.hideLayerLoading(parent.layerIndex);
		var state = rsMap.state;
		if(state){
			top.layer.alert("表字段显示设置成功！",{icon:6});
			$("#showSetWin").window("close");
			$('#menuTree').tree('update', {
				target: editNode.target,
				showcolumns:showcolumns
			});
		}
		savelog("业务图层管理","设置业务图层显示字段",JSON.stringify(param));
	});
}
//生成数据表格导入模板
function saveExcelTemplate(){
	if(editNode==null){
		return;
	}
	var table = editNode.tablename;
	var layername = editNode.text;
	if(table=="t_shortmsg"){
		return;
	}
	var param = new Object();
	param.method="deleteTableColumn";
	param.table = table;
	param.layername = layername;
	parent.layerIndex = parent.showLayerLoading("正在生成批量导入模板，请稍候……");
	doAjax(basePath+"/json/Layer!createLayerExcelTemplate",param,{},function(rsMap,op){
		if(rsMap.state){
			parent.layer.msg("表格数据批量导入模板文件生成成功！",{icon:6});
		}else{
			parent.layer.msg("表格数据批量导入模板文件生成失败！",{icon:0});
		}
		savelog("业务图层管理","生成业务图层数据模板",JSON.stringify(param));
	});
}

//修改图层分类
function updateMenu(){
	var paramArr = new Array();
	var id = $("#layer_id").val();
	var name = $("#layer_name").val();
	var iconurl = $("#layer_iconurl").attr("path");
	var param = new Object();
	param.id = id;
	param.name = name;
	param.iconurl = iconurl;
	param.method = "editLayer";
	if(id==null||id==""){
		showErrorMsg("opMessage","请从左边功能树中选择你要修改的图层！");
		return;
	}
	var t = $('#menuTree');
	var treeNode = t.tree('find',id);
	doAjax(basePath+"/json/DataService!editData",param,{},function(rsMap,op){
		if(rsMap.data=="1"){
			top.layer.msg("图层信息修改成功！",{icon:6});
			treeNode.text = name;
			treeNode.iconurl = iconurl;
			t.tree('update',treeNode);
		}else{
			top.layer.msg("图层信息修改失败！",{icon:5});
		}
		savelog("业务图层管理","修改业务图层",JSON.stringify(param));
	});
}

//加载图标
function loadIcons(obj){
	doAjax(basePath+"/json/Web!loadIcons",{},{},function(rsMap,op){
		var data = rsMap.data;
		var count = data.length;
		var sb = new Array();
		for(var i=0;i<count;i++){
			sb.push('<img path="'+data[i]+'" src="../'+data[i]+'"/>');
		}
		$("#iconlist").html(sb.join(""));
		$("#iconlist img").click(function(){
			var path = $(this).attr("path");
			$("#layer_iconurl").attr("path",path);
			$("#layer_iconurl").attr("src",$(this).attr("src"));
			hideIcons();
		});
	});
}

function onBeforeClose(){
	$("#iconbox").slideUp("fast",function(){
	});
	return false;
}

function showIcons(){
	$("#iconbox").slideDown("fast",function(){
	});
}
function hideIcons(){
	$("#iconbox").slideUp("fast",function(){
	});
}
/**
var webconfigs = [];
//加载配置文件
function loadConfigs(){
	doAjax(basePath+"/json/Web!loadConfigs",{},{},function(rsMap,op){
		if(rsMap.state){
			webconfigs = rsMap.list;
		}
	});
}


//修改图层
function editLayer(){
	var t = $('#menuTree');
	var node = t.tree('getSelected');
	var id = node.id;
	var pcode = node.pcode;
	var code = node.code;
	var name = node.text;
	var type = node.type;
	var iconurl = node.iconurl;
	var url = basePath+"/web/editLayer.jsp?id="+id+"&pcode="+pcode+"&code="+code+"&name="+name+"&type="+type+"&iconurl="+iconurl;
	top.editLayerIndex = top.openLayerWindow("修改图层", 0.5, ["600px","360px"], 2, url,function(index){
		top.editLayerIndex = index;
		top.layer.close(index);
	});
}


//删除图层分类信息
function removeit(){
	var node = $('#menuTree').tree('getSelected');
	if(node.text=="业务图层"||node.text=="固定信息"||node.text=="短期信息"||node.text=="人工信息"){
		top.layer.alert('图层根节点及分类图层不允许删除！', {icon: 0});
		return;
	}
	top.layer.confirm('确定要删除该图层信息吗？', {icon: 3, title:'提示信息'}, function(index){
		top.layer.close(index);
		var param = new Object();
		param.method="deleteLayer";
		param.id = node.id;
		var layerName = node.text;
		top.loadingIndex = top.showLayerLoading("执行中，请稍候……");
		doAjax(basePath+"/json/DataService!editData",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				top.layer.msg("图层["+layerName+"]删除成功！",{icon:6});
				$("#menuTree").tree("remove",node.target);
				//showSuccessMsg("opMessage","图层信息删除成功！");
				//loadData();
			}
			top.hideLayerLoading(top.loadingIndex);
		});
	});
}
var selectNodeTable = "";
var selectNodeTableText = "";
var selectLayercodeArr = [];
//初始化表字段
function initColumns(){
	selectLayercodeArr = [];
	var dataArr = ["固定信息","短期信息","人工信息"];
	var isLeaf = $("#menuTree").tree("isLeaf",editNode.target);
	selectNodeTable = "t_fixdata";
	selectNodeTableText = "固定信息";
	var text = editNode.text;
	if(text=="业务图层"){
		return;
	}
	if($.inArray(text,dataArr)!=-1){
		var children = $("#menuTree").tree("getChildren",editNode.target);
		for(var i=0;i<children.length;i++){
			selectLayercodeArr.push("'"+children[i].code+"'");
		}
		initSelectNodeInfo(text);
	}else{
		var pnode = $("#menuTree").tree("getParent",editNode.target);
		text = pnode.text;
		initSelectNodeInfo(text);
		selectLayercodeArr.push("'"+editNode.code+"'");
	}
	//alert(selectNodeTable+",,,"+selectNodeTableText+"..."+selectLayercodeArr);
	var count = webconfigs.length;
	var config = null;
	var nodeName = selectNodeTable+"_columns";
	var nodeValue = "";
	for(var i=0;i<count;i++){
		config = webconfigs[i];
		if(config.nodeName==nodeName){
			nodeValue = config.nodeValue;
			break;
		}
	}
	var columnArray = eval("("+nodeValue+")");
	var columns = new Array();
	var field = null;
	for(var i=0;i<columnArray.length;i++){
		field = columnArray[i].field;
		if(field=="id"||field=="layercode"||field=="state"||field=="createtime"||field=="edittime"){
			continue;
		}
		columns.push({field:columnArray[i].field,title:columnArray[i].text,fieldtype:columnArray[i].type});
	}
	cache["table_columns"]=columns;
	initDataTable(columns);
}
//选择节点初始化全局变量
function initSelectNodeInfo(text){
	if(text=="固定信息"){
		selectNodeTable = "t_fixdata";
		selectNodeTableText = "固定信息";
	}else if(text=="短期信息"){
		selectNodeTableText = "短期信息";
		selectNodeTable = "t_shortmsg";
	}else if(text=="人工信息"){
		selectNodeTableText = "人工信息";
		selectNodeTable = "t_rengongxx";
	}
}

var pageSize = 2;
//初始化数据表格
function initDataTable(columns){
	top.clearLayerMarkers();
	top.clearJobLines();
	$("#dg").datagrid({
		title:selectNodeTableText+"数据表["+selectNodeTable+"]数据列表",
		url:basePath+"/json/DataService!loadLayerDataByPage?table="+selectNodeTable+"&layercodes="+selectLayercodeArr.join(","),
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:true,
		fit:true,
		pagination:true,
		pageNumber:1,
		pageSize:10,
		remoteSort:false,
		toolbar:'#tb',
		//closed:true,
		loadMsg:"正在加载数据，请稍候……",
		columns:[columns],
		onLoadSuccess:function(data){
			if(pagetype == "map"){
				if(data.rows.length>0){
					parent.showLayerData(data.rows,selectNodeTable);
				}
			}
		}
	});
}
//加载表数据
function loadTableData(){
	var table = selectNodeTable;
	var param = {table:table,method:"getTableData",layercode:editNode.code};
	$("#dg").datagrid("loading");
	doAjax(basePath+"/json/DataService!listAll",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			$("#dg").datagrid("loadData",data);
			if(pagetype == "map"){
				alert(222);
				parent.showLayerData(data,table);
			}
		}
	});
}

//点击进行定位
function clickRowAction(index,row){
	if(pagetype != "map"){
		return;
	}
	var lon = row.longitude;
	var lat = row.latitude;
	if(lon!=""&&lat!=""){
		lon = parseFloat(lon);
		lat = parseFloat(lat);
		if(isNaN(lon)||isNaN(lat)){
			top.layer.msg("经纬度数据格式不正确，无法定位！", {icon:0});
		}else{
			parent.jumpTo(lon, lat);
		}
	}else{
		top.layer.msg("经纬度数据格式不正确，无法定位！", {icon:0});
	}
	/**
	if(selectNodeTable=="t_fixdata"||selectNodeTable=="t_rengongxx"){
		var lon = row.longitude;
		var lat = row.latitude;
		if(lon!=""&&lat!=""){
			lon = parseFloat(lon);
			lat = parseFloat(lat);
			if(isNaN(lon)||isNaN(lat)){
				top.layer.msg("经纬度数据格式不正确，无法定位！", {icon:0});
			}else{
				parent.jumpTo(lon, lat);
			}
		}else{
			top.layer.msg("经纬度数据格式不正确，无法定位！", {icon:0});
		}
	}else{
		var address = row.job_address;
		var pointstr = top.getStringNumber(address);
		var points = top.getRailwayLineInfo(pointstr);
		top.drawPolygonLine(points);
	}
	* /
}
//加载图标
function loadIcons(obj){
	doAjax(basePath+"/json/Web!loadIcons",{},{},function(rsMap,op){
		var data = rsMap.data;
		var count = data.length;
		var sb = new Array();
		for(var i=0;i<count;i++){
			sb.push('<img path="'+data[i]+'" src="../'+data[i]+'"/>');
		}
		$("#iconlist").html(sb.join(""));
		$("#iconlist img").click(function(){
			var path = $(this).attr("path");
			$("#layer_iconurl").attr("path",path);
			$("#layer_iconurl").attr("src",$(this).attr("src"));
			hideIcons();
		});
	});
}

function onBeforeClose(){
	$("#iconbox").slideUp("fast",function(){
	});
	return false;
}

function showIcons(){
	$("#iconbox").slideDown("fast",function(){
	});
}
function hideIcons(){
	$("#iconbox").slideUp("fast",function(){
	});
}
 *****图层数据相关操作************** 
//添加图层数据
function addLayerData(){
	var columns = cache["table_columns"];
	var count = columns.length;
	var sb = new Array();
	sb.push('<form id="myform" method="POST">');
	sb.push('<input type="hidden" id="table" name="table" fieldtype="varchar" value="'+selectNodeTable+'"/>');
	sb.push('<input type="hidden" id="layercode" name="layercode" fieldtype="varchar" value="'+editNode.code+'"/>');
	sb.push('<table border="1" bordercolor="#eee" class="tbform" cellpading="0" cellspacing="0" style="width:100%;border-collapse:collapse;">')
	var fieldtype = "number";
	for(var i=0;i<count;i++){
		fieldtype = columns[i].fieldtype;
		sb.push('<tr>');
		sb.push('<td class="rightcss" style="width:110px;">'+columns[i].title+'</td>');
		sb.push('<td><input type="text" id="'+columns[i].field+'" name="'+columns[i].field+'" fieldtype="'+fieldtype+'"/>'+getFieldTips(columns[i].fieldtype)+'</td>');
		sb.push('</tr>');
	}
	sb.push('</table>');
	sb.push('</form>');
	top.saveLayerIndex = top.layer.open({
		  title:"图层数据添加",
		  type: 1,
		  btn: ['提交','取消'],
		  btn1: function(index, layero){
			  top.$("#myform").form("submit",{
				 url:basePath+"/json/Web!saveLayerData",
					onSubmit : function(){
						var unNumberCount = 0;
						$(this).find("input[fieldtype!=varchar]").css("border","1px solid #A9A9A9");
						$(this).find("input[fieldtype!=varchar]").each(function(){
							var v = $(this).val();
							var ftype = $(this).attr("fieldtype");
							if(v!=""&&isNaN(v)){
								unNumberCount++;
								$(this).css("border","1px solid red");
							}else{
								if(ftype=="int"){
									if(v.indexOf(".")!=-1){
										unNumberCount++;
										$(this).css("border","1px solid red");
									}
								}else{
									$(this).css("border","1px solid #A9A9A9");
								}
							}
						});
						if(unNumberCount>0){
							top.layer.msg("请确认输入表单中的值是否符合字段类型要求！",{icon:0});
							return false;
						}
						return true;
					},
					success : function(jsonstr) {
						var rsMap = eval("("+jsonstr+")");
						if(rsMap.state){
							top.layer.close(top.saveLayerIndex);
							loadTableData();
							top.layer.msg("数据保存成功！",{icon:6});
						}
					}
			  });
		  }
		  ,btn2: function(index, layero){
			  top.layer.close(index);
		  },
		  skin: 'layui-layer-rim',//加上边框
		  area: ['800px', '450px'], //宽高
		  content: sb.join("")
	});
}
//获取字段类型
function getFieldTips(type){
	var fieldtype = "浮点数值，例如：123.456";
	var isNum = true;
	if(type=="int"){
		fieldtype = "整形数值，例如：123";
	}else if(type=="varchar"){
		fieldtype = "字符串，例如：XXX字符串";
		isNum = false;
	}
	var tipshtml = '<span style="color:red;margin-left:5px;">(字段类型：'+fieldtype+')</span>';
	if(isNum){
		tipshtml = '<span style="color:blue;margin-left:5px;">(字段类型：'+fieldtype+')</span>';
	}
	return tipshtml;
}

//修改图层数据
function editLayerData(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要修改的数据！",{icon:0});
		return;
	}
	var columns = cache["table_columns"];
	var count = columns.length;
	var sb = new Array();
	sb.push('<form id="myform" method="POST">');
	sb.push('<input type="hidden" id="id" name="id" fieldtype="varchar"  value="'+selectRow.id+'"/>');
	sb.push('<input type="hidden" id="table" name="table" fieldtype="varchar"  value="'+selectNodeTable+'"/>');
	sb.push('<input type="hidden" id="layercode" name="layercode" fieldtype="varchar"  value="'+editNode.code+'"/>');
	sb.push('<table border="1" bordercolor="#eee" class="tbform" cellpading="0" cellspacing="0" style="width:100%;border-collapse:collapse;">')
	var fieldtype = "number";
	for(var i=0;i<count;i++){
		fieldtype = columns[i].fieldtype;
		sb.push('<tr>');
		sb.push('<td class="rightcss" style="width:110px;">'+columns[i].title+'</td>');
		sb.push('<td><input type="text" id="'+columns[i].field+'" name="'+columns[i].field+'" fieldtype="'+fieldtype+'" value="'+selectRow[columns[i].field]+'"/>'+getFieldTips(columns[i].fieldtype)+'</td>');
		sb.push('</tr>');
	}
	sb.push('</table>');
	sb.push('</form>');
	top.editLayerIndex = top.layer.open({
		  title:"图层数据修改",
		  type: 1,
		  btn: ['提交','取消'],
		  btn1: function(index, layero){
			  top.$("#myform").form("submit",{
				 url:basePath+"/json/Web!editLayerData",
					onSubmit : function(){
						var unNumberCount = 0;
						$(this).find("input[fieldtype!=varchar]").css("border","1px solid #A9A9A9");
						$(this).find("input[fieldtype!=varchar]").each(function(){
							var v = $(this).val();
							var ftype = $(this).attr("fieldtype");
							if(v!=""&&isNaN(v)){
								unNumberCount++;
								$(this).css("border","1px solid red");
							}else{
								if(ftype=="int"){
									if(v.indexOf(".")!=-1){
										unNumberCount++;
										$(this).css("border","1px solid red");
									}
								}else{
									$(this).css("border","1px solid #A9A9A9");
								}
							}
						});
						if(unNumberCount>0){
							top.layer.msg("请确认输入表单中的值是否符合字段类型要求！",{icon:0});
							return false;
						}
						return true;
					},
					success : function(jsonstr) {
						var rsMap = eval("("+jsonstr+")");
						if(rsMap.state){
							top.layer.close(top.editLayerIndex);
							loadTableData();
							top.layer.msg("数据修改成功！",{icon:6});
						}
					}
			  });
		  }
		  ,btn2: function(index, layero){
			  top.layer.close(index);
		  },
		  skin: 'layui-layer-rim',//加上边框
		  area: ['800px', '450px'], //宽高
		  content: sb.join("")
	});
}
//删除图层数据
function deleteLayerData(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的数据！",{icon:0});
		return;
	}
	top.layer.confirm("确定要删除该图层数据吗？", function(index){
		top.layer.close(index);
		var param = new Object();
		param.table = selectNodeTable;
		param.id = selectRow.id;
		doAjax(basePath+"/json/Web!deleteLayerData",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				loadTableData();
				top.layer.alert("图层数据删除成功！",{icon:6});
			}else{
				top.layer.alert("图层数据删除失败！",{icon:5});
			}
		});
	});
}

//平面图上传
function opFormatter(value,row,index){
	return '<a href="javascript:void(0)" style="color:orange;font-weight:bold;" onclick="showImgUploadWin(\''+row.id+'\')">平面图管理</a>';
}

//打开图片上传窗口
function showImgUploadWin(id){
	var pageurl = basePath+"/upload-dataimg.jsp?id="+id;
	top.openLayerWindow("图片上传", 0.5, ["860px","500px"], 2, pageurl, function(index){
		top.layer.close(index);
	});
}

function closeImportWin(){
	$("#importWin").window("close");
}

//显示批量导入窗口
function showImportWin(){
	if(editNode==null){
		top.layer.msg("请从图层树中选择你要操作的图层！");
		return;
	}
	top.layercode = editNode.code;
	var page = basePath+"upload-excel-layerdata.jsp?table="+selectNodeTable+"&layercode="+top.layercode;
	top.uploadLayerIndex = top.layer.open({
		  title:"图层数据修改",
		  type: 2,
		  skin: 'layui-layer-rim',//加上边框
		  area: ['800px', '450px'], //宽高
		  content: page,
		  cancel:function(index){
			  loadTableData();
			  top.layer.close(top.uploadLayerIndex);
		  }
	});
}
*/