$(function(){
	$("#tbForm td").css("padding","10px 5px");
	$("input[name=rtype]").click(function(){
		loadTableColumns();
	});
});

//页面加载完成事件
function onloadComplete(){
	initDataTable();
	loadConfigs();
	loadTableColumns();
}
var webconfigs = [];
//加载配置文件
function loadConfigs(){
	doAjax("../json/Web!loadConfigs",{},{},function(rsMap,op){
		if(rsMap.state){
			webconfigs = rsMap.list;
		}else{
			
		}
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
			{field:'is_nullable',title:'是否允许为空',width:'120',align:"center",formatter:function(value,row,index){
				if(value=="true"){
					return '<span style="color:blue;font-weight:bold;">是</span>';
				}else{
					return '<span style="color:red;font-weight:bold;">否</span>';
				}
			}},
			{field:'description',title:'字段说明',width:'170',align:"center"}
		]]
	});
}
//加载数据表字段数据
function loadTableColumns(){
	var table = $("input[name=rtype]:checked").val();
	$("#dg").datagrid("loading");
	doAjax("../json/DataService!listAll",{method:"getTableColumns",table:table},{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.state){
			var data = rsMap.data;
			$("#dg").datagrid("loadData",data);
		}else{
			
		}
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
	var table = $("input[name=rtype]:checked").val();
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
	var serviceUrl = "../json/DataService!addColumn";
	var msgtips = "添加";
	if(op=="edit"){
		serviceUrl = "../json/DataService!editColumn";
		param.updatecomment = true;
		msgtips = "修改";
		if(selectRow.description==null||selectRow.description=="null"){
			param.addcomment = true;
		}else if(selectRow.description!=description){
			param.editcomment = true;
		}
	}
	doAjax(serviceUrl,param,{},
	function(rsMap,op){
		hideLoading();
		if(rsMap.state){
			loadTableColumns();
			$("#formWin").window("close");
			top.layer.alert(msgtips+"字段【"+name+"】成功！",{icon:6});
		}else{
			top.layer.alert(msgtips+"字段【"+name+"】失败！"+rsMap.error,{icon:2});
		}
	});
}
//添加按钮事件
function addAction(){
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
	selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要修改的数据！",{icon:0});
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

//删除数据
function deleteAction(){
	var selectRow = $("#dg").datagrid("getSelected");
	if(selectRow==null){
		top.layer.alert("请从下面列表中选择您要删除的数据！",{icon:0});
	}else{
		top.layer.confirm("确定要删除该表字段吗？删除后该表字段对应的所有数据也会被删除！", function(index){
    		top.layer.close(index);
    		var table = $("input[name=rtype]:checked").val();
    		var columnName = selectRow.column_name;
    		var param = new Object();
			param.method="deleteTableColumn";
			param.table = table;
			param.column_name = columnName;
			doAjax("../json/DataService!deleteColumn",param,{},function(rsMap,op){
				var state = rsMap.state;
				if(state){
					loadTableColumns();
					top.layer.alert("表字段【"+columnName+"】删除成功！",{icon:6});
				}else{
					top.layer.alert("表字段【"+columnName+"】删除失败！"+rsMap.error,{icon:5});
				}
			});
    	});
	}
}
//判断字段是否设置显示
function checkColumnShow(type,field){
	var isShow = false;
	var count = webconfigs.length;
	var config = null;
	for(var i=0;i<count;i++){
		if(webconfigs[i].nodeName==type+"_showcolumns"){
			config = webconfigs[i];
			break;
		}
	}
	var nodeValue = config.nodeValue;
	var jsonArray = eval("("+nodeValue+")");
	for(var i=0;i<jsonArray.length;i++){
		if(jsonArray[i].field==field){
			isShow = true;
			break;
		}
	}
	return isShow;
}

var selectTable = null;
//显示字段设置窗口
function showSetWin(){
	var table = $("input[name=rtype]:checked").val();
	selectTable = table;
	var rows = $("#dg").datagrid("getRows");
	var count = rows.length;
	var sb = new Array();
	var row = null;
	sb.push('<li style="width:630px;border:0px;border-bottom:1px solid #eee;"><input type="checkbox" name="ckball" value="0" id="ckb-all"/><label for="ckb-all">全选</label><span style="margin-left:10px;color:red;font-weight:bold;">说明：请选择您需要在地图标注点详细信息中显示的字段！</span></li>');
	var isShow = false;
	for(var i=0;i<count;i++){
		row = rows[i];
		isShow = checkColumnShow(table,row.column_name);
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
	});
}
//保存字段设置
function saveColumnShowAction(){
	var jsonArray = new Array();
	$("input[name=ckb]").each(function(){
		var checked = $(this)[0].checked;
		if(checked){
			var field = $(this).val();
			var text = $(this).attr("text");
			jsonArray.push({"field":field,"text":text});
		}
	});
	var nodeName = selectTable+"_showcolumns";
	var nodeValue = JSON.stringify(jsonArray);
	var param = new Object();
	param.nodeName = nodeName;
	param.nodeValue = nodeValue;
	doAjax("../json/Web!editConfig",param,{},function(rsMap,op){
		var state = rsMap.state;
		if(state){
			loadConfigs();
			top.layer.alert("表字段显示设置成功！",{icon:6});
			$("#showSetWin").window("close");
		}
	});
}