/**
 * 主要脚本方法
 */
$(function(){
	$("#tbForm td").css("padding", "10px");
	initDataGrid();
	loadData();
});
//初始化数据表格
function initDataGrid(){
	$("#dg").datagrid({
		title:"数据列表",
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
		loadMsg:"正在加载数据，请稍候……",
		onLoadSuccess:function(data){
			
		}
	}).datagrid("loadData",{total:0,rows:[]});
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
		var isLeaf = $("#menuTree").tree("isLeaf",node.target);
		if(isLeaf){
			$('#menuTree').tree('select', node.target);
			break;
		}
	}
}

var sysNodeArray=["业务图层","人工信息","固定信息","短期信息"];
//树节点点击事件
function clickTreeNode(node){
	editNode = node;
	if(node.nodetype==1){
		loadLayerColumn();
	}
	return;
	var isLeaf = $("#menuTree").tree("isLeaf",node.target);
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
			treeNode.template = record.templatepath;
			treeNode.children = getTreeChildren(record.code);
			treeNode.nodetype = record.nodetype;
			if(treeNode.nodetype==0){
				treeNode.iconCls="icon-hamburg-folder";
			}else{
				treeNode.iconCls="icon-hamburg-database";
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
	var param = {};
	param.method="getTableColumns";
	param.table=selectNodeTable;
	$("#dg").datagrid("loadData",[]).datagrid("loading");
	doAjax(basePath+"/json/DataService!listAll",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
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
	initDataTable(columns);
}
var pageSize = 10;
//初始化数据表格
function initDataTable(columns){
	var layerName = editNode.text;
	$("#dg").datagrid("loading");
	$("#dg").datagrid({
		title:"["+layerName+"]数据表数据列表",
		url:basePath+"/json/Layer!loadLayerDataByPage?table="+selectNodeTable,
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
		loadMsg:"正在加载数据，请稍候……",
		columns:[columns],
		onLoadSuccess:function(data){
			savelog("业务图层数据管理","查询业务图层分类",layerName+"["+selectNodeTable+"]");
		}
	});
}
//重新加载数据
function loadTableData(){
	$("#dg").datagrid("reload");
}

//显示批量导入窗口
function showImportWin(){
	if(editNode==null){
		top.layer.msg("请从图层树中选择你要操作的图层！");
		return;
	}
	var template = editNode.template;
	var page = basePath+"upload-excel-layerdata.jsp?table="+selectNodeTable+"&template="+template;
	top.uploadLayerIndex = top.layer.open({
		  title:"图层数据批量导入",
		  type: 2,
		  skin: 'layui-layer-rim',//加上边框
		  area: ['800px', '450px'], //宽高
		  content: page,
		  cancel:function(index){
			  top.layer.close(top.uploadLayerIndex);
			  loadTableData();
		  }
	});
}
/*****图层数据相关操作**************/
function saveOrUpdateLayerData(){
	var op = $("#myform").attr("op");
	var msg = "添加图层数据";
	var apiurl = basePath+"/json/Web!saveLayerData";
	if(op=="add"){
		msg = "添加图层数据";
		apiurl = basePath+"/json/Web!saveLayerData";
	}else{
		apiurl = basePath+"/json/Web!editLayerData";
		msg = "修改图层数据";
	}
	//alert(op+",,"+apiurl);
	$("#myform").form("submit",{
		 url:apiurl,
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
				layer.close(top.openLayerIndex);
				var rsMap = eval("("+jsonstr+")");
				if(rsMap.state){
					loadTableData();
					top.layer.msg(msg+"成功！",{icon:6});
				}else{
					top.layer.msg(msg+"失败！",{icon:5});
				}
				if(op=="add"){
					savelog("业务图层数据管理","添加图层数据",JSON.stringify($("#myform").serializeArray()));
				}else{
					savelog("业务图层数据管理","修改图层数据",JSON.stringify($("#myform").serializeArray()));
				}
			}
	  });
}
//添加图层数据
function addLayerData(){
	var columns = cache["form_columns"];
	var count = columns.length;
	var sb = new Array();
	sb.push('<form id="myform" method="POST" op="add">');
	sb.push('<input type="hidden" id="table" name="table" fieldtype="varchar" value="'+selectNodeTable+'"/>');
	sb.push('<table border="1" bordercolor="#eee" class="tbform" cellpading="0" cellspacing="0" style="width:100%;border-collapse:collapse;">')
	var fieldtype = "number";
	var field = "";
	//如果不是短期信息，则增加地图选点功能
	if(selectNodeTable!="t_shortmsg"){
		sb.push('<tr>');
		sb.push('<td class="rightcss" style="width:110px;">地理位置</td>');
		sb.push('<td>经度：<input type="text" id="longitude" name="longitude" value="113.23" readonly style="width:120px;margin-right:30px;"/>纬度：<input type="text" id="latitude" name="latitude" readonly value="33.23" style="width:120px;margin-right:10px;"/><a id="lonlatBtn" class="easyui-linkbutton c9" iconCls="icon-hamburg-flag" href="javscript:void(0)" onclick="showMapWin()">在地图上选点</a></td>');
		sb.push('</tr>');
	}
	for(var i=0;i<count;i++){
		fieldtype = columns[i].fieldtype;
		field = columns[i].field;
		if(field=="longitude" || field=="latitude"){
			continue;
		}
		sb.push('<tr>');
		sb.push('<td class="rightcss" style="width:110px;">'+columns[i].title+'</td>');
		sb.push('<td><input type="text" id="'+field+'" name="'+field+'" fieldtype="'+fieldtype+'"/>'+getFieldTips(fieldtype)+'</td>');
		sb.push('</tr>');
	}
	sb.push('</table>');
	sb.push('</form>');
	top.openLayerIndex = layer.open({
		  title:"图层数据添加",
		  type: 1,
		  btn: ['提交','取消'],
		  shade: [0],
		  btn1: function(index, layero){
			  saveOrUpdateLayerData();
		  }
		  ,btn2: function(index, layero){
			  layer.close(index);
		  },
		  skin: 'layui-layer-rim',//加上边框
		  area: ['800px', '450px'], //宽高
		  content: sb.join(""),
		  success:function(){
			  $("#lonlatBtn").linkbutton();
		  }
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
	console.log(selectRow);
	var columns = cache["form_columns"];
	var count = columns.length;
	var sb = new Array();
	sb.push('<form id="myform" method="POST" op="edit">');
	sb.push('<input type="hidden" id="id" name="id" fieldtype="varchar"  value="'+selectRow.id+'"/>');
	sb.push('<input type="hidden" id="table" name="table" fieldtype="varchar" value="'+selectNodeTable+'"/>');
	sb.push('<table border="1" bordercolor="#eee" class="tbform" cellpading="0" cellspacing="0" style="width:100%;border-collapse:collapse;">')
	var fieldtype = "number";
	var field = "";
	//如果不是短期信息，则增加地图选点功能
	if(selectNodeTable!="t_shortmsg"){
		sb.push('<tr>');
		sb.push('<td class="rightcss" style="width:110px;">地理位置</td>');
		sb.push('<td>经度：<input type="text" id="longitude" name="longitude" value="'+selectRow.longitude+'" readonly style="width:120px;margin-right:30px;"/>纬度：<input type="text" id="latitude" name="latitude" readonly value="'+selectRow.latitude+'" style="width:120px;margin-right:10px;"/><a id="lonlatBtn" class="easyui-linkbutton c9" iconCls="icon-hamburg-flag" href="javscript:void(0)" onclick="showMapWin()">在地图上选点</a></td>');
		sb.push('</tr>');
	}
	for(var i=0;i<count;i++){
		fieldtype = columns[i].fieldtype;
		field = columns[i].field;
		if(field=="longitude" || field=="latitude"){
			continue;
		}
		sb.push('<tr>');
		sb.push('<td class="rightcss" style="width:110px;">'+columns[i].title+'</td>');
		sb.push('<td><input type="text" id="'+field+'" name="'+field+'" fieldtype="'+fieldtype+'" value="'+selectRow[field]+'"/>'+getFieldTips(fieldtype)+'</td>');
		sb.push('</tr>');
	}
	sb.push('</table>');
	sb.push('</form>');
	top.openLayerIndex = layer.open({
		  title:"图层数据修改",
		  type: 1,
		  btn: ['提交','取消'],
		  btn1: function(index, layero){
			  saveOrUpdateLayerData();
		  }
		  ,btn2: function(index, layero){
			  layer.close(index);
		  },
		  skin: 'layui-layer-rim',//加上边框
		  area: ['800px', '450px'], //宽高
		  content: sb.join(""),
		  success:function(){
			  $("#lonlatBtn").linkbutton();
		  }
	});
}
//删除图层数据
function deleteLayerData(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.msg("请从下面列表中选择您要删除的数据！",{icon:0});
		return;
	}
	top.layer.confirm("确定要删除该图层数据吗？", function(index){
		top.layer.close(index);
		var param = new Object();
		param.method = "deleteLayerData";
		param.table = selectNodeTable;
		param.id = selectRow.id;
		doAjax(basePath+"/json/DataService!editData",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				loadTableData();
				top.layer.msg("图层数据删除成功！",{icon:6});
			}else{
				top.layer.msg("图层数据删除失败！",{icon:5});
			}
			selectRow.method = "deleteLayerData";
			selectRow.table = selectNodeTable;
			savelog("业务图层管理","删除业务图层数据",JSON.stringify(selectRow));
		});
	});
}
var mapLayerIndex = 0;
//显示地图窗口
function showMapWin(){
	var x = $("#longitude").val();
	var y = $("#latitude").val();
	mapLayerIndex = layer.open({
		title:"采集坐标点",
		type: 1,
		closeBtn: 1,
		skin: 'layui-layer-rim',//加上边框
		area: ['800px', '486px'], //宽高
		content: $('#mapWin'),
		success:function(index){
			mapLayerIndex = index;
			$("#mapFrame").attr("src","bdmap-position.html?x="+x+"&y="+y);
		}
	});
	
}
//设置用户坐标
function setUserPosition(lon,lat){
	$("#l-lon").html(lon);
	$("#l-lat").html(lat);
	$("#pointinfo").css("display","block");
}
//设置坐标点
function setPosition(){
	var lon = $("#l-lon").text();
	var lat = $("#l-lat").text();
	$("#longitude").val(lon);
	$("#latitude").val(lat);
	cancelSave();
}

//取消
function cancelSave(){
	layer.close(mapLayerIndex);
}