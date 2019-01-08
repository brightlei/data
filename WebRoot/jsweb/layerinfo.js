/**
 * 主要脚本方法
 */
$(function(){
	$("#tbForm td").css("padding", "10px");
	//initDataGrid();
	loadData();
	$(window).resize(function() {
		fitPage();
	});
});

//页面自适应
function fitPage(){
	$("#myTabs").tabs({fit:true});
}
//初始化数据表格
function initDataGrid(layerId){
	$("#dg-"+layerId).datagrid({
		title:"数据列表",
		fit:true,
		pagination:true,
		pageNumber:1,
		pageSize:10,
		remoteSort:false,
		toolbar:'#tb-'+layerId,
		loadMsg:"正在加载数据，请稍候……"
	}).datagrid("loadData",{total:0,rows:[]});
	loadLayerColumn();
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
		savelog("业务图层数据管理","查询业务图层分类",JSON.stringify(param));
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
		checkbox:true,
		onlyLeafCheck:true,
		onCheck:function(node,checked){
			editNode = node;
			if(checked){
				//动态添加tab页
				$("#myTabs").tabs("add",{
					title:node.text,
					selected:true
				});
				$("#myTabs").tabs({fit:true}).tabs("select",node.text);
			}else{
				//关闭当前tab页面，同时清除该图层所有数据
				$("#myTabs").tabs("close",node.text);
				top.clearLayerTypeMarkers(node.tablename);
				//如果关闭车间日计划信息，则关闭刷新任务
				if(node.tablename=="t_shortmsg"){
					clearTimeout(shortmsg_timeout);
					shortmsg_timeout = null;
				}
			}
		},onLoadSuccess:function(node,data){
			initTreeNode(data);
		}
	});
	$("#menuTree").tree("loadData",treeData);
}
var selectTabTitle = "";
var selectTabIndex = 0;
//选择tab面板
function onSelectTab(title,index){
	selectTabTitle = title;
	selectTabIndex = index;
	setTimeout(function(){
		var selectPanel = $("#myTabs .tabs-panels>.panel").eq(selectTabIndex);
		var tabContent = selectPanel.find(".panel-body").html();
		if(tabContent!=""){
			return;
		}
		var tab = $('#myTabs').tabs('getSelected');
		var index = $('#myTabs').tabs('getTabIndex',tab);
		var layerId = editNode.id;
		initTabInfo(index,layerId);
	},100);
}

//初始化数据tab面板
function initTabInfo(index,layerId){
	var selectPanel = $("#myTabs .tabs-panels>.panel").eq(index);
	var sb = [];
	sb.push('<div id="tb-'+layerId+'" style="padding:5px;">');
	sb.push('<input id="search-'+layerId+'" class="easyui-searchbox" style="width:300px;height:30px;" data-options="searcher:searchKey,prompt:\'请输入查询关键字\'"></input>');
	sb.push('</div>');
	sb.push('<table id="dg-'+layerId+'"></table>');
	selectPanel.find(".panel-body").html(sb.join(""));
	$('#search-'+layerId).searchbox({});
	initDataGrid(layerId);
}

//初始化选择第一个叶子节点
function initTreeNode(data){
	var t = $("#menuTree");
	var nodes = t.tree("getChildren",data[0].target);
	var count = nodes.length;
	for(var i=0;i<count;i++){
		node = nodes[i];
		if(node.nodetype==1){
			t.tree('check', node.target);
			break;
		}
		/**
		var isLeaf = t.tree("isLeaf",node.target);
		if(isLeaf){
			t.tree('select', node.target);
			break;
		}
		*/
	}
}

var sysNodeArray=["业务图层","人工信息","固定信息","短期信息"];
//树节点点击事件
function clickTreeNode(node){
	editNode = node;
	alert(node);
	return;
	var t = $("#menuTree");
	var isLeaf = t.tree("isLeaf",node.target);
	if(isLeaf){
		var nodetext = node.text;
		if(sysNodeArray.hasChild(nodetext)){
			return;
		}else{
			loadLayerColumn();
		}
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
			treeNode.syscolumns = record.syscolumns;
			treeNode.showcolumns = record.showcolumns;
			treeNode.template = record.templatepath;
			treeNode.iconurl = record.iconurl;
			treeNode.children = getTreeChildren(record.code);
			treeNode.nodetype = record.nodetype;
			if(treeNode.nodetype==0){
				treeNode.iconCls="icon-hamburg-folder";
			}else{
				treeNode.iconCls="icon-hamburg-database";
			}
			if(treeNode.children.length==0 && treeNode.nodetype==0){
				continue;
			}
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}
var selectNodeTable = null;
//加载图层字段
function loadLayerColumn(){
	selectNodeTable = editNode.tablename;
	var layerId = editNode.id;
	var param = {};
	param.method="getTableColumns";
	param.table=selectNodeTable;
	$("#dg-"+layerId).datagrid("loadData",[]).datagrid("loading");
	doAjax(basePath+"/json/DataService!listAll",param,{},function(rsMap,op){
		$("#dg-"+layerId).datagrid("loaded");
		var data = rsMap.data;
		cache["table_columns"] = data;
		initColumns(data);
	});
}
//初始化表字段
function initColumns(data){
	var count = data.length;
	var columns = new Array();
	var form_columns = new Array();
	var record = null;
	var field = null;
	var title = null;
	var fieldtype = null;
	for(var i=0;i<count;i++){
		record = data[i];
		field = record.column_name;
		title = record.description;
		fieldtype = record.data_type;
		if(field=="id"||field=="state"||field=="createtime"||field=="edittime"){
		//if(field=="id"||field=="state"){
			continue;
		}
		columns.push({field:field,title:title,width:100});
		form_columns.push({field:field,title:title,fieldtype:fieldtype});
	}
	cache["form_columns"]=form_columns;
	cache["field_columns"]=columns;
	initDataTable();
}
var pageSize = 10;
//初始化数据表格
function initDataTable(){
	var layerId = editNode.id;
	var keyword = $("#search-"+layerId).searchbox("getValue");
	var columns = cache["field_columns"];
	var layerName = editNode.text;
	$("#dg-"+layerId).datagrid("loading");
	$("#dg-"+layerId).datagrid({
		title:"["+layerName+"]数据表数据列表",
		url:basePath+"/json/Layer!searchLayerDataByPage?table="+selectNodeTable+"&keyword="+keyword,
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
		toolbar:'#tb-'+layerId,
		loadMsg:"正在加载数据，请稍候……",
		columns:[columns],
		onLoadSuccess:function(data){
			savelog("业务图层数据管理","查询业务图层分类",layerName+"["+selectNodeTable+"]");
			var pointdata = data.rows;
			top.showLayerData(pointdata,selectNodeTable);
		},onClickRow:function(index,row){
			row.layertable = selectNodeTable;
			top.clickRowAction(index,row);
		}
	});
	//如果是车间日计划数据，则开启定时刷新程序
	if(selectNodeTable=="t_shortmsg"){
		shortmsg_layerid = layerId;
		refreshShortMsg();
	}
}
var shortmsg_timeout = null;
var shortmsg_layerid = null;
//定时刷新车间日计划数据
function refreshShortMsg(){
	shortmsg_timeout = setTimeout(function(){
		$("#dg-"+shortmsg_layerid).datagrid("reload");
		refreshShortMsg();
	},5000);
}

//重新加载数据
function loadTableData(){
	var layerId = editNode.id;
	$("#dg-"+layerId).datagrid("reload");
}
//查询搜索
function searchKey(value,name){
	initDataTable();
}