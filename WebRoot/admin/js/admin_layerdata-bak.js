/**
 * 主要脚本方法
 */
var basePath = window.location.href;
basePath = basePath.substring(0,basePath.indexOf("/data/")+6);
var pagetype = "";
$(function(){
	pagetype = getUrlParam("type");
	$(document.body).click(function(){
	});
	$("#tbForm td").css("padding", "10px");
	loadData();
	//loadIcons();
	//loadConfigs();
});

var webconfigs = [];
//加载配置文件
function loadConfigs(){
	doAjax(basePath+"/json/Web!loadConfigs",{},{},function(rsMap,op){
		if(rsMap.state){
			webconfigs = rsMap.list;
		}
	});
}

//加载图层分类目录树数据
function loadData(){
	$("#menuTree").html("正在加载数据，请稍候......");
	doAjax(basePath+"/json/DataService!listAll",{method:"queryLayerInfo"},{},function(rsMap,op){
		if(rsMap.state){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}else{
			top.layer.msg("加载图层目录树数据失败！"+rsMap.error,{icon:5});
		}
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
			$("#layer_id").val(node.id);
			$("#layer_code").val(node.code);
			$("#layer_name").val(node.text);
			$("#layer_type").val(node.type);
			var iconurl = node.iconurl;
			if(iconurl!=null&&iconurl!=""){
				$("#layer_iconurl").attr("path",node.iconurl);
				$("#layer_iconurl").attr("src","../"+node.iconurl);
			}else{
				$("#layer_iconurl").attr("path","");
				$("#layer_iconurl").attr("src","");
			}
			initColumns();
			//loadLayerData();
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
			treeNode.type = record.type;
			treeNode.iconurl = record.iconurl;
			treeNode.children = getTreeChildren(record.code);
			treeArr.push(treeNode);
		}
	}
	return treeArr;
}
//添加树节点
function append(){
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
		var children = t.tree("getChildren",node.target);
		var count = children.length;
		var code = count<10?"0"+count:""+count;
		param.code = pcode+code;
		var layerIndex = top.showLayerLoading("正在保存数据，请稍候……");
		doAjax(basePath+"/json/DataService!saveData",param,{},
		function(rsMap,op){
			top.hideLayerLoading(layerIndex);
			if(rsMap.data!="-1"){
				loadData();
				top.layer.msg("添加图层["+nodeText+"]成功！",{icon:6});
			}
		});
	});
	return;
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

//修改图层分类
function updateMenu(){
	var paramArr = new Array();
	var id = $("#layer_id").val();
	var name = $("#layer_name").val();
	var type = $("#layer_type").val();
	var iconurl = $("#layer_iconurl").attr("path");
	var param = new Object();
	param.id = id;
	param.name = name;
	param.type = type;
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
			treeNode.type = type;
			treeNode.iconurl = iconurl;
			t.tree('update',treeNode);
		}else{
			top.layer.msg("图层信息修改失败！",{icon:5});
		}
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
	*/
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
/*****图层数据相关操作**************/
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
